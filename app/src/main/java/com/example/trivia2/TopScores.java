package com.example.trivia2;

public class TopScores {

    private String email;//מזהה השחקן
    private String maxScore;//ניקוד השחקן במשחק
    /**
     * TopScores
     *      פעולה שבונה אשר בונה ניקוד של שחקן
     */
    public TopScores(String email, String maxScore) {
        this.email = email;
        this.maxScore = maxScore;
    }
    /**
     *     פעולות מאחזרות וקובעות לתכונות
     */
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
    /**
     *    toString
     *    פעולה מדפיסה אשר מפרטת פרטי המשחק
     */
    @Override
    public String toString() {
        return "TopScores{" +
                "email='" + email + '\'' +
                ", maxScore='" + maxScore + '\'' +
                '}';
    }
}