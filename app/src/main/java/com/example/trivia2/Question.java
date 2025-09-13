package com.example.trivia2;

public class Question {

    private String question;// שאלה
    private String possibleAnswer1, possibleAnswer2, possibleAnswer3, possibleAnswer4;//תשובות אפשריות
    private String correctAnswer;//התשובה הנכונה
    /**
     * Question
     *      פעולה שבונה אשר בונה שאלה חדשה
     */
    public Question(String question, String possibleAnswer1, String possibleAnswer2, String possibleAnswer3, String possibleAnswer4, String correctAnswer) {
        this.question = question;
        this.possibleAnswer1 = possibleAnswer1;
        this.possibleAnswer2 = possibleAnswer2;
        this.possibleAnswer3 = possibleAnswer3;
        this.possibleAnswer4 = possibleAnswer4;
        this.correctAnswer = correctAnswer;
    }
    /**
     *     פעולות מאחזרות וקובעות לתכונות השאלה
     */
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getPossibleAnswer1() {
        return possibleAnswer1;
    }
    public void setPossibleAnswer1(String possibleAnswer1) {
        this.possibleAnswer1 = possibleAnswer1;
    }
    public String getPossibleAnswer2() {
        return possibleAnswer2;
    }
    public void setPossibleAnswer2(String possibleAnswer2) {
        this.possibleAnswer2 = possibleAnswer2;
    }
    public String getPossibleAnswer3() {
        return possibleAnswer3;
    }
    public void setPossibleAnswer3(String possibleAnswer3) {
        this.possibleAnswer3 = possibleAnswer3;
    }
    public String getPossibleAnswer4() {
        return possibleAnswer4;
    }
    public void setPossibleAnswer4(String possibleAnswer4) {
        this.possibleAnswer4 = possibleAnswer4;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    /**
     *    toString
     *    פעולה מדפיסה אשר מפרטת פרטי השאלה
     */
    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", possibleAnswer1='" + possibleAnswer1 + '\'' +
                ", possibleAnswer2='" + possibleAnswer2 + '\'' +
                ", possibleAnswer3='" + possibleAnswer3 + '\'' +
                ", possibleAnswer4='" + possibleAnswer4 + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}

