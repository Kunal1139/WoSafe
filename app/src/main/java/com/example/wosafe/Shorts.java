package com.example.wosafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Shorts extends Fragment {

    private EditText editTextCabDriver, editTextVehicleNumber, editTextPickup, editTextDestination, editTextStop;
    private Button buttonSelectRoute, buttonReset, buttonStart;
    private ImageView imageViewPickup, imageViewDestination, imageViewStop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shorts, container, false);

        // Initialize views
        editTextCabDriver = view.findViewById(R.id.editTextText2);
        editTextVehicleNumber = view.findViewById(R.id.editTextText3);
        editTextPickup = view.findViewById(R.id.editTextText5);
        editTextDestination = view.findViewById(R.id.editTextText);
        editTextStop = view.findViewById(R.id.editTextText4);

        buttonSelectRoute = view.findViewById(R.id.button);
        buttonReset = view.findViewById(R.id.button2);
        buttonStart = view.findViewById(R.id.button3);

        imageViewPickup = view.findViewById(R.id.imageView7);
        imageViewDestination = view.findViewById(R.id.imageView9);
        imageViewStop = view.findViewById(R.id.imageView8);

        // Open MapsFragment when icons are clicked
        imageViewPickup.setOnClickListener(v -> replaceFragment(new MapsFragment()));

        imageViewDestination.setOnClickListener(v -> replaceFragment(new MapsFragment()));

        imageViewStop.setOnClickListener(v -> replaceFragment(new MapsFragment()));

        // Handle Reset button
        buttonReset.setOnClickListener(v -> resetFields());

        // Handle Start button
        buttonStart.setOnClickListener(v -> startRoute());

        // Select Route (Optional – add your logic here)
        buttonSelectRoute.setOnClickListener(v ->
                Toast.makeText(getContext(), "Select Route Clicked", Toast.LENGTH_SHORT).show()
        );

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        // This is how you replace fragments inside a fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);  // Make sure your activity has a frame_layout
        transaction.addToBackStack(null);  // Optional: adds to back stack
        transaction.commit();
    }

    private void resetFields() {
        editTextCabDriver.setText("");
        editTextVehicleNumber.setText("");
        editTextPickup.setText("");
        editTextDestination.setText("");
        editTextStop.setText("");
        Toast.makeText(getContext(), "Fields Reset", Toast.LENGTH_SHORT).show();
    }

    private void startRoute() {
        if (editTextCabDriver.getText().toString().isEmpty() ||
                editTextVehicleNumber.getText().toString().isEmpty() ||
                editTextPickup.getText().toString().isEmpty() ||
                editTextDestination.getText().toString().isEmpty()) {

            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Starting Route", Toast.LENGTH_SHORT).show();
            // Add navigation logic if required
        }
    }
}
