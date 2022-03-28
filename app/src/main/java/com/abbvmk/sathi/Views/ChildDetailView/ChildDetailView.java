package com.abbvmk.sathi.Views.ChildDetailView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.ChildDetail;
import com.abbvmk.sathi.User.UserValidationException;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ChildDetailView extends ConstraintLayout {

    private ChildDetail childDetail;
    private TextView header;
    private TextInputEditText name, dd, mm, yyyy, occupation, qualification;
    private RadioGroup maritalStatus;
    private RadioButton married, unmarried, widowed, separated;

    public ChildDetailView(@NonNull Context context) {
        super(context);
        childDetail = new ChildDetail();
        init(context, 0);
    }

    public ChildDetailView(@NonNull Context context, int index) {
        super(context);
        childDetail = new ChildDetail();
        init(context, index);
    }

    public ChildDetailView(@NonNull Context context, int index, ChildDetail detail) {
        super(context);
        childDetail = detail;
        init(context, index);
    }

    private void init(Context context, int index) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_child_detail, this, true);
        header = view.findViewById(R.id.headerTV);
        name = view.findViewById(R.id.nameET);
        dd = view.findViewById(R.id.ddET);
        mm = view.findViewById(R.id.mmET);
        yyyy = view.findViewById(R.id.yyyyET);
        occupation = view.findViewById(R.id.occupationET);
        qualification = view.findViewById(R.id.qualificationET);
        maritalStatus = view.findViewById(R.id.marital_status);
        married = findViewById(R.id.married);
        unmarried = findViewById(R.id.unmarried);
        widowed = findViewById(R.id.widowed);
        separated = findViewById(R.id.separated);
        setHeaderText(String.format("Detail of %s Child", ordinal(index)));
        updateData();
    }

    private void updateData() {
        name.setText(childDetail.getName());
        qualification.setText(childDetail.getQualification());
        occupation.setText(childDetail.getOccupation());
        if (childDetail.getMaritalStatus() != null) {
            if (childDetail.getMaritalStatus().equalsIgnoreCase("Married")) {
                maritalStatus.check(married.getId());
            } else if (childDetail.getMaritalStatus().equalsIgnoreCase("Unmarried")) {
                maritalStatus.check(unmarried.getId());
            } else if (childDetail.getMaritalStatus().equalsIgnoreCase("Separated")) {
                maritalStatus.check(separated.getId());
            } else if (childDetail.getMaritalStatus().equalsIgnoreCase("Widowed")) {
                maritalStatus.check(widowed.getId());
            }
        }

        if (childDetail.getDob() != null) {
            String[] date = childDetail.getDob().split("-");
            if (date.length == 3) {
                dd.setText(String.valueOf(date[0]));
                mm.setText(String.valueOf(date[1]));
                yyyy.setText(String.valueOf(date[2]));
            }
        }
    }


    public void resolveData() {
        childDetail.setName(Objects.requireNonNull(name.getText()).toString());
        childDetail.setQualification(Objects.requireNonNull(qualification.getText()).toString());
        childDetail.setOccupation(Objects.requireNonNull(occupation.getText()).toString());


        if (maritalStatus.getCheckedRadioButtonId() == married.getId()) {
            childDetail.setMaritalStatus("Married");
        } else if (maritalStatus.getCheckedRadioButtonId() == unmarried.getId()) {
            childDetail.setMaritalStatus("Unmarried");
        } else if (maritalStatus.getCheckedRadioButtonId() == separated.getId()) {
            childDetail.setMaritalStatus("Separated");
        } else if (maritalStatus.getCheckedRadioButtonId() == widowed.getId()) {
            childDetail.setMaritalStatus("Widowed");
        }

        String date = dd.getText() + "-" + mm.getText() + "-" + yyyy.getText();
        childDetail.setDob(date);

    }


    public void setChildDetail(ChildDetail detail) {
        childDetail = detail;
        updateData();
    }

    public ChildDetail getChildDetail() {
        return childDetail;
    }

    public void setHeaderText(String text) {
        header.setText(text);
    }

    public static String ordinal(int i) {
        return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"}[i % 10];
    }
}
