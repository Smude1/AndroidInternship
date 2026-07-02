package com.mudesuraj.studentmanagementapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mudesuraj.studentmanagementapp.R;
import com.mudesuraj.studentmanagementapp.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface StudentActionListener {
        void onView(Student student);

        void onEdit(Student student);

        void onDelete(Student student);

        void onFavoriteToggle(Student student);
    }

    private final List<Student> students = new ArrayList<>();
    private final StudentActionListener listener;

    public StudentAdapter(StudentActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Student> updatedStudents) {
        students.clear();
        students.addAll(updatedStudents);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student, listener);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvCourse;
        private final TextView tvType;
        private final ImageButton btnFavorite;
        private final MaterialButton btnDetails;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvCourse = itemView.findViewById(R.id.tvStudentCourse);
            tvType = itemView.findViewById(R.id.tvStudentType);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Student student, StudentActionListener listener) {
            tvName.setText(student.getName());
            tvEmail.setText(student.getEmail());

            if (student.getCourse() == null || student.getCourse().trim().isEmpty()) {
                tvCourse.setVisibility(View.GONE);
            } else {
                tvCourse.setVisibility(View.VISIBLE);
                tvCourse.setText(student.getCourse());
            }

            if (student.isFromApi()) {
                tvType.setText(R.string.api_users);
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnFavorite.setVisibility(View.GONE);
            } else {
                tvType.setText(R.string.local_students);
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnFavorite.setVisibility(View.VISIBLE);
            }

            btnFavorite.setImageResource(student.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            btnDetails.setOnClickListener(v -> listener.onView(student));
            btnEdit.setOnClickListener(v -> listener.onEdit(student));
            btnDelete.setOnClickListener(v -> listener.onDelete(student));
            btnFavorite.setOnClickListener(v -> listener.onFavoriteToggle(student));
        }
    }
}

