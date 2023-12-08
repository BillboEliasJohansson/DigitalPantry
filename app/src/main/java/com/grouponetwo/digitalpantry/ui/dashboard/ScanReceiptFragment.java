package com.grouponetwo.digitalpantry.ui.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.grouponetwo.digitalpantry.databinding.FragmentDashboardBinding;

import java.io.ByteArrayOutputStream;

public class ScanReceiptFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private FragmentDashboardBinding binding;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanReceiptModel scanReceiptViewModel =
                new ViewModelProvider(this).get(ScanReceiptModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textDashboard;

        openCamera();

        return root;
    }
    private void openCamera() {

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            // Permission already granted, open the camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {

            // retrive image and encode to Base64
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            // IMAGE IN Base64 ready for API CALL
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            OpenAIChat.makeOpenAIRequest(textView, getContext(), "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", encoded);

            Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Connecting", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}