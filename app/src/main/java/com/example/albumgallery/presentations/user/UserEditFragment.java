package com.example.albumgallery.presentations.user;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.DialogFragment;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.UserController;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.google.android.material.textfield.TextInputEditText;

public class UserEditFragment extends DialogFragment {
    FragToActivityListener listener;
    private UserController userController;
    private String username, email, phone, birth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as a dialog or embedded fragment.
        return inflater.inflate(R.layout.fragment_user_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userController = new UserController(getActivity());
        listener = (FragToActivityListener) getActivity();
        String uid = userController.getFirebaseManager().getFirebaseAuth().getCurrentUser().getUid();

        initiateView(view);
        view.findViewById(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((TextInputEditText) view.findViewById(R.id.edit_username)).getText().toString();
                email = ((TextInputEditText) view.findViewById(R.id.edit_email)).getText().toString();
                phone = ((TextInputEditText) view.findViewById(R.id.edit_phone)).getText().toString();
                birth = ((TextInputEditText) view.findViewById(R.id.edit_date_of_birth)).getText().toString();

                if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || birth.isEmpty()) {
                    return;
                }
                updateField("username", username, uid);
                updateField("email", email, uid);
                updateField("phone", phone, uid);
                updateField("birth", birth, uid);
                dismiss();
                listener.onFragmentAction("update", true);
            }
        });

        view.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initiateView(View view) {
        username = userController.getUser().getUsername();
        email = userController.getUser().getEmail();
        phone = userController.getUser().getPhone();
        birth = userController.getUser().getBirth();
        ((TextInputEditText) view.findViewById(R.id.edit_username)).setText(username);
        ((TextInputEditText) view.findViewById(R.id.edit_email)).setText(email);
        ((TextInputEditText) view.findViewById(R.id.edit_phone)).setText(phone);
        ((TextInputEditText) view.findViewById(R.id.edit_date_of_birth)).setText(birth);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private String getTextFromInput(View view, int id) {
        TextInputEditText inputField = view.findViewById(id);
        return inputField != null ? inputField.getText().toString() : null;
    }

    private void updateField(String fieldName, String value, String uid) {
        Log.v("UserEditFragment", "Updating field: " + fieldName + " with value: " + value + " for UID: " + uid);
        if (value != null) {
            String where = "id = '" + uid + "'";
            userController.update(fieldName, value, where);
        }
    }
}

