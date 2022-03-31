package com.abbvmk.sathi.Fragments.Posts;

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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.Helper.GlideHelper;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context mContext;
    private final ArrayList<Post> posts;
    private final PostCardInterface postCardInterface;
    private final PrettyTime prettyTime = new PrettyTime(Locale.ENGLISH);

    public interface PostCardInterface {
        void profileHeaderClicker(User user);

        void openComments(Post post);
    }

    public PostAdapter(Context mContext, ArrayList<Post> posts, boolean committeePostsOnly, User user, PostCardInterface postCardInterface) {
        this.mContext = mContext;
        this.postCardInterface = postCardInterface;
        if (committeePostsOnly) {
            ArrayList<Post> _posts = new ArrayList<>(posts);
            for (Iterator<Post> it = _posts.iterator(); it.hasNext(); ) {
                User _user = MainApplication.findUser(it.next().getUser());
                if (_user == null || !_user.isAdmin()) {
                    it.remove();
                }
            }
            this.posts = _posts;
        } else if (user != null) {
            ArrayList<Post> _posts = new ArrayList<>(posts);
            for (Iterator<Post> it = _posts.iterator(); it.hasNext(); ) {
                if (!it.next().getUser().equals(user.getId())) {
                    it.remove();
                }
            }
            this.posts = _posts;
        } else {
            this.posts = posts;
        }


    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView dp, image, delete;
        TextView name, designation, time, caption;
        MaterialCardView share, comment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
            name = itemView.findViewById(R.id.name);
            designation = itemView.findViewById(R.id.designation);
            time = itemView.findViewById(R.id.time);
            caption = itemView.findViewById(R.id.caption);
            share = itemView.findViewById(R.id.share);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        User user = MainApplication.findUser(post.getUser());
        if (user == null) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        holder.name.setText(user.getName());
        holder.designation.setText(user.getDesignation());
        holder.time.setText(prettyTime.format(post.getTime()));
        holder.caption.setText(post.getCaption());

        holder.dp.setOnClickListener(v -> {
            if (postCardInterface != null) {
                postCardInterface.profileHeaderClicker(user);
            }
        });
        holder.name.setOnClickListener(v -> {
            if (postCardInterface != null) {
                postCardInterface.profileHeaderClicker(user);
            }
        });

        File dp = FilesHelper.dp(mContext, user.getId());
        if (dp != null) {
            GlideHelper.loadDPImage(holder.itemView.getContext(), dp, holder.dp);
        } else {
            FilesHelper.downloadDP(mContext, user.getId(), (file) -> {
                GlideHelper.loadDPImage(holder.itemView.getContext(), dp, holder.dp);
            });
        }
        if (post.getPhoto() != null) {
            GlideHelper.loadPostImage(holder.itemView.getContext(), post.getPhoto(), holder.image);
        }

        if (post.canBeDeleted()) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setTag(position);
            holder.delete.setOnClickListener(v -> {
                Toast.makeText(mContext, "Deleting ...", Toast.LENGTH_SHORT).show();
                Firebase
                        .deletePost(post.getId(), success -> {
                            if (success) {
                                Toast.makeText(mContext, "Post deleted", Toast.LENGTH_SHORT).show();
                                posts.remove(post);
                                notifyItemRemoved((Integer) v.getTag());
                            } else {
                                Toast.makeText(mContext, "Unable to delete this post", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }


        holder.share.setOnClickListener(v -> {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.abbvmk-sathi.com?post=" + post.getId()))
                    .setDomainUriPrefix("https://sathiabbvmk.page.link")
                    .buildShortDynamicLink()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Uri shortLink = task.getResult().getShortLink();
                            String whatsAppMessage = "अखिल भारतीय बंगी वैश्य महासभा, खगड़िया इकाई  का app आ गया है । \nऐसे और पोस्ट देखने के लिए और अखिल भारतीय बंगी वैश्य महासभा से जुड़ने के लिए क्लिक करें \uD83D\uDC47\uD83D\uDC47 \n\n\n";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage + shortLink);
                            intent.setType("text/plain");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            if (post.getPhoto() != null) {
                                File shareFile = FilesHelper.drawableToFile(mContext, holder.image.getDrawable());
                                Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", shareFile);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.setType("image/jpg");
                            }

                            try {
                                mContext.startActivity(intent);
                            } catch (Exception e) {

                                Toast.makeText(v.getContext(), "No social app installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Cannot generate sharing link.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        holder.comment.setOnClickListener(v -> {
            if (postCardInterface != null) {
                postCardInterface.openComments(post);
            }
        });
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

}
