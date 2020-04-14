package com.zalocoders.videoup2.Views.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.zalocoders.videoup2.R;
import com.zalocoders.videoup2.Views.models.UploadItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class VideoRecordingFragment extends Fragment {

    private StorageReference mstorageRef;
    private DatabaseReference mdatabaseRef;
    private Uri finalUri;
    private View v;
   private TextInputEditText desc,title;
    private MaterialButton select;
    private JCVideoPlayerStandard video;
    private Context context;
    private ProgressDialog progressDialog;
    private static final int VIDEO_CAPTURE = 101;

    public VideoRecordingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_video_recording, container, false);
        context = container.getContext();

        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mstorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading");
        progressDialog.setCancelable(false);


        //upload progress dialog
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "minimize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(context,"you will be notified when video is uploaded",Toast.LENGTH_LONG).show();
                //transact to feed fragment
                Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.nav_host_fragment).navigate(R.id.videoFeedFragment);
            }
        });




        //if video is null this handles videos intent
        video = v.findViewById(R.id.videoPalyer);

        select = v.findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideo();
            }
        });



        //attaching postinf function to button (post)
        MaterialButton upload = v.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInput()){

                    if(finalUri!=null){
                        PrepareUploadVideo();

                    }else {
                        Toast.makeText(context,"Please record a Video",Toast.LENGTH_LONG).show();
                    }


                }
            }
        });


        captureVideo();


        return v;
    }



    // gets the extension of the video file
    private String GetFileExtension(Uri uri) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }



    //opens capture video intent
    private void captureVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == VIDEO_CAPTURE) {

            if (resultCode == RESULT_OK) {
                assert data != null;

                finalUri = data.getData();
                initializeVideoView();
                checkUriAvailability();


            } else if (resultCode == RESULT_CANCELED) {
                checkUriAvailability();


            }
        }

    }

    //this function uploads video and then retrieves url
    private void PrepareUploadVideo(){
        progressDialog.show();
        final StorageReference filepath = mstorageRef.child(System.currentTimeMillis()+"."+GetFileExtension(finalUri));
        StorageTask uploadTask = filepath.putFile(finalUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //this function retrieves the download url of thr current uploaded video
                getDownloadUrl(filepath);

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


            }
        });
    }




    //this function retrieves the download url of thr current uploaded video
    private void getDownloadUrl(final StorageReference storagePath){

        storagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                storeToDatabase(mdatabaseRef,uri);
            }
        });


    }


    private void storeToDatabase(DatabaseReference databaseReference, Uri videoUri){

       UploadItem  item = new UploadItem(Objects.requireNonNull(title.getText()).toString(),videoUri.toString(),desc.getText().toString());

        String uploadId = databaseReference.push().getKey();

        assert uploadId != null;
        databaseReference.child(uploadId).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context,"Video posted Successfully",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();


                //transact to feed fragment
                Navigation.findNavController(Objects.requireNonNull(getActivity()),R.id.nav_host_fragment).navigate(R.id.videoFeedFragment);

            }
        });
    }


    //this function validates all inputs on edit text
    public boolean validateInput(){

       desc = v.findViewById(R.id.desc);
         title = v.findViewById(R.id.titletext);

         if(Objects.requireNonNull(desc.getText()).toString().isEmpty()&& Objects.requireNonNull(title.getText()).toString().isEmpty()){
             Toast.makeText(context,"Fill a fields",Toast.LENGTH_LONG).show();
             return false;
         }
         return true;
    }


    // initializing he video player
    public void initializeVideoView(){

        video.setUp(finalUri.toString(),JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN);

        video.battery_level.setVisibility(View.GONE);
        video.batteryTimeLayout.setVisibility(View.GONE);
        video.backButton.setVisibility(View.GONE);

        //auto play
        video.fullscreenButton.setVisibility(View.VISIBLE);
        RequestOptions requestOptions = new RequestOptions();

        //setting thumbnail
        requestOptions.isMemoryCacheable();
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(finalUri) .diskCacheStrategy(DiskCacheStrategy.DATA).into(video.thumbImageView);
    }




    // this method checks if user has record a video by checking the url returned
    private void checkUriAvailability(){

        if(finalUri==null)
        {
            video.setVisibility(View.GONE);
            select.setVisibility(View.VISIBLE);

        }else {

            video.setVisibility(View.VISIBLE);
            select.setVisibility(View.GONE);
        }
    }

}
