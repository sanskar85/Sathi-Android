package com.abbvmk.sathi.Fragments.Notice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
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
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.Helper.GlideHelper;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.google.android.material.imageview.ShapeableImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class NoticeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_UNSUPPORTED = -1;
    private static final int TYPE_PDF = 1;
    private static final int TYPE_AUDIO = 2;
    private static final int TYPE_IMAGE = 3;
    private static final int TYPE_MESSAGE = 4;
    private final ArrayList<Notice> notices;
    private final Context mContext;
    private final PrettyTime prettyTime = new PrettyTime(Locale.ENGLISH);

    public NoticeListAdapter(Context mContext, ArrayList<Notice> notices) {
        this.mContext = mContext;
        this.notices = notices;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView title, name, designation, time;
        ShapeableImageView dp;
        ImageView delete;

        public FileViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            ImageView fileTypeImageView = itemView.findViewById(R.id.type);
            title = itemView.findViewById(R.id.title);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            time = itemView.findViewById(R.id.time);
            dp = itemView.findViewById(R.id.dp);
            delete = itemView.findViewById(R.id.delete);
            if (type == TYPE_AUDIO) {
                fileTypeImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_audio));
            }
        }
    }

    static class GeneralViewHolder extends RecyclerView.ViewHolder {
        TextView message, name, designation, time;
        ShapeableImageView dp;
        ImageView image, delete;

        public GeneralViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            time = itemView.findViewById(R.id.time);
            dp = itemView.findViewById(R.id.dp);
            delete = itemView.findViewById(R.id.delete);
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
        User user = MainApplication.findUser(notice.getUser());
        if (user == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        if (viewType == TYPE_UNSUPPORTED) {
            holder.itemView.setVisibility(View.GONE);
        } else if (viewType == TYPE_PDF || viewType == TYPE_AUDIO) {
            FileViewHolder fileViewHolder = (FileViewHolder) holder;

            fileViewHolder.name.setText(user.getName());
            fileViewHolder.designation.setText(user.getDesignation());
            fileViewHolder.title.setText(R.string.click_here_to_open);
            fileViewHolder.time.setText(prettyTime.format(notice.getTime()));
            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                GlideHelper.loadDPImage(holder.itemView.getContext(), file, fileViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    GlideHelper.loadDPImage(holder.itemView.getContext(), dpFile, fileViewHolder.dp);
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

            setupDeleteBTN(notice, fileViewHolder.delete, position);
        } else if (viewType == TYPE_IMAGE) {
            GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
            generalViewHolder.name.setText(user.getName());
            generalViewHolder.designation.setText(user.getDesignation());
            generalViewHolder.message.setText(notice.getMessage());
            generalViewHolder.time.setText(prettyTime.format(notice.getTime()));
            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                GlideHelper.loadDPImage(holder.itemView.getContext(), file, generalViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    GlideHelper.loadDPImage(holder.itemView.getContext(), dpFile, generalViewHolder.dp);
                });
            }

            File noticeFile = FilesHelper.getNoticeFile(mContext, notice);
            System.out.println(noticeFile);
            if (noticeFile == null) {
                FilesHelper.downloadNotice(mContext, notice, (noticeFile1) -> {
                    GlideHelper.loadNoticeImage(holder.itemView.getContext(), noticeFile1, generalViewHolder.image);
                });
            } else {
                GlideHelper.loadNoticeImage(holder.itemView.getContext(), noticeFile, generalViewHolder.image);
            }

            if (notice.getFile() == null) {
                GlideHelper.loadPostImage(holder.itemView.getContext(), notice.getFile(), generalViewHolder.image);
            }

            setupDeleteBTN(notice, generalViewHolder.delete, position);
        } else if (viewType == TYPE_MESSAGE) {
            GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
            generalViewHolder.time.setText(prettyTime.format(notice.getTime()));
            generalViewHolder.image.setVisibility(View.GONE);
            generalViewHolder.name.setText(user.getName());
            generalViewHolder.designation.setText(user.getDesignation());
            generalViewHolder.message.setText(notice.getMessage());

            File file = FilesHelper.dp(mContext, user.getId());
            if (file != null) {
                GlideHelper.loadDPImage(holder.itemView.getContext(), file, generalViewHolder.dp);
            } else {
                FilesHelper.downloadDP(mContext, user.getId(), (dpFile) -> {
                    GlideHelper.loadDPImage(holder.itemView.getContext(), dpFile, generalViewHolder.dp);
                });
            }

            setupDeleteBTN(notice, generalViewHolder.delete, position);
        }
    }

    private void setupDeleteBTN(Notice notice, ImageView delete, int position) {
        if (notice.canBeDeleted()) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                Toast.makeText(mContext, "Deleting ...", Toast.LENGTH_SHORT).show();
                Firebase
                        .deleteNotice(notice.getId(), success -> {
                            if (success) {
                                Toast.makeText(mContext, "Notice deleted", Toast.LENGTH_SHORT).show();
                                notices.remove(notice);
                                notifyItemRemoved(position);
                            } else {
                                Toast.makeText(mContext, "Unable to delete this post", Toast.LENGTH_SHORT).show();
                            }
                        });

            });
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
        if (notice.getFile() != null && !TextUtils.isEmpty(notice.getFile())) {
            String ext = getFileExtension(notice.getFile());
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

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}