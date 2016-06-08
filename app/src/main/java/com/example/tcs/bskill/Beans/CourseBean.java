package com.example.tcs.bskill.Beans;

public class CourseBean {
    String courseID, courseName, courseDesc, courseAudioURL, courseVideoURL;

    public CourseBean() {
    }

    public CourseBean(String courseID, String courseName, String courseDesc, String courseAudioURL, String courseVideoURL) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.courseDesc = courseDesc;
        this.courseAudioURL = courseAudioURL;
        this.courseVideoURL = courseVideoURL;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    public String getCourseAudioURL() {
        return courseAudioURL;
    }

    public void setCourseAudioURL(String courseAudioURL) {
        this.courseAudioURL = courseAudioURL;
    }

    public String getCourseVideoURL() {
        return courseVideoURL;
    }

    public void setCourseVideoURL(String courseVideoURL) {
        this.courseVideoURL = courseVideoURL;
    }
}
