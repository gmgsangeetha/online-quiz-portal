package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "results")
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    private int score;
    private int total;
    private String status;
    
    @Column(name = "attempt_time")
    private String attemptTime;

    public ResultEntity() {}

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(String attemptTime) {
        this.attemptTime = attemptTime;
    }
}