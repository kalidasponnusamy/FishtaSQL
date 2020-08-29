package com.vedhafishfarm.fishtasql.feature.postupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.feature.homepage.MainActivity;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.utils.ViewModelFactory;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUploadActivity extends AppCompatActivity {

    private AppCompatSpinner spinner;
    private  PostUploadViewModel viewModel;
    private TextView postTxt;
    private TextInputEditText textInputEditText;

    private int privacylevel = 0;
    private ProgressDialog progressDialog;

    private Boolean isImageSelected = false;
    ImageView addImage, previewImage;

    private File compressedImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        viewModel = new ViewModelProvider(this,new ViewModelFactory()).get(PostUploadViewModel.class);
        spinner = findViewById(R.id.spinner_privacy);
        postTxt = findViewById(R.id.postbtn);
        textInputEditText = findViewById(R.id.input_post);
        addImage = findViewById(R.id.add_image);
        previewImage = findViewById(R.id.image_preview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Uploading Post... Please Wait!");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) view;
                if (selectedTextView != null){
                    selectedTextView.setTextColor(Color.BLACK);
                    selectedTextView.setTypeface(null, Typeface.BOLD);
                }
                privacylevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                privacylevel = 0;
            }
        });

        addImage.setOnClickListener(v -> selectImage());
        previewImage.setOnClickListener(v -> selectImage());

        postTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = textInputEditText.getText().toString();
                String userId = FirebaseAuth.getInstance().getUid();

                if (status.trim().length() > 0 || isImageSelected){
                    progressDialog.show();

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("post", status);
                    builder.addFormDataPart("postUserId", userId);
                    builder.addFormDataPart("privacy",privacylevel+"");

                    if (isImageSelected){
                        builder.addFormDataPart("file", compressedImageFile.getName(),
                                RequestBody.create(compressedImageFile, MediaType.parse("multipart/form-data")));
                    }
                    MultipartBody multipartBody = builder.build();

                    viewModel.uploadPost(multipartBody, false).observe(PostUploadActivity.this, new Observer<GeneralResponse>() {
                        @Override
                        public void onChanged(GeneralResponse generalResponse) {
                            progressDialog.hide();
                            Toast.makeText(PostUploadActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (generalResponse.getStatus()==200){
                                onBackPressed();
                            }
                        }
                    });
                }else {
                    Toast.makeText(PostUploadActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                compressedImageFile = new Compressor(this).setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                isImageSelected = true;
                addImage.setVisibility(View.GONE);
                previewImage.setVisibility(View.VISIBLE);

                Glide.with(PostUploadActivity.this).load(selectedImage.getPath())
                        .error(R.drawable.ic_add_a_photo).placeholder(R.drawable.ic_add_a_photo)
                        .into(previewImage);

            }catch (IOException e){
                previewImage.setVisibility(View.GONE);
                addImage.setVisibility(View.VISIBLE);
            }
        }
    }
}