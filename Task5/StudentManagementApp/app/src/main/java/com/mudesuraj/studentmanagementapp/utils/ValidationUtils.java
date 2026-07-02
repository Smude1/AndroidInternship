package com.mudesuraj.studentmanagementapp.utils;

import android.util.Patterns;

/**
 * Utility class for input validation.
 * Provides methods to validate email, password, and other user inputs.
 */
public class ValidationUtils {

    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Check if email is valid using Android's Patterns.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check if password meets minimum length requirement.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Check if a string is not empty.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validate student name.
     */
    public static boolean isValidStudentName(String name) {
        return isNotEmpty(name) && name.length() >= 2;
    }

    /**
     * Validate email format.
     */
    public static boolean isValidStudentEmail(String email) {
        return isValidEmail(email);
    }

    /**
     * Validate roll number (not empty).
     */
    public static boolean isValidRollNumber(String rollNumber) {
        return isNotEmpty(rollNumber);
    }

    /**
     * Validate course name.
     */
    public static boolean isValidCourse(String course) {
        return isNotEmpty(course) && course.length() >= 2;
    }

    /**
     * Get error message for invalid email.
     */
    public static String getEmailErrorMessage() {
        return "Please enter a valid email address";
    }

    /**
     * Get error message for invalid password.
     */
    public static String getPasswordErrorMessage() {
        return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
    }

    /**
     * Get error message for empty field.
     */
    public static String getEmptyFieldMessage(String fieldName) {
        return fieldName + " cannot be empty";
    }
}

