package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.buildDB;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapEntities;
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
        return snapEntities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iVImage, ivCheck;
        TextView tvName;

        ViewHolder(final View itemView) {
            super(itemView);
            iVImage = itemView.findViewById(R.id.photosImage);
            iVImage.setOnLongClickListener(view -> {
                toggleCheckBox(getAbsoluteAdapterPosition());
                return true;
            });
            iVImage.setOnClickListener(view -> {
                    showBigPhoto();
            });

            ivCheck = itemView.findViewById(R.id.checked);
            ivCheck.setOnLongClickListener(view -> {
                toggleCheckBox(getAbsoluteAdapterPosition());
                return true;
            });
            ivCheck.setOnClickListener(view -> {
                showBigPhoto();
            });
            tvName = itemView.findViewById(R.id.photoName);
            tvName.setOnLongClickListener(view -> {
                toggleCheckBox(getAbsoluteAdapterPosition());
                return true;
            });
            tvName.setOnClickListener(view -> {
                showBigPhoto();
            });
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snap_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SnapEntity sna = snapEntities.get(position);
        holder.ivCheck.setImageResource((sna.isChecked) ? R.mipmap.checked : R.mipmap.unchecked);
        holder.tvName.setText(sna.photoName);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.iVImage.getLayoutParams();
        params.width = spanWidth; params.height = spanWidth* 9 / 16;
        holder.iVImage.setLayoutParams(params);
        SnapEntity sna2 = snapDao.getByPhotoName(sna.fullFolder, sna.photoName);
        if (sna2 != null && sna2.sumNailMap != null) {
            holder.iVImage.setImageBitmap(buildDB.stringToBitMap(sna2.sumNailMap));
        }
//        if (sna.sumNailMap != null)
//            holder.iVImage.setImageBitmap(buildDB.stringToBitMap(sna.sumNailMap));
    }

}