package com.example.albumgallery.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.albumgallery.view.activity.BackgroundProcessingCallback;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.adapter.ImageAdapterListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

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
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;

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
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
//      imageAdapter.setImageAdapterListener(this);
        recyclerMediaView = view.findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v -> openCamera());

        Button btnPickImageFromDevice = view.findViewById(R.id.btnPickImageFromDevice);
        Button btnPickMultipleImages = view.findViewById(R.id.btnPickMultipleImages);
        Button btnDeleteMultipleImages = view.findViewById(R.id.btnDeleteMultipleImages);

        numberOfImagesSelected = view.findViewById(R.id.numberOfSelectedImages);

        btnPickImageFromDevice.setOnClickListener(v -> {
            isBackgroundTaskCompleted = false;
            mainController.getImageController().pickMultipleImages(bgListener);
        });

        btnDeleteMultipleImages.setOnClickListener(v -> {
            // mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask, 1);
            showDeleteConfirmationDialog();
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
        if (data != null) {
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

    @SuppressLint("SetTextI18n")
    public void getSelectedItemsCount(int count) {
        Log.v("SelectedItems", count + " items selected");
        numberOfImagesSelected.setText(count + " images selected");

//        List<Task<Uri>> task =  new ArrayList<>();
//        for (String uri : imageURIs) {
//            task.add(Tasks.forResult(Uri.parse(uri)));
//        }

        for (int i = 0; i < count; i++) {
            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
        }

        for (int i = 0; i < count; i++) {
            selectedImageURLs.add(imageURIs.get(i));
            Log.d("Deleted images", selectedImageURLs.get(i));
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask);
            }
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
        startActivity(intent, options.toBundle());
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