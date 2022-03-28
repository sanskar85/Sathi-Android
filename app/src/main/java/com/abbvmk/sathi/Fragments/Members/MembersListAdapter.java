package com.abbvmk.sathi.Fragments.Members;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.MembersListViewHolder> {

    private final ArrayList<User> users;
    private final Context mContext;
    private final MemberCardInterface memberCardInterface;

    public interface MemberCardInterface {
        void MemberCardClicked(User user);
    }

    public MembersListAdapter(Context context, ArrayList<User> users, MemberCardInterface memberCardInterface) {
        this.mContext = context;
        this.users = users;
        this.memberCardInterface = memberCardInterface;
    }


    static class MembersListViewHolder extends RecyclerView.ViewHolder {
        protected final ShapeableImageView dp;
        protected final TextView name, designation, id_number;

        public MembersListViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            id_number = itemView.findViewById(R.id.id_number);


        }
    }


    @NonNull
    @Override
    public MembersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersListViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_member, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MembersListAdapter.MembersListViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.designation.setText(user.getDesignation());
        holder.id_number.setText(String.format("ID: %s", user.getMemberId()));

        File file = FilesHelper.dp(mContext, user.getId());
        if (file != null) {
            loadImage(file, holder.dp);
        } else {
            FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                loadImage(dpFile, holder.dp);
            });
        }
        holder.itemView.setOnClickListener(v -> {
            if (memberCardInterface != null) {
                memberCardInterface.MemberCardClicked(user);
            }
        });
    }

    private void loadImage(File file, ShapeableImageView dp) {
        if (file != null) {
            Glide
                    .with(mContext.getApplicationContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(dp);
        }
    }


    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }
}
