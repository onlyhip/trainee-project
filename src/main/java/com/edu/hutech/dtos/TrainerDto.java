package com.edu.hutech.dtos;

import com.edu.hutech.entities.Role;

public class TrainerDto {

    public int id;


    public String account;

    public String national;

    public String name;

    public String email;

    public String telNumber;

    public String facebook;

    public TrainerDto(int id, String account, String national, String name, String email, String telNumber, String facebook) {
        this.id = id;
        this.account = account;
        this.national = national;
        this.name = name;
        this.email = email;
        this.telNumber = telNumber;
        this.facebook = facebook;
    }
}
