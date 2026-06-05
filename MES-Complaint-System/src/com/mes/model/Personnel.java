
package com.mes.model;

public class Personnel {
    private final String serviceNumber;
    private final String rank; // This can stay as 'rank'
    private final String name;
    private final String unit;

    public Personnel(String serviceNumber, String rank, String name, String unit) {
        this.serviceNumber = serviceNumber;
        this.rank = rank;
        this.name = name;
        this.unit = unit;
    }

    public String getServiceNumber() { return serviceNumber; }
    public String getRank() { return rank; } // This remains the same
    public String getName() { return name; }
    public String getUnit() { return unit; }
}