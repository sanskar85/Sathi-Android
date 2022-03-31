package com.abbvmk.sathi.Helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import com.abbvmk.sathi.Fragments.Notice.Notice;
import com.abbvmk.sathi.Fragments.Posts.Post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FilesHelper {


    public interface FileResponse {
        void onFileDownloaded(File file);
    }

    public static File dp(@NonNull Context context, @NonNull String userID) {
        File file = new File(context.getExternalFilesDir("dp"), userID + ".jpg");
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static File getNoticeFile(@NonNull Context context, Notice notice) {
        File file = new File(context.getExternalFilesDir("notice"), notice.getFile());
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static File post(@NonNull Context context, @NonNull Post post) {
        File file = new File(context.getExternalFilesDir("post"), post.getPhoto());
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static void downloadDP(@NonNull Context context, @NonNull String userID, FileResponse callback) {
        File file = new File(context.getExternalFilesDir("dp"), userID + ".jpg");
        File tempFile;
        try {
            tempFile = File.createTempFile(userID, ".jpg");
        } catch (IOException e) {
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dpRef = storageRef.child("dp/" + userID + ".jpg");

        File finalTempFile = tempFile;
        dpRef.getFile(finalTempFile)
                .addOnSuccessListener(taskSnapshot -> {
                    try {
                        copy(finalTempFile, file);
                        if (callback != null) {
                            callback.onFileDownloaded(file);
                            tempFile.delete();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


    public static void downloadNotice(@NonNull Context context, @NonNull Notice notice, FileResponse callback) {
        File file = new File(context.getExternalFilesDir("notice"), notice.getFile());
        File tempFile;
        try {
            tempFile = File.createTempFile(notice.getId(), getExtension(file));
        } catch (IOException e) {
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference noticeRef = storageRef.child("notices/" + notice.getFile());

        File finalTempFile = tempFile;
        noticeRef.getFile(finalTempFile)
                .addOnSuccessListener(taskSnapshot -> {
                    try {
                        copy(finalTempFile, file);
                        if (callback != null) {
                            callback.onFileDownloaded(file);
                            tempFile.delete();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static String getExtension(@NonNull File file) {
        // convert the file name into string
        String fileName = file.toString();

        return getExtension(fileName);
    }

    public static String getExtension(@NonNull String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "abc";
    }


    public static File copyToTempFile(Context mContext, Uri uri, String fileName) {
        if (uri == null) return null;
        String tempName = String.valueOf(System.currentTimeMillis());
        try {
            File file = File.createTempFile(tempName, "." + getExtension(fileName), mContext.getExternalCacheDir());
            InputStream in = mContext.getContentResolver().openInputStream(uri);

            if (in == null) {
                Log.e(FilesHelper.class.getName(), "Unable to obtain input stream from URI");
                return null;
            }
            try (OutputStream out = new FileOutputStream(file)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void copy(@NonNull File src, @NonNull File dst) throws IOException {
        if (!dst.exists() && !dst.createNewFile()) {
            throw new IOException("Cannot Create File");
        }
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static String getFileName(Context mContext, Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }

        cursor.moveToFirst();

        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

        cursor.close();

        return fileName;
    }


    public static File drawableToFile(Context mContext, Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        return bitmapToFile(mContext, bitmap);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static File bitmapToFile(Context mContext, Bitmap inImage) {
        try {
            File file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", mContext.getExternalCacheDir());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            return null;
        }

    }

}
