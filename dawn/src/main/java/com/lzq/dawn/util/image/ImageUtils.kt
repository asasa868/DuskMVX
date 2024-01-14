package com.lzq.dawn.util.image

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.RenderScript.RSMessageHandler
import android.renderscript.ScriptIntrinsicBlur
import android.text.TextUtils
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.lzq.dawn.DawnBridge
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

/**
 * @Name :ImageUtils
 * @Time :2022/8/1 15:48
 * @Author :  Lzq
 * @Desc : image
 */
object ImageUtils {
    /**
     * Bitmap 转 bytes.
     *
     * @param bitmap bitmap.
     * @return bytes
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
        return bitmap2Bytes(bitmap, CompressFormat.PNG, 100)
    }

    /**
     * Bitmap 转 bytes.
     *
     * @param bitmap  bitmap.
     * @param format  位图的格式。
     * @param quality 质量。
     * @return bytes
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap?, format: CompressFormat, quality: Int): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, quality, baos)
        return baos.toByteArray()
    }

    /**
     * Bytes 转 bitmap.
     *
     * @param bytes bytes.
     * @return bitmap
     */
    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.isEmpty()) null else BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )
    }

    /**
     * Drawable 转 bitmap.
     *
     * @param drawable drawable.
     * @return bitmap
     */
    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Bitmap 转 drawable.
     *
     * @param bitmap bitmap.
     * @return drawable
     */
    @JvmStatic
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
        return if (bitmap == null) null else BitmapDrawable(DawnBridge.getApp().resources, bitmap)
    }

    /**
     * Drawable 转 bytes.
     *
     * @param drawable The drawable.
     * @return bytes
     */
    @JvmStatic
    fun drawable2Bytes(drawable: Drawable?): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable))
    }

    /**
     * Drawable 转 bytes.
     *
     * @param drawable drawable.
     * @param format   位图的格式
     * @return bytes
     */
    @JvmStatic
    fun drawable2Bytes(drawable: Drawable?, format: CompressFormat, quality: Int): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format, quality)
    }

    /**
     * Bytes 转 drawable.
     *
     * @param bytes bytes.
     * @return drawable
     */
    @JvmStatic
    fun bytes2Drawable(bytes: ByteArray?): Drawable? {
        return bitmap2Drawable(bytes2Bitmap(bytes))
    }

    /**
     * 返回 bitmap.
     *
     * @param file file.
     * @return bitmap
     */
    fun getBitmap(file: File?): Bitmap? {
        return if (file == null) {
            null
        } else BitmapFactory.decodeFile(file.absolutePath)
    }

    /**
     * 返回 bitmap.
     *
     * @param file      file.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(file: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (file == null) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    /**
     * 返回 bitmap.
     *
     * @param filePath 文件的路径。
     * @return bitmap
     */
    fun getBitmap(filePath: String?): Bitmap? {
        return if (DawnBridge.isSpace(filePath)) {
            null
        } else BitmapFactory.decodeFile(filePath)
    }

    /**
     * 返回 bitmap.
     *
     * @param filePath  文件的路径.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(filePath: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (DawnBridge.isSpace(filePath)) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 返回 bitmap.
     *
     * @param is 输入流。
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?): Bitmap? {
        return if (`is` == null) {
            null
        } else BitmapFactory.decodeStream(`is`)
    }

    /**
     * 返回 bitmap.
     *
     * @param is        输入流.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (`is` == null) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    /**
     * 返回 bitmap.
     *
     * @param data   data.
     * @param offset 偏移量。
     * @return bitmap
     */
    fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
        return if (data.isEmpty()) {
            null
        } else BitmapFactory.decodeByteArray(data, offset, data.size)
    }

    /**
     * 返回 bitmap.
     *
     * @param data      data.
     * @param offset    偏移量。
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(
        data: ByteArray, offset: Int, maxWidth: Int, maxHeight: Int
    ): Bitmap? {
        if (data.isEmpty()) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, offset, data.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, offset, data.size, options)
    }

    /**
     * 返回 bitmap.
     *
     * @param resId resource id.
     * @return bitmap
     */
    fun getBitmap(@DrawableRes resId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(DawnBridge.getApp(), resId) ?: return null
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 返回 bitmap.
     *
     * @param resId     resource id.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(
        @DrawableRes resId: Int, maxWidth: Int, maxHeight: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        val resources = DawnBridge.getApp().resources
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    /**
     * 返回 bitmap.
     *
     * @param fd 文件描述符
     * @return bitmap
     */
    fun getBitmap(fd: FileDescriptor?): Bitmap? {
        return if (fd == null) {
            null
        } else BitmapFactory.decodeFileDescriptor(fd)
    }

    /**
     * 返回 bitmap.
     *
     * @param fd        文件描述符
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    fun getBitmap(
        fd: FileDescriptor?, maxWidth: Int, maxHeight: Int
    ): Bitmap? {
        if (fd == null) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fd, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFileDescriptor(fd, null, options)
    }
    /**
     * 返回具有指定颜色的位图。
     *
     * @param src     位图的来源
     * @param color   color.
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 具有指定颜色的位图。
     */
    /**
     * 返回具有指定颜色的位图。
     *
     * @param src   位图的来源。
     * @param color color.
     * @return 具有指定颜色的位图。
     */
    @JvmOverloads
    fun drawColor(
        src: Bitmap, @ColorInt color: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = if (recycle) src else src.copy(src.config, true)
        val canvas = Canvas(ret)
        canvas.drawColor(color, PorterDuff.Mode.DARKEN)
        return ret
    }
    /**
     * 返回缩放的位图。
     *
     * @param src       位图的来源
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @param recycle   True 回收位图的来源，否则为 false。
     * @return the scaled bitmap
     */
    /**
     * 返回缩放的位图。
     *
     * @param src       位图的来源
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @return 缩放的位图。
     */
    @JvmOverloads
    fun scale(
        src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回缩放的位图
     *
     * @param src         位图的来源
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 高度的比例。
     * @return 返回缩放的位图
     */
    @JvmOverloads
    fun scale(
        src: Bitmap, scaleWidth: Float, scaleHeight: Float, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val matrix = Matrix()
        matrix.setScale(scaleWidth, scaleHeight)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回剪切的位图。
     *
     * @param src    位图的来源
     * @param x      第一个像素的 x 坐标。
     * @param y      第一个像素的 y 坐标。
     * @param width  width.
     * @param height height.
     * @return 返回剪切的位图。
     */
    @JvmOverloads
    fun clip(
        src: Bitmap, x: Int, y: Int, width: Int, height: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = Bitmap.createBitmap(src, x, y, width, height)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回倾斜的位图。
     *
     * @param src     位图的来源
     * @param kx      x 的偏斜系数。
     * @param ky      y 的偏斜系数。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 倾斜的位图。
     */
    fun skew(
        src: Bitmap, kx: Float, ky: Float, recycle: Boolean
    ): Bitmap? {
        return skew(src, kx, ky, 0f, 0f, recycle)
    }


    /**
     * 返回倾斜的位图。
     *
     * @param src 位图的来源
     * @param kx  x 的偏斜系数。
     * @param ky  y 的偏斜系数。
     * @param px  轴心点的 x 坐标。
     * @param py  轴心点的 y 坐标。
     * @return 返回倾斜的位图。
     */
    @JvmOverloads
    fun skew(
        src: Bitmap, kx: Float, ky: Float, px: Float = 0f, py: Float = 0f, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val matrix = Matrix()
        matrix.setSkew(kx, ky, px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回旋转的位图。
     *
     * @param src     位图的来源
     * @param degrees 度数。
     * @param px      轴心点的 x 坐标。
     * @param py      轴心点的 y 坐标。
     * @return 返回旋转的位图。
     */
    @JvmOverloads
    fun rotate(
        src: Bitmap, degrees: Int, px: Float, py: Float, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        if (degrees == 0) {
            return src
        }
        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat(), px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回旋转的度数。
     *
     * @param filePath 文件的路径。
     * @return 旋转的度数。
     */
    fun getRotateDegree(filePath: String?): Int {
        return try {
            val exifInterface = ExifInterface(
                filePath!!
            )
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * 返回圆形位图
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回圆形位图
     */
    fun toRound(src: Bitmap, recycle: Boolean): Bitmap? {
        return toRound(src, 0, 0, recycle)
    }

    /**
     * 返回圆形位图
     *
     * @param src         位图的来源
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @return 返回圆形位图
     */
    @JvmOverloads
    fun toRound(
        src: Bitmap,
        @IntRange(from = 0) borderSize: Int = 0,
        @ColorInt borderColor: Int = 0,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val width = src.width
        val height = src.height
        val size = Math.min(width, height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val center = size / 2f
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        rectF.inset((width - size) / 2f, (height - size) / 2f)
        val matrix = Matrix()
        matrix.setTranslate(rectF.left, rectF.top)
        if (width != height) {
            matrix.preScale(size.toFloat() / width, size.toFloat() / height)
        }
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        paint.shader = shader
        val canvas = Canvas(ret)
        canvas.drawRoundRect(rectF, center, center, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize.toFloat()
            val radius = center - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        }
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回圆角位图。
     *
     * @param src     位图的来源
     * @param radius  圆角的半径。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回圆角位图。
     */
    fun toRoundCorner(
        src: Bitmap, radius: Float, recycle: Boolean
    ): Bitmap? {
        return toRoundCorner(src, radius, 0f, 0, recycle)
    }

    /**
     * 返回圆角位图。
     *
     * @param src         位图的来源
     * @param radius      圆角的半径。
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @return 返回圆角位图。
     */
    @JvmOverloads
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        @FloatRange(from = 0.0) borderSize: Float = 0f,
        @ColorInt borderColor: Int = 0,
        recycle: Boolean = false
    ): Bitmap? {
        val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        return toRoundCorner(src, radii, borderSize, borderColor, recycle)
    }

    /**
     * 返回圆角位图。
     *
     * @param src         位图的来源
     * @param radii       8 个值的数组，4 对 [X,Y] 半径
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @return 返回圆角位图。
     */
    @JvmOverloads
    fun toRoundCorner(
        src: Bitmap,
        radii: FloatArray?,
        @FloatRange(from = 0.0) borderSize: Float,
        @ColorInt borderColor: Int,
        recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val width = src.width
        val height = src.height
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        val canvas = Canvas(ret)
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val halfBorderSize = borderSize / 2f
        rectF.inset(halfBorderSize, halfBorderSize)
        val path = Path()
        path.addRoundRect(rectF, radii!!, Path.Direction.CW)
        canvas.drawPath(path, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawPath(path, paint)
        }
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回带边框的圆角位图。
     *
     * @param src          位图的来源
     * @param borderSize   边框的大小
     * @param color        边框的颜色
     * @param cornerRadius 圆角的半径。
     * @return 返回带边框的圆角位图
     */
    fun addCornerBorder(
        src: Bitmap,
        @FloatRange(from = 1.0) borderSize: Float,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, false)
    }

    /**
     * 返回带边框的圆角位图
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @param radii      8 个值的数组，4 对 [X,Y] 半径
     * @return 返回带边框的圆角位图
     */
    fun addCornerBorder(
        src: Bitmap, @FloatRange(from = 1.0) borderSize: Float, @ColorInt color: Int, radii: FloatArray
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, radii, false)
    }

    /**
     * 返回带边框的圆角位图
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @param radii      8 个值的数组，4 对 [X,Y] 半径
     * @param recycle    True 回收位图的来源，否则为 false。
     * @return 返回带边框的圆角位图
     */
    fun addCornerBorder(
        src: Bitmap,
        @FloatRange(from = 1.0) borderSize: Float,
        @ColorInt color: Int,
        radii: FloatArray,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, radii, recycle)
    }

    /**
     * 返回带边框的圆角位图
     *
     * @param src          位图的来源
     * @param borderSize   边框的大小
     * @param color        边框的颜色
     * @param cornerRadius 圆角的半径。
     * @param recycle      True 回收位图的来源，否则为 false。
     * @return 返回带边框的圆角位图
     */
    fun addCornerBorder(
        src: Bitmap,
        @FloatRange(from = 1.0) borderSize: Float,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle)
    }

    /**
     * 返回带边框的圆形位图。
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @return 返回带边框的圆形位图。
     */
    fun addCircleBorder(
        src: Bitmap, @FloatRange(from = 1.0) borderSize: Float, @ColorInt color: Int
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, false)
    }

    /**
     * 返回带边框的圆形位图。
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @param recycle    True 回收位图的来源，否则为 false。
     * @return 返回带边框的圆形位图。
     */
    fun addCircleBorder(
        src: Bitmap, @FloatRange(from = 1.0) borderSize: Float, @ColorInt color: Int, recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, recycle)
    }

    /**
     * 返回带边框的位图。
     *
     * @param src          位图的来源
     * @param borderSize   边框的大小
     * @param color        边框的颜色
     * @param isCircle     真画圆，假画圆角。
     * @param cornerRadius 圆角的半径。
     * @param recycle      True 回收位图的来源，否则为 false。
     * @return 返回带边框的位图。
     */
    private fun addBorder(
        src: Bitmap,
        @FloatRange(from = 1.0) borderSize: Float,
        @ColorInt color: Int,
        isCircle: Boolean,
        cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        val radii = floatArrayOf(
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius
        )
        return addBorder(src, borderSize, color, isCircle, radii, recycle)
    }

    /**
     * 返回带边框的位图。
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @param isCircle   真画圆，假画圆角。
     * @param radii      8 个值的数组，4 对 [X,Y] 半径
     * @param recycle    True 回收位图的来源，否则为 false。
     * @return 返回带边框的位图。
     */
    private fun addBorder(
        src: Bitmap,
        @FloatRange(from = 1.0) borderSize: Float,
        @ColorInt color: Int,
        isCircle: Boolean,
        radii: FloatArray,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = if (recycle) src else src.copy(src.config, true)
        val width = ret.width
        val height = ret.height
        val canvas = Canvas(ret)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        if (isCircle) {
            val radius = Math.min(width, height) / 2f - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        } else {
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val halfBorderSize = borderSize / 2f
            rectF.inset(halfBorderSize, halfBorderSize)
            val path = Path()
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, paint)
        }
        return ret
    }
    /**
     * 用反射返回位图。
     *
     * @param src              位图的来源
     * @param reflectionHeight 反射的高度
     * @param recycle          True 回收位图的来源，否则为 false。
     * @return 用反射返回位图。
     */
    @JvmOverloads
    fun addReflection(
        src: Bitmap, reflectionHeight: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val REFLECTION_GAP = 0
        val srcWidth = src.width
        val srcHeight = src.height
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        val reflectionBitmap = Bitmap.createBitmap(
            src, 0, srcHeight - reflectionHeight, srcWidth, reflectionHeight, matrix, false
        )
        val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
        val canvas = Canvas(ret)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP).toFloat(), null)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader = LinearGradient(
            0f,
            srcHeight.toFloat(),
            0f,
            (ret.height + REFLECTION_GAP).toFloat(),
            0x70FFFFFF,
            0x00FFFFFF,
            Shader.TileMode.MIRROR
        )
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(
            0f,
            (srcHeight + REFLECTION_GAP).toFloat(),
            srcWidth.toFloat(),
            ret.height.toFloat(),
            paint
        )
        if (!reflectionBitmap.isRecycled) {
            reflectionBitmap.recycle()
        }
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 返回带有文本水印的位图。
     *
     * @param src      位图的来源
     * @param content  文本的内容。
     * @param textSize 文本的大小。
     * @param color    文本的颜色。
     * @param x        第一个像素的 x 坐标。
     * @param y        第一个像素的 y 坐标。
     * @return 返回带有文本水印的位图。
     */
    fun addTextWatermark(
        src: Bitmap, content: String?, textSize: Int, @ColorInt color: Int, x: Float, y: Float
    ): Bitmap? {
        return addTextWatermark(src, content, textSize.toFloat(), color, x, y, false)
    }

    /**
     * 返回带有文本水印的位图
     *
     * @param src      位图的来源
     * @param content  文本的内容。
     * @param textSize 文本的大小。
     * @param color    文本的颜色。
     * @param x        第一个像素的 x 坐标。
     * @param y        第一个像素的 y 坐标。
     * @param recycle  True 回收位图的来源，否则为 false。
     * @return 返回带有文本水印的位图
     */
    fun addTextWatermark(
        src: Bitmap,
        content: String?,
        textSize: Float,
        @ColorInt color: Int,
        x: Float,
        y: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src) || content == null) {
            return null
        }
        val ret = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.color = color
        paint.textSize = textSize
        val bounds = Rect()
        paint.getTextBounds(content, 0, content.length, bounds)
        canvas.drawText(content, x, y + textSize, paint)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }
    /**
     * 返回带有图像水印的位图。
     *
     * @param src       位图的来源
     * @param watermark 图片水印。
     * @param x         第一个像素的 x 坐标。
     * @param y         第一个像素的 y 坐标。
     * @param alpha     水印的alpha
     * @param recycle   True 回收位图的来源，否则为 false。
     * @return 返回带有图像水印的位图。
     */

    @JvmOverloads
    fun addImageWatermark(
        src: Bitmap, watermark: Bitmap?, x: Int, y: Int, alpha: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = src.copy(src.config, true)
        if (!isEmptyBitmap(watermark)) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.alpha = alpha
            canvas.drawBitmap(watermark!!, x.toFloat(), y.toFloat(), paint)
        }
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }
    /**
     * 返回 alpha 位图
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回 alpha 位图
     */
    @JvmOverloads
    fun toAlpha(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = src.extractAlpha()
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }
    /**
     * 返回灰色位图。
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回灰色位图。
     */
    @JvmOverloads
    fun toGray(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val ret = Bitmap.createBitmap(src.width, src.height, src.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }
    /**
     * 返回模糊位图。
     *
     * zoom out, blur, zoom in
     *
     * @param src           位图的来源
     * @param scale         比例（0...1）。
     * @param radius        radius(0...25).
     * @param recycle       True 回收位图的来源，否则为 false。
     * @param isReturnScale 返回比例模糊位图为真，否则为假。
     * @return 返回模糊位图
     */
    /**
     * 返回模糊位图。
     *
     * zoom out, blur, zoom in
     *
     * @param src    位图的来源
     * @param scale  比例（0...1）。
     * @param radius radius(0...25).
     * @return 模糊位图。
     */
    /**
     * 返回模糊位图。
     *
     * zoom out, blur, zoom in
     *
     * @param src    位图的来源
     * @param scale  比例（0...1）。
     * @param radius radius(0...25).
     * @return 返回模糊位图。
     */
    @JvmOverloads
    fun fastBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) scale: Float,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
        recycle: Boolean = false,
        isReturnScale: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val width = src.width
        val height = src.height
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        var scaleBitmap = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas()
        val filter = PorterDuffColorFilter(
            Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
        )
        paint.colorFilter = filter
        canvas.scale(scale, scale)
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
        scaleBitmap = renderScriptBlur(scaleBitmap, radius, recycle)
        if (scale == 1f || isReturnScale) {
            if (recycle && !src.isRecycled && scaleBitmap != src) {
                src.recycle()
            }
            return scaleBitmap
        }
        val ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true)
        if (!scaleBitmap.isRecycled) {
            scaleBitmap.recycle()
        }
        if (recycle && !src.isRecycled && ret != src) {
            src.recycle()
        }
        return ret
    }

    /**
     * 使用渲染脚本返回模糊位图。
     *
     * @param src    位图的来源
     * @param radius radius(0...25).
     * @return 使用渲染脚本返回模糊位图。
     */
    fun renderScriptBlur(
        src: Bitmap, @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float
    ): Bitmap {
        return renderScriptBlur(src, radius, false)
    }

    /**
     * 使用渲染脚本返回模糊位图。
     *
     * @param src     位图的来源
     * @param radius  radius(0...25).
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用渲染脚本返回模糊位图。
     */
    fun renderScriptBlur(
        src: Bitmap, @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float, recycle: Boolean
    ): Bitmap {
        var rs: RenderScript? = null
        val ret = if (recycle) src else src.copy(src.config, true)
        try {
            rs = RenderScript.create(DawnBridge.getApp())
            rs.messageHandler = RSMessageHandler()
            val input = Allocation.createFromBitmap(
                rs, ret, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            blurScript.setRadius(radius)
            blurScript.forEach(output)
            output.copyTo(ret)
        } finally {
            rs?.destroy()
        }
        return ret
    }
    /**
     * 使用堆栈返回模糊位图
     *
     * @param src     位图的来源
     * @param radius  radius(0...25).
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用堆栈返回模糊位图
     */
    /**
     * 使用堆栈返回模糊位图。
     *
     * @param src    位图的来源。
     * @param radius radius(0...25).
     * @return 使用堆栈返回模糊位图。
     */
    @JvmOverloads
    fun stackBlur(src: Bitmap, radius: Int, recycle: Boolean = false): Bitmap {
        var radius = radius
        val ret = if (recycle) src else src.copy(src.config, true)
        if (radius < 1) {
            radius = 1
        }
        val w = ret.width
        val h = ret.height
        val pix = IntArray(w * h)
        ret.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h)
        return ret
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @param recycle  True 回收位图的来源，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(
        src: Bitmap, filePath: String?, format: CompressFormat?, recycle: Boolean
    ): Boolean {
        return save(src, filePath, format, 100, recycle)
    }

    /**
     * 保存位图。
     *
     * @param src     位图的来源
     * @param file    file.
     * @param format  图像的格式。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(
        src: Bitmap, file: File, format: CompressFormat?, recycle: Boolean
    ): Boolean {
        return save(src, file, format, 100, recycle)
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @param quality  提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle  True 回收位图的来源，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun save(
        src: Bitmap,
        filePath: String?,
        format: CompressFormat?,
        quality: Int = 100,
        recycle: Boolean = false
    ): Boolean {
        return save(src, DawnBridge.getFileByPath(filePath), format, quality, recycle)
    }
    /**
     * 保存位图。
     *
     * @param src     位图的来源
     * @param file    file.
     * @param format  图像的格式。
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle True 回收位图的来源，否则为 false。
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    fun save(
        src: Bitmap,
        file: File,
        format: CompressFormat?,
        quality: Int = 100,
        recycle: Boolean = false
    ): Boolean {
        if (isEmptyBitmap(src)) {
            Log.e("ImageUtils", "bitmap is empty.")
            return false
        }
        if (src.isRecycled) {
            Log.e("ImageUtils", "bitmap is recycled.")
            return false
        }
        if (!DawnBridge.createFileByDeleteOldFile(file)) {
            Log.e("ImageUtils", "create or delete file <$file> failed.")
            return false
        }
        var os: OutputStream? = null
        var ret = false
        try {
            os = BufferedOutputStream(FileOutputStream(file))
            ret = src.compress(format!!, quality, os)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ret
    }

    /**
     * 保存到相册
     *
     * @param src    位图的来源
     * @param format 图像的格式。
     * @return 文件保存成功，否则返回null。
     */
    fun save2Album(
        src: Bitmap, format: CompressFormat
    ): File? {
        return save2Album(src, "", format, 100, false)
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param format  图像的格式。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 文件保存成功，否则返回null
     */
    fun save2Album(
        src: Bitmap, format: CompressFormat, recycle: Boolean
    ): File? {
        return save2Album(src, "", format, 100, recycle)
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param format  图像的格式。
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @return 文件保存成功，否则返回null
     */
    fun save2Album(
        src: Bitmap, format: CompressFormat, quality: Int
    ): File? {
        return save2Album(src, "", format, quality, false)
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param format  图像的格式。
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 文件保存成功，否则返回null
     */
    fun save2Album(
        src: Bitmap, format: CompressFormat, quality: Int, recycle: Boolean
    ): File? {
        return save2Album(src, "", format, quality, recycle)
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 文件保存成功，否则返回null
     */
    fun save2Album(
        src: Bitmap, dirName: String?, format: CompressFormat, recycle: Boolean
    ): File? {
        return save2Album(src, dirName, format, 100, recycle)
    }
    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 文件保存成功，否则返回null
     */
    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @return 文件保存成功，否则返回null
     */
    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @return 文件保存成功，否则返回null
     */
    @JvmOverloads
    fun save2Album(
        src: Bitmap, dirName: String?, format: CompressFormat, quality: Int = 100, recycle: Boolean = false
    ): File? {
        val safeDirName = if (TextUtils.isEmpty(dirName)) DawnBridge.getApp().packageName else dirName!!
        val suffix = if (CompressFormat.JPEG == format) "JPG" else format.name
        val fileName = System.currentTimeMillis().toString() + "_" + quality + "." + suffix
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!DawnBridge.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("ImageUtils", "save to album need storage permission")
                return null
            }
            val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val destFile = File(picDir, "$safeDirName/$fileName")
            if (!save(src, destFile, format, quality, recycle)) {
                return null
            }
            DawnBridge.notifySystemToScan(destFile)
            destFile
        } else {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            val contentUri: Uri
            contentUri = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            }
            contentValues.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DCIM + "/" + safeDirName
            )
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
            val uri = DawnBridge.getApp().contentResolver.insert(contentUri, contentValues) ?: return null
            var os: OutputStream? = null
            try {
                os = DawnBridge.getApp().contentResolver.openOutputStream(uri)
                src.compress(format, quality, os!!)
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                DawnBridge.getApp().contentResolver.update(uri, contentValues, null, null)
                DawnBridge.uri2File(uri)
            } catch (e: Exception) {
                DawnBridge.getApp().contentResolver.delete(uri, null, null)
                e.printStackTrace()
                null
            } finally {
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 根据文件名返回是否为图片
     *
     * @param file file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isImage(file: File?): Boolean {
        return if (file == null || !file.exists()) {
            false
        } else isImage(file.path)
    }

    /**
     * 根据文件名返回是否为图片
     *
     * @param filePath 文件的路径。
     * @return `true`: yes<br></br>`false`: no
     */
    fun isImage(filePath: String?): Boolean {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            options.outWidth > 0 && options.outHeight > 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 返回图像的类型。
     *
     * @param filePath 文件的路径。
     * @return 图像的类型。
     */
    fun getImageType(filePath: String?): ImageType? {
        return getImageType(DawnBridge.getFileByPath(filePath))
    }

    /**
     * 返回图像的类型
     *
     * @param file file.
     * @return 图像的类型。
     */
    fun getImageType(file: File?): ImageType? {
        if (file == null) {
            return null
        }
        var `is`: InputStream? = null
        try {
            `is` = FileInputStream(file)
            val type = getImageType(`is`)
            if (type != null) {
                return type
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun getImageType(`is`: InputStream?): ImageType? {
        return if (`is` == null) {
            null
        } else try {
            val bytes = ByteArray(12)
            if (`is`.read(bytes) != -1) getImageType(bytes) else null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getImageType(bytes: ByteArray): ImageType {
        val type = DawnBridge.bytes2HexString(bytes).uppercase(Locale.getDefault())
        return if (type.contains("FFD8FF")) {
            ImageType.TYPE_JPG
        } else if (type.contains("89504E47")) {
            ImageType.TYPE_PNG
        } else if (type.contains("47494638")) {
            ImageType.TYPE_GIF
        } else if (type.contains("49492A00") || type.contains("4D4D002A")) {
            ImageType.TYPE_TIFF
        } else if (type.contains("424D")) {
            ImageType.TYPE_BMP
        } else if (type.startsWith("52494646") && type.endsWith("57454250")) { //524946461c57000057454250-12个字节
            ImageType.TYPE_WEBP
        } else if (type.contains("00000100") || type.contains("00000200")) {
            ImageType.TYPE_ICO
        } else {
            ImageType.TYPE_UNKNOWN
        }
    }

    private fun isJPEG(b: ByteArray): Boolean {
        return b.size >= 2 && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte()
    }

    private fun isGIF(b: ByteArray): Boolean {
        return b.size >= 6 && b[0] == 'G'.code.toByte() && b[1] == 'I'.code.toByte() && b[2] == 'F'.code.toByte() && b[3] == '8'.code.toByte() && (b[4] == '7'.code.toByte() || b[4] == '9'.code.toByte()) && b[5] == 'a'.code.toByte()
    }

    private fun isPNG(b: ByteArray): Boolean {
        return b.size >= 8 && b[0] == 137.toByte() && b[1] == 80.toByte() && b[2] == 78.toByte() && b[3] == 71.toByte() && b[4] == 13.toByte() && b[5] == 10.toByte() && b[6] == 26.toByte() && b[7] == 10.toByte()
    }

    private fun isBMP(b: ByteArray): Boolean {
        return b.size >= 2 && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d
    }

    /**
     * 使用比例返回压缩位图。
     *
     * @param src       位图的来源。
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @return 使用比例返回压缩位图。
     */
    fun compressByScale(
        src: Bitmap, newWidth: Int, newHeight: Int
    ): Bitmap? {
        return scale(src, newWidth, newHeight, false)
    }

    /**
     * 使用比例返回压缩位图。
     *
     * @param src       位图的来源。
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @param recycle   True 回收位图的来源，否则为 false。
     * @return 使用比例返回压缩位图
     */
    fun compressByScale(
        src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean
    ): Bitmap? {
        return scale(src, newWidth, newHeight, recycle)
    }

    /**
     * 使用比例返回压缩位图
     *
     * @param src         位图的来源。
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 宽度的比例。
     * @return 使用比例返回压缩位图
     */
    fun compressByScale(
        src: Bitmap, scaleWidth: Float, scaleHeight: Float
    ): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, false)
    }

    /**
     * 使用比例返回压缩位图
     *
     * @param src         位图的来源。
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 宽度的比例。
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 使用比例返回压缩位图
     */
    fun compressByScale(
        src: Bitmap, scaleWidth: Float, scaleHeight: Float, recycle: Boolean
    ): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, recycle)
    }
    /**
     * 使用质量返回压缩数据。
     *
     * @param src     位图的来源
     * @param quality 质量
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用质量返回压缩数据。
     */
    /**
     * 使用质量返回压缩数据。
     *
     * @param src     位图的来源
     * @param quality 质量
     * @return 使用质量返回压缩数据。
     */
    @JvmOverloads
    fun compressByQuality(
        src: Bitmap, @IntRange(from = 0, to = 100) quality: Int, recycle: Boolean = false
    ): ByteArray? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, quality, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        return bytes
    }
    /**
     * 使用质量返回压缩数据。
     *
     * @param src         位图的来源
     * @param maxByteSize 字节的最大大小。
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 使用质量返回压缩数据。
     */
    /**
     * 使用质量返回压缩数据。
     *
     * @param src         位图的来源
     * @param maxByteSize 字节的最大大小。
     * @return 使用质量返回压缩数据。
     */
    @JvmOverloads
    fun compressByQuality(
        src: Bitmap, maxByteSize: Long, recycle: Boolean = false
    ): ByteArray {
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return ByteArray(0)
        }
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray()
            } else {
                // find the best quality using binary search
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        return bytes
    }
    /**
     * 使用样本大小返回压缩位图。
     *
     * @param src        位图的来源
     * @param sampleSize 样本量。
     * @param recycle    True 回收位图的来源，否则为 false。
     * @return 使用样本大小返回压缩位图
     */
    /**
     * 使用样本大小返回压缩位图。
     *
     * @param src        位图的来源
     * @param sampleSize 样本量。
     * @return 使用样本大小返回压缩位图
     */
    @JvmOverloads
    fun compressBySampleSize(
        src: Bitmap, sampleSize: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }
    /**
     * 使用样本大小返回压缩位图
     *
     * @param src       位图的来源
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param recycle   True 回收位图的来源，否则为 false。
     * @return 使用样本大小返回压缩位图
     */
    /**
     * 使用样本大小返回压缩位图
     *
     * @param src       位图的来源
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 使用样本大小返回压缩位图
     */
    @JvmOverloads
    fun compressBySampleSize(
        src: Bitmap, maxWidth: Int, maxHeight: Int, recycle: Boolean = false
    ): Bitmap? {
        if (isEmptyBitmap(src)) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        if (recycle && !src.isRecycled) {
            src.recycle()
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    /**
     * 返回样本大小。
     *
     * @param options   options.
     * @param maxWidth  最大宽度。
     * @param maxHeight 最大高度。
     * @return 样本大小。
     */
    fun calculateInSampleSize(
        options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int
    ): Int {
        var height = options.outHeight
        var width = options.outWidth
        var inSampleSize = 1
        while (height > maxHeight || width > maxWidth) {
            height = height shr 1
            width = width shr 1
            inSampleSize = inSampleSize shl 1
        }
        return inSampleSize
    }

    /**
     * 返回位图的大小。
     *
     * @param filePath 文件路径
     * @return 位图的大小。
     */
    fun getSize(filePath: String?): IntArray {
        return getSize(DawnBridge.getFileByPath(filePath))
    }

    /**
     * 返回位图的大小
     *
     * @param file The file.
     * @return 返回位图的大小
     */
    fun getSize(file: File?): IntArray {
        if (file == null) {
            return intArrayOf(0, 0)
        }
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, opts)
        return intArrayOf(opts.outWidth, opts.outHeight)
    }

    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }
}