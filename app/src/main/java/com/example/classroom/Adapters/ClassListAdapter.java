package com.example.classroom.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.ObjectClasses.Classroom;
import com.example.classroom.R;

import java.util.ArrayList;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.viewholder> {

    ArrayList<Classroom> all_classes = new ArrayList<>();
    ClickListener minterface;

    public ClassListAdapter(ArrayList<Classroom> classes) {
        all_classes = classes;
    }

    public void OnitemClicked(ClickListener listener){
        minterface=listener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.class_item_view, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Classroom current_class = all_classes.get(position);
        holder.subject_name.setText(current_class.subject);
        holder.teacher_name.setText(current_class.teacher_name);
        holder.class_name.setText(current_class.name);
    }


    public void setList(ArrayList<Classroom> updated_list) {
        all_classes = updated_list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return all_classes.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView class_name, subject_name, teacher_name;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            class_name = itemView.findViewById(R.id.tv_class);
            teacher_name = itemView.findViewById(R.id.tv_teacher);
            subject_name = itemView.findViewById(R.id.tv_subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    minterface.itemClick(all_classes.get(getAdapterPosition()));
                }
            });
        }

    }

    public interface ClickListener {
        public void itemClick(Classroom current_class);
    }
}
