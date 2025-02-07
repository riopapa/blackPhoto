package com.urrecliner.blackphoto;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.Snackbar.SnackbarLayout;

public class CustomToast {

    public static void showCustomToast(Activity activity, String message, int imageResId) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Find and set the ImageView and TextView in the layout
        ImageView toastImage = layout.findViewById(R.id.toast_image);
        TextView toastText = layout.findViewById(R.id.toast_text);

        // Set the image and message dynamically
        toastImage.setImageResource(imageResId);
        toastText.setText(message);

        // Create a Toast object using the recommended method
        Toast toast = new Toast(activity);
        toast.setView(layout);  // This is still available but use with care
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        // Show the toast
        toast.show();
    }
}
