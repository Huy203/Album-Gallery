package com.example.albumgallery.presentations.bin;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_DETAIL_IMAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.example.albumgallery.view.activity.MainFragmentController;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class BinFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<String> imageURIs;
    List<String> selectedImageURLs;
    private ImageAdapter imageAdapter;
    List<Task> selectedImageURLsTask;
    private TextView numberOfImagesSelected;
    private MainController mainController;
    private FragToActivityListener fragToActivityListener;
    private boolean isSelectAll = false;

    public BinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageAdapterListener && context instanceof BackgroundProcessingCallback && context instanceof FragToActivityListener) {
            fragToActivityListener = (FragToActivityListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement Interface");
        }
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
        imageURIs = mainController.getImageController().getAllImageURLsDeleted();

        selectedImageURLsTask = new ArrayList<>();

        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBin);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();

        numberOfImagesSelected = view.findViewById(R.id.numberOfSelectedImages);
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
        if (imageURIs.contains(uri)) {
            Log.v("BinFragment", "Image found: " + uri);
            String id = mainController.getImageController().getIdByRef(uri);
            intent.putExtra("id", id);
            intent.putExtra("position", position);
            if (options != null) {
                startActivityForResult(intent, REQUEST_CODE_DETAIL_IMAGE, options.toBundle());
            }
        } else {
            Log.v("HomeScreenFragment", "Image not found: " + uri);
        }
    }

//    private void showDeleteConfirmationDialog() {
//        Log.d("size of image urls before delete", String.valueOf(selectedImageURLsTask.size()));
//        for (Task taskImageURL : selectedImageURLsTask) {
//            String imageURL = taskImageURL.getResult().toString();
//            Log.d("image url", imageURL);
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Confirm Deletion");
//        builder.setMessage("Are you sure you want to delete this image forever?");
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Call deleteSelectedImage() method from ImageController
//                mainController.getImageController().deleteSelectedImageAtBin(selectedImageURLsTask);
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }

    public void showDeleteConfirmationDialog() {
        if (isAdded() && getActivity() != null) {
            if (!getActivity().isFinishing()) {
                getActivity().runOnUiThread(() -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    Log.v("HomeScreenFragment", "showDeleteConfirmationDialog" + getActivity());

                    builder.setTitle("Confirm Deletion");
                    builder.setMessage("Are you sure you want to delete this image forever?");

                    builder.setPositiveButton("Delete", (dialog, which) -> {
                        selectedImageURLsTask = imageAdapter.getSelectedImageURLsTask();

                        mainController.getImageController().deleteSelectedImageAtBin(selectedImageURLsTask);

                        imageAdapter.clearSelectedItems();
                        onResume();
                        fragToActivityListener.onFragmentAction("Delete", true);
                        updateUI();
//                        this.mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask);
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void getSelectedItemsCount(int count) {
        Log.v("SelectedItems", count + " items selected");
        // numberOfImagesSelected.setText(count + " images selected");

        selectedImageURLsTask.clear();
        selectedImageURLs.clear();

        for (int i = 0; i < count; i++) {
            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
        }

        for (int i = 0; i < count; i++) {
            selectedImageURLs.add(imageURIs.get(i));
        }
    }

    public void updateUI() {
        imageURIs.clear();
//        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        // lấy ảnh sort theo date (mới nhất xếp trước).
        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDateAtBin());
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    public boolean toggleMultipleChoice() {
        int length = imageAdapter.getSelectedItems().size(); // get the number of selected items
        fragToActivityListener.onFragmentAction("ShowMultipleChoice", length);

        // if no items are selected, clear the selected items and return false
        Log.v("BinFragment", "Length " + length);
        if (length == 0) {
            Log.v("HomeScreenFragment", "No items selected");
            imageAdapter.clearSelectedItems();
            return false;
        }
        return true;
    }

    public void ActivityToFragListener(String action) {
        switch (action) {
            case "Delete":
                showDeleteConfirmationDialog();
                onPause();
                break;
            case "Restore":
                break;
        }
    }
}