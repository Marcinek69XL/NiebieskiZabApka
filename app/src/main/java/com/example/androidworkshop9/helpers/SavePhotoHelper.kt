package com.example.androidworkshop9.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

public class SavePhotoHelper(var quality: Int, var isPngSelected: Boolean){
    /// @param folderName can be your app's name
    public fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
          //if (true){
              val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName : String
            if(isPngSelected){
                fileName = System.currentTimeMillis().toString() + ".png"
            }else{
                fileName = System.currentTimeMillis().toString() + ".jpeg"//jpeg
            }

            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        if(isPngSelected){
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }else{
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")//jpeg
        }
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                if(isPngSelected)
                {
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
                }else{
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
