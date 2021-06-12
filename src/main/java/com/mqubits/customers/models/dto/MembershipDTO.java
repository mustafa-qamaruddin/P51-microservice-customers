package com.mqubits.customers.models.dto;

public class MembershipDTO {
    private String employer;
    private String employee;
    private String timeline;

    public MembershipDTO() {
    }

    public MembershipDTO(String employer, String employee, String timeline) {
        this.employer = employer;
        this.employee = employee;
        this.timeline = timeline;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }
}
