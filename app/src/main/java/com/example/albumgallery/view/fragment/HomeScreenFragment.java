package com.example.albumgallery.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.BackgroundProcessingCallback;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.adapter.ImageAdapterListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreenFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1000;
    private boolean isBackgroundTaskCompleted = true;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    private TextView numberOfImagesSelected;
    private ImageAdapterListener iListener;
    private BackgroundProcessingCallback bgListener;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageAdapterListener && context instanceof BackgroundProcessingCallback) {
            iListener = (ImageAdapterListener) context;
            bgListener = (BackgroundProcessingCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bổ sung
        // mainController
        mainController = new MainController(getActivity());
        // imageURIs
        imageURIs = new ArrayList<>();
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
//      imageAdapter.setImageAdapterListener(this);
        recyclerMediaView = view.findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v -> openCamera());

        Button btnPickImageFromDevice = view.findViewById(R.id.btnPickImageFromDevice);
        Button btnPickMultipleImages = view.findViewById(R.id.btnPickMultipleImages);

        numberOfImagesSelected = view.findViewById(R.id.numberOfSelectedImages);

        btnPickImageFromDevice.setOnClickListener(v -> {
            isBackgroundTaskCompleted = false;
            mainController.getImageController().pickMultipleImages(bgListener);
        });

        btnPickMultipleImages.setOnClickListener(v -> {
            if (imageAdapter.toggleMultipleChoiceImagesEnabled()) {
                btnPickMultipleImages.setText("Cancel");
                numberOfImagesSelected.setVisibility(TextView.VISIBLE);
                numberOfImagesSelected.setText("0 images selected");
            } else {
                btnPickMultipleImages.setText("Select");
                numberOfImagesSelected.setVisibility(TextView.GONE);
                imageAdapter.clearSelectedItems();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        Toast.makeText(requireContext(), "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (data.getData() == null) {
                Log.d("Check data", "is null");
            } else {
                Log.d("Check data", "is not null");
            }
        }
        mainController.getImageController().onActivityResult(requestCode, resultCode, data);
    }

    public void updateUI() {
        imageURIs.clear();
//        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        // lấy ảnh sort theo date (mới nhất xếp trước).
        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    public void getSelectedItemsCount(int count) {
        numberOfImagesSelected.setText(count + " images selected");
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iListener = null;
        bgListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainController = null;
    }
}