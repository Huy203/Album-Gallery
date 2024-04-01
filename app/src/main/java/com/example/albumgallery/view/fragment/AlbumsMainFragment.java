package com.example.albumgallery.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.CreateAlbumActivity;
import com.example.albumgallery.view.adapter.AlbumAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlbumsMainFragment extends Fragment {
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    Bundle bundle;
    List<String> test;
    List<String> albumNames;
    MainController mainController;

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
        mainController = new MainController(getActivity());
        handleInteractions(view);

        initializeData(); // just for testing

        recyclerView = (RecyclerView) view.findViewById(R.id.albumsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        albumAdapter = new AlbumAdapter(getActivity(), albumNames);
        recyclerView.setAdapter(albumAdapter);
    }

    private void handleInteractions(View view) {
        ImageButton btnCreateAlbum = (ImageButton) view.findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CreateAlbumActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void initializeData() {
        this.albumNames = new ArrayList<>();
        albumNames = mainController.getAlbumController().getAlbumNames();
    }
}