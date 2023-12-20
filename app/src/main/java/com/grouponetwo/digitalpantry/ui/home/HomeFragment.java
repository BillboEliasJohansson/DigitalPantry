package com.grouponetwo.digitalpantry.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grouponetwo.digitalpantry.databinding.FragmentHomeBinding;
import com.grouponetwo.digitalpantry.ui.dashboard.SSH;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private FloatingActionButton voice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.stockTextView;
        voice = binding.VoiceControl;

        voice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SSH.executeSSHcommand("python VoiceControl.py");
            }
        });

        SSH.executeSSHcommand("python PrintList.py", textView);

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}