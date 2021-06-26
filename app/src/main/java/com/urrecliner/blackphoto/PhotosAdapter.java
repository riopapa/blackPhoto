package com.urrecliner.blackphoto;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.photosAdapter;
import static com.urrecliner.blackphoto.Vars.photos;
import static com.urrecliner.blackphoto.Vars.spanWidth;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iVImage, ivCheck;

        ViewHolder(final View itemView) {
            super(itemView);
            iVImage = itemView.findViewById(R.id.photosImage);
            iVImage.setOnClickListener(view -> {
                toggleCheckBox(getAdapterPosition());
            });

            iVImage.setOnLongClickListener(view -> {
                    showBigPhoto();
                return true;
            });
            ivCheck = itemView.findViewById(R.id.select);
            ivCheck.setOnClickListener(view -> {
                toggleCheckBox(getAdapterPosition());
            });
        }

        private void showBigPhoto() {
            nowPos = getAdapterPosition();
            Intent intent = new Intent(mContext, PhotoBigView.class);
            mActivity.startActivity(intent);
        }

        private void toggleCheckBox(int position) {
            Photo photo = photos.get(position);
            photo.checked = !photo.checked;
            photosAdapter.notifyItemChanged(position);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Photo photo = photos.get(position);
        if (photo.bitMap == null) {
            photo.bitMap = makeSumNail(photo.fullFileName);
            photos.set(position, photo);
        }
        holder.ivCheck.setImageResource((photo.checked) ? R.mipmap.checked : R.mipmap.unchecked);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.iVImage.getLayoutParams();
        params.width = spanWidth; params.height = spanWidth* 9 / 16;
        holder.iVImage.setLayoutParams(params);
        holder.iVImage.setImageBitmap(photo.bitMap);
    }

    static Bitmap makeSumNail(File fullFileName) {
        Bitmap bitmap = BitmapFactory.decodeFile(fullFileName.toString()).copy(Bitmap.Config.RGB_565, false);
        int width = bitmap.getWidth() * 5 / 16;
        int height = bitmap.getHeight() * 5 / 16;
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
