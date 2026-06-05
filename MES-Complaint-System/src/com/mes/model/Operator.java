package com.mes.model;

public class Operator extends User {
    public Operator(int userId, String username, String password, String name, String email) {
        super(userId, username, password, name, email, "Operator");
    }
}
