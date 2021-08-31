package com.example.classroom.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.ObjectClasses.Submissions;
import com.example.classroom.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;


public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.viewholder> {
    ArrayList<Submissions> allSubmission = new ArrayList<>();
    Context mcontext;
    ClickListener mlistener;

    public SubmissionsAdapter(ArrayList<Submissions> lst, Context context) {
        mcontext = context;
        allSubmission = lst;
    }

    public void OnItemClicked(ClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cv = inflater.inflate(R.layout.submission_item_view, parent, false);
        return new viewholder(cv);
    }


    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.studentName.setText(allSubmission.get(position).studentName);
        holder.submissionTime.setText("Submission Time: " + getDate(allSubmission.get(position).submitted_time));
        holder.marks.setText("Marks: " + allSubmission.get(position).marks_Assigned);
    }

    public String getDate(long timeinmillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return allSubmission.size();
    }

    public void setList(ArrayList<Submissions> updated) {
        allSubmission = updated;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView studentName, marks, submissionTime;
        Button download, assignMarks;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.tv_stdntName);
            marks = itemView.findViewById(R.id.tv_mrks);
            submissionTime = itemView.findViewById(R.id.tv_submsnTime);
            download = itemView.findViewById(R.id.btn_dsol);
            assignMarks = itemView.findViewById(R.id.btn_asgnMarks);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(allSubmission.get(getAdapterPosition()).submission_url));
                    mcontext.startActivity(intent);
                }
            });

            assignMarks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = allSubmission.get(getAdapterPosition()).studentId;
                    mlistener.OnAssignedClick(id);
                }
            });
        }

    }

    public interface ClickListener {
        public void OnAssignedClick(String id);
    }
}
