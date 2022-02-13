package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.buildDB;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapImages;
import static com.urrecliner.blackphoto.Vars.spanWidth;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SnapImageAdaptor extends RecyclerView.Adapter<SnapImageAdaptor.ViewHolder> {

    @Override
    public int getItemCount() {
        return snapImages.size();
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
            Intent intent = new Intent(mContext, SnapBigView.class);
            mActivity.startActivity(intent);
        }

        private void toggleCheckBox(int position) {
            SnapImage s = snapImages.get(position);
            s.isChecked = !s.isChecked;
            snapImages.set(position, s);
            snapImageAdaptor.notifyItemChanged(position, s);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SnapImage sna = snapImages.get(position);
        holder.ivCheck.setImageResource((sna.isChecked) ? R.mipmap.checked : R.mipmap.unchecked);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.iVImage.getLayoutParams();
        params.width = spanWidth; params.height = spanWidth* 9 / 16;
        holder.iVImage.setLayoutParams(params);
        if (sna.sumNailMap == null) {
            sna = snapDao.getByPhotoName(sna.fullFolder, sna.photoName);
            if (sna.sumNailMap != null)
                snapImages.set(position, sna);
        }
        if (sna.sumNailMap != null)
            holder.iVImage.setImageBitmap(buildDB.stringToBitMap(sna.sumNailMap));
        holder.tvName.setText(sna.photoName);
    }

}