package com.alex.grocer_free;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by alex on 6/08/15.
 */
public class Utils {
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}
