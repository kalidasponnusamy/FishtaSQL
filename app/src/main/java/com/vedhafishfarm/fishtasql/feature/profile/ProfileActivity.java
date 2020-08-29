package com.vedhafishfarm.fishtasql.feature.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.data.remote.ApiClient;
import com.vedhafishfarm.fishtasql.feature.fullimage.FullImageActivity;
import com.vedhafishfarm.fishtasql.feature.postupload.PostUploadActivity;
import com.vedhafishfarm.fishtasql.feature.search.SearchActivity;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.model.profile.ProfileResponse;
import com.vedhafishfarm.fishtasql.utils.ViewModelFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private String uid = "", profileUrl="", coverUrl="";
    private int current_state = 0;

    /*
        0 = profile is still loading
        1 = two people are friends (un friend)
        2 = we have friend request to that person (cancel request)
        3 = we have received friend request from that person (reject or accept request)
        4= we are unknown (you can send request)
        5 = our own profile
     */

    private Button profileOptionBtn;
    private ImageView coverImage;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProfileViewModel viewModel;

    private Boolean isCoverImage = false;
    private ProgressDialog progressDialog;
    private CircularImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this,new ViewModelFactory()).get(ProfileViewModel.class);
        profileOptionBtn = findViewById(R.id.profile_action_btn);
        profileImage = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.recyv_profile);
        progressBar = findViewById(R.id.progressbar);
        coverImage = findViewById(R.id.profile_cover);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");

        uid = getIntent().getStringExtra("uid");

        if (uid.equals(FirebaseAuth.getInstance().getUid())){
            current_state = 5;
            profileOptionBtn.setText("Edit Profile");
        } else {
            // find Current state from backend
            profileOptionBtn.setText("Loading...");
            profileOptionBtn.setEnabled(false);
        }
        fetchProfileInfo();

        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(coverImage, coverUrl);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(profileImage, profileUrl);
            }
        });
    }

    private void fetchProfileInfo() {
        progressDialog.show();
        //call Api
        Map<String, String> params = new HashMap<>();
        params.put("userId",FirebaseAuth.getInstance().getUid());
        if (current_state==5){
            params.put("current_state",current_state+"");
        } else {
            params.put("profileId", uid);
        }
        viewModel.fetchProfileInfo(params).observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                progressDialog.hide();
                if(profileResponse.getStatus()==200){
                    collapsingToolbarLayout.setTitle(profileResponse.getProfile().getName());
                    profileUrl = profileResponse.getProfile().getProfileUrl();
                    coverUrl = profileResponse.getProfile().getCoverUrl();
                    current_state = Integer.parseInt(profileResponse.getProfile().getState());

                    if (!profileUrl.isEmpty()){
                        Uri profileUri = Uri.parse(profileUrl);

                        if (profileUri.getAuthority() == null){
                            profileUrl = ApiClient.BASE_URL+profileUrl;
                        }
                        Glide.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                    }

                    if (!coverUrl.isEmpty()){
                        Uri coverUri = Uri.parse(coverUrl);

                        if (coverUri.getAuthority() == null){
                            coverUrl = ApiClient.BASE_URL+coverUrl;
                        }
                        Glide.with(ProfileActivity.this).load(coverUrl).into(coverImage);
                    }

                    if (current_state ==0){
                        profileOptionBtn.setText("Loading...");
                        profileOptionBtn.setEnabled(false);
                        return;
                    } else if (current_state==1){
                        profileOptionBtn.setText("Friends");
                    } else if (current_state==2){
                        profileOptionBtn.setText("Cancel Request");
                    }else if (current_state==3){
                        profileOptionBtn.setText("Accept Request");
                    }else if (current_state==4){
                        profileOptionBtn.setText("Send Request");
                    }else if (current_state==5){
                        profileOptionBtn.setText("Edit Profile");
                    }
                    profileOptionBtn.setEnabled(true);
                    loadProfileOptionButton();
                } else {
                    Toast.makeText(ProfileActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadProfileOptionButton() {
        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileOptionBtn.setEnabled(false);

                if (current_state == 5){
                    CharSequence[] options = new CharSequence[]{"Change Cover Image", "Change Profile Image", "View Cover Image", "View Profile Image"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                isCoverImage = true;
                                selectImage();
                            }else if ( position == 1){
                                isCoverImage = false;
                                selectImage();
                            } else if (position == 2){
                                viewFullImage(coverImage, coverUrl);
                            } else if (position == 3){
                                viewFullImage(profileImage, profileUrl);
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 4){
                    CharSequence[] options = new CharSequence[]{"Send Friend Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    performAction();
                                }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 3){
                    CharSequence[] options = new CharSequence[]{"Accept Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 2){
                    CharSequence[] options = new CharSequence[]{"Cancel Request"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                } else if (current_state == 1){
                    CharSequence[] options = new CharSequence[]{"Unfriend"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                performAction();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setOnDismissListener(ProfileActivity.this);
                    dialog.show();
                }
            }
        });
    }

    private void performAction() {
        progressDialog.show();
        viewModel.performAction(new PerformAction(current_state+"",FirebaseAuth.getInstance().getUid(),
                                uid)).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus() == 200){
                    profileOptionBtn.setEnabled(true);
                    if (current_state ==4){
                        current_state=2;
                        profileOptionBtn.setText("Cancel Request");
                    } else if (current_state ==3){
                        current_state=1;
                        profileOptionBtn.setText("Friends");
                    } else if (current_state ==2){
                        current_state=4;
                        profileOptionBtn.setText("Send Request");
                    } else if (current_state ==1){
                        current_state=4;
                        profileOptionBtn.setText("Send Request");
                    }
                }else {
                    profileOptionBtn.setEnabled(false);
                    profileOptionBtn.setText("Error");
                }
            }
        });
    }

    private void viewFullImage(ImageView imageView, String imageUrl) {
            Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
            intent.putExtra("imageUrl", imageUrl);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(imageView, imageUrl);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
                startActivity(intent, activityOptions.toBundle());
            } else {
                startActivity(intent);
            }
    }

    private void selectImage() {
        ImagePicker.create(this).single().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode,resultCode,data)){
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);

            try {
                File compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));
                
                uploadImage(compressedImageFile);

            }catch (IOException e){
                Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File compressedImageFile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", FirebaseAuth.getInstance().getUid()+"");
        builder.addFormDataPart("isCoverImage", isCoverImage+"");
        builder.addFormDataPart("file",
                compressedImageFile.getName(),
                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));

        progressDialog.show();

        viewModel.uploadPost(builder.build(), true).observe(this, new Observer<GeneralResponse>() {
            @Override
            public void onChanged(GeneralResponse generalResponse) {
                progressDialog.hide();
                Toast.makeText(ProfileActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (generalResponse.getStatus()==200){
                    if (isCoverImage){
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL+generalResponse.getExtra())
                                .into(coverImage);
                    }else{
                        Glide.with(ProfileActivity.this).load(ApiClient.BASE_URL+generalResponse.getExtra())
                                .into(profileImage);
                    }
                }
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        profileOptionBtn.setEnabled(true);
    }

    public static class PerformAction{
        String operationType, uid, profileId;

        public PerformAction(String operationType, String uid, String profileId) {
            this.operationType = operationType;
            this.uid = uid;
            this.profileId = profileId;
        }
    }

}