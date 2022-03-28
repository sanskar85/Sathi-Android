package com.abbvmk.sathi.Notice;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Members.MembersListAdapter;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.Loading.Loading;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        new Thread(() -> {
            API
                    .instance()
                    .fetchNotices()
                    .enqueue(new Callback<ArrayList<Notice>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Notice>> call, @NonNull Response<ArrayList<Notice>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                notices.clear();
                                notices.addAll(response.body());
                                adapter.notifyDataSetChanged();
                                loading.setProgressVisible(false);
                            } else {
                                Toast.makeText(mContext, "Unable to load notices", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Notice>> call, @NonNull Throwable t) {
                            Toast.makeText(mContext, "Unable to load notices", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }
}