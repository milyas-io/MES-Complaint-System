package com.mes.model;

import java.util.Date;

public class Feedback {
    private int feedbackId;
    private int complaintId;
    private int rating;
    private String comments;
    private Date feedbackDate;

    public Feedback(int complaintId, int rating, String comments) {
        this.complaintId = complaintId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = new Date();
    }
    public int getComplaintId() { return complaintId; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
}
