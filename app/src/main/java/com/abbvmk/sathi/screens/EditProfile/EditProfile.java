package com.abbvmk.sathi.screens.EditProfile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.User.UserValidationException;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.abbvmk.sathi.screens.LandingPage.LandingPage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

public class EditProfile extends AppCompatActivity implements ProgressButton.OnClickListener, FilesHelper.FileResponse {

    private User user;
    private ShapeableImageView dp;
    private TextInputEditText name, relationName, fatherName, motherName, dd, mm, yyyy, bloodGroup, qualification, occupation, about, address1, address2, address3, pincode;
    private RadioGroup gender, relationType, maritalStatus;
    private RadioButton male, female, s_o, d_o, w_o, married, unmarried, widowed, separated;
    private ProgressButton submit;
    private boolean newMember;
    private Uri dpPath;
    private FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_edit_profile);

        Intent intent = getIntent();
        newMember = intent.getBooleanExtra("newMember", false);
        if (newMember) {
            user = new User();
        } else {
            user = AuthHelper.getLoggedUser();
            if (user == null) {
                user = new User();
            }
        }
        mStorage = FirebaseStorage.getInstance();

        initView();
    }

    public void initView() {
        dp = findViewById(R.id.dp);
        name = findViewById(R.id.nameET);
        gender = findViewById(R.id.genderET);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        relationType = findViewById(R.id.relation_type);
        s_o = findViewById(R.id.s_o);
        d_o = findViewById(R.id.d_o);
        w_o = findViewById(R.id.w_o);
        relationName = findViewById(R.id.relationNameET);
        fatherName = findViewById(R.id.fatherNameET);
        motherName = findViewById(R.id.motherNameET);
        dd = findViewById(R.id.ddET);
        mm = findViewById(R.id.mmET);
        yyyy = findViewById(R.id.yyyyET);
        bloodGroup = findViewById(R.id.bloodGroupET);
        qualification = findViewById(R.id.qualificationET);
        occupation = findViewById(R.id.occupationET);
        about = findViewById(R.id.aboutET);
        maritalStatus = findViewById(R.id.marital_status);
        married = findViewById(R.id.married);
        unmarried = findViewById(R.id.unmarried);
        widowed = findViewById(R.id.widowed);
        separated = findViewById(R.id.separated);
        address1 = findViewById(R.id.address1ET);
        address2 = findViewById(R.id.address2ET);
        address3 = findViewById(R.id.address3ET);
        pincode = findViewById(R.id.pincodeET);
        submit = findViewById(R.id.submit_btn);
        relationType.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getCheckedRadioButtonId() == s_o.getId()) {
                ((TextInputLayout) findViewById(R.id.relationNameLayout)).setHint("S/o");
            } else if (radioGroup.getCheckedRadioButtonId() == d_o.getId()) {
                ((TextInputLayout) findViewById(R.id.relationNameLayout)).setHint("D/o");
            } else if (radioGroup.getCheckedRadioButtonId() == w_o.getId()) {
                ((TextInputLayout) findViewById(R.id.relationNameLayout)).setHint("W/o");
            }
        });
        submit.setText("Save");
        submit.setOnClickListener(this);
        dp.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(200)
                    .maxResultSize(400, 400)
                    .start();
        });
        name.setText(user.getName());
        relationName.setText(user.getRelationName());
        fatherName.setText(user.getFname());
        motherName.setText(user.getMname());
        bloodGroup.setText(user.getBloodGroup());
        qualification.setText(user.getQualification());
        occupation.setText(user.getOccupation());
        about.setText(user.getAbout());
        address1.setText(user.getAddress1());
        address2.setText(user.getAddress2());
        address3.setText(user.getAddress3());
        if (user.getPincode() > 0) {
            pincode.setText(String.valueOf(user.getPincode()));
        }

        if (user.getGender() != null) {
            if (user.getGender().equalsIgnoreCase("Male")) {
                gender.check(male.getId());
            } else if (user.getGender().equalsIgnoreCase("Female")) {
                gender.check(female.getId());
            }
        }

        if (user.getRelationName() != null) {
            if (user.getRelationName().equalsIgnoreCase("S/o")) {
                relationType.check(s_o.getId());
            } else if (user.getRelationName().equalsIgnoreCase("D/o")) {
                relationType.check(d_o.getId());
            } else if (user.getRelationName().equalsIgnoreCase("W/o")) {
                relationType.check(w_o.getId());
            }
        }
        if (user.getMaritalStatus() != null) {
            if (user.getMaritalStatus().equalsIgnoreCase("Married")) {
                maritalStatus.check(married.getId());
            } else if (user.getMaritalStatus().equalsIgnoreCase("Unmarried")) {
                maritalStatus.check(unmarried.getId());
            } else if (user.getMaritalStatus().equalsIgnoreCase("Separated")) {
                maritalStatus.check(separated.getId());
            } else if (user.getMaritalStatus().equalsIgnoreCase("Widowed")) {
                maritalStatus.check(widowed.getId());
            }
        }

        if (user.getDob() != null) {
            String[] date = user.getDob().split("-");
            if (date.length == 3) {
                dd.setText(String.valueOf(date[0]));
                mm.setText(String.valueOf(date[1]));
                yyyy.setText(String.valueOf(date[2]));
            }
        }

        File file = FilesHelper.dp(this, AuthHelper.getUID());
        if (file != null) {
            loadImage(file);
        } else {
            FilesHelper.downloadDP(this, AuthHelper.getUID(), this);
        }
    }


    public void resolveData() {
        user.setName(Objects.requireNonNull(name.getText()).toString());
        user.setRelationName(Objects.requireNonNull(relationName.getText()).toString());
        user.setFname(Objects.requireNonNull(fatherName.getText()).toString());
        user.setMname(Objects.requireNonNull(motherName.getText()).toString());
        user.setBloodGroup(Objects.requireNonNull(bloodGroup.getText()).toString());
        user.setQualification(Objects.requireNonNull(qualification.getText()).toString());
        user.setOccupation(Objects.requireNonNull(occupation.getText()).toString());
        user.setAbout(Objects.requireNonNull(about.getText()).toString());
        user.setAddress1(Objects.requireNonNull(address1.getText()).toString());
        user.setAddress2(Objects.requireNonNull(address2.getText()).toString());
        user.setAddress3(Objects.requireNonNull(address3.getText()).toString());
        user.setPincode(Integer.parseInt(Objects.requireNonNull(pincode.getText()).toString()));

        if (gender.getCheckedRadioButtonId() == male.getId()) {
            user.setGender("Male");
        } else if (gender.getCheckedRadioButtonId() == female.getId()) {
            user.setGender("Female");
        }

        if (relationType.getCheckedRadioButtonId() == s_o.getId()) {
            user.setRelationType("S/o");
        } else if (relationType.getCheckedRadioButtonId() == d_o.getId()) {
            user.setRelationType("D/o");
        } else if (relationType.getCheckedRadioButtonId() == w_o.getId()) {
            user.setRelationType("W/o");
        }

        if (maritalStatus.getCheckedRadioButtonId() == married.getId()) {
            user.setMaritalStatus("Married");
        } else if (maritalStatus.getCheckedRadioButtonId() == unmarried.getId()) {
            user.setMaritalStatus("Unmarried");
        } else if (maritalStatus.getCheckedRadioButtonId() == separated.getId()) {
            user.setMaritalStatus("Separated");
        } else if (maritalStatus.getCheckedRadioButtonId() == widowed.getId()) {
            user.setMaritalStatus("Widowed");
        }

        String date = dd.getText() + "-" + mm.getText() + "-" + yyyy.getText();
        user.setDob(date);

    }

    @Override
    public void onClick() {
        if (!submit.isViewEnabled()) {
            return;
        }
        try {
            resolveData();
            user.validate();
        } catch (UserValidationException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception e) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        submit.setViewEnabled(false);
        submit.buttonActivated();
        if (dpPath != null) {
            String path = AuthHelper.getUID() + ".jpg";
            StorageReference ref = mStorage.getReference("dp").child(path);

            UploadTask uploadTask = ref.putFile(dpPath);

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
                    user.setPhoto(downloadUri.toString());
                    saveProfile();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to save your photo.", Toast.LENGTH_SHORT).show();
                    submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                    submit.buttonFinished("Failure");

                    new Handler().postDelayed(() -> {
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                        submit.buttonFinished("Save");
                        submit.setViewEnabled(true);
                    }, 3000);
                }
            });

        } else {
            saveProfile();
        }
    }

    public void saveProfile() {
        Firebase
                .saveProfile(user, success -> {
                    if (success) {
                        MainApplication.fetchUsers();
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.success));
                        submit.buttonFinished("Saved");
                        new Handler().postDelayed(() -> {

                            if (user.getMaritalStatus().equalsIgnoreCase("Married")) {
                                Intent intent = new Intent(getApplicationContext(), ChildDetails.class);
                                intent.putExtra("newMember",newMember);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }

                            finish();

                        }, 2000);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to save profile", Toast.LENGTH_SHORT).show();
                        submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                        submit.buttonFinished("Failure");

                        new Handler().postDelayed(() -> {
                            submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            submit.buttonFinished("Save");
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
                dpPath = uri;
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileDownloaded(File file) {
        loadImage(file);
    }

    private void loadImage(File file) {
        if (file != null) {
            Glide
                    .with(this.getApplicationContext())
                    .load(file)
                    .centerCrop()
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(dp);
        }
    }
}