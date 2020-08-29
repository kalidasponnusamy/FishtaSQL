package com.vedhafishfarm.fishtasql.data.remote;

import com.vedhafishfarm.fishtasql.feature.auth.LoginActivity;
import com.vedhafishfarm.fishtasql.feature.profile.ProfileActivity;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.model.auth.Auth;
import com.vedhafishfarm.fishtasql.model.auth.AuthResponse;
import com.vedhafishfarm.fishtasql.model.friend.FriendResponse;
import com.vedhafishfarm.fishtasql.model.post.PostResponse;
import com.vedhafishfarm.fishtasql.model.profile.ProfileResponse;
import com.vedhafishfarm.fishtasql.model.search.SearchResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);
    @POST("uploadpost")
    Call<GeneralResponse> uploadPost(@Body MultipartBody body);

    @POST("uploadImage")
    Call<GeneralResponse> uploadImage(@Body MultipartBody body);

    @GET("loadprofileinfo")
    Call<ProfileResponse> fetchProfileInfo(@QueryMap Map<String, String> params);

    @GET("search")
    Call<SearchResponse> search(@QueryMap Map<String, String> params);

    @GET("loadfriends")
    Call<FriendResponse> loadFriends(@Query("uid") String uid);

    @GET("getnewsfeed")
    Call<PostResponse> getNewsFeed(@QueryMap Map<String, String> params);

    @POST("performaction")
    Call<GeneralResponse> performAction(@Body ProfileActivity.PerformAction performAction);
}
