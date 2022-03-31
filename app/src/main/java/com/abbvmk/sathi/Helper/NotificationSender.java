package com.abbvmk.sathi.Helper;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class NotificationSender {
    private static NotificationSenderInterface request = null;

    public static NotificationSenderInterface getClient() {
        if (request == null) {
            request = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(NotificationSenderInterface.class);
        }
        return request;
    }

    public interface NotificationSenderInterface {
        @Headers(
                {
                        "Content-Type:application/json",
                        "Authorization: key=AAAA55ZFpck:APA91bHdsY1hRvfT5SeRaDB4m2hmUdasC7GINrnCieA6WqMiQVvVxEVBU8nY9bWKzYdka7l5f_tI3pux6bKOgUdcWCyAYZSaYpeRqjHf1wBIKvD6D7-Sy4ncDa2M8AdDFriHGMTCgkd2" // Your server key refer to video for finding your server key
                }
        )
        @POST("fcm/send")
        Call<ResponseBody> sendNotification(@Body Map<String, Object> body);
    }
}

