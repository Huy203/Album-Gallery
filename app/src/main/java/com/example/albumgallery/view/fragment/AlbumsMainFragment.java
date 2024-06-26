package com.example.albumgallery.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.AlbumContentActivity;
import com.example.albumgallery.view.activity.CreateAlbumActivity;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.example.albumgallery.view.activity.PasswordAlbumActivity;
import com.example.albumgallery.view.adapter.AlbumAdapter;
import com.example.albumgallery.view.adapter.AlbumAdapterListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumsMainFragment extends Fragment implements AlbumAdapterListener {
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private List<String> albumNames;
    private List<String> thumbnails;
    private MainController mainController;

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

        initializeData();
        recyclerView = (RecyclerView) view.findViewById(R.id.albumsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        albumAdapter = new AlbumAdapter(getActivity(), albumNames, thumbnails);
        albumAdapter.setAlbumAdapterListener(this);
        recyclerView.setAdapter(albumAdapter);
        albumAdapter.notifyDataSetChanged();

        // Find the included layouts by their IDs
        View favouriteLayout = getView().findViewById(R.id.favourite);
        View binLayout = getView().findViewById(R.id.bin);

        favouriteLayout.setOnClickListener(view1 -> {
            Intent mainFragment = new Intent(getContext(), MainFragmentController.class);
            mainFragment.putExtra("fragmentToLoad", "Favourite");
            startActivity(mainFragment);
        });

        binLayout.setOnClickListener(view1 -> {
            Intent mainFragment = new Intent(getContext(), MainFragmentController.class);
            mainFragment.putExtra("fragmentToLoad", "Bin");
            startActivity(mainFragment);
        });
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
        this.thumbnails = new ArrayList<>();
        albumNames = mainController.getAlbumController().getAlbumNames();
        thumbnails = mainController.getAlbumController().getAllThumbnails();
    }

    @Override
    public void onAlbumClicked(String albumName) {
        Intent intent = new Intent(getContext(), AlbumContentActivity.class);
        intent.putExtra("albumName", albumName);
        String password = mainController.getAlbumController().getPasswordByAlbumName(albumName);
        if (!password.isEmpty()) {
            Intent confirmPassword = new Intent(getContext(), PasswordAlbumActivity.class);
            confirmPassword.putExtra("albumName", albumName);
            startActivity(confirmPassword);
        } else {
            startActivity(intent);
        }
    }
}