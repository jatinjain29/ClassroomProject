package com.example.classroom.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.ObjectClasses.StudentTest;
import com.example.classroom.R;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.viewholder> {
    ArrayList<StudentTest> allStudents = new ArrayList<>();
    clickListener mlistener;

    public StudentListAdapter(ArrayList<StudentTest> allStudents) {
        this.allStudents = allStudents;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_itemview, parent, false);
        return new viewholder(view);
    }

    public void ItemClicked(clickListener listener) {
        mlistener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.studentName.setText(allStudents.get(position).name);
        holder.marks.setText(allStudents.get(position).marks);
    }

    public void setList(ArrayList<StudentTest> updated) {
        allStudents = updated;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allStudents.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView studentName, marks;
        Button assign;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.tv_StudentName);
            marks = itemView.findViewById(R.id.tv_studentMarks);
            assign = itemView.findViewById(R.id.btn_assignTesstmarks);
            assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.assignClicked(allStudents.get(getAdapterPosition()).studId);
                }
            });
        }
    }

    public interface clickListener {
        public void assignClicked(String studentId);
    }
}
