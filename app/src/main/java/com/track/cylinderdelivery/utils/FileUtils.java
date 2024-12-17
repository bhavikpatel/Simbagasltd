package com.track.cylinderdelivery.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static File convertUriToFile(Context context, Uri uri) {
        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // Open the InputStream for the content URI
        try (InputStream inputStream = contentResolver.openInputStream(uri)) {
            if (inputStream == null) {
                return null;  // Unable to open InputStream for the URI
            }

            // Create a temporary file in the app's cache directory to save the file
            File tempFile = new File(context.getCacheDir(), "po_file_" + System.currentTimeMillis()+".pdf");
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                // Copy content from InputStream to temporary file
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return tempFile;
            } catch (IOException e) {
                Log.e("FileUtils", "Error writing file: " + e.getMessage());
                return null;
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading file: " + e.getMessage());
            return null;
        }
    }

    public static String getFilePathFromUri(Uri uri,Context context) {
        // If the URI is from Downloads or another provider, we will need special handling
        if ("content".equals(uri.getScheme())) {
            String authority = uri.getAuthority();

            if ("com.android.providers.downloads.documents".equals(authority)) {
                return handleDownloadsUri(uri,context);
            }
            // Handle other content URI cases here if necessary
        }
        return null;  // Return null if URI scheme is not supported or unknown
    }

    private static String handleDownloadsUri(Uri uri, Context context) {
        // Handle special case for downloads
        String[] docId = uri.getLastPathSegment().split(":");
        if (docId.length == 2) {
            String id = docId[1];
            Uri contentUri = Uri.parse("content://downloads/public_downloads");
            Uri fileUri = ContentUris.withAppendedId(contentUri, Long.parseLong(id));

            // Try to get file path for the download URI
            Cursor cursor = context.getContentResolver().query(fileUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    return cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return null;
    }
}
