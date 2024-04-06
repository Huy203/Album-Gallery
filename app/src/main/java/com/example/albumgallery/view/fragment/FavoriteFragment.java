package com.example.albumgallery.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.example.albumgallery.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<String> imageURIs;
    private ImageAdapter imageAdapter;
    private MainController mainController;
    public FavoriteFragment() {
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
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainController = new MainController(getActivity());
        handleInteractions(view);

        imageURIs = new ArrayList<>();
        imageURIs = mainController.getImageController().getAllFavoriteImageRef();

        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewFavorite);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    private void handleInteractions(View view) {
        ImageButton backBtn = (ImageButton) view.findViewById(R.id.backButtonFavorite);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(getContext(), MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "HomeScreen");
                startActivity(mainFragment);
            }
        });
    }

    public void handleImagePick(View view, String uri, int position) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent(activity, DetailPicture.class);
        ActivityOptionsCompat options = null;
        if (activity != null) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "image");
        }
        long id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        Log.v("ImageAdapter", "Image selected: " + view);
        startActivity(intent, options.toBundle());
    }
}