package com.example.albumgallery.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.view.activity.DetailPicture;
import com.example.albumgallery.view.activity.HomeScreen;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private final List<String> imageURLs;
    private final SparseBooleanArray selectedItems;
    private boolean isMultipleChoice = false;
    private final ImageAdapterListener listener;

    public ImageAdapter(Activity activity, List<String> imageURLs) {
        this.context = (Context)activity;
        this.imageURLs = imageURLs;
        this.selectedItems = new SparseBooleanArray();
        this.listener = (ImageAdapterListener)activity;
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
        holder.checkbox.setChecked(selectedItems.get(position, false));
        if (imageURL == null) {
            return;
        }
//        Glide.with(context).load(imageURL).into(holder.imageView);
        Glide.with(context).load(Uri.parse(imageURL)).into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            if (!isMultipleChoice) {
                Intent intent = new Intent(view.getContext(), DetailPicture.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, holder.imageView, "image");
                intent.putExtra("imageURL", imageURL);
                view.getContext().startActivity(intent, options.toBundle());
            } else {
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    holder.checkbox.setChecked(false);
                    holder.checkbox.setVisibility(View.INVISIBLE);
                } else {
                    selectedItems.put(position, true);
                    holder.checkbox.setChecked(true);
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
                System.out.println("Selected items: " + selectedItems);
                listener.getSelectedItemsCount(selectedItems.size());
            }
        });
    }

    public void setMultipleChoiceEnabled(boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }

    public boolean getMultipleChoiceImagesEnabled() {
        return isMultipleChoice;
    }

    public int getItemCount() {
        if (imageURLs != null) return imageURLs.size();
        else return 0;
    }

    public boolean toggleMultipleChoiceImagesEnabled() {
        Log.v("ImageAdapter", "toggleMultipleChoiceImagesEnabled: " + listener);
        if (listener != null) {
            setMultipleChoiceEnabled(!isMultipleChoice);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
    // Xử lý sắp xếp hình ảnh theo date
    public void sortImageByDate() {
        Collections.sort(imageURLs, new Comparator<String>() {
            @Override
            public int compare(String path_1, String path_2) {
                long date_1 = getImageDate(path_1);
                long date_2 = getImageDate(path_2);
                return Long.compare(date_1, date_2);
            }
        });
    }

    private long getImageDate(String imageURL) {
        File imageFile = new File(imageURL);
        if(imageFile.exists()) {
            return imageFile.lastModified();
        } else {
            return 0;
        }
    }
}
