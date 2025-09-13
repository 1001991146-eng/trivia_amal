package com.example.trivia2;

public class TopScores {

    private String email;
    private String maxScore;

    public TopScores(String email, String maxScore) {
        this.email = email;
        this.maxScore = maxScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    @Override
    public String toString() {
        return "TopScores{" +
                "email='" + email + '\'' +
                ", maxScore='" + maxScore + '\'' +
                '}';
    }
}