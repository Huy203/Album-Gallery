package com.example.albumgallery.view.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.albumgallery.R;
import com.example.albumgallery.view.activity.CreateAlbumActivity;

public class AlbumsMainFragment extends Fragment {
    public AlbumsMainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton btnCreateAlbum = (ImageButton) view.findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CreateAlbumActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


    }
}