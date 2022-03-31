package com.abbvmk.sathi.screens.Admin.Designation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;

public class AssignDesignationAdapter extends RecyclerView.Adapter<AssignDesignationAdapter.AssignDesignationViewHolder> {
    private final ArrayList<PendingDesignationClass> list;

    private final OnApprovalClicked listener;

    public interface OnApprovalClicked {
        void reject(PendingDesignationClass object);

        void success(PendingDesignationClass object);
    }

    public AssignDesignationAdapter(ArrayList<PendingDesignationClass> list, OnApprovalClicked listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignDesignationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignDesignationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_assign_designation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignDesignationViewHolder holder, int position) {
        PendingDesignationClass obj = list.get(position);
        User requestedFor = MainApplication.findUser(obj.getRequestedFor());
        User requestedBy = MainApplication.findUser(obj.getRequestedBy());
        if (requestedFor == null || requestedBy == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        holder.name.setText(requestedFor.getName());
        holder.designation.setText(String.format("%s → %s", requestedFor.getDesignation(), obj.getDesignation()));
        holder.requestedBy.setText(String.format("Requested By : %s", requestedBy.getName()));


        File file = FilesHelper
                .dp(holder.itemView.getContext().getApplicationContext(),
                        requestedFor.getId());
        if (file != null) {
            loadImage(file, holder.dp);
        } else {
            FilesHelper
                    .downloadDP(holder.itemView.getContext().getApplicationContext(),
                            requestedFor.getId(),
                            (dpFile) -> loadImage(dpFile, holder.dp));
        }

        holder.success.setOnClickListener(v -> {
            if (listener != null) {
                holder.itemView.setVisibility(View.GONE);
                listener.success(obj);
            }
        });

        holder.reject.setOnClickListener(v -> {
            if (listener != null) {
                holder.itemView.setVisibility(View.GONE);
                listener.reject(obj);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AssignDesignationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView success, reject;
        TextView name, designation, requestedBy;
        ShapeableImageView dp;

        public AssignDesignationViewHolder(@NonNull View itemView) {
            super(itemView);
            success = itemView.findViewById(R.id.success);
            reject = itemView.findViewById(R.id.reject);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            requestedBy = itemView.findViewById(R.id.requested_by);
            dp = itemView.findViewById(R.id.dp);
        }
    }

    private void loadImage(File file, ShapeableImageView dp) {
        if (file != null) {
            Glide
                    .with(dp.getContext().getApplicationContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(dp);
        }
    }
}
