package com.example.classroom.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.viewholder> {
    ArrayList<String> allMesages = new ArrayList();

    public messageAdapter(ArrayList<String> allMesages) {
        this.allMesages = allMesages;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageitemview, parent, false);


        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.mesg.setText("> "+allMesages.get(allMesages.size()-position-1).toString());
    }

    @Override
    public int getItemCount() {
        return allMesages.size();
    }

    public void setList(ArrayList<String> updated) {
        allMesages = updated;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView mesg;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            mesg = itemView.findViewById(R.id.tvmsgDisplay);
        }
    }
}
