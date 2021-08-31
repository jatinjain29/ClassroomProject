package com.example.classroom.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.ObjectClasses.Test;
import com.example.classroom.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TestTvAdapter extends RecyclerView.Adapter<TestTvAdapter.viewholder> {

    ArrayList<Test> allTests = new ArrayList<>();
    boolean isTeacher = false;
    Context mcontext;
    ClickListener mlistener;

    public TestTvAdapter(Context context, boolean isTeacher, ArrayList<Test> clist) {
        mcontext = context;
        this.isTeacher = isTeacher;
        allTests = clist;
    }

    public void OnItemClicked(ClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.test_item_view, parent, false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        if (!isTeacher) {
            holder.teacherview.setVisibility(View.GONE);
        } else {
            holder.teacherview.setVisibility(View.VISIBLE);
        }
        if (!isTeacher) {
            holder.marks.setVisibility(View.VISIBLE);
            holder.marks.setText("Marks: "+allTests.get(position).umarks + "");
        }

        Test ctest = allTests.get(position);
        holder.instructions.setText(ctest.instructions);
        holder.name.setText(ctest.name);
        holder.date.setText("Date: " + getDate(ctest.dueTime));
    }

    @Override
    public int getItemCount() {
        return allTests.size();
    }

    public String getDate(long timeinmillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeinmillis);
        return formatter.format(calendar.getTime());

    }

    public void setList(ArrayList<Test> updated) {
        allTests = updated;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView name, date, instructions, marks;
        Button submission, referenceMaterial;
        ImageButton dropdown;
        LinearLayout drpview, teacherview;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_TestName);
            date = itemView.findViewById(R.id.tv_testDate);
            marks = itemView.findViewById(R.id.userMarks);
            instructions = itemView.findViewById(R.id.tv_instruction);
            submission = itemView.findViewById(R.id.btn_check);
            referenceMaterial = itemView.findViewById(R.id.bn_dwnld);
            dropdown = itemView.findViewById(R.id.opnDropdown);
            drpview = itemView.findViewById(R.id.ll_expanded);
            teacherview = itemView.findViewById(R.id.ll_tchr_test);

            dropdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (drpview.getVisibility() == View.VISIBLE) {
                        drpview.setVisibility(View.GONE);
                        dropdown.setImageResource(R.mipmap.dropdown);
                    } else {
                        drpview.setVisibility(View.VISIBLE);
                        dropdown.setImageResource(R.mipmap.arrowup);
                    }
                }
            });
            referenceMaterial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = allTests.get(getAdapterPosition()).refereneMaterial;
                    if (url == null || url.isEmpty()) {
                        Toast.makeText(mcontext, "No material attached", Toast.LENGTH_SHORT).show();
                        // referenceMaterial.setClickable(false);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setData(Uri.parse(url));
                    mcontext.startActivity(intent);
                }
            });
            submission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.SubmissionClicked(allTests.get(getAdapterPosition()));
                }
            });

        }
    }

    public interface ClickListener {
        public void SubmissionClicked(Test test);
    }
}
