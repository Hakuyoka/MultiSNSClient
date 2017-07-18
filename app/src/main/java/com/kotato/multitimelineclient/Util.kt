package com.kotato.multitimelineclient

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.graphics.Bitmap
import android.graphics.Matrix


/**
 * Created by kotato on 2017/07/12.
 */

fun getPathFromUri(context: Context, uri: Uri): String? {
    val isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    Log.e(ContentValues.TAG, "uri:" + uri.authority)
    if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if ("com.android.externalstorage.documents" == uri.authority) {// ExternalStorageProvider
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else {
                return "/stroage/" + type + "/" + split[1]
            }
        } else if ("com.android.providers.downloads.documents" == uri.authority) {// DownloadsProvider
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
            return getDataColumn(context, contentUri, null, null)
        } else if ("com.android.providers.media.documents" == uri.authority) {// MediaProvider
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            contentUri = MediaStore.Files.getContentUri("external")
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {//MediaStore
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {// File
        return uri.path
    }
    return null
}

fun getDataColumn(context: Context, uri: Uri, selection: String?,
                  selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    try {
        cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor!!.moveToFirst()) {
            val cindex = cursor!!.getColumnIndexOrThrow(projection[0])
            return cursor!!.getString(cindex)
        }
    } finally {
        if (cursor != null)
            cursor!!.close()
    }
    return null
}


/**
 * 画像リサイズ
 * @param bitmap 変換対象ビットマップ
 * *
 * @param newWidth 変換サイズ横
 * *
 * @param newHeight 変換サイズ縦
 * *
 * @return 変換後Bitmap
 */
fun resize(bitmap: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {

    if (bitmap == null) {
        return null
    }

    val oldWidth = bitmap.width
    val oldHeight = bitmap.height

    if (oldWidth < newWidth && oldHeight < newHeight) {
        // 縦も横も指定サイズより小さい場合は何もしない
        return bitmap
    }

    val scaleWidth = newWidth.toFloat() / oldWidth
    val scaleHeight = newHeight.toFloat() / oldHeight
    val scaleFactor = Math.min(scaleWidth, scaleHeight)

    val scale = Matrix()
    scale.postScale(scaleFactor, scaleFactor)

    val resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false)
    bitmap.recycle()

    return resizeBitmap

}