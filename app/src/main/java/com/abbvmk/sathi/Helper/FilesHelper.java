package com.abbvmk.sathi.Helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import com.abbvmk.sathi.BuildConfig;
import com.abbvmk.sathi.Fragments.Posts.Post;
import com.abbvmk.sathi.Notice.Notice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static File idCard(@NonNull Context context, @NonNull String userID) {
        File file = new File(context.getExternalFilesDir("ids"), userID + ".jpg");
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static File getNoticeFile(@NonNull Context context, Notice notice) {
        File file = new File(context.getExternalFilesDir("notice"), notice.getFilename());
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static File post(@NonNull Context context, @NonNull Post post) {
        File file = new File(context.getExternalFilesDir("post"), post.getFilename());
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            return null;
        }
    }

    public static void downloadDP(@NonNull Context context, @NonNull String userID, FileResponse callback) {
        API
                .instance()
                .getDPDownloadLink(userID)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String url = response.body();
                            final String resolvedURL = response.body().substring(1, url.length() - 1);
                            API
                                    .instance()
                                    .downloadFile(resolvedURL)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                File file = new File(context.getExternalFilesDir("dp"), userID + ".jpg");
                                                boolean fileSaved = saveFile(file, response.body());
                                                if (fileSaved && callback != null) {
                                                    callback.onFileDownloaded(file);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });
    }


    public static void downloadID(@NonNull Context context, @NonNull String userID, FileResponse callback) {
        API
                .instance()
                .getIDDownloadLink(userID)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String url = response.body();
                            final String resolvedURL = response.body().substring(1, url.length() - 1);
                            API
                                    .instance()
                                    .downloadFile(resolvedURL)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                File file = new File(context.getExternalFilesDir("ids"), userID + ".jpg");
                                                boolean fileSaved = saveFile(file, response.body());
                                                if (fileSaved && callback != null) {
                                                    callback.onFileDownloaded(file);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });
    }


    public static void downloadNotice(@NonNull Context context, @NonNull Notice notice, FileResponse callback) {
        API
                .instance()
                .getNoticeDownloadLink(notice.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String url = response.body();
                            final String resolvedURL = response.body().substring(1, url.length() - 1);
                            API
                                    .instance()
                                    .downloadFile(resolvedURL)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                File file = new File(context.getExternalFilesDir("notice"), notice.getFilename());
                                                boolean fileSaved = saveFile(file, response.body());
                                                if (fileSaved && callback != null) {
                                                    callback.onFileDownloaded(file);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });
    }


    public static void downloadPost(Context context, Post post, FileResponse callback) {
        API
                .instance()
                .getPostDownloadLink(post.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String url = response.body();
                            final String resolvedURL = response.body().substring(1, url.length() - 1);
                            API
                                    .instance()
                                    .downloadFile(resolvedURL)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            if (response.isSuccessful() && response.body() != null) {
                                                File file = new File(context.getExternalFilesDir("post"), post.getFilename());
                                                boolean fileSaved = saveFile(file, response.body());
                                                if (fileSaved && callback != null) {
                                                    callback.onFileDownloaded(file);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });

    }


    public static void downloadUpdate(Context context, int resultCode, FileResponse callback) {
        API
                .instance()
                .checkForUpdates(BuildConfig.VERSION_CODE)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200 && response.body() != null) {
                            String url = response.body();
                            final String resolvedURL = response.body().substring(1, url.length() - 1);
                            API
                                    .instance()
                                    .downloadFile(resolvedURL)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            if (response.isSuccessful() && response.body() != null) {

                                                File file = null;
                                                try {
                                                    file = File.createTempFile("update", ".apk", context.getExternalCacheDir());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    return;
                                                }

                                                boolean fileSaved = saveFile(file, response.body());
                                                if (fileSaved && callback != null) {
                                                    callback.onFileDownloaded(file);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });

    }


    private static boolean saveFile(@NonNull File dest, ResponseBody body) {
        try {

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                File tempFile = File.createTempFile(dest.getName(), getExtension(dest));
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(tempFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                copy(tempFile, dest);

                Log.d(FilesHelper.class.getName(), "file download: at " + dest.getAbsolutePath() + " " + fileSizeDownloaded + " of " + fileSize);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(FilesHelper.class.getName(), "file download Failed");
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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


}
