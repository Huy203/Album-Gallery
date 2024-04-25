package com.example.albumgallery.view.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.albumgallery.R;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.listeners.ImageInfoListener;

public class ImageInfo extends Fragment {
    private ImageModel image;
    private ImageInfoListener listener;

    public ImageInfo() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageInfoListener) {
            listener = (ImageInfoListener) context;
        } else {
            throw new RuntimeException(context + " must implement ImageInfoListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_info, container, false);
        TextView name = view.findViewById(R.id.imageName);
        TextView size = view.findViewById(R.id.imageSize);
        TextView capacity = view.findViewById(R.id.imageCapacity);
        TextView created_at = view.findViewById(R.id.imageCreatedAt);
        EditText notice = view.findViewById(R.id.imageNotice);
        TextView editImageTime = view.findViewById(R.id.editImageTime);
        EditText imageCreatedAt = view.findViewById(R.id.imageCreatedAt);

        editImageTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCreatedAt.setEnabled(true);
                imageCreatedAt.setFocusableInTouchMode(true);
                imageCreatedAt.setFocusable(true);
                imageCreatedAt.requestFocus(); // Set focus to the EditText
            }
        });

        imageCreatedAt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(imageCreatedAt.getWindowToken(), 0);

                String editedText = imageCreatedAt.getText().toString();
                // Send the edited text to the activity
                sendTimeDataToActivity(editedText);
                return true;
            }
            return false;
        });

        notice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(notice.getWindowToken(), 0);

                // Save notice to database
                sendDataToActivity(notice.getText().toString());
                return true;
            }
            return false;
        });

        try {
            name.setText(image.getName());
            size.setText(String.format("%dx%d", image.getWidth(), image.getHeight()));

            if (image.getCapacity() > 1024) {
                capacity.setText(image.getCapacity() / 1024 + " KB");
            } else if (image.getCapacity() > 1024 * 1024) {
                capacity.setText(image.getCapacity() / (1024 * 1024) + " MB");
            } else if (image.getCapacity() > 1024 * 1024 * 1024) {
                capacity.setText(image.getCapacity() / (1024 * 1024 * 1024) + " GB");
            } else capacity.setText(image.getCapacity() + " bytes");
            created_at.setText(image.getCreated_at());
            notice.setText(image.getNotice());
            return view;
        } catch (Exception e) {
            Log.e("ImageInfo", "Error loading image info: " + e.getMessage());
            return null;
        }
    }

    public void setImage(ImageModel image) {
        this.image = image;
    }

    private void sendDataToActivity(String data) {
        if (listener != null) {
            listener.onNoticePassed(data);
        }
    }

    private void sendTimeDataToActivity(String data) {
        if (listener != null) {
            listener.onTimePassed(data);
        }
    }
}