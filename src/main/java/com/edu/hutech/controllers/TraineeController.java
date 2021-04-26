package com.edu.hutech.controllers;

import com.edu.hutech.dtos.CourseDto;
import com.edu.hutech.dtos.TOScoreDto;
import com.edu.hutech.dtos.TraineeDto;
import com.edu.hutech.entities.Attendance;
import com.edu.hutech.entities.Course;
import com.edu.hutech.entities.Trainee;
import com.edu.hutech.models.PaginationRange;
import com.edu.hutech.dtos.TraineeScoreDto;
import com.edu.hutech.repositories.AttendanceRepository;
import com.edu.hutech.repositories.ScoreRepository;
import com.edu.hutech.repositories.TraineeRepository;
import com.edu.hutech.utils.excel.ExcelExporter;
import com.edu.hutech.utils.page.Pagination;
import com.edu.hutech.utils.sort.GenericComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/trainee-management")
public class TraineeController {
    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    /**
     * Display List of all trainees
     *
     * @param model
     * @param page  is the page number of paging trainee list
     * @param size  is the quantity of elements in a page
     * @param field is the name of attribute for sorted type
     * @return the trainee-list view
     */
    @GetMapping()
    public String displayTraineeManagement(Model model,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("field") Optional<String> field,
                                           @RequestParam("search") Optional<String> search) {

        int cPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortField = field.orElse("default");
        String keyword = search.orElse("");

        pageSize = pageSize < 5 ? 5 : pageSize > 500 ? 500 : pageSize;

        List<TraineeScoreDto> listTrainees = traineeRepository.findScoreByAllTrainee();

        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);
            Collections.sort(listTrainees, new GenericComparator(true, splits[0]));
        } else {
            if (sortField.equals("default")) {
            } else {
                Collections.sort(listTrainees, new GenericComparator(false, sortField));
            }
        }

        List<TraineeScoreDto> trainees = Pagination.getPage(listTrainees, cPage, pageSize);


        int totalPages = (int) Math.ceil((double) listTrainees.size() / (double) pageSize);

        model.addAttribute("trainees", trainees);
        model.addAttribute("cPage", cPage);
        model.addAttribute("size", pageSize);
        model.addAttribute("totalElements", listTrainees.size());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("field", sortField);
        model.addAttribute("search", keyword);

        PaginationRange p = Pagination.paginationByRange(cPage, listTrainees.size(), pageSize, 5);
        model.addAttribute("paginationRange", p);

        return "pages/trainee-views/trainee-management";
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("field") Optional<String> field) throws IOException {

        int cPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortField = field.orElse("default");


        pageSize = pageSize < 5 ? 5 : pageSize > 500 ? 500 : pageSize;

        List<TraineeScoreDto> listTrainees = traineeRepository.findScoreByAllTrainee();

        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);
            Collections.sort(listTrainees, new GenericComparator(true, splits[0]));
        } else {
            Collections.sort(listTrainees, new GenericComparator(false, sortField));
        }

        List<TraineeScoreDto> trainees = Pagination.getPage(listTrainees, cPage, pageSize);

        response.setContentType("application/octet-stream");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=trainee-management_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        List<TraineeDto> traineeDtoList = new ArrayList<>();

        for (TraineeScoreDto tsd : trainees) {
            traineeDtoList.add(new TraineeDto(tsd.getId(), tsd.getName(), tsd.getAccount(), tsd.getScore(), tsd.getEmail(), tsd.getUniversity()));
        }


        ExcelExporter<TraineeDto> excelExporter = new ExcelExporter<>(traineeDtoList, TraineeDto.class);

        excelExporter.export(response);

    }


    /**
     * Display infor of trainee when user click on trainee list
     *
     * @param model
     * @param traineeId is the ID of trainee which was clicked on trainee-list by User
     * @return trainee infor view
     */
    @GetMapping("/trainee-details")
    public String displayAllTraineeDetails(Model model, @RequestParam("id") int traineeId) {

        Trainee trainee = traineeRepository.getOne(traineeId);
        double finalScore = scoreRepository.findAvgScoreByTraineeId(traineeId);
        int presentAttendance = attendanceRepository.findPresentAttendanceByTraineeId(traineeId);
        int totalAttendance = attendanceRepository.findTotalAttendanceByTraineeId(traineeId);
        List<TOScoreDto> listNameAndScore = scoreRepository.findScoreEachTOByTraineeId(traineeId);
        List<Attendance> listDateAndAttendance = attendanceRepository.findAttendanceByTraineeId(traineeId);

        double scale = Math.pow(10, 1);

        model.addAttribute("trainee", trainee);
        model.addAttribute("finalScore", (int) (Math.round(finalScore * scale) / scale) * 10);
        model.addAttribute("presentAttendance", presentAttendance);
        model.addAttribute("totalAttendance", totalAttendance);
        model.addAttribute("listNameAndScore", listNameAndScore);
        model.addAttribute("listDateAndAttendance", listDateAndAttendance);

        return "pages/trainee-views/trainee-details";
    }
}
