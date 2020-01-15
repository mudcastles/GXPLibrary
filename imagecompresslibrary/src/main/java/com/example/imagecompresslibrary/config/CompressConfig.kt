package com.example.imagecompresslibrary.config

import android.os.Environment
import java.io.File
import java.lang.RuntimeException

class CompressConfig private constructor() {
    private lateinit var params: CompressParams

    init {
        params = CompressParams.builder()
    }

    fun getParams(): CompressParams {
        return params
    }

    fun setUnCompressSize(unCompressSize: Int): CompressParams {
        this.params.setUnCompressSize(unCompressSize)
        return this.params
    }

    fun getUnCompressSize() = this.params.getUnCompressSize()


    fun setMaxWidthPixel(maxWidthPixel: Int): CompressParams {
        this.params.setMaxWidthPixel(maxWidthPixel)
        return this.params
    }

    fun getMaxWidthPixel() = this.params.getMaxWidthPixel()

    fun setMaxHeightPixel(maxHeightPixel: Int): CompressParams {
        this.params.setMaxHeightPixel(maxHeightPixel)
        return this.params
    }

    fun getMaxHeightPixel() = this.params.getMaxHeightPixel()

    fun setMaxSize(maxSize: Int): CompressParams {
        this.params.setMaxSize(maxSize)
        return this.params
    }

    fun getMaxSize() = this.params.getMaxSize()

    fun setCacheDir(cacheDir: String): CompressParams {
        this.params.setCacheDir(cacheDir)
        return this.params
    }

    fun getCacheDir() = this.params.getCacheDir()

    fun setEnablePixelCompress(enablePixelCompress: Boolean): CompressParams {
        this.params.setEnablePixelCompress(enablePixelCompress)
        return this.params
    }

    fun enablePixelCompress() = this.params.enablePixelCompress()

    fun setEnableQualityCompress(enableQualityCompress: Boolean): CompressParams {
        this.params.setEnableQualityCompress(enableQualityCompress)
        return this.params
    }

    fun enableQualityCompress() = this.params.enableQualityCompress()

    fun setDeleteOriginalPicture(deleteOriginalPicture: Boolean): CompressParams {
        this.params.setDeleteOriginalPicture(deleteOriginalPicture)
        return this.params
    }

    fun deleteOriginalPicture() = this.params.deleteOriginalPicture()

    companion object {
        private var config: CompressConfig? = null
        fun builder(): CompressParams {
            config = CompressConfig()
            return config!!.getParams()
        }

        fun getDefasultConfig(): CompressConfig {
            config = CompressConfig()
            config!!.params = CompressParams.getDefaultParams()
            return config!!
        }

        fun build(): CompressConfig {
            if (config == null) throw RuntimeException("请先调用builder()方法创建CompressConfig对象")
            return config!!
        }


        class CompressParams private constructor() {

            companion object {
                fun builder(): CompressParams {
                    return CompressParams()
                }

                fun getDefaultParams(): CompressParams {
                    return CompressParams().setCacheDir(Environment.getExternalStorageDirectory().absolutePath + File.separator + "ImageCompress")
                }
            }

            /**
             * 最小占用空间不压缩，低于此大小将不会压缩
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
             * 占用空间压缩到此值以下
             */
            private var maxSize = 200 * 1024
            /**
             * 是否启用像素压缩
             */
            private var enablePixelCompress = true
            /**
             * 是否启用质量压缩
             */
            private var enableQualityCompress = true
            /**
             * 压缩完成后是否删除源文件
             */
            private var deleteOriginalPicture = false
            /**
             * 压缩文件的存储目录
             */
            private var cacheDir: String? = null

            fun setUnCompressSize(unCompressSize: Int): CompressParams {
                this.unCompressMinSize = unCompressMinSize
                return this
            }

            fun getUnCompressSize() = unCompressMinSize


            fun setMaxWidthPixel(maxWidthPixel: Int): CompressParams {
                this.maxWidthPixel = maxWidthPixel
                return this
            }

            fun getMaxWidthPixel() = maxWidthPixel

            fun setMaxHeightPixel(maxHeightPixel: Int): CompressParams {
                this.maxHeightPixel = maxHeightPixel
                return this
            }

            fun getMaxHeightPixel() = maxHeightPixel

            fun setMaxSize(maxSize: Int): CompressParams {
                this.maxSize = maxSize
                return this
            }

            fun getMaxSize() = maxSize

            fun setCacheDir(cacheDir: String): CompressParams {
                this.cacheDir = cacheDir
                return this
            }

            fun getCacheDir() = cacheDir

            fun setEnablePixelCompress(enablePixelCompress: Boolean): CompressParams {
                this.enablePixelCompress = enablePixelCompress
                return this
            }

            fun enablePixelCompress() = enablePixelCompress

            fun setEnableQualityCompress(enableQualityCompress: Boolean): CompressParams {
                this.enableQualityCompress = enableQualityCompress
                return this
            }

            fun enableQualityCompress() = enableQualityCompress

            fun setDeleteOriginalPicture(deleteOriginalPicture: Boolean): CompressParams {
                this.deleteOriginalPicture = deleteOriginalPicture
                return this
            }

            fun deleteOriginalPicture() = deleteOriginalPicture
        }
    }


}