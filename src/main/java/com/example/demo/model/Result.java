package com.example.demo.model;

import java.util.List;

public class Result {

    private String userName;
    private int score;
    private int total;
    private String status;
    private String attemptTime;
    // NEW FIELDS (for review feature)
    private int correctCount;
    private int incorrectCount;

    private List<QuestionReview> correctList;
    private List<QuestionReview> incorrectList;

    public Result() {}

    public Result(String userName, int score, int total, String status,
                  int correctCount, int incorrectCount,
                  List<QuestionReview> correctList,
                  List<QuestionReview> incorrectList) {
        this.userName = userName;
        this.score = score;
        this.total = total;
        this.status = status;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.correctList = correctList;
        this.incorrectList = incorrectList;
    }

    // getters & setters

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getAttemptTime() { return attemptTime; }
    public void setAttemptTime(String attemptTime) { this.attemptTime = attemptTime; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getIncorrectCount() { return incorrectCount; }
    public void setIncorrectCount(int incorrectCount) { this.incorrectCount = incorrectCount; }

    public List<QuestionReview> getCorrectList() { return correctList; }
    public void setCorrectList(List<QuestionReview> correctList) { this.correctList = correctList; }

    public List<QuestionReview> getIncorrectList() { return incorrectList; }
    public void setIncorrectList(List<QuestionReview> incorrectList) { this.incorrectList = incorrectList; }
}