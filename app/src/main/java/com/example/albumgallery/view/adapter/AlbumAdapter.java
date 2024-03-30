package com.example.albumgallery.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private final Context context;
    private final List<String> albumNames;

    public AlbumAdapter(Activity activity, List<String> albumNames) {
        this.context = activity;
        this.albumNames = albumNames;
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
        holder.albumNameTxtView.setText(albumName);
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameTxtView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameTxtView = (TextView) itemView.findViewById(R.id.albumNameItemAlbum);

        }
    }

}
