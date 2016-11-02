package com.example.tcs.bskill.Beans;

public class EmployeeBean {
    String EmployeeName, LeaderBoardId, EmployeeScore, EmployeeImageURL, CourseId;

    public EmployeeBean(String employeeName, String leaderBoardId, String employeeScore, String employeeImageURL, String courseId) {
        EmployeeName = employeeName;
        LeaderBoardId = leaderBoardId;
        EmployeeScore = employeeScore;
        EmployeeImageURL = employeeImageURL;
        CourseId = courseId;
    }

    public EmployeeBean() {
    }

    public String getLeaderBoardId() {
        return LeaderBoardId;
    }

    public void setLeaderBoardId(String leaderBoardId) {
        LeaderBoardId = leaderBoardId;
    }

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String courseId) {
        CourseId = courseId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }


    public String getEmployeeScore() {
        return EmployeeScore;
    }

    public void setEmployeeScore(String employeeScore) {
        EmployeeScore = employeeScore;
    }

    public String getEmployeeImageURL() {
        return EmployeeImageURL;
    }

    public void setEmployeeImageURL(String employeeImageURL) {
        EmployeeImageURL = employeeImageURL;
    }
}
