package com.dev.testproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.testproject.R;
import com.dev.testproject.model.PhotoItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    Context context;
    ArrayList<PhotoItem> photoItemArrayList;

    public PhotoAdapter(Context context , ArrayList<PhotoItem> photoItemArrayList) {
        this.photoItemArrayList = photoItemArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public PhotoAdapter.PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,null,false);
            return new PhotoHolder(v); }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        PhotoItem currPhoto = photoItemArrayList.get(position);
        holder.photographer.setText(currPhoto.getPhotographer());
        holder.location.setText(currPhoto.getLocation());
        Glide.with(context).load(currPhoto.getImage()).fitCenter().into(holder.shapeableImageView);
    }

    public ArrayList<PhotoItem> getList() {
        return photoItemArrayList;
    }

    @Override
    public int getItemCount() {
        return photoItemArrayList.size();
    }

    public void clearList(){
        photoItemArrayList.clear();
    }

    public void add(PhotoItem photoItem){
        photoItemArrayList.add(photoItem);
        notifyDataSetChanged();
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        ShapeableImageView shapeableImageView;
        TextView photographer;
        TextView location;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = (ShapeableImageView) itemView.findViewById(R.id.main_img);
            photographer = (TextView) itemView.findViewById(R.id.photographer);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }
}