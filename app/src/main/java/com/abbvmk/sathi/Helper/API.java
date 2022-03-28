package com.abbvmk.sathi.Helper;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.abbvmk.sathi.Fragments.Posts.Post;
import com.abbvmk.sathi.Notice.Notice;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.Admin.Designation.PendingDesignationClass;
import com.abbvmk.sathi.screens.Login.LoginResponse;
import com.abbvmk.sathi.screens.PostViewer.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

class AuthInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain)
            throws IOException {
        Request request = chain.request();

        String authToken = AuthHelper.getAuthenticationToken();
        if (authToken != null && !TextUtils.isEmpty(authToken)) {//essentially checking if the prefs has a non null token
            request = request.newBuilder()
                    .addHeader("authorization", "Bearer " + authToken)
                    .build();
        }
        return chain.proceed(request);
    }
}

public class API {
    private static final String BASE_URL = "https://abbvmk.herokuapp.com/";
    private static APIRequest request;

    public static APIRequest instance() {
        if (request == null) {
            init();
        }
        return request;
    }

    private static void init() {
        OkHttpClient client = new OkHttpClient.
                Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        request = retrofit.create(APIRequest.class);
    }


    public interface APIRequest {
        @FormUrlEncoded
        @POST("auth/login-request")
        Call<String> loginRequest(@FieldMap Map<String, String> fields);

        @FormUrlEncoded
        @POST("auth/login-verify")
        Call<LoginResponse> loginVerify(@FieldMap Map<String, String> fields);

        @POST("profile/save")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Call<User> saveProfile(@Body User user);

        @GET("profile/dp/{userID}")
        Call<String> getDPDownloadLink(@Path(value = "userID", encoded = true) String userID);

        @GET("profile/id-card/{userID}")
        Call<String> getIDDownloadLink(@Path(value = "userID", encoded = true) String userID);

        @GET("profile/profile/{userID}")
        Call<User> fetchProfile(@Path(value = "userID", encoded = true) String userID);

        @GET("profile/all")
        Call<ArrayList<User>> fetchAllUsers();

        @GET("profile/committee")
        Call<ArrayList<User>> fetchCommitteeUsers();

        @GET("notice/fetch/{noticeID}")
        Call<String> getNoticeDownloadLink(@Path(value = "noticeID", encoded = true) String noticeID);

        @GET("post/image/{postID}")
        Call<String> getPostDownloadLink(@Path(value = "postID", encoded = true) String postID);

        @POST("post/create")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Call<String> uploadPost(@Body Post post);

        @FormUrlEncoded
        @POST("notice/create")
        Call<String> uploadNotice(@Field("message") String message, @Field("filename") String filename);

        @GET("post/fetch")
        Call<ArrayList<Post>> fetchPosts();

        @GET("post/fetch/{postID}")
        Call<Post> fetchPost(@Path(value = "postID", encoded = true) String postID);

        @POST("post/delete/{postID}")
        Call<String> deletePost(@Path(value = "postID", encoded = true) String postID);

        @FormUrlEncoded
        @POST("post/comment/create")
        Call<Comment> createComment(@Field("id") String id, @Field("message") String message);

        @GET("post/comments/{postID}")
        Call<ArrayList<Comment>> fetchComments(@Path(value = "postID", encoded = true) String postID);

        @GET("notice/all")
        Call<ArrayList<Notice>> fetchNotices();

        @GET("designation/all")
        Call<ArrayList<String>> fetchDesignations();

        @FormUrlEncoded
        @POST("designation/create")
        Call<String> createDesignations(@Field("title") String title);

        @FormUrlEncoded
        @POST("designation/request")
        Call<String> requestDesignationUpdate(@Field("userID") String userID, @Field("updateTo") String updateTo);

        @GET("designation/pending")
        Call<ArrayList<PendingDesignationClass>> fetchPendingApprovals();

        @POST("designation/approve/{id}")
        Call<String> approvePendingRequest(@Path(value = "id", encoded = true) String id);

        @POST("designation/reject/{id}")
        Call<String> rejectPendingRequest(@Path(value = "id", encoded = true) String id);

        @GET
        Call<ResponseBody> downloadFile(@Url String url);

        @FormUrlEncoded
        @POST("update")
        Call<String> checkForUpdates(@Field("versionCode") int versionCode);

    }
}
