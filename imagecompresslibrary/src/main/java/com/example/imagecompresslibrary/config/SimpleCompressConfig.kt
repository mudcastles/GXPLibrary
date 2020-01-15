package com.example.imagecompresslibrary.config

import android.os.Environment
import java.io.File

/**
 * 压缩参数
 */
class SimpleCompressConfig private constructor() {
    companion object {
        fun create(): SimpleCompressConfig {
            return SimpleCompressConfig()
        }

        fun getDefaultParams(): SimpleCompressConfig {
            return SimpleCompressConfig()
                .setSizeCompressCacheDir(Environment.getExternalStorageDirectory().absolutePath + File.separator + "SizeCompress")
                .setQualityCompressCacheDir(Environment.getExternalStorageDirectory().absolutePath + File.separator + "QualityCompress")
        }
    }

    /**
     * 最小占用空间不压缩，低于此大小将不会压缩,单位KB
     */
    private var unCompressMinSize = 1000
    /**
     * 宽度压缩到此值以下
     */
    private var maxWidthPixel = 1080
    /**
     * 高度压缩到此值以下
     */
    private var maxHeightPixel = 1920
    /**
     * 宽度缩放至此值
     */
    private var exactWidthPixel = 1080
    /**
     * 高度缩放至此值
     */
    private var exactHeightPixel = 1920
    /**
     * 占用空间压缩到此值以下
     */
    private var maxSize = 200 * 1024
    /**
     * 是否启用像素压缩
     */
    private var enablePixelCompress = true
    /**
     * 是否缩放图片尺寸到精确的指定值
     */
    private var enableExactPixelScale = false
    /**
     * 是否启用质量压缩
     */
    private var enableQualityCompress = true
    /**
     * 压缩完成后是否删除源文件
     */
    private var deleteOriginalPicture = false
    /**
     * 尺寸压缩压缩文件的存储目录
     */
    private var sizeCompressCacheDir: String? = null
    /**
     * 质量压缩压缩压缩文件的存储目录
     */
    private var qualityCompressCacheDir: String? = null
    /**
     * 尺寸缩放文件的存储目录
     */
    private var pixelScaleCacheDir: String? = null

    fun setUnCompressSize(unCompressMinSize: Int): SimpleCompressConfig {
        this.unCompressMinSize = unCompressMinSize
        return this
    }

    fun getUnCompressSize() = unCompressMinSize


    fun setMaxWidthPixel(maxWidthPixel: Int): SimpleCompressConfig {
        this.maxWidthPixel = maxWidthPixel
        return this
    }

    fun getMaxWidthPixel() = maxWidthPixel

    fun setMaxHeightPixel(maxHeightPixel: Int): SimpleCompressConfig {
        this.maxHeightPixel = maxHeightPixel
        return this
    }

    fun getMaxHeightPixel() = maxHeightPixel

    fun setExactWidthPixel(exactWidthPixel: Int): SimpleCompressConfig {
        this.exactWidthPixel = exactWidthPixel
        return this
    }

    fun getExactWidthPixel() = exactWidthPixel

    fun setExactHeightPixel(exactHeightPixel: Int): SimpleCompressConfig {
        this.exactHeightPixel = exactHeightPixel
        return this
    }

    fun getExactHeightPixel() = exactHeightPixel

    fun setMaxSize(maxSize: Int): SimpleCompressConfig {
        this.maxSize = maxSize
        return this
    }

    fun getMaxSize() = maxSize

    fun setSizeCompressCacheDir(cacheDir: String): SimpleCompressConfig {
        this.sizeCompressCacheDir = cacheDir
        return this
    }

    fun getSizeCompressCacheDir() = sizeCompressCacheDir

    fun setQualityCompressCacheDir(cacheDir: String): SimpleCompressConfig {
        this.qualityCompressCacheDir = cacheDir
        return this
    }

    fun getQualityCompressCacheDir() = qualityCompressCacheDir

    fun setPixelScaleCacheDir(cacheDir: String): SimpleCompressConfig {
        this.pixelScaleCacheDir = cacheDir
        return this
    }

    fun getPixelScaleCacheDir() = pixelScaleCacheDir

    fun setEnablePixelCompress(enablePixelCompress: Boolean): SimpleCompressConfig {
        this.enablePixelCompress = enablePixelCompress
        return this
    }

    fun enablePixelCompress() = enablePixelCompress

    fun setEnableExactPixelScale(enableExactPixelScale: Boolean): SimpleCompressConfig {
        this.enableExactPixelScale = enableExactPixelScale
        return this
    }

    fun enableExactPixelScale() = enableExactPixelScale

    fun setEnableQualityCompress(enableQualityCompress: Boolean): SimpleCompressConfig {
        this.enableQualityCompress = enableQualityCompress
        return this
    }

    fun enableQualityCompress() = enableQualityCompress

    fun setDeleteOriginalPicture(deleteOriginalPicture: Boolean): SimpleCompressConfig {
        this.deleteOriginalPicture = deleteOriginalPicture
        return this
    }

    fun deleteOriginalPicture() = deleteOriginalPicture
}