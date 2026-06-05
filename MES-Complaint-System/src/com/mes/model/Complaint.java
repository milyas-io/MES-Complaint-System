package com.mes.model;

import java.util.Date;

public class Complaint {
    // Internal enum uses underscores; DB uses spaces for status
    public enum Status { PENDING, IN_PROGRESS, RESOLVED, CLOSED }
    public enum Priority { LOW, MEDIUM, HIGH }

    private int complaintId;
    private String title;
    private String description;
    private Date creationDate;
    private Priority priority;
    private Status status;
    private String remarks;
    private Date resolutionDate;
    private int loggedByOperatorId;
    private Integer assignedToEngineerId;
    private String complainantServiceNumber; // optional (UC-4 linkage)

    // Constructor for new complaints
    public Complaint(String title, String description, Priority priority, int operatorId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.loggedByOperatorId = operatorId;
        this.status = Status.PENDING;
        this.creationDate = new Date();
    }

    // Full constructor for DAO mapping
    public Complaint(int id, String title, String desc, Date created, Priority p, Status s, String rem,
                     Date resDate, int opId, Integer engId) {
        this.complaintId = id;
        this.title = title;
        this.description = desc;
        this.creationDate = created;
        this.priority = p;
        this.status = s;
        this.remarks = rem;
        this.resolutionDate = resDate;
        this.loggedByOperatorId = opId;
        this.assignedToEngineerId = engId;
    }

    public int getComplaintId() { return complaintId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getCreationDate() { return creationDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public String getStatusString() { return status.toString().replace("_", " "); }
    public String getPriorityString() { return priority.toString(); }
    public String getRemarks() { return remarks; }
    public Date getResolutionDate() { return resolutionDate; }
    public int getLoggedByOperatorId() { return loggedByOperatorId; }
    public Integer getAssignedToEngineerId() { return assignedToEngineerId; }
    public String getComplainantServiceNumber() { return complainantServiceNumber; }

    public void setComplainantServiceNumber(String sn) { this.complainantServiceNumber = sn; }
}
