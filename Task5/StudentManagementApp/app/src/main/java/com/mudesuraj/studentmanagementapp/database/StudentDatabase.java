package com.mudesuraj.studentmanagementapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mudesuraj.studentmanagementapp.models.Student;

/**
 * Room Database for local persistence.
 * Manages the SQLite database for student records.
 * Singleton pattern to ensure only one database instance.
 */
@Database(entities = {Student.class}, version = 1, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    private static StudentDatabase instance;
    private static final String DATABASE_NAME = "student_management_db";

    /**
     * Get the StudentDao for database operations.
     */
    public abstract StudentDao studentDao();

    /**
     * Get singleton instance of the database.
     */
    public static synchronized StudentDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    StudentDatabase.class,
                    DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // This helps during development
            .build();
        }
        return instance;
    }
}

