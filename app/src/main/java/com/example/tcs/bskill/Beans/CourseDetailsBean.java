package com.example.tcs.bskill.Beans;

public class CourseDetailsBean {
    String courseID, audioStatus, videoStatus, quizStatus, courseProgress;

    public CourseDetailsBean() {
    }

    public CourseDetailsBean(String courseID, String audioStatus, String videoStatus, String quizStatus, String courseProgress) {
        this.courseID = courseID;
        this.audioStatus = audioStatus;
        this.videoStatus = videoStatus;
        this.quizStatus = quizStatus;
        this.courseProgress = courseProgress;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getAudioStatus() {
        return audioStatus;
    }

    public void setAudioStatus(String audioStatus) {
        this.audioStatus = audioStatus;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getQuizStatus() {
        return quizStatus;
    }

    public void setQuizStatus(String quizStatus) {
        this.quizStatus = quizStatus;
    }

    public String getCourseProgress() {
        return courseProgress;
    }

    public void setCourseProgress(String courseProgress) {
        this.courseProgress = courseProgress;
    }
}
