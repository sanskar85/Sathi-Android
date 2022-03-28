package com.abbvmk.sathi.screens.PostViewer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private ArrayList<Comment> list;

    public CommentsAdapter(ArrayList<Comment> comments) {
        this.list = comments;
    }


    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = list.get(position);
        User user = comment.getUser();

        holder.message.setText(comment.getMessage());
        holder.name.setText(user.getName());

        File dp = FilesHelper.dp(holder.dp.getContext(), user.getId());
        if (dp != null) {
            loadImage(dp, holder.dp);
        } else {
            FilesHelper
                    .downloadDP(holder.dp.getContext().getApplicationContext(),
                            user.getId(),
                            ( file) -> loadImage(file, holder.dp));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CommentsViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView dp;
        TextView message, name;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            message = itemView.findViewById(R.id.message);
            name = itemView.findViewById(R.id.name);
        }
    }


    private void loadImage(File file, ImageView iv) {
        if (file != null) {
            Glide
                    .with(iv.getContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(iv);
        }
    }
}
