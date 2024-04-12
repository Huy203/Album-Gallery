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
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.AlbumInfoListener;

public class AlbumInfo extends Fragment {
    private AlbumModel album;
    private AlbumInfoListener listener;
    public AlbumInfo(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AlbumInfoListener) {
            listener = (AlbumInfoListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AlbumInfoListener");
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.album_info, container, false);
        TextView name = view.findViewById(R.id.albumName);
        TextView capacity = view.findViewById(R.id.albumCapacity);
        TextView created_at = view.findViewById(R.id.albumCreatedAt);
        EditText notice = view.findViewById(R.id.albumNotice);
        TextView addLocation = view.findViewById(R.id.albumLocation);

        addLocation.setOnClickListener(v -> {
            // Add location
            Log.v("AlbumInfo", "Add location");
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
        name.setText(album.getName());

        if (album.getCapacity() > 1024) {
            capacity.setText(album.getCapacity() / 1024 + " KB");
        } else if (album.getCapacity() > 1024 * 1024) {
            capacity.setText(album.getCapacity() / (1024 * 1024) + " MB");
        } else if (album.getCapacity() > 1024 * 1024 * 1024) {
            capacity.setText(album.getCapacity() / (1024 * 1024 * 1024) + " GB");
        } else capacity.setText(album.getCapacity() + " bytes");

        created_at.setText(album.getCreated_at());
        if(album.getNotice() != ""){
            notice.setText(album.getNotice());
        }
        else{
            notice.setText("album.getNotice()");
        }

        Log.d("album notice", album.getNotice());

        return view;
    }
    public void setAlbumInfo(AlbumModel album) {
        this.album = album;
        Log.d("album info notice", album.getNotice());
    }

    private void sendDataToActivity(String data) {
        if (listener != null) {
            listener.onNoticePassed(data);
        }
    }
}
