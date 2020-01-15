package com.example.imagecompresslibrary.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import com.example.imagecompresslibrary.bean.Photo
import com.example.imagecompresslibrary.config.SimpleCompressConfig
import com.example.imagecompresslibrary.listener.CompressListener
import java.io.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt


object ImageCompressUtil {

    /**
     * 按照SimpleCompressConfig的配置进行压缩
     */
    fun compressImage(
        photo: Photo,
        config: SimpleCompressConfig,
        compressListener: CompressListener
    ) {
        val options = BitmapFactory.Options()
        //开始读取图片，此时把inJustDecodeBounds设为true
        options.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(photo.originalPath, options)
        if (bitmap == null) {
            compressListener.onSingleCompressFailed(photo, "路径为${photo.originalPath}的图片无法解析成Bitmap")
            return
        }

        if ((!config.enableQualityCompress() || (getBitmapSize(bitmap) / 1024 < config.getUnCompressSize()))
            && (!config.enablePixelCompress() || (bitmap.width < config.getMaxWidthPixel() && bitmap.height < config.getMaxHeightPixel()))
            && (!config.enableExactPixelScale() || (bitmap.width == config.getExactWidthPixel() && bitmap.height == config.getExactHeightPixel()))
        ) {
            //循环判断压缩后的图片是否大于100kb，如果大于100kb则继续压缩
            compressListener.onSingleCompressFailed(photo, "图片无需压缩")
            return
        }

        if (config.enablePixelCompress()) {
            bitmap = compressImageWithRatio(photo, config, options)
            if (bitmap == null) {
                compressListener.onSingleCompressFailed(photo, "路径为${photo.originalPath}的图片尺寸压缩失败")
                return
            }
        }

        if (config.enableQualityCompress()) {
            bitmap = compressImageWithQuality(photo, config, bitmap)
            if (bitmap == null) {
                compressListener.onSingleCompressFailed(photo, "路径为${photo.originalPath}的图片质量压缩失败")
                return
            }
        }

        compressListener.onSingleCompressSuccess(photo)
        bitmap.recycle()

        if (config.deleteOriginalPicture()) {
            val file = File(photo.originalPath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * 只进行尺寸缩放
     */
    fun scale(
        photo: Photo,
        config: SimpleCompressConfig,
        compressListener: CompressListener
    ) {
        if (!config.enableExactPixelScale()) {
            compressListener.onSingleCompressFailed(photo, "参数错误")
            return
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(photo.originalPath, options)

        // 创建操作图片用的matrix对象
        val matrix = Matrix()
        val scaleWidth = config.getExactWidthPixel().toFloat() / bitmap.width
        val scaleHeight = config.getExactHeightPixel().toFloat() / bitmap.height

        matrix.postScale(scaleWidth, scaleHeight)
        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val file = File(
            config.getPixelScaleCacheDir(),
            renameFileNameAppendString(
                photo.originalPath.substring(photo.originalPath.lastIndexOf(File.separator) + 1),
                "pixelScaled"
            )
        )
        saveBitmapFile(file,newBitmap)
        bitmap.recycle()
        newBitmap.recycle()
        photo.isPixelScaled = true
        photo.pixelScalePath = file.absolutePath

        compressListener.onSingleCompressSuccess(photo)
    }

    /**
     * 按照比例压缩宽高
     */
    private fun compressImageWithRatio(
        photo: Photo,
        config: SimpleCompressConfig,
        options: BitmapFactory.Options
    ): Bitmap? {
        //计算采样，目前主流手机分辨率多为800 * 480
        options.inSampleSize =
            calculateInSimpleSize(options, config.getMaxWidthPixel(), config.getMaxHeightPixel())

        //用样本大小集解码位图,当值为true时，值解析图片的长度和宽度，不会加载图片，因此不会浪费内存开销
        options.inJustDecodeBounds = false

        val bm = BitmapFactory.decodeFile(photo.originalPath, options) ?: return null
        val file = File(
            config.getSizeCompressCacheDir(),
            renameFileNameAppendString(
                photo.originalPath.substring(photo.originalPath.lastIndexOf(File.separator) + 1),
                "sizeCompressed"
            )
        )
        saveBitmapFile(file, bm)

        photo.isSizeCompressed = true
        photo.sizeCompressPath = file.absolutePath
        return bm
    }

    /**
     * 质量压缩
     */
    private fun compressImageWithQuality(
        photo: Photo,
        config: SimpleCompressConfig,
        bitmap: Bitmap
    ): Bitmap? {
        val imagePath: String =
            if (photo.isSizeCompressed) photo.sizeCompressPath!! else photo.originalPath

        val baos = ByteArrayOutputStream()
        var quality = 100

        var compressFormat = Bitmap.CompressFormat.JPEG
        if (imagePath.endsWith(".jpeg", true) || imagePath.endsWith(".jpg", true))
            compressFormat = Bitmap.CompressFormat.JPEG
        else if (imagePath.endsWith(".png", true))
            compressFormat = Bitmap.CompressFormat.PNG

        //质量压缩，100表示不压缩，将压缩后的数据存放到baos中
        bitmap.compress(compressFormat, quality, baos)


        while (baos.toByteArray().size / 1024 > config.getMaxSize()) {  //循环判断压缩后的图片是否大于限制，如果大于则继续压缩
            baos.reset()    //清空baos
            quality -= 10
            bitmap.compress(compressFormat, quality, baos)
        }
        val targetFile = File(
            config.getQualityCompressCacheDir(),
            renameFileNameAppendString(
                imagePath.substring(imagePath.lastIndexOf(File.separator) + 1),
                "qualityCompressed"
            )
        )
        saveBitmapFile(targetFile, baos.toByteArray())

        val inputStreamBitmap =
            ByteArrayInputStream(baos.toByteArray())//把baos存放到ByteArrayInputStream中
        val bm =
            BitmapFactory.decodeStream(inputStreamBitmap, null, null)
                ?: return null //把ByteArrayInputStream生成图片

        photo.isQualityCompressed = true
        photo.qualityCompressPath = targetFile.absolutePath
        return bm
    }


    /**
     * 计算inSimpleSize
     * @param options 图片的options
     * @param reqHeight 要求的最大高度
     * @param reqWidth 要求的最大宽度
     * */
    private fun calculateInSimpleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        //图片的raw Height 和raw Width
        val width = options.outWidth
        val height = options.outHeight

        var inSimpleSize = 1
        if (width > reqWidth || height > reqHeight) {
            //计算图片实际宽度与要求宽度的比值
            val widthRatio = ceil(width.toFloat() / reqWidth.toFloat()).toInt()
            //计算图片实际高度与要求高度的比值
            val heightRatio = ceil(height.toFloat() / reqHeight.toFloat()).toInt()

            //选择最大的ratio作为最终的inSimpleSize的值，
            // 这样可以确保最终图片的宽和高都会小于或者等于要求的宽和高
            inSimpleSize = if (widthRatio > heightRatio) widthRatio else heightRatio
        }
        return inSimpleSize
    }

    /**
     * 将ByteArray保存为图片文件
     */
    private fun saveBitmapFile(targetFile: File, byteArray: ByteArray?) {
        val fout = FileOutputStream(targetFile)
        val bufferedOutputStream = BufferedOutputStream(fout)
        bufferedOutputStream.write(byteArray)
        bufferedOutputStream.flush()
        bufferedOutputStream.close()
    }

    /**
     * 将bitmap保存为图片文件
     */
    private fun saveBitmapFile(targetFile: File, bm: Bitmap) {
        try {
            if (!targetFile.parentFile.exists()) {
                targetFile.parentFile.mkdirs()
            }
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(targetFile))
            if (targetFile.absolutePath.endsWith(".jpeg", true) || targetFile.absolutePath.endsWith(
                    ".jpg",
                    true
                )
            )
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
            else if (targetFile.absolutePath.endsWith(".png", true))
                bm.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 为sourceFileName重命名，在后缀前面添加appendStr
     * 如 sourceFileName="file.jpeg" , appendStr="sizeCompressed" ,则最后返回"file-sizeCompressed.jpeg"
     */
    private fun renameFileNameAppendString(sourceFileName: String, appendStr: String): String {
        val lastDotIndex = sourceFileName.lastIndexOf(".")
        return "${sourceFileName.substring(0, lastDotIndex)}-$appendStr${sourceFileName.substring(
            lastDotIndex,
            sourceFileName.length
        )}"
    }

    /**
     * 得到bitmap的大小
     */
    @SuppressLint("ObsoleteSdkInt")
    fun getBitmapSize(bitmap: Bitmap): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //API 19
            return bitmap.allocationByteCount
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) { //API 12
            bitmap.byteCount
        } else bitmap.rowBytes * bitmap.height
        // 在低版本中用一行的字节x高度
        //earlier version
    }

    /**
     * 将图片旋转degree角度
     */
    fun rotateBitmap(bitmap: Bitmap?, degree: Float): Bitmap? {
        if (bitmap == null) return null

        val width = bitmap.width
        val height = bitmap.height

        //设置postRotate为degree
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    /**
     * 读取图片的角度
     */
    fun readPictureDegree(filePath: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> {
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }
}