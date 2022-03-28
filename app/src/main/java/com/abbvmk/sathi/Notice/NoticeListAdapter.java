package com.abbvmk.sathi.Notice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

public class NoticeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_UNSUPPORTED = -1;
    private static final int TYPE_PDF = 1;
    private static final int TYPE_AUDIO = 2;
    private static final int TYPE_IMAGE = 3;
    private static final int TYPE_MESSAGE = 4;
    private final ArrayList<Notice> notices;
    private final Context mContext;

    public NoticeListAdapter(Context mContext, ArrayList<Notice> notices) {
        this.mContext = mContext;
        this.notices = notices;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView title, name, designation, time;
        ShapeableImageView dp;

        public FileViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            ImageView fileTypeImageView = itemView.findViewById(R.id.type);
            title = itemView.findViewById(R.id.title);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            time = itemView.findViewById(R.id.time);
            dp = itemView.findViewById(R.id.dp);
            if (type == TYPE_AUDIO) {
                fileTypeImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_audio));
            }
        }
    }

    static class GeneralViewHolder extends RecyclerView.ViewHolder {
        TextView message, name, designation, time;
        ShapeableImageView dp;
        ImageView image;

        public GeneralViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            time = itemView.findViewById(R.id.time);
            dp = itemView.findViewById(R.id.dp);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PDF || viewType == TYPE_AUDIO) {
            return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice_file, parent, false), viewType);
        } else if (viewType == TYPE_IMAGE) {
            return new GeneralViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice, parent, false));
        }
        return new GeneralViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        Notice notice = notices.get(position);
        if (viewType == TYPE_UNSUPPORTED) {
            holder.itemView.setVisibility(View.GONE);
        } else if (viewType == TYPE_PDF || viewType == TYPE_AUDIO) {
            User user = notice.getUser();
            FileViewHolder fileViewHolder = (FileViewHolder) holder;

            fileViewHolder.name.setText(user.getName());
            fileViewHolder.designation.setText(user.getDesignation());
            fileViewHolder.title.setText(R.string.click_here_to_open);
            fileViewHolder.time.setText(notice.getTime());
            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                loadImage(file, fileViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    loadImage(dpFile, fileViewHolder.dp);
                });
            }
            fileViewHolder.itemView.setOnClickListener(v -> {
                File noticeFile = FilesHelper.getNoticeFile(mContext, notice);
                if (noticeFile == null) {
                    Toast.makeText(mContext, "Downloading please wait...", Toast.LENGTH_SHORT).show();
                    FilesHelper.downloadNotice(mContext, notice, (noticeFile1) -> {
                        if (noticeFile1 != null) {
                            if (viewType == TYPE_PDF) {
                                openPDF(mContext, noticeFile1);
                            } else {
                                openAudio(mContext, noticeFile1);
                            }
                        }
                    });

                } else {
                    if (viewType == TYPE_PDF) {
                        openPDF(mContext, noticeFile);
                    } else {
                        openAudio(mContext, noticeFile);
                    }
                }
            });

        } else if (viewType == TYPE_IMAGE) {
            User user = notice.getUser();
            GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
            generalViewHolder.message.setVisibility(View.GONE);
            generalViewHolder.name.setText(user.getName());
            generalViewHolder.designation.setText(user.getDesignation());
            generalViewHolder.time.setText(notice.getTime());
            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                loadImage(file, generalViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    loadImage(dpFile, generalViewHolder.dp);
                });
            }

            File noticeFile = FilesHelper.getNoticeFile(mContext, notice);
            System.out.println(noticeFile);
            if (noticeFile == null) {
                FilesHelper.downloadNotice(mContext, notice, (noticeFile1) -> {
                    loadImage(noticeFile1, generalViewHolder.image);
                });
            } else {
                loadImage(noticeFile, generalViewHolder.image);
            }

        } else if (viewType == TYPE_MESSAGE) {
            User user = notice.getUser();
            GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
            generalViewHolder.image.setVisibility(View.GONE);
            generalViewHolder.name.setText(user.getName());
            generalViewHolder.designation.setText(user.getDesignation());
            generalViewHolder.message.setText(notice.getMessage());

            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                loadImage(file, generalViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    loadImage(dpFile, generalViewHolder.dp);
                });
            }

        }
    }

    private void openPDF(Context mContext, @NonNull File file) {
        Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

    private void openAudio(Context mContext, @NonNull File file) {
        Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        Notice notice = notices.get(position);
        if (notice.getFilename() != null) {
            String ext = getFileExtension(notice.getFilename());
            if (ext.contains("pdf")) {
                return TYPE_PDF;
            } else if (ext.contains("mp3")) {
                return TYPE_AUDIO;
            } else if (ext.contains("jpg") || ext.contains("png")) {
                return TYPE_IMAGE;
            }
        } else if (notice.getMessage() != null) {
            return TYPE_MESSAGE;
        }
        return TYPE_UNSUPPORTED;

    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    private void loadImage(File file, ImageView dp) {
        if (file != null && mContext != null) {
            Glide
                    .with(mContext)
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(dp);
        }
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}