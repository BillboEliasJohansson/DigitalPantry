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
import com.grouponetwo.digitalpantry.databinding.FragmentStockBinding;
import com.grouponetwo.digitalpantry.ui.scanReceipt.SSH;

public class StockFragment extends Fragment {

    private FragmentStockBinding binding;
    private FloatingActionButton voiceButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView stockTextView = binding.stockTextView;
        voiceButton = binding.VoiceControl;
        stockTextView.setMovementMethod(new ScrollingMovementMethod());

        Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "The Pantry is refreshing...", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        voiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SSH.executeSSHcommand("export OPENAI_API_KEY=\"APIKEYPLACEAHOLDER\"; python VoiceControl.py");
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "Please Wait...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    voiceButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

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

        SSH.executeSSHcommand("python PrintList.py", stockTextView);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}