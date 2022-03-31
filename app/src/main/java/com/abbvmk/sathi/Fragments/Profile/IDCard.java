package com.abbvmk.sathi.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IDCard extends Fragment {

    private User user;
    private Context mContext;
    private ImageView image;
    private View card;

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

        TextView name, id, relationName, designation;

        image = view.findViewById(R.id.dp);
        name = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        relationName = view.findViewById(R.id.relationName);
        designation = view.findViewById(R.id.designation);
        card = view.findViewById(R.id.card);

        name.setText(user.getName());
        id.setText(String.format("ID:- %s", user.getMemberId()));
        relationName.setText(String.format("%s %s", user.getRelationType(), user.getRelationName()));
        designation.setText(user.getDesignation());

        File dp = FilesHelper.dp(mContext, user.getId());
        if (dp != null) {
            loadImage(dp);
        }

        view.findViewById(R.id.share).setOnClickListener(v -> {
            File shareFile = saveBitMap(v.getContext(), card);
            if (shareFile == null) {
                Toast.makeText(v.getContext(), "ID card not loaded yet please wait...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getActivity() == null) return;
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.abbvmk-sathi.com?user=" + user.getId()))
                    .setDomainUriPrefix("https://sathiabbvmk.page.link")
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder()
                                    .setFallbackUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/abvmk-343315.appspot.com/o/app_updates%2Fandroid%2Fsathi_2.2.apk?alt=media&token=207e3a8d-46fd-4514-a43e-94560245f16e"))
                                    .build()
                    )
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
                            if (task.getException() != null)
                                task.getException().printStackTrace();
                            Toast.makeText(v.getContext(), "Cannot generate sharing link.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }


    private void loadImage(File file) {
        if (file != null) {
            Glide
                    .with(mContext.getApplicationContext())
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(image);
        }
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private File saveBitMap(Context context, View drawView) {
        String filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            boolean isFileCreated = pictureFile.createNewFile();
            if (!isFileCreated)
                return null;
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictureFile;
    }


}