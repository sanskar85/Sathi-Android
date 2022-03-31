package com.abbvmk.sathi.Helper;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class GlideHelper {


    public static void loadDPImage(Context mContext, File file, ImageView iv) {
        if (file != null) {
            Glide
                    .with(mContext)
                    .load(file)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(iv);
        }
    }

    public static void loadPostImage(@NonNull Context mContext, @NonNull String url, @NonNull ImageView iv) {
        Glide
                .with(mContext)
                .load(url)
                .into(iv);
    }

    public static void loadNoticeImage(Context mContext, File file, ImageView iv) {
        if (file != null) {
            Glide
                    .with(mContext)
                    .load(file)
                    .into(iv);
        }
    }
}
