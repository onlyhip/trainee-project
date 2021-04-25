package com.edu.hutech.dtos;

import java.util.Date;

public class CourseDto {

    public int id;

    public String name;

    public String openDate;

    public String endDate;

    public int duration;

    public String note;

    public int planCount;

    public int currCount;

    public String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenDate() {
        return openDate;
    }


    public String getEndDate() {
        return endDate;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getPlanCount() {
        return planCount;
    }

    public void setPlanCount(int planCount) {
        this.planCount = planCount;
    }

    public int getCurrCount() {
        return currCount;
    }

    public void setCurrCount(int currCount) {
        this.currCount = currCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CourseDto(int id, String name, String openDate, String endDate, int duration, String note, int planCount, int currCount, String status) {
        this.id = id;
        this.name = name;
        this.openDate = openDate;
        this.endDate = endDate;
        this.duration = duration;
        this.note = note;
        this.planCount = planCount;
        this.currCount = currCount;
        this.status = status;
    }
}
