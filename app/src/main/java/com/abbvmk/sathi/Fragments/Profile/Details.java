package com.abbvmk.sathi.Fragments.Profile;

import static com.abbvmk.sathi.Views.ChildDetailView.ChildDetailView.ordinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.ChildDetail;
import com.abbvmk.sathi.User.User;

public class Details extends Fragment {

    private User user;


    public Details() {
        // Required empty public constructor
    }

    public Details(User user) {
        this.user = user;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (user == null) {
            return;
        }
        ((TextView) view.findViewById(R.id.name)).setText(String.format("Name : %s", user.getName()));
        ((TextView) view.findViewById(R.id.relationName)).setText(String.format("%s %s", user.getRelationType(), user.getRelationName()));
        ((TextView) view.findViewById(R.id.fname)).setText(String.format("Father's Name : %s", user.getFname()));
        ((TextView) view.findViewById(R.id.mname)).setText(String.format("Mother's Name : %s", user.getMname()));
        ((TextView) view.findViewById(R.id.gender)).setText(String.format("Gender : %s", user.getGender()));
        ((TextView) view.findViewById(R.id.bloodGroup)).setText(String.format("Blood Group : %s", user.getBloodGroup()));
        ((TextView) view.findViewById(R.id.dob)).setText(String.format("D.O.B : %s", user.getDob()));
        ((TextView) view.findViewById(R.id.qualification)).setText(String.format("Qualification : %s", user.getQualification()));
        ((TextView) view.findViewById(R.id.occupation)).setText(String.format("Occupation : %s", user.getOccupation()));
        ((TextView) view.findViewById(R.id.about)).setText(String.format("About : %s", user.getAbout()));
        ((TextView) view.findViewById(R.id.maritalStatus)).setText(String.format("Marital Status : %s", user.getMaritalStatus()));
        ((TextView) view.findViewById(R.id.address1)).setText(String.format("Village : %s", user.getAddress1()));
        ((TextView) view.findViewById(R.id.address2)).setText(String.format("Post Office : %s", user.getAddress2()));
        ((TextView) view.findViewById(R.id.address3)).setText(String.format("District : %s", user.getAddress3()));
        ((TextView) view.findViewById(R.id.pincode)).setText(String.format("Pincode : %s", user.getPincode()));

        if (user.getChildCount() == 0) {
            view.findViewById(R.id.child_details).setVisibility(View.GONE);
            return;
        }

        LinearLayoutCompat layout_container = view.findViewById(R.id.layout_container);

        for (int i = 0; i < user.getChildDetails().size(); i++) {
            ChildDetail detail = user.getChildDetails().get(i);
            View detailView = getLayoutInflater().inflate(R.layout.custom_child_detail, null);
            ((TextView) detailView.findViewById(R.id.title)).setText(String.format("Details of %s child", ordinal(i + 1)));
            ((TextView) detailView.findViewById(R.id.name)).setText(String.format("Name : %s", detail.getName()));
            ((TextView) detailView.findViewById(R.id.dob)).setText(String.format("D.O.B : %s", detail.getDob()));
            ((TextView) detailView.findViewById(R.id.qualification)).setText(String.format("Qualification : %s", detail.getQualification()));
            ((TextView) detailView.findViewById(R.id.occupation)).setText(String.format("Occupation : %s", detail.getOccupation()));
            ((TextView) detailView.findViewById(R.id.maritalStatus)).setText(String.format("Marital Status : %s", detail.getMaritalStatus()));
            layout_container.addView(detailView);
        }

    }
}