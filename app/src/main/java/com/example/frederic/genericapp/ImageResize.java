package com.example.frederic.genericapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by nixsterchan on 26/2/2018.
 */

public class ImageResize extends AppCompatActivity {

    /* TODO: 28/2/2018
      - Pull image from URL
      - Get its resource ID
      - Try using Picasso
      - Try out with resize function
       */


     public static Drawable pullImageFromDatabase (String url){
        try{
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(inputStream, "src name");
            return d;
        } catch (Exception e){
            return null;
        }
    }


  /*  protected void loadImageFromUrl (String url){
        ImageView imageView = null;
        Picasso.with(getApplicationContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher) //if error
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
        imageView.setTag(R.drawable.button_cancel);
        return (Integer) imageView.getTag();
    }*/



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

}
