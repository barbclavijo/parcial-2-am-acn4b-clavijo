package com.example.clubapp;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfilePhoto {

    private static final String FILE_NAME = "profile_photo.jpg";

    private static File getFile(Context context) {
        return new File(context.getFilesDir(), FILE_NAME);
    }

    // copia la imagen
    public static boolean save(Context context, Uri source) {
        try (InputStream in = context.getContentResolver().openInputStream(source);
             OutputStream out = new FileOutputStream(getFile(context))) {
            if (in == null) {
                return false;
            }
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // eleccion foto
    public static void applyTo(Context context, ImageView imageView) {
        File file = getFile(context);
        if (!file.exists()) {
            return;
        }
        Glide.with(context)
                .load(file)
                .signature(new ObjectKey(file.lastModified()))
                .into(imageView);
    }
}