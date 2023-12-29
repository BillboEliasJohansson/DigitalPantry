package com.grouponetwo.digitalpantry.ui.scanReceipt;

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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.grouponetwo.digitalpantry.R;
import com.grouponetwo.digitalpantry.databinding.FragmentScanReceiptBinding;

import java.io.ByteArrayOutputStream;

public class ScanReceiptFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private FragmentScanReceiptBinding binding;
    private TextView receiptText;
    private Button accept;
    private Button decline;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScanReceiptBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        receiptText = binding.GroceryList;
        accept = binding.AcceptButton;
        decline = binding.DeclineButton;

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String listOfItems = receiptText.getText().toString();
                listOfItems = listOfItems.replace("\n", "§").replace(" ", "").replace("kg", "").replace(",", ".");
                Log.d("Items", listOfItems);
                SSH.executeSSHcommand("python SaveItems.py " + listOfItems);
                createSnackbar("Saving Items");

                goHome(container);
            }

        });

        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goHome(container);
            }

        });

        openCamera();

        return root;
    }

    private static void goHome(ViewGroup container) {
        NavController navController = Navigation.findNavController(container);
        navController.navigate(R.id.navigation_stock);
    }

    private void openCamera() {

        // if camera permission not granted
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        // Permission granted, open the camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {

            // retrieve image and encode to Base64
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream imageBaus = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageBaus);
            byte[] byteArray = imageBaus .toByteArray();

            // Image in Base64 ready for API CALL
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // API call
            OpenAIChat.makeOpenAIRequest(receiptText, getContext(), "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", encoded);

            // Feedback snackbar that progress is being made
            createSnackbar("Connecting");


        }
    }

    private void createSnackbar(String text) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                        text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}