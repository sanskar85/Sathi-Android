package com.abbvmk.sathi.screens.CreatePost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Posts.Post;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class CreatePost extends AppCompatActivity implements ProgressButton.OnClickListener {

    private ProgressButton submit;
    private ImageView image;
    private EditText caption;
    private Post post;
    private Uri uploadURI;
    private FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_create_post);
        post = new Post();

        mStorage = FirebaseStorage.getInstance();

        init();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                new Thread(() -> {
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (imageUri != null) {
                        String fileName = FilesHelper.getFileName(this, imageUri);
                        File tempFIle = FilesHelper.copyToTempFile(this, imageUri, fileName);
                        if (tempFIle != null) {
                            runOnUiThread(() -> {
                                loadImage(tempFIle);
                                uploadURI = Uri.fromFile(tempFIle);
                            });
                        }
                    }
                }).start();
            }
        }

    }

    private void init() {
        submit = findViewById(R.id.upload_btn);
        image = findViewById(R.id.image);
        caption = findViewById(R.id.caption);
        image.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(200)
                    .start();
        });
        findViewById(R.id.selector).setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(200)
                    .start();
        });
        submit.setOnClickListener(this);
    }


    @Override
    public void onClick() {
        if (!submit.isViewEnabled()) return;
        String text = caption.getText().toString();
        if (uploadURI == null && TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Please upload an image or write a caption", Toast.LENGTH_SHORT).show();
            return;
        }
        submit.setViewEnabled(false);
        submit.buttonActivated();
        post.setCaption(text);

        if (uploadURI != null) {
            String path = Calendar.getInstance().getTimeInMillis() + ".jpg";
            StorageReference ref = mStorage.getReference("post").child(path);

            UploadTask uploadTask = ref.putFile(uploadURI);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    if (task.getException() != null) {
                        throw task.getException();
                    } else {
                        throw new Exception("File upload failed");
                    }
                }
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    post.setPhoto(downloadUri.toString());
                    uploadPost();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to upload your photo.", Toast.LENGTH_SHORT).show();
                    submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                    submit.buttonFinished("Failure");

                    new Handler().postDelayed(() -> {
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                        submit.buttonFinished("Upload");
                        submit.setViewEnabled(true);
                    }, 3000);
                }
            });


        } else {
            uploadPost();
        }
    }

    private void uploadPost() {
        Firebase
                .createPost(post, success -> {
                    if (success) {
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.success));
                        submit.buttonFinished("Uploaded");
                        new Handler().postDelayed(this::finish, 2000);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to save your profile", Toast.LENGTH_SHORT).show();
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                        submit.buttonFinished("Failure");
                        new Handler().postDelayed(() -> {
                            submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            submit.buttonFinished("Upload");
                            submit.setViewEnabled(true);
                        }, 3000);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                loadImage(new File(uri.getPath()));
                uploadURI = uri;
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(File file) {
        if (file != null) {
            Glide
                    .with(this.getApplicationContext())
                    .load(file)
                    .centerCrop()
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(image);
        }
    }

}