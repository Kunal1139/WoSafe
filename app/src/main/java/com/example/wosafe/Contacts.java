package com.example.wosafe;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class Contacts extends Fragment {

    Button save_contact;
    EditText contact1, contact2, contact3;
    ImageView imageView3, imageView4, imageView5;

    private static final int PICK_CONTACT1 = 1;
    private static final int PICK_CONTACT2 = 2;
    private static final int PICK_CONTACT3 = 3;
    private static final int CONTACT_PERMISSION_CODE = 100;

    Set<String> selectedContacts = new HashSet<>(); // To track selected contacts

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set ActionBar title to "Contacts" when ContactsFragment is resumed
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Contacts");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        save_contact = view.findViewById(R.id.save_contact);
        contact1 = view.findViewById(R.id.contact1);
        contact2 = view.findViewById(R.id.contact2);
        contact3 = view.findViewById(R.id.contact3);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView4 = view.findViewById(R.id.imageView4);
        imageView5 = view.findViewById(R.id.imageView5);

        // Load saved contacts from SharedPreferences
        loadSavedContacts();

        // Set up contact pickers
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkContactPermission()) {
                    openContactPicker(PICK_CONTACT1);
                }
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkContactPermission()) {
                    openContactPicker(PICK_CONTACT2);
                }
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkContactPermission()) {
                    openContactPicker(PICK_CONTACT3);
                }
            }
        });

        // Save contacts to SharedPreferences
        save_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c1 = contact1.getText().toString().trim();
                String c2 = contact2.getText().toString().trim();
                String c3 = contact3.getText().toString().trim();

                if (!c1.isEmpty() || !c2.isEmpty() || !c3.isEmpty()) {
                    saveContacts(c1, c2, c3);

                    // Create a new HomePage fragment and pass the contact numbers using a Bundle
                    HomePage fragment1 = new HomePage();
                    Bundle bundle = new Bundle();
                    bundle.putString("contact_number1", c1);
                    bundle.putString("contact_number2", c2);
                    bundle.putString("contact_number3", c3);
                    fragment1.setArguments(bundle);

                    // Switch to the HomePage fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment1); // assuming frame_layout is your container
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    Toast.makeText(getActivity(), "Contacts Saved and Passed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please enter at least one valid contact number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Method to check contact permission
    private boolean checkContactPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    // Method to open contact picker
    private void openContactPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Contact permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Contact permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getData() != null) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);

                    if (phoneCursor != null && phoneCursor.moveToFirst()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // Check if contact is already selected
                        if (selectedContacts.contains(contactName + " (" + phoneNumber + ")")) {
                            Toast.makeText(getActivity(), "Contact already selected for another field", Toast.LENGTH_SHORT).show();
                        } else {
                            selectedContacts.add(contactName + " (" + phoneNumber + ")");

                            // Set contact to appropriate EditText based on requestCode
                            switch (requestCode) {
                                case PICK_CONTACT1:
                                    contact1.setText(contactName + " (" + phoneNumber + ")");
                                    break;
                                case PICK_CONTACT2:
                                    contact2.setText(contactName + " (" + phoneNumber + ")");
                                    break;
                                case PICK_CONTACT3:
                                    contact3.setText(contactName + " (" + phoneNumber + ")");
                                    break;
                            }
                        }
                        phoneCursor.close();
                    }
                }
                cursor.close();
            }
        }
    }

    // Save contacts to SharedPreferences
    private void saveContacts(String c1, String c2, String c3) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contact1", c1);
        editor.putString("contact2", c2);
        editor.putString("contact3", c3);
        editor.apply();
    }

    // Load saved contacts from SharedPreferences
    private void loadSavedContacts() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
        String savedContact1 = sharedPreferences.getString("contact1", "");
        String savedContact2 = sharedPreferences.getString("contact2", "");
        String savedContact3 = sharedPreferences.getString("contact3", "");

        if (!savedContact1.isEmpty()) {
            contact1.setText(savedContact1);
        }
        if (!savedContact2.isEmpty()) {
            contact2.setText(savedContact2);
        }
        if (!savedContact3.isEmpty()) {
            contact3.setText(savedContact3);
        }
    }
}
