package com.example.albumgallery.view.fragment;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_DETAIL_IMAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;
    private View view;
    private SearchView searchView;
    private FragToActivityListener fragToActivityListener;
    private boolean isSelectAll = false;

    public SearchFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);
        updateUI();
    }

    private void initializeVariables(View view) {
        mainController = new MainController(getActivity());
        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView = view.findViewById(R.id.recyclerMediaView);
        this.view = view;

        MaterialButton changeGridViewBtn = view.findViewById(R.id.changeGridViewBtn);
        MaterialButton unChooseBtn = view.findViewById(R.id.unChooseBtn);
        MaterialButton tickBtn = view.findViewById(R.id.tickBtn);

        changeGridViewBtn.setOnClickListener(this::changeViewAction);
        tickBtn.setOnClickListener(view1 -> {
            choiceAll(view);
            if (imageAdapter.getMultipleChoiceEnabled()) {
                unChooseBtn.setVisibility(View.VISIBLE);
            } else {
                unChooseBtn.setVisibility(View.GONE);
            }
        });
        unChooseBtn.setOnClickListener(view1 -> {
            choiceAll(view);
            unChooseBtn.setVisibility(View.GONE);
        });

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits the query (e.g., presses Enter)
                searchImages(query);
                Log.d("query seach", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
//                searchImages(newText);
//                Log.d("new text seach", newText);
                return true;
            }
        });
    }

    private void changeViewAction(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, getResources().getString(R.string.square_grid));
        popupMenu.getMenu().add(Menu.NONE, 1, 1, getResources().getString(R.string.ratio_grid));
        popupMenu.getMenu().add(Menu.NONE, 2, 2, getResources().getString(R.string.full_screen_grid));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    // default grid view
                    SharePreferenceHelper.setGridLayoutEnabled(requireContext(), "default");
                    updateUI();
                    return true;
                case 1:
                    // ratio grid view
                    SharePreferenceHelper.setGridLayoutEnabled(requireContext(), "ratio");
                    updateUI();
                    return true;
                case 2:
                    // full screen grid view
                    SharePreferenceHelper.setGridLayoutEnabled(requireContext(), "full");
                    updateUI();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void choiceAll(View view) {
        isSelectAll = !isSelectAll;
        MaterialButton tickBtn = view.findViewById(R.id.tickBtn);
        SparseBooleanArray selectedItems = new SparseBooleanArray();
        if (isSelectAll) {
            tickBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            tickBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_200)));
            for (int i = 0; i < imageURIs.size(); i++) {
                selectedItems.put(i, true);
            }
        } else {
            if (SharePreferenceHelper.isDarkModeEnabled(requireContext())) {
                tickBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                tickBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.none)));
            } else {
                tickBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                tickBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.none)));
            }
        }
        imageAdapter.setMultipleChoiceEnabled(isSelectAll);
        imageAdapter.setSelectedItems(selectedItems);
        fragToActivityListener.onFragmentAction("SelectAll", true);
        imageAdapter.notifyItemRangeChanged(0, imageURIs.size());
    }

    public boolean toggleMultipleChoice() {
        int length = imageAdapter.getSelectedItems().size(); // get the number of selected items
        fragToActivityListener.onFragmentAction("ShowMultipleChoice", length);

        MaterialButton unChooseBtn = view.findViewById(R.id.unChooseBtn);
        if (imageAdapter.getMultipleChoiceEnabled()) {
            unChooseBtn.setVisibility(View.VISIBLE);
            isSelectAll = true;
        }
        // if no items are selected, clear the selected items and return false
        if (length == 0) {
            Log.v("HomeScreenFragment", "No items selected");
            imageAdapter.clearSelectedItems();
            unChooseBtn.setVisibility(View.GONE);
            isSelectAll = false;
            return false;
        }
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        Toast.makeText(requireContext(), "onResume of Homesscreen", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    public void onPause() {
        Toast.makeText(requireContext(), "onPause of Homesscreen", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    public void updateUI() {
        Log.v("HomeScreenFragment", "updateUI");
        String searchText = searchView.getQuery().toString().trim();
        if (!searchText.isEmpty()) {
            // If the search box contains text, perform the search
            searchImages(searchText);
        } else {
            // If the search box is empty, keep imageURIs empty
            imageURIs.clear();
        }

        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        // Switch to list display
        if (SharePreferenceHelper.isGridLayoutEnabled(getActivity()).equals("full")) {
            recyclerMediaView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerMediaView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.toggleAll();
    }

    public void showDeleteConfirmationDialog() {
        if (isAdded() && getActivity() != null) {
            if (!getActivity().isFinishing()) {
                getActivity().runOnUiThread(() -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    Log.v("HomeScreenFragment", "showDeleteConfirmationDialog" + getActivity());

                    builder.setTitle("Confirm Deletion");
                    builder.setMessage("Are you sure you want to delete this image?");

                    builder.setPositiveButton("Delete", (dialog, which) -> {
                        for (String uri : imageAdapter.getSelectedImageURLs()) {
                            String id = mainController.getImageController().getIdByRef(uri);
                            if (id != null) {
                                mainController.getImageController().setDelete(id, true);
                            }
                        }
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

    public void handleImagePick(View view, String uri, int position) {
        FragmentActivity activity = getActivity();
        Intent intent = new Intent(activity, DetailPicture.class);
        ActivityOptionsCompat options = null;
        if (activity != null) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "image");
        }
        if (imageURIs.contains(uri)) {
            Log.v("HomeScreenFragment", "Image selected: " + uri);
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

    public void ActivityToFragListener(String action) {
        switch (action) {
            case "Delete":
                showDeleteConfirmationDialog();
                onPause();
                break;
//            case "Share":
////                Intent intent = new Intent(getActivity(), MainFragmentController.class);
////                intent.putExtra("key", data); // Replace "key" with your desired key
////                getActivity().startActivity(intent);
            case "Select":
//                pickImagesFromDevice();
                break;
            case "Share":
                List<Uri> tempUri = new ArrayList<>();
                for (String url : imageAdapter.getSelectedImageURLs()) {
                    tempUri.add(Uri.parse(url));
                }
                // Frag to activity listener
                fragToActivityListener.onFragmentAction("Share", tempUri);
                break;
            case "Like":
                for (String url : imageAdapter.getSelectedImageURLs()) {
                    String id = mainController.getImageController().getIdByRef(url);
                    mainController.getImageController().toggleFavoriteImage(id);
                }
                imageAdapter.clearSelectedItems();
                fragToActivityListener.onFragmentAction("Like", true);
                updateUI();
                break;
            case "Add":
                break;
        }
    }

    public void searchImages(String query) {
        imageURIs.clear();
        imageURIs.addAll(mainController.getImageController().selectImagesByNotice(query));
        Log.d("Size of image uris", String.valueOf(imageURIs.size()));
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }
}