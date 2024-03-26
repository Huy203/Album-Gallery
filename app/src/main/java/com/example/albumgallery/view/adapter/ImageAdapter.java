package com.example.albumgallery.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.view.activity.DetailPicture;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context context;
    private final List<String> imageURLs;
    private final SparseBooleanArray selectedItems;
    private boolean isMultipleChoice = false;
    private ImageAdapterListener listener;

    public ImageAdapter(Activity activity, List<String> imageURLs) {
        this.context = activity;
        this.imageURLs = imageURLs;
        this.selectedItems = new SparseBooleanArray();
        this.listener = (ImageAdapterListener) activity;
    }

//    public void setImageAdapterListener(ImageAdapterListener listener) {
//        this.listener = listener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageURL = imageURLs.get(position);
        holder.bind(imageURL, position);
    }

    public void setMultipleChoiceEnabled(boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }

    public boolean getMultipleChoiceImagesEnabled() {
        return isMultipleChoice;
    }

    public int getItemCount() {
        return imageURLs.size();
    }

    public boolean toggleMultipleChoiceImagesEnabled() {
        if (listener != null) {
            setMultipleChoiceEnabled(!isMultipleChoice);
        }
        else{
            Log.v("ImageAdapter", "toggleMultipleChoiceImagesEnabled: listener is null");
        }
        if (getMultipleChoiceImagesEnabled()) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, null);
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.INVISIBLE);
        }
        return isMultipleChoice;
    }

    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkbox = itemView.findViewById(R.id.checkbox);
        }

        public void bind(String imageURL, int position) {
            checkbox.setChecked(selectedItems.get(position, false));
            Glide.with(context).load(Uri.parse(imageURL)).into(imageView);

            itemView.setOnClickListener(view -> {
                if (!isMultipleChoice) {
                    listener.handleImagePick(imageView, imageURL, position);
                } else {
                    toggleSelection(position);
                    listener.getSelectedItemsCount(selectedItems.size());
                }
            });

            checkbox.setVisibility(isMultipleChoice ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    // Xử lý sắp xếp hình ảnh theo date
    public void sortImageByDate() {
        Collections.sort(imageURLs, (path_1, path_2) -> Long.compare(getImageDate(path_1), getImageDate(path_2)));
        notifyDataSetChanged();
    }

    private long getImageDate(String imageURL) {
        File imageFile = new File(imageURL);
        return imageFile.exists() ? imageFile.lastModified() : 0;
    }
}
