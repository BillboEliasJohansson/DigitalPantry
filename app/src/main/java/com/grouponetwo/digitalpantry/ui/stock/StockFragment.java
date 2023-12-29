package com.grouponetwo.digitalpantry.ui.stock;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.grouponetwo.digitalpantry.R;
import com.grouponetwo.digitalpantry.databinding.FragmentHomeBinding;
import com.grouponetwo.digitalpantry.ui.scanReceipt.SSH;

public class StockFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FloatingActionButton voice;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.stockTextView;
        voice = binding.VoiceControl;
        textView.setMovementMethod(new ScrollingMovementMethod());

        Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "The Pantry is refreshing...", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        voice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SSH.executeSSHcommand("export OPENAI_API_KEY=\"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\"; python VoiceControl.py");
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Please Wait...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    voice.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NavController navController = Navigation.findNavController(container);
                            navController.navigate(R.id.navigation_stock);
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