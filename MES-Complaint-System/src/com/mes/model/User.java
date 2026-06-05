package com.mes.model;

public class User {
    protected int userID;
    protected String username;
    protected String password; // hashed or blank if not needed in memory
    protected String name;
    protected String email;
    protected String role;

    public User(int userID, String username, String password, String name, String email, String role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userID; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
