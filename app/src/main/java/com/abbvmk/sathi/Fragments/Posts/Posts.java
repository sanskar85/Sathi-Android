package com.abbvmk.sathi.Fragments.Posts;

import android.content.Context;
import android.content.Intent;
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
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.Views.Loading.Loading;
import com.abbvmk.sathi.screens.PostViewer.PostViewer;
import com.abbvmk.sathi.screens.ProfileViewer.ProfileViewer;

import java.util.ArrayList;

public class Posts extends Fragment implements PostAdapter.PostCardInterface {

    private final boolean committeePostsOnly;
    private final User user;
    private Context mContext;
    private PostAdapter adapter;
    private static ArrayList<Post> posts;
    private Loading loading;


    public Posts() {
        // Required empty public constructor
        this.committeePostsOnly = false;
        this.user = null;
    }

    public Posts(boolean committeePostsOnly) {
        this.committeePostsOnly = committeePostsOnly;
        this.user = null;
    }

    public Posts(User user) {
        this.user = user;
        this.committeePostsOnly = false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;
        mContext = getContext();
        loading = view.findViewById(R.id.loading);

        if (posts == null) {
            posts = new ArrayList<>();
        }

        RecyclerView recyclerView = view.findViewById(R.id.postRecycler);
        adapter = new PostAdapter(mContext, posts, committeePostsOnly, user, this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        fetchPosts();
    }

    private void fetchPosts() {
        loading.setProgressVisible(true);
        Firebase
                .fetchPosts(_posts -> {
                    if (_posts != null) {
                        posts.clear();
                        posts.addAll(_posts);
                        adapter.notifyDataSetChanged();
                        loading.setProgressVisible(false);
                    } else {
                        Toast.makeText(mContext, "Unable to fetch posts.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void profileHeaderClicker(User user) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ProfileViewer.class);
        intent.putExtra("user", user);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void openComments(Post post) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), PostViewer.class);
        intent.putExtra("post", post);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}