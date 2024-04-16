package com.example.albumgallery.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private final List<String> imageURLs;  // List of image URLs
    private List<String> imageURLsFavourited; // List of favourited image URLs
    private final SparseBooleanArray selectedItems; // SparseBooleanArray to store selected items
    private boolean isMultipleChoice = false; // Flag to determine if multiple choice is enabled
    private ImageAdapterListener listener; // Listener to handle image selection

    public ImageAdapter(Activity activity, List<String> imageURLs) {
        this.context = activity;
        this.imageURLs = imageURLs;
        this.selectedItems = new SparseBooleanArray();
        this.listener = (ImageAdapterListener) activity;
        this.imageURLsFavourited =new ArrayList<>();
    }

    public void setImageURLsFavourite(List<String> imageURLsFavourited) {
        this.imageURLsFavourited = imageURLsFavourited;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageURL = getImageURLs().get(position);
        holder.bind(imageURL);
        holder.isLiked.setVisibility(imageURLsFavourited.contains(imageURL) ? View.VISIBLE : View.GONE);
        holder.checkbox.setVisibility(isMultipleChoice ? View.VISIBLE : View.GONE); // Update visibility based on isMultipleChoice
    }

    @Override
    public int getItemCount() {
        return getImageURLs().size();
    }

    public void setMultipleChoiceEnabled(boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }

    public boolean getMultipleChoiceEnabled() {
        return isMultipleChoice;
    }

    public List<String> getImageURLs() {
        return imageURLs;
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.selectedItems.clear();

        for (int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            this.selectedItems.put(key, selectedItems.get(key));
        }
    }
    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public void emptySelectedItems() {
        selectedItems.clear();
    }

    public List<String> getSelectedImageURLs() {
        List<String> selectedImageURLs = new ArrayList<>();
        for (int i = 0; i < getSelectedItems().size(); i++) {
            int key = getSelectedItems().keyAt(i);
            if (getSelectedItems().get(key)) {
                selectedImageURLs.add(getImageURLs().get(key));
            }
        }
        return selectedImageURLs;
    }

    public boolean toggleMultipleChoiceImagesEnabled() {
        if (listener != null) {
            setMultipleChoiceEnabled(!isMultipleChoice);
            notifyDataSetChanged(); // Notify adapter to update views
        } else {
            Log.v("ImageAdapter", "toggleMultipleChoiceImagesEnabled: listener is null");
        }
        return isMultipleChoice;
    }

    public void clearSelectedItems() {
        getSelectedItems().clear();
        isMultipleChoice = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageView isLiked;
        private final CheckBox checkbox;
        private final CircularProgressIndicator progressIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            isLiked = itemView.findViewById(R.id.isLiked);
            checkbox = itemView.findViewById(R.id.checkbox);
            progressIndicator = itemView.findViewById(R.id.circularProgressIndicator);
            progressIndicator.setVisibility(View.VISIBLE);
        }

        public void bind(String imageURL) {
            showImage(imageURL);

            checkbox.setOnClickListener(view -> toggleSelection());

            itemView.setOnClickListener(view -> {
                if (!isMultipleChoice) {
                    listener.handleImagePick(imageView, imageURL, getAdapterPosition());
                } else {
//                    Log.d("justclick", imageURL);
                    listener.getInteractedURIs(imageURL);
                    toggleSelection();
                }
            });

            itemView.setOnLongClickListener(view -> {
                isMultipleChoice = true;
                toggleSelection();
                listener.getInteractedURIs(imageURL);
                return true;
            });
        }

        private void toggleSelection() {
            int position = getAdapterPosition();
            if (getSelectedItems().get(position, false)) {
                checkbox.setChecked(false);
                checkbox.setVisibility(View.GONE);
                getSelectedItems().delete(position);
            } else {
                checkbox.setChecked(true);
                checkbox.setVisibility(View.VISIBLE);
                getSelectedItems().put(position, true);
            }
            listener.toggleMultipleChoice();
        }

        private void showImage(String imageURL) {
            if (SharePreferenceHelper.isGridLayoutEnabled(context)) {
                Glide.with(context)
                        .load(Uri.parse(imageURL))
                        .override(Target.SIZE_ORIGINAL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                progressIndicator.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                progressIndicator.setVisibility(View.GONE);
                                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                imageView.setAdjustViewBounds(true);
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(Uri.parse(imageURL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                progressIndicator.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                progressIndicator.setVisibility(View.GONE);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setAdjustViewBounds(false);
                                return false;
                            }
                        })
                        .into(imageView);
            }
        }
    }
}

//
//    // Xử lý sắp xếp hình ảnh theo date
//    public void sortImageByDate() {
//        Collections.sort(getImageURLs(), (path_1, path_2) -> Long.compare(getImageDate(path_1), getImageDate(path_2)));
//        notifyDataSetChanged();
//    }
//
//    private long getImageDate(String imageURL) {
//        File imageFile = new File(imageURL);
//        return imageFile.exists() ? imageFile.lastModified() : 0;
//    }

