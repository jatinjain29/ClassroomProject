package com.example.classroom.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.ObjectClasses.Assingment;
import com.example.classroom.ObjectClasses.UserAssingment;
import com.example.classroom.R;

import java.util.ArrayList;

public class TeacherAssingmentAdapter extends RecyclerView.Adapter<TeacherAssingmentAdapter.viewholder> {
    ArrayList<Assingment> all_Assingment = new ArrayList<>();
    ClickListener mlistener;

    public TeacherAssingmentAdapter(ArrayList<Assingment> asngmnt) {
        all_Assingment = asngmnt;
    }

    public void ItemClicked(ClickListener listener) {
        mlistener = listener;
    }

    public void setList(ArrayList<Assingment> updatedlist) {
        all_Assingment = updatedlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_asngmnt_item_view, parent, false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.asngmt_name.setText(all_Assingment.get(position).assingment_name);
    }

    @Override
    public int getItemCount() {
        return all_Assingment.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView asngmt_name;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            asngmt_name = itemView.findViewById(R.id.tv_asng_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.onItemClicked(all_Assingment.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ClickListener {
        public void onItemClicked(Assingment asngmnt);
    }
}
