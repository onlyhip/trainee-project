package com.edu.hutech.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.edu.hutech.dtos.TraineeScoreDto;
import com.edu.hutech.entities.Course;
import com.edu.hutech.entities.Trainer;
import com.edu.hutech.models.PaginationRange;
import com.edu.hutech.repositories.CourseRepository;
import com.edu.hutech.repositories.TraineeRepository;
import com.edu.hutech.repositories.TrainerRepository;
import com.edu.hutech.utils.page.Pagination;

import com.edu.hutech.utils.sort.GenericComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
