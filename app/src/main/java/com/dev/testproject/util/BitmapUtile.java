package com.dev.testproject.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class BitmapUtile {

    public BitmapUtile() {
    }

    public static byte[] convertIMGtoBYTEbyIV(ImageView imageView){
        Bitmap bitmap = null;
        if (imageView !=null){
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmapFromIV = imageView.getDrawingCache();
            bitmap = bitmapFromIV;}
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] convertIMGtoBYTEbyBM(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertBYTEtoBITMAP(byte[] bytesimage){
        return BitmapFactory.decodeByteArray(bytesimage, 0, bytesimage.length);
    }
}
