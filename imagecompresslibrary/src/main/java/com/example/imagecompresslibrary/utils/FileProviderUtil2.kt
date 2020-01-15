package com.example.imagecompresslibrary.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.content.ContentUris
import android.provider.DocumentsContract


object FileProviderUtil2 {

    /**
     * 由uri获取真实路径
     */
    fun getPhotoPathFromContentUri(context: Context?, uri: Uri?): String? {
        var imagePath: String = ""

        if (context == null || uri == null) {
            return imagePath
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                //Log.d(TAG, uri.toString());
                val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(context!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri!!.authority) {
                //Log.d(TAG, uri.toString());
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(context!!, contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            //Log.d(TAG, "content: " + uri.toString());
            imagePath = getImagePath(context!!, uri, null)
        }
        return imagePath
    }

    private fun getImagePath(context: Context, uri: Uri, selection: String?): String {
        var path: String = ""
        val cursor = context.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }


}