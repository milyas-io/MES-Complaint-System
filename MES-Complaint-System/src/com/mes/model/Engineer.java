package com.mes.model;

public class Engineer extends User {
    public enum Status { AVAILABLE, BUSY }
    private Status currentStatus;

    public Engineer(int userId, String username, String password, String name, String email, Status status) {
        super(userId, username, password, name, email, "Engineer");
        this.currentStatus = status;
    }

    public Status getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(Status status) { this.currentStatus = status; }
}
