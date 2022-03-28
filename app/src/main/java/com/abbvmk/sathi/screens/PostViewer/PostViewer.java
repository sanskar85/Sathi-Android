package com.abbvmk.sathi.screens.PostViewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Posts.Post;
import com.abbvmk.sathi.Fragments.Profile.Profile;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.Views.Loading.Loading;
import com.abbvmk.sathi.screens.ProfileViewer.ProfileViewer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostViewer extends AppCompatActivity {

    private Post mPost;
    private ArrayList<Comment> comments;
    private Context mContext;
    private CommentsAdapter commentsAdapter;
    private EditText commentBox;
    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_post_viewer);
        mPost = (Post) getIntent().getSerializableExtra("post");
        String postID = getIntent().getStringExtra("postID");


        if (mPost == null && postID == null) finish();
        loading = findViewById(R.id.loading);
        mContext = this;
        if (mPost != null) {
            resolveData();
        } else {
            fetchPost(postID);
        }


    }

    private void fetchPost(String postID) {

        loading.setProgressVisible(true);
        new Thread(() -> {
            API
                    .instance()
                    .fetchPost(postID)
                    .enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                            if (response.code() == 200 && response.body() != null) {
                                mPost = response.body();
                                resolveData();
                            } else {
                                Toast.makeText(mContext, "Unable to load this post", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                            Toast.makeText(mContext, "Unable to load this post", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }).start();
    }

    private void resolveData() {
        loading.setProgressVisible(false);
        User user = mPost.getUser();

        ImageView dp, image;
        TextView name, designation, time, caption;

        commentBox = findViewById(R.id.commentET);
        dp = findViewById(R.id.dp);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        designation = findViewById(R.id.designation);
        time = findViewById(R.id.time);
        caption = findViewById(R.id.caption);

        name.setText(user.getName());
        designation.setText(user.getDesignation());
        time.setText(mPost.getTime());
        caption.setText(mPost.getCaption());

        dp.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileViewer.class);
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        name.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileViewer.class);
            intent.putExtra("user", user);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        File dpFile = FilesHelper.dp(mContext, user.getId());
        if (dpFile != null) {
            loadImage(dpFile, dp);
        } else {
            FilesHelper.downloadDP(mContext, user.getId(), (file) -> loadImage(file, dp));
        }
        if (mPost.getFilename() != null) {

            File postImage = FilesHelper.post(mContext, mPost);
            if (postImage != null) {
                loadImage(postImage, image);
            } else {
                FilesHelper.downloadPost(mContext, mPost, (file) -> loadImage(file, image));
            }
        }

        findViewById(R.id.share).setOnClickListener(v -> {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.abbvmk-sathi.com?post=" + mPost.getId()))
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

                            File shareFile = FilesHelper.post(mContext, mPost);
                            if (shareFile != null) {
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


        RecyclerView commentRecycler = findViewById(R.id.commentRecycler);
        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(comments);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        commentRecycler.setHasFixedSize(false);
        commentRecycler.setAdapter(commentsAdapter);

        setupCommentBox();
        fetchComments();
    }

    private void setupCommentBox() {

        commentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.length() > 0) {
                    findViewById(R.id.share).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.share).setVisibility(View.VISIBLE);
                }
            }
        });


        commentBox.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String comment = commentBox.getText().toString();
                if (TextUtils.isEmpty(comment)) return false;
                Toast.makeText(textView.getContext(), "Sending...", Toast.LENGTH_SHORT).show();
                sendComment(comment);
                commentBox.setText("");
                return true;
            }
            return false;
        });

    }

    private void sendComment(String comment) {
        new Thread(() -> {
            API
                    .instance()
                    .createComment(mPost.getId(), comment)
                    .enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                            if (response.code() == 200 && response.body() != null) {
                                comments.add(response.body());
                                commentsAdapter.notifyItemInserted(comments.size() - 1);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                        }
                    });

        }).start();

    }

    private void fetchComments() {
        Context context = this;
        new Thread(() -> {
            API
                    .instance()
                    .fetchComments(mPost.getId())
                    .enqueue(new Callback<ArrayList<Comment>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Comment>> call, @NonNull Response<ArrayList<Comment>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                comments.clear();
                                comments.addAll(response.body());
                                commentsAdapter.notifyDataSetChanged();
                            } else {

                                Toast.makeText(context, "Error fetching comments", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Comment>> call, @NonNull Throwable t) {
                            Toast.makeText(context, "Error fetching comments", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }

    private void loadImage(File file, ImageView iv) {
        if (file != null) {
            Glide
                    .with(mContext.getApplicationContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(iv);
        }
    }

}