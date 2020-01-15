package com.example.imagecompresslibrary.managers

import android.content.Context
import com.example.imagecompresslibrary.bean.Photo
import com.example.imagecompresslibrary.config.SimpleCompressConfig
import com.example.imagecompresslibrary.listener.CompressListener
import com.example.imagecompresslibrary.utils.ImageCompressUtil

/**
 * 压缩框架管理器
 */
class CompressManager private constructor(){

    private lateinit var context: Context
    private lateinit var config: SimpleCompressConfig
    private lateinit var compressListener:CompressListener
    constructor(context: Context, config: SimpleCompressConfig,compressListener:CompressListener) :this(){
        this.context = context
        this.config = config
        this.compressListener = compressListener
    }

    companion object{
        /**
         * 须配置config
         */
        fun build(context: Context, config: SimpleCompressConfig,compressListener:CompressListener):CompressManager{
            return CompressManager(context, config,compressListener)
        }
    }

    /**
     * 压缩
     */
    fun compress(photo: Photo){
        ImageCompressUtil.compressImage(photo,config,compressListener)
    }

    /**
     * 缩放
     */
    fun scale(photo: Photo){
        ImageCompressUtil.scale(photo,config,compressListener)
    }
}