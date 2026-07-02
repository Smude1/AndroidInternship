package com.mudesuraj.studentmanagementapp.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Student model class to represent a student in the system.
 * Can be from local database or API.
 */
@Entity(tableName = "students")
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String rollNumber;
    private String email;
    private String course;
    private boolean isFavorite;
    private boolean isFromApi; // to distinguish between local and API students

    // Constructors
    public Student() {
        this.isFavorite = false;
        this.isFromApi = false;
    }

    @Ignore
    public Student(String name, String rollNumber, String email, String course) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.course = course;
        this.isFavorite = false;
        this.isFromApi = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFromApi() {
        return isFromApi;
    }

    public void setFromApi(boolean fromApi) {
        isFromApi = fromApi;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", isFavorite=" + isFavorite +
                ", isFromApi=" + isFromApi +
                '}';
    }
}

