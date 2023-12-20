package com.grouponetwo.digitalpantry.ui.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grouponetwo.digitalpantry.R;
import com.grouponetwo.digitalpantry.databinding.FragmentHomeBinding;
import com.grouponetwo.digitalpantry.ui.dashboard.SSH;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FloatingActionButton voice;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.stockTextView;
        voice = binding.VoiceControl;

        voice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SSH.executeSSHcommand("export OPENAI_API_KEY=\"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\"; python VoiceControl.py");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    voice.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NavController navController = Navigation.findNavController(container);
                            navController.navigate(R.id.navigation_home);
                        }
                    }, 5000);
                }
            }, 8000);
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