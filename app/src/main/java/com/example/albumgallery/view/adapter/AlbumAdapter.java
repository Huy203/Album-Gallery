package com.example.albumgallery.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private final Context context;
    private final List<String> albumNames;
    private final List<String> thumbnails;
    private AlbumAdapterListener listener;

    public AlbumAdapter(Activity activity, List<String> albumNames, List<String> thumbnails) {
        this.context = activity;
        this.albumNames = albumNames;
        this.thumbnails = thumbnails;
    }

    public void setAlbumAdapterListener(AlbumAdapterListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String albumName = albumNames.get(position);
        String thumbnail = thumbnails.get(position);
        holder.albumNameTxtView.setText(albumName);
        Glide.with(context)
                .load(thumbnail)
                .placeholder(R.drawable.baseline_add_24)
                .error(R.drawable.back_arrow)
                .into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        if(albumNames != null) {
            return albumNames.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameTxtView;
        ImageView thumbnailImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameTxtView = (TextView) itemView.findViewById(R.id.albumNameItemAlbum);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.albumThumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String albumName = albumNames.get(position);
                            listener.onAlbumClicked(albumName);
                        }
                    }
                }
            });
        }
    }

}
