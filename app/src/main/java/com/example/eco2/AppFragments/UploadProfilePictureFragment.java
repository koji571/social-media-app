package com.example.eco2.AppFragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.eco2.R;
import com.example.eco2.RegisterActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UploadProfilePictureFragment extends Fragment {

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    ImageView profilePic;
    TextView uploadPic;

    public UploadProfilePictureFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            Glide.with(getContext()).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                            // Notify the activity about the selected image URI
                            ((RegisterActivity) getActivity()).setProfilePictureUri(selectedImageUri);
                        }
                    }
                }
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_profile_picture, container, false);

        profilePic = view.findViewById(R.id.profile_image);
        uploadPic = view.findViewById(R.id.uploadPic);


        View.OnClickListener clickListener = v->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        };

        profilePic.setOnClickListener(clickListener);
        uploadPic.setOnClickListener(clickListener);

        return view;
    }
}
