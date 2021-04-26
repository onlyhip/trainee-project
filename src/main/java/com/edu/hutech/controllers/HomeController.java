package com.edu.hutech.controllers;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.edu.hutech.entities.Course;
import com.edu.hutech.entities.Fresher;
import com.edu.hutech.entities.Trainee;
import com.edu.hutech.repositories.*;

import com.edu.hutech.utils.data.CreateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private CourseRepository courseRepository;


    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private FresherRepository fresherRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private TrainingObjectiveRepository toRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    /**
     * Handling Home request
     *
     * @param model
     * @return the reporting view of trainee and class
     */
    @GetMapping(value = {"/", "home","/show-report"})
    public String viewHomePage(Model model, @RequestParam("start-date") Optional<String> startDate,
                               @RequestParam("end-date") Optional<String> endDate) {

        int waitingCourse = 0;
        int releaseCourse = 0;
        int runningCourse = 0;
        int waitingTrainee = 0;
        int releaseTrainee = 0;
        int runningTrainee = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Course> listCourse = new ArrayList<>();
        List<Trainee> listTrainee = new ArrayList<>();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String defaultEndDate = dateFormat.format(date);
        String defaultEndDate = "yyyy-MM-dd";
        String defaultStartDate = "yyyy-MM-dd";

        String start = startDate.orElse(defaultStartDate);
        String end = endDate.orElse(defaultEndDate);

        if (start.equals(defaultStartDate) && end.equals(defaultEndDate)) {
            listCourse = courseRepository.findAll();
            listTrainee = traineeRepository.findAll();
            for (Course c : listCourse) {
                if (c.getStatus().equals("Done"))
                    releaseCourse++;
                else if (c.getStatus().equals("Waiting"))
                    waitingCourse++;
                else
                    runningCourse++;
            }

            for (Trainee f : listTrainee) {
                if (Timestamp.valueOf(LocalDateTime.now()).compareTo(f.getTraineeStatus().getStartDay()) < 0)
                    waitingTrainee++;
                else if (Timestamp.valueOf(LocalDateTime.now()).compareTo(f.getTraineeStatus().getEndDate()) > 0)
                    releaseTrainee++;
                else
                    runningTrainee++;
            }
        } else {

            Date dateStart = new Date();
            Date dateEnd = new Date();

            try {
                dateStart = simpleDateFormat.parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                dateEnd = simpleDateFormat.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            listCourse = courseRepository.findAllByOpenDateGreaterThanEqualAndEndDateLessThanEqual(dateStart, dateEnd);

            for (Course c : listCourse) {
                if (c.getStatus().equals("Done"))
                    releaseCourse++;
                else if (c.getStatus().equals("Waiting"))
                    waitingCourse++;
                else
                    runningCourse++;
            }
            for (Course c : listCourse) {
                listTrainee.addAll(c.getTrainee());
            }

            for (Trainee f : listTrainee) {
                if (dateEnd.compareTo(f.getTraineeStatus().getStartDay()) < 0)
                    waitingTrainee++;
                else if (dateEnd.compareTo(f.getTraineeStatus().getEndDate()) > 0)
                    releaseTrainee++;
                else
                    runningTrainee++;
            }
        }

        model.addAttribute("totalCourse", listCourse.size());
        model.addAttribute("totalTrainee", listTrainee.size());
        model.addAttribute("wCourse", waitingCourse);
        model.addAttribute("rCourse", releaseCourse);
        model.addAttribute("rnCourse", runningCourse);
        model.addAttribute("wTrainee", waitingTrainee);
        model.addAttribute("rTrainee", releaseTrainee);
        model.addAttribute("rnTrainee", runningTrainee);

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "/pages/index";
    }


    /**
     * Handling error request
     *
     * @return the error view
     */
    @GetMapping("/404")
    public String error() {
        return "/pages/util-views/404";
    }

    /**
     * Create demo data
     *
     * @return create-database.html
     */
    @GetMapping("/create-data-first")
    public String createDataFirst() {
        CreateData createData = new CreateData();
        Random rand = new Random(System.currentTimeMillis());
        createData.createTrainer(trainerRepository);
        createData.createCourse(trainerRepository, courseRepository);
        createData.createStatus(statusRepository);
        createData.createFresher(courseRepository, statusRepository, fresherRepository);
        createData.createInternship(courseRepository, statusRepository, internshipRepository);
        createData.createTO(trainerRepository, toRepository, courseRepository);
        // createData.createScore(courseRepository,scoreRepository,toRepository, traineeRepository);
        for (Course c : courseRepository.findAll()) {
            c.setCurrCount(traineeRepository.countCourseByCourseId(c.getId()));
            c.setStatus(Timestamp.valueOf(LocalDateTime.now()).compareTo(c.getEndDate()) > 0 ? "Done"
                    : Timestamp.valueOf(LocalDateTime.now()).compareTo(c.getOpenDate()) < 0 ? "Waiting" : "In Process");
            c.setDuration(rand.nextInt(50) + 1);
            courseRepository.save(c);
        }

        return "pages/util-views/create-database";
    }

    /**
     * Create Demo data
     *
     * @return create-database.html
     */
    @GetMapping("/create-data-second")
    public String createDataSecond() {
        CreateData createData = new CreateData();
        createData.createScore(courseRepository, scoreRepository, toRepository, traineeRepository);
        createData.createAttendance(traineeRepository, attendanceRepository);

        scoreRepository.findAll().forEach(s -> {
            toRepository.getOne(s.getTrainingObjective().getId()).setName(s.getName());
            toRepository.save(toRepository.getOne(s.getTrainingObjective().getId()));
        });

        return "pages/util-views/create-database";
    }

}
