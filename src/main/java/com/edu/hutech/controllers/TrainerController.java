package com.edu.hutech.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.edu.hutech.dtos.TrainerDto;
import com.edu.hutech.entities.Trainer;
import com.edu.hutech.models.PaginationRange;
import com.edu.hutech.repositories.CourseRepository;
import com.edu.hutech.repositories.TraineeRepository;
import com.edu.hutech.repositories.TrainerRepository;
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

@Controller
@RequestMapping("/trainer-management")
public class TrainerController {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private CourseRepository courseRepository;


    /**
     * Playing trainer list
     * @param model
     * @return trainer-list view
     */
    @GetMapping()
    public String displayTrainerList(Model model, @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     @RequestParam("field") Optional<String> field) {


        int cPage = page.orElse(1);
        int pageSize =size.orElse(5);
        String sortField = field.orElse("default");

        pageSize = pageSize < 5 ? 5 : pageSize > 500 ? 500 : pageSize;

        List<Trainer> trainers = trainerRepository.findAll();
        model.addAttribute("trainers", trainers);




        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);
            Collections.sort(trainers, new GenericComparator(true, splits[0]));
        } else {
            if (sortField.equals("default")) {
            } else {
                Collections.sort(trainers, new GenericComparator(false, sortField));
            }
        }

        List<Trainer> trainersAfterPaging = Pagination.getPage(trainers, cPage, pageSize);

        int currIndex = trainers.indexOf(trainersAfterPaging.get(0));
        int totalPages = (int) Math.ceil( (double)trainers.size()/ (double) pageSize);
        int totalElements = trainers.size();

        model.addAttribute("trainers", trainersAfterPaging);
        model.addAttribute("cPage", cPage);
        model.addAttribute("currIndex", currIndex);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements",totalElements);
        model.addAttribute("size",pageSize);
        model.addAttribute("field",sortField);


        PaginationRange p = Pagination.paginationByRange(cPage, totalElements, pageSize, 5);
        model.addAttribute("paginationRange", p);

        return "pages/trainer-views/trainer-management";
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("field") Optional<String> field) throws IOException {

        int cPage = page.orElse(1);
        int pageSize =size.orElse(5);
        String sortField = field.orElse("default");

        pageSize = pageSize < 5 ? 5 : pageSize > 500 ? 500 : pageSize;

        List<Trainer> trainers = trainerRepository.findAll();

        if (sortField.contains("-asc")) {
            String[] splits = sortField.split("-asc", 2);
            Collections.sort(trainers, new GenericComparator(true, splits[0]));
        } else {
            if (sortField.equals("default")) {
            } else {
                Collections.sort(trainers, new GenericComparator(false, sortField));
            }
        }

        List<Trainer> trainersAfterPaging = Pagination.getPage(trainers, cPage, pageSize);


        response.setContentType("application/octet-stream");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=trainer-management_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);


        List<TrainerDto> trainerDtoList = new ArrayList<>();

        for (Trainer t : trainersAfterPaging) {
            trainerDtoList.add(new TrainerDto(t.getId(),t.getAccount(), t.getNational(),t.getName(),t.getEmail(),t.getTelNumber(),t.getFacebook()));
        }


        ExcelExporter<TrainerDto> excelExporter = new ExcelExporter<>(trainerDtoList, TrainerDto.class);

        excelExporter.export(response);

    }

}
