package com.example.albumgallery.view.fragment;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_DETAIL_IMAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1000;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    private TextView numberOfImagesSelected;
    private ImageAdapterListener iListener;
    private BackgroundProcessingCallback bgListener;
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;
    private View view;

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
            throw new RuntimeException(context
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
        Log.v("HomeScreenFragment", "onViewCreated");
        initializeVariables(view);
        setupButtons();
        updateUI();
    }

    private void initializeVariables(View view) {
        this.view = view;
        mainController = new MainController(getActivity());
        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView = view.findViewById(R.id.recyclerMediaView);
        numberOfImagesSelected = view.findViewById(R.id.numberOfSelectedImages);
    }

    private void setupButtons() {
        view.findViewById(R.id.btnCamera).setOnClickListener(v -> openCamera());
        view.findViewById(R.id.action_add).setOnClickListener(v -> pickImagesFromDevice());
        view.findViewById(R.id.btnDeleteMultipleImages).setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void pickImagesFromDevice() {
        mainController.getImageController().pickMultipleImages(bgListener);
    }

    public void toggleMultipleChoice(int length) {
        if (imageAdapter.getMultipleChoiceImagesEnabled()) {
            numberOfImagesSelected.setVisibility(TextView.VISIBLE);
            numberOfImagesSelected.setText("0 images selected");
        }
        if(length == 0) {
            numberOfImagesSelected.setVisibility(TextView.GONE);
            imageAdapter.clearSelectedItems();
        }
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
        getActivity();
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mainController.getImageController().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_CODE_DETAIL_IMAGE && resultCode == getActivity().RESULT_OK) {
            boolean isUpdate = data.getBooleanExtra("update", false);
            if (isUpdate) {
                updateUI();
            }

        } else {
            mainController.getImageController().onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateUI() {
        imageURIs.clear();
        // lấy ảnh sort theo date (mới nhất xếp trước).
//        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    public void getSelectedItemsCount(int count) {
        numberOfImagesSelected.setText(count + " images selected");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
        startActivityForResult(intent, REQUEST_CODE_DETAIL_IMAGE, options.toBundle());
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
        Log.v("HomeScreenFragment", "onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainController = null;
        Log.v("HomeScreenFragment", "onDestroy");
    }
}