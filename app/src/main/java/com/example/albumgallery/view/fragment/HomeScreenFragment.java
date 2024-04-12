package com.example.albumgallery.view.fragment;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_DETAIL_IMAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.albumgallery.model.auth.AuthenticationManager;
import com.example.albumgallery.model.auth.AuthenticationManagerSingleton;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.activity.LoginScreen;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1000;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    private ImageAdapterListener iListener;
    private BackgroundProcessingCallback bgListener;
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;
    private View view;

    private FragToActivityListener fragToActivityListener;

    private AuthenticationManager authManager = AuthenticationManagerSingleton.getInstance();

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ImageAdapterListener && context instanceof BackgroundProcessingCallback && context instanceof FragToActivityListener) {
            iListener = (ImageAdapterListener) context;
            bgListener = (BackgroundProcessingCallback) context;
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
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

//    private void setupButtons() {
//        view.findViewById(R.id.btnCamera).setOnClickListener(v -> openCamera());
//        view.findViewById(R.id.btnPickImageFromDevice).setOnClickListener(v -> pickImagesFromDevice());
//        view.findViewById(R.id.btnPickMultipleImages).setOnClickListener(v -> showDeleteConfirmationDialog());
//        view.findViewById(R.id.btnDeleteMultipleImages).setOnClickListener(v -> toggleMultipleChoiceImages(view.findViewById(R.id.btnDeleteMultipleImages)));
//    }

    private void setupButtons() {
//        view.findViewById(R.id.btnCamera).setOnClickListener(v -> openCamera());
//        view.findViewById(R.id.action_add).setOnClickListener(v -> pickImagesFromDevice());
//        view.findViewById(R.id.action_delete).setOnClickListener(v -> showDeleteConfirmationDialog());
        view.findViewById(R.id.btnLogOut).setOnClickListener(v -> logOut());
    }

//    private void pickImagesFromDevice() {
////        mainController.getImageController().pickMultipleImages(bgListener);
//    }

    public boolean toggleMultipleChoice() {
        int length = imageAdapter.getSelectedItems().size(); // get the number of selected items
        fragToActivityListener.onFragmentAction("ShowMultipleChoice", length);

        // if no items are selected, clear the selected items and return false
        if (length == 0) {
            Log.v("HomeScreenFragment", "No items selected");
            imageAdapter.clearSelectedItems();
            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void getSelectedItemsCount(int count) {
        for (int i = 0; i < count; i++) {
            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
        }

        for (int i = 0; i < count; i++) {
            selectedImageURLs.add(imageURIs.get(i));
            Log.d("Deleted images", selectedImageURLs.get(i));
        }
    }

    private void logOut() {
        // Thực hiện đăng xuất
        if(authManager!=null){
            authManager.signOut(new AuthenticationManager.OnLogoutListener() {
                @Override
                public void onSuccess() {
                    // Đăng xuất thành công, thực hiện các hành động cần thiết (ví dụ: chuyển hướng đến màn hình đăng nhập)
                    Intent intent = new Intent(getActivity(), LoginScreen.class);
                    startActivity(intent);
                    getActivity().finish(); // Đóng màn hình hiện tại (HomeScreenFragment)
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý khi đăng xuất thất bại (nếu cần)
                    Toast.makeText(getActivity(), "Logout failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        Log.v("HomeScreenFragment", "updateUI");
        imageURIs.clear();
        // lấy ảnh sort theo date (mới nhất xếp trước).
        //imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageURIs.addAll(mainController.getImageController().getAllImageURLsUndeleted());
        imageAdapter = new ImageAdapter(getActivity(), imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
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
                            long id = mainController.getImageController().getIdByRef(uri);
                            mainController.getImageController().setDelete(id, true);
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

    public List<Task> getSelectedImageURLsTask() {
        return selectedImageURLsTask;
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
            case "Camera":
                openCamera();
                break;
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
        }
    }
}