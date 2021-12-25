package com.urrecliner.blackphoto;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Random;

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
        TextView tvName;

        ViewHolder(final View itemView) {
            super(itemView);
            iVImage = itemView.findViewById(R.id.photosImage);
            iVImage.setOnClickListener(view -> toggleCheckBox(getAbsoluteAdapterPosition()));
            iVImage.setOnLongClickListener(view -> {
                    showBigPhoto();
                return true;
            });

            ivCheck = itemView.findViewById(R.id.checked);
            ivCheck.setOnClickListener(view -> toggleCheckBox(getAbsoluteAdapterPosition()));
            ivCheck.setOnLongClickListener(view -> {
                showBigPhoto();
                return true;
            });
            tvName = itemView.findViewById(R.id.photoName);
            tvName.setOnClickListener(view -> toggleCheckBox(getAbsoluteAdapterPosition()));
            tvName.setOnLongClickListener(view -> {
                showBigPhoto();
                return true;
            });
        }

        private void showBigPhoto() {
            nowPos = getAbsoluteAdapterPosition();
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
        if (photo.bitMap == null &&  new Random().nextInt(3) == 0) {
                photo.bitMap = makeSumNail(photo.fullFileName);
                photos.set(position, photo);
        }
        holder.ivCheck.setImageResource((photo.checked) ? R.mipmap.checked : R.mipmap.unchecked);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.iVImage.getLayoutParams();
        params.width = spanWidth; params.height = spanWidth* 9 / 16;
        holder.iVImage.setLayoutParams(params);
        holder.iVImage.setImageBitmap(photo.bitMap);
        holder.tvName.setText(photo.shortName);
    }

    static Bitmap makeSumNail(File fullFileName) {
        Bitmap bitmap = BitmapFactory.decodeFile(fullFileName.toString()).copy(Bitmap.Config.RGB_565, false);
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 5 / 40,
                bitmap.getHeight() * 5 / 40, false);
    }
}