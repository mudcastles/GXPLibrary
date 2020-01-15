package com.example.imagecompresslibrary.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


object FileProviderUtil1 {

    /**
     * 由uri获取真实路径
     */
    fun getPhotoPathFromContentUri(context: Context?, uri: Uri?): String? {
        var photoPath: String = ""
        if (context == null || uri == null) {
            return photoPath
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if (isExternalStorageDocument(uri)) {
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.size >= 2) {
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        photoPath = Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                photoPath = getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.size >= 2) {
                    val type = split[0]
                    var contentUris: Uri? = null
                    if ("image" == type) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = MediaStore.Images.Media._ID + "=?"
                    val selectionArgs = arrayOf(split[1])
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs)
                }
            }
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            photoPath = uri.path
        } else {
            photoPath = getDataColumn(context, uri, null, null)
        }

        return photoPath
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close()
            }
        }
        return ""
    }
}