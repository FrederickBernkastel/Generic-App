package com.example.frederic.genericapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by nixsterchan on 26/2/2018.
 */

public class ImageResize extends Object {



    // FOR PULLING IMAGE FROM URL TO LOAD INTO AN IMAGEVIEW

    public static void loadImageByUrl(Context context, String url, ImageView newImage, int theWidth, int theHeight){
        if (theWidth<48||theHeight<48) {
            Picasso.with(context).load(url).resize(theWidth, theHeight).placeholder(R.drawable.progress_image_animation_small).into(newImage);
        } else {
            Picasso.with(context).load(url).resize(theWidth, theHeight).placeholder(R.drawable.progress_image_animation_med).into(newImage);
        }
    }

    // FOR RESIZING OF DRAWABLES IN LOCAL DRAWABLES FOLDER
    // Main function to call for image resize.
    // Parameters to use are in the order ( getResources(), R.id.blahblah, int width that you want, int height that you want
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // Decode with inJustDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options , int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;

    }


}
