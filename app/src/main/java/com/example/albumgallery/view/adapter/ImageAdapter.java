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
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<String> imageURLs;
    private final List<String> ids;
    private final SparseBooleanArray selectedItems;
    private boolean isMultipleChoice = false;
    private ImageAdapterListener listener;

    private List<String> selectedURIs;


    public ImageAdapter(Activity activity, List<String> imageURLs) {
        this.context = activity;
        this.imageURLs = imageURLs;
        this.selectedItems = new SparseBooleanArray();
        this.listener = (ImageAdapterListener) activity;
        this.ids = new ArrayList<>();
        this.selectedURIs = new ArrayList<>();
    }

    public ImageAdapter(Activity activity, List<String> imageURLs, List<String> ids) {
        this.context = activity;
        this.imageURLs = imageURLs;
        this.selectedItems = new SparseBooleanArray();
        this.listener = (ImageAdapterListener) activity;
        this.ids = ids;
        this.selectedURIs = new ArrayList<>();
    }

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
        selectedURIs.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final CheckBox checkbox;
        private final CircularProgressIndicator progressIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkbox = itemView.findViewById(R.id.checkbox);
            progressIndicator = (CircularProgressIndicator) itemView.findViewById(R.id.circularProgressIndicator);
            progressIndicator.setVisibility(View.VISIBLE);
        }

        public void bind(String imageURL, int position) {
            checkbox.setChecked(selectedItems.get(position, false));
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
                            return false;
                        }
                    })
                    .into(imageView);

            itemView.setOnClickListener(view -> {
                if (!isMultipleChoice) {
                    listener.handleImagePick(imageView, imageURL, position);
                } else {
                    toggleSelection(position, imageURL);
                    listener.getInteractedURIs(imageURL);
                    listener.getSelectedItemsCount(selectedItems.size());
                }
            });

            checkbox.setVisibility(isMultipleChoice ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void toggleSelection(int position, String uri) {
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
