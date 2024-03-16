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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.view.activity.DetailPicture;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private final List<String> imagePaths;
    private final SparseBooleanArray selectedItems;
    private boolean isMultipleChoose = false;
    private final ImageAdapterListener listener;

    public ImageAdapter(Activity activity, List<String> imagePaths) {
        this.context = (Context)activity;
        this.imagePaths = imagePaths;
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
        String imagePath = imagePaths.get(position);
        holder.checkbox.setChecked(selectedItems.get(position, false));
        if (imagePath == null) {
            return;
        }
//        Glide.with(context).load(imagePath).into(holder.imageView);
        Glide.with(context).load(Uri.parse(imagePath)).into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            if (!isMultipleChoose) {
                Intent intent = new Intent(view.getContext(), DetailPicture.class);
                intent.putExtra("imagePath", imagePath);
                view.getContext().startActivity(intent);
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

    public void setMultipleChooseEnabled(boolean isMultipleChoose) {
        this.isMultipleChoose = isMultipleChoose;
    }

    public boolean getMultipleChooseImagesEnabled() {
        return isMultipleChoose;
    }

    public int getItemCount() {
        if (imagePaths != null) return imagePaths.size();
        else return 0;
    }

    public boolean toggleMultipleChooseImagesEnabled() {
        Log.v("ImageAdapter", "toggleMultipleChooseImagesEnabled: " + listener);
        if (listener != null) {
            setMultipleChooseEnabled(!isMultipleChoose);
        }
        if (getMultipleChooseImagesEnabled()) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, null);
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.INVISIBLE);
        }
        return isMultipleChoose;
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
}
