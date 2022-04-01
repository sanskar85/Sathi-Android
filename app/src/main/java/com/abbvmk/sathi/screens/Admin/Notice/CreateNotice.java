package com.abbvmk.sathi.screens.Admin.Notice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.abbvmk.sathi.Fragments.Notice.Notice;
import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.Helper.NotificationSender;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNotice extends AppCompatActivity implements ProgressButton.OnClickListener {

    private ProgressButton save;
    private File uploadFile;
    private EditText caption;
    private FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_create_notice);

        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.selector).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            // Update with mime types
            intent.setType("*/*");

            // Update with additional mime types here using a String[].
            String[] mimeTypes = new String[]{"application/pdf", "audio/*", "image/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            // Only pick openable and local files. Theoretically we could pull files from google drive
            // or other applications that have networked files, but that's unnecessary for this example.
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            someActivityResultLauncher.launch(intent);
        });
        caption = findViewById(R.id.caption);
        save = findViewById(R.id.upload_btn);
        save.setText("Publish");
        save.setOnClickListener(this);
    }


    @Override
    public void onClick() {
        String text = caption.getText().toString();
        if (uploadFile == null && TextUtils.isEmpty(text)) return;

        if (!save.isViewEnabled()) return;

        save.setViewEnabled(false);
        save.buttonActivated();
        if (uploadFile != null) {
            new Thread(() -> {
                Uri uploadPath = Uri.fromFile(uploadFile);
                String path = Calendar.getInstance().getTimeInMillis() + "." + FilesHelper.getExtension(uploadFile);
                StorageReference ref = mStorage.getReference("notices").child(path);

                UploadTask uploadTask = ref.putFile(uploadPath);

                uploadTask
                        .addOnFailureListener(exception -> {
                            Toast.makeText(getApplicationContext(), "Unable to upload your photo.", Toast.LENGTH_SHORT).show();
                            save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                            save.buttonFinished("Failed");
                            new Handler().postDelayed(() -> {
                                save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                                save.buttonFinished("Publish");
                                save.setViewEnabled(true);
                            }, 2000);
                        })
                        .addOnSuccessListener(taskSnapshot -> uploadPost(text, path));
            }).start();
        } else {
            uploadPost(text, "");
        }
    }

    private void uploadPost(String text, String path) {
        Notice notice = new Notice();
        notice.setFile(path);
        notice.setMessage(text);

        Firebase
                .createNotice(notice, success -> {
                    if (success) {
                        if (AuthHelper.getLoggedUser() != null) {
                            String title = AuthHelper.getLoggedUser().getName() + " ने एक नोटिस प्रकाशित किया है|";
                            String message = AuthHelper.getLoggedUser().getName() + " ने एक नोटिस प्रकाशित किया है| देखने के लिए यहां क्लिक करें \uD83D\uDC48";
                            Map<String, Object> notification = new HashMap<>();
                            Map<String, Object> notificationBody = new HashMap<>();

                            notificationBody.put("title", title);
                            notificationBody.put("message", message);
                            notification.put("to", "/topics/notice");
                            notification.put("data", notificationBody);
                            NotificationSender
                                    .getClient()
                                    .sendNotification(notification)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                            System.out.println(response.code());
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                            System.out.println(t.getMessage());
                                        }
                                    });
                        }
                        save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.success));
                        save.buttonFinished("Published");
                        new Handler().postDelayed(this::finish, 2000);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to save your profile", Toast.LENGTH_SHORT).show();
                        save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                        save.buttonFinished("Failed");
                        new Handler().postDelayed(() -> {
                            save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            save.buttonFinished("Publish");
                            save.setViewEnabled(true);
                        }, 3000);
                    }
                });

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        findViewById(R.id.selector).setVisibility(View.GONE);
                        importFile(data.getData());
                    }
                }
            });


    private void importFile(Uri uri) {
        new Thread(() -> {
            try {
                String fileName = FilesHelper.getFileName(this, uri);
                File tempFIle = FilesHelper.copyToTempFile(this, uri, fileName);
                if (tempFIle != null) {
                    uploadFile = tempFIle;
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "File not found in media.", Toast.LENGTH_SHORT).show();
            }

        }).start();
    }

}