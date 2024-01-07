package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.buildDB;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.snapEntities;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.spanWidth;
import static com.urrecliner.blackphoto.Vars.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SnapImageAdaptor extends RecyclerView.Adapter<SnapImageAdaptor.ViewHolder> {

    @Override
    public int getItemCount() {
        return snapEntities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iVImage, ivCheck, ivSend;
        TextView tvName;

        ViewHolder(final View itemView) {
            super(itemView);
            iVImage = itemView.findViewById(R.id.photosImage);
            iVImage.setOnLongClickListener(view -> {
                toggleCheckBox(getAbsoluteAdapterPosition());
                return true;
            });
            iVImage.setOnClickListener(view -> showBigPhoto());

            ivCheck = itemView.findViewById(R.id.checked);
            ivCheck.setOnClickListener(v -> {
                ivCheck.setBackgroundResource(R.drawable.check_animation);
                AnimationDrawable animation = (AnimationDrawable) ivCheck.getBackground();
                toggleCheckBox(getAbsoluteAdapterPosition());
                animation.start();
            });

            ivSend = itemView.findViewById(R.id.send);
            ivSend.setOnClickListener(view -> {
                ivSend.setImageResource(R.mipmap.airplane_black);
                Animation rotation = AnimationUtils.loadAnimation(mContext, R.anim.flight_ani);
                ivSend.startAnimation(rotation);

                SnapEntity snapEntity = snapEntities.get(getAbsoluteAdapterPosition());
                File dest = new File (selectedJpgFolder, snapEntity.photoName);
                try {
                    Files.copy(new File(snapEntity.fullFolder, snapEntity.photoName).toPath(), dest.toPath());
                    utils.showToast( snapEntity.photoName+" copied");
                } catch (IOException e) {}
            });

            tvName = itemView.findViewById(R.id.photoName);
            tvName.setOnClickListener(view -> {
                toggleCheckBox(getAbsoluteAdapterPosition());
            });
            tvName.setOnLongClickListener(view -> {showBigPhoto(); return true;});
        }

        private void showBigPhoto() {
            nowPos = getAbsoluteAdapterPosition();
            Intent intent = new Intent(mContext, SnapBigViewActivity.class);
            mActivity.startActivity(intent);
        }

        private void toggleCheckBox(int position) {
            SnapEntity s = snapEntities.get(position);
            s.isChecked = !s.isChecked;
            snapEntities.set(position, s);
            snapImageAdaptor.notifyItemChanged(position, s);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_snap_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SnapEntity sna = snapEntities.get(position);
        holder.ivCheck.setImageResource((sna.isChecked) ? R.mipmap.checked : R.mipmap.unchecked);
        holder.tvName.setText(sna.photoName);
        SnapEntity sna2 = snapDao.getByPhotoName(sna.fullFolder, sna.photoName);
        if (sna2 != null && sna2.sumNailMap != null) {
            Bitmap bitmap = buildDB.stringToBitMap(sna2.sumNailMap);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.iVImage.getLayoutParams();
            params.width = spanWidth; params.height = spanWidth * bitmap.getHeight() / bitmap.getWidth();
            holder.iVImage.setLayoutParams(params);
            holder.iVImage.setImageBitmap(bitmap);
        }
    }

}