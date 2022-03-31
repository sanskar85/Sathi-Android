package com.abbvmk.sathi.Fragments.Notice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.Loading.Loading;

import java.util.ArrayList;

public class NoticeList extends Fragment {
    private Context mContext;
    private static ArrayList<Notice> notices;
    private NoticeListAdapter adapter;

    private Loading loading;

    public NoticeList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;
        if (notices == null) {
            notices = new ArrayList<>();
        }
        mContext = getContext();
        loading = view.findViewById(R.id.loading);
        RecyclerView recyclerView = view.findViewById(R.id.noticeRecycler);
        adapter = new NoticeListAdapter(mContext, notices);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        fetchNotices();

    }

    private void fetchNotices() {
        loading.setProgressVisible(true);
        Firebase
                .fetchNotice(_notices -> {
                    if (_notices != null) {
                        notices.clear();
                        notices.addAll(_notices);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Unable to load notices", Toast.LENGTH_SHORT).show();
                    }
                    loading.setProgressVisible(false);
                });


    }
}