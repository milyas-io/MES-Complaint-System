package com.mes.model;

public class Admin extends User {
    public Admin(int userId, String username, String password, String name, String email) {
        super(userId, username, password, name, email, "Admin");
    }
}
