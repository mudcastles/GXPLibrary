package com.example.imagecompresslibrary.listener

import com.example.imagecompresslibrary.bean.Photo

interface CompressListener {
    fun onSingleCompressSuccess(photo:Photo)
    fun onSingleCompressFailed(photo:Photo,error:String)
    fun onAllCompressComplete(photoList:List<Photo>)
}