package com.example.trivia2;

public class UserProperties {

    private String firstName;//שם פרטי
    private String lastName;//שם משפחה
    private String email;// דוא"ל
    private String phone;// מספר טלפון
    private String password;//סיסמה
    /**
     * UserProperties
     *      פעולה שבונה אשר בונה  שחקן
     */
    public UserProperties(String firstName, String lastName, String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
    /**
     *     פעולות מאחזרות וקובעות לתכונות
     */
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     *    toString
     *    פעולה מדפיסה אשר מפרטת פרטי השחקן
     */
    @Override
    public String toString() {
        return "UserProperties{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

