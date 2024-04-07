package com.example.albumgallery.view.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.activity.DetailDeletedPicture;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class BinFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs;
    List<String> selectedImageURLs;
    private ImageAdapter imageAdapter;
    List<Task> selectedImageURLsTask;
    private TextView numberOfImagesSelected;
    private MainController mainController;
    public BinFragment() {
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
        return inflater.inflate(R.layout.fragment_bin, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainController = new MainController(getActivity());
        handleInteractions(view);

        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        imageURIs = mainController.getImageController().getAllDeleteImageRef();
        selectedImageURLsTask = new ArrayList<>();

        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBin);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        // recyclerMediaView = view.findViewById(R.id.recyclerMediaView);

        Button btnPickMultipleImages = view.findViewById(R.id.btnPickMultipleImages);
        Button btnDeleteMultipleImages = view.findViewById(R.id.btnDeleteMultipleImages);
        numberOfImagesSelected = view.findViewById(R.id.numberOfSelectedImages);

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

        btnDeleteMultipleImages.setOnClickListener(v -> {
            // mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask, 1);
            showDeleteConfirmationDialog();
        });
    }

    private void handleInteractions(View view) {
        ImageButton backBtn = (ImageButton) view.findViewById(R.id.backButtonBin);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(getContext(), MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "HomeScreen");
                startActivity(mainFragment);
            }
        });
    }

    public void handleDeletedImagePick(View view, String uri, int position) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent(activity, DetailDeletedPicture.class);
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
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image forever?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImageAtBin(selectedImageURLsTask);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
    public void updateUI() {
        imageURIs.clear();
//        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        // lấy ảnh sort theo date (mới nhất xếp trước).
        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }
}