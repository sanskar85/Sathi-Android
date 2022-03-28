package com.abbvmk.sathi.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.File;

public class IDCard extends Fragment implements FilesHelper.FileResponse {

    private User user;
    private ImageView card;
    private Context mContext;

    public IDCard() {
        // Required empty public constructor
    }

    public IDCard(Context context, @NonNull User user) {
        this.mContext = context;
        this.user = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_idcard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mContext == null) return;
        card = view.findViewById(R.id.card);
        File file = FilesHelper.idCard(mContext, user.getId());
        if (file != null) {
            loadImage(file);
        }
        FilesHelper.downloadID(mContext, user.getId(), this);

        view.findViewById(R.id.share).setOnClickListener(v -> {
            File shareFile = FilesHelper.idCard(v.getContext(), user.getId());
            if (shareFile == null) {
                Toast.makeText(v.getContext(), "ID card not loaded yet please wait...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getActivity() == null) return;
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.abbvmk-sathi.com?user=" + user.getId()))
                    .setDomainUriPrefix("https://sathiabbvmk.page.link")
                    .buildShortDynamicLink()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            Context context = getContext();
                            if (context == null) return;

                            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", shareFile);
                            Uri shortLink = task.getResult().getShortLink();
                            String whatsAppMessage = "अखिल भारतीय बंगी वैश्य महासभा, खगड़िया इकाई  का app आ गया है । \nनीचे दिए लिंक पे क्लिक करके " + user.getName() + " का सदस्य ID देखे । \n\n\uD83D\uDC49";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage + shortLink);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setType("image/jpg");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {
                                startActivity(intent);
                            } catch (Exception e) {

                                Toast.makeText(v.getContext(), "No social app installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(v.getContext(), "Cannot generate sharing link.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }


    @Override
    public void onFileDownloaded(File file) {
        loadImage(file);

    }

    private void loadImage(File file) {
        if (file != null) {
            Glide
                    .with(mContext.getApplicationContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(card);
        }
    }

}