package com.edu.hutech.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.edu.hutech.dtos.CourseDto;
import com.edu.hutech.dtos.TraineeDto;
import com.edu.hutech.entities.Course;
import com.edu.hutech.entities.Trainee;
import com.edu.hutech.models.PaginationRange;
import com.edu.hutech.dtos.TraineeScoreDto;
import com.edu.hutech.repositories.CourseRepository;
import com.edu.hutech.repositories.TraineeRepository;

import com.edu.hutech.services.core.CourseService;
import com.edu.hutech.utils.excel.ExcelExporter;
import com.edu.hutech.utils.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/class-management")
public class ClassController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private CourseService courseService;

    /**
     * Handing the request representing classes infor
     *
     * @param model
     * @param page  is page number in paging
     * @param size  is the quantity of element in a page
     * @param field is the field that user want to sorted by
     * @return class-management.html
     */
    @GetMapping()
    public String displayCourseList(Model model, @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size,
                                    @RequestParam("field") Optional<String> field,
                                    @RequestParam("search") Optional<String> search) {

        int cPage = page.orElse(1);
        int pageSize = size.orElse(5);
        String sortField = field.orElse("default");
        String keyword = search.orElse("");


        if (pageSize < 5) {
            pageSize = 5;
        }
        if (pageSize > 500) {
            pageSize = 500;
        }

        Page<Course> classPage;


        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);

            if (splits[0].equals("head-teacher")) {
                classPage = courseService.findPaginated(cPage, pageSize, "trainer.name");
            } else {
                classPage = courseService.findPaginated(cPage, pageSize, splits[0]);
            }
        } else {
            if (sortField.equals("default")) {
                classPage = courseService.findPaginated(cPage, pageSize);
            } else {
                if (sortField.equals("head-teacher")) {
                    classPage = courseService.findPaginatedDesc(cPage, pageSize, "trainer.name");
                } else {
                    classPage = courseService.findPaginatedDesc(cPage, pageSize, sortField);
                }
            }
        }


        model.addAttribute("classPage", classPage);
        model.addAttribute("cPage", cPage);
        model.addAttribute("size", pageSize);
        model.addAttribute("field", sortField);

        model.addAttribute("search", keyword);


        PaginationRange p = Pagination.paginationByRange(cPage, classPage.getTotalElements(), pageSize, 5);
        model.addAttribute("paginationRange", p);

        return "pages/class-views/class-management";
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("field") Optional<String> field) throws IOException {

        int cPage = page.orElse(1);
        int pageSize = size.orElse(5);
        String sortField = field.orElse("default");



        if (pageSize < 5) {
            pageSize = 5;
        }
        if (pageSize > 500) {
            pageSize = 500;
        }

        Page<Course> classPage;


        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);

            if (splits[0].equals("head-teacher")) {
                classPage = courseService.findPaginated(cPage, pageSize, "trainer.name");
            } else {
                classPage = courseService.findPaginated(cPage, pageSize, splits[0]);
            }
        } else {
            if (sortField.equals("default")) {
                classPage = courseService.findPaginated(cPage, pageSize);
            } else {
                if (sortField.equals("head-teacher")) {
                    classPage = courseService.findPaginatedDesc(cPage, pageSize, "trainer.name");
                } else {
                    classPage = courseService.findPaginatedDesc(cPage, pageSize, sortField);
                }
            }
        }

        List<Course> list = classPage.getContent();

        response.setContentType("application/octet-stream");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=class-management_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        List<CourseDto> courseDtoList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Course c : list) {
            courseDtoList.add(new CourseDto(c.getId(), c.getName(), simpleDateFormat.format(c.getOpenDate()), simpleDateFormat.format(c.getEndDate()), c.getDuration(), c.getNote(), c.getPlanCount(), c.getCurrCount(), c.getStatus()));
        }

        ExcelExporter<CourseDto> excelExporter = new ExcelExporter<>(courseDtoList, CourseDto.class);

        excelExporter.export(response);
    }

    /**
     * Handling request of seeing class detail infor
     *
     * @param model
     * @param classId is the id of class which user want to see detail
     * @param page    is the page number when paging
     * @param view    is the type of view in showing list trainees in a class, there is two view
     * @return class-details
     */
    @GetMapping("/class-details")
    public String displayClassDetail(Model model, @RequestParam("id") int classId,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("view") Optional<String> view) {

        int cPage = page.orElse(1);
        int pageSize = 10;
        String modeView = view.orElse("list");

        Course course = courseRepository.findById(classId);
        model.addAttribute("class", course);

        List<TraineeScoreDto> listTrainees = traineeRepository.findScoreByTrainee(classId);

        List<TraineeScoreDto> trainees = Pagination.getPage(listTrainees, cPage);

        int totalPages = (int) Math.ceil((double) listTrainees.size() / (double) pageSize);


        model.addAttribute("modeView", modeView);
        model.addAttribute("classId", classId);
        model.addAttribute("trainees", trainees);
        model.addAttribute("cPage", cPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);
        model.addAttribute("totalElements", listTrainees.size());

        PaginationRange p = Pagination.paginationByRange(cPage, listTrainees.size(), pageSize, 5);
        model.addAttribute("paginationRange", p);

        return "pages/class-views/class-details";
    }

    @GetMapping("/class-details/export")
    public void exportToExcelClassDetails(HttpServletResponse response,
                                          @RequestParam("id") int classId) throws IOException {


        Course course = courseRepository.findById(classId);

        List<TraineeScoreDto> listTrainees = traineeRepository.findScoreByTrainee(classId);



        response.setContentType("application/octet-stream");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=class-details_id_" + classId + "_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        List<TraineeDto> traineeDtoList = new ArrayList<>();

        for (TraineeScoreDto tsd : listTrainees) {
            traineeDtoList.add(new TraineeDto(tsd.getId(),tsd.getName(),tsd.getAccount(),tsd.getScore(),tsd.getEmail(),tsd.getUniversity()));
        }

        ExcelExporter<TraineeDto> excelExporter = new ExcelExporter<>(traineeDtoList, TraineeDto.class);

        excelExporter.export(response);
    }


    /**
     * handling request when user change the number of page size
     *
     * @param model
     * @param page  is page number in paging
     * @param size  is the quantity of element in a page
     * @return
     */
    @PostMapping()
    public String displayCourseListByPageSize(Model model, @RequestParam("page") Optional<Integer> page,
                                              @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Course> ClassPage = courseService.findPaginated(currentPage, pageSize, "name");
        List<Course> listCourses = ClassPage.getContent();

        model.addAttribute("classes", listCourses);

        return "pages/class-views/class-management";
    }

}
