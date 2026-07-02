package com.mudesuraj.studentmanagementapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mudesuraj.studentmanagementapp.models.Student;

import java.util.List;

/**
 * Data Access Object (DAO) for Student entity.
 * Provides database operations for Student records.
 */
@Dao
public interface StudentDao {

    /**
     * Insert a new student into the database.
     */
    @Insert
    void insertStudent(Student student);

    /**
     * Update an existing student record.
     */
    @Update
    void updateStudent(Student student);

    /**
     * Delete a student from the database.
     */
    @Delete
    void deleteStudent(Student student);

    /**
     * Get all students from the database (local only, not API).
     */
    @Query("SELECT * FROM students WHERE isFromApi = 0 ORDER BY id DESC")
    List<Student> getAllLocalStudents();

    /**
     * Get all favorite students.
     */
    @Query("SELECT * FROM students WHERE isFavorite = 1 ORDER BY id DESC")
    List<Student> getFavoriteStudents();

    /**
     * Get a student by ID.
     */
    @Query("SELECT * FROM students WHERE id = :studentId")
    Student getStudentById(int studentId);

    /**
     * Search students by name or email.
     */
    @Query("SELECT * FROM students WHERE isFromApi = 0 AND (name LIKE :query OR email LIKE :query OR rollNumber LIKE :query) ORDER BY id DESC")
    List<Student> searchStudents(String query);

    /**
     * Get total count of local students.
     */
    @Query("SELECT COUNT(*) FROM students WHERE isFromApi = 0")
    int getLocalStudentCount();

    /**
     * Delete all students (for reset).
     */
    @Query("DELETE FROM students")
    void deleteAllStudents();
}

