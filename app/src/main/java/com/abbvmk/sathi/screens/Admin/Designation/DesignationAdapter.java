package com.abbvmk.sathi.screens.Admin.Designation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.R;

import java.util.ArrayList;

public class DesignationAdapter extends RecyclerView.Adapter<DesignationAdapter.DesignationViewHolder> {
    private final ArrayList<String> designations;

    private DesignationClickListener listener;

    public interface DesignationClickListener {
        void onClick(String text);
    }

    public DesignationAdapter(ArrayList<String> designations) {
        this.designations = designations;
    }

    public DesignationAdapter(ArrayList<String> designations, DesignationClickListener listener) {
        this.designations = designations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DesignationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DesignationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_designation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DesignationViewHolder holder, int position) {
        holder.designationTV.setText(designations.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(designations.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return designations.size();
    }

    static class DesignationViewHolder extends RecyclerView.ViewHolder {
        private final TextView designationTV;

        public DesignationViewHolder(@NonNull View itemView) {
            super(itemView);
            designationTV = itemView.findViewById(R.id.designationTV);
        }
    }

}
