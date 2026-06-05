package com.mes.model;

import java.util.Date;

public class Report {
    private String reportType;
    private Date startDate;
    private Date endDate;

    public Report(String type, Date start, Date end) {
        this.reportType = type;
        this.startDate = start;
        this.endDate = end;
    }

    public String generate(int total, int resolved) {
        double efficiency = (total > 0) ? (((double) resolved / total) * 100.0) : 0.0;
        return String.format(
            " --- MES SERVICE PERFORMANCE SUMMARY --- \n" +
            "Report Focus: %s\n" +
            "Review Period: %s to %s\n" +
            "Total Requests Received: %d\n" +
            "Total Tasks Resolved: %d\n" +
            "Team Efficiency: %.2f%% \n",
            reportType, startDate, endDate, total, resolved, efficiency
        );
    }
}
