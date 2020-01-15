package com.example.imagecompresslibrary.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 照片
 */
@Parcelize
data class Photo(
    var originalPath: String,
    var isSizeCompressed: Boolean,
    var sizeCompressPath: String?,
    var isQualityCompressed: Boolean,
    var qualityCompressPath: String?,
    var isPixelScaled: Boolean,
    var pixelScalePath: String?
) : Parcelable {
    constructor(originalPath: String) : this(originalPath, false, null, false, null, false, null)
}