package com.lzq.dawn.util.image;

import android.Manifest;
import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Name :ImageUtils
 * @Time :2022/8/1 15:48
 * @Author :  Lzq
 * @Desc : image
 */
public final class ImageUtils {
    private ImageUtils() {
    }

    /**
     * Bitmap 转 bytes.
     *
     * @param bitmap bitmap.
     * @return bytes
     */
    public static byte[] bitmap2Bytes(final Bitmap bitmap) {
        return bitmap2Bytes(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    /**
     * Bitmap 转 bytes.
     *
     * @param bitmap  bitmap.
     * @param format  位图的格式。
     * @param quality 质量。
     * @return bytes
     */
    public static byte[] bitmap2Bytes(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format, int quality) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        return baos.toByteArray();
    }


    /**
     * Bytes 转 bitmap.
     *
     * @param bytes bytes.
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(@Nullable final byte[] bytes) {
        return (bytes == null || bytes.length == 0)
                ? null
                : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Drawable 转 bitmap.
     *
     * @param drawable drawable.
     * @return bitmap
     */
    public static Bitmap drawable2Bitmap(@Nullable final Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap 转 drawable.
     *
     * @param bitmap bitmap.
     * @return drawable
     */
    public static Drawable bitmap2Drawable(@Nullable final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(DawnBridge.getApp().getResources(), bitmap);
    }


    /**
     * Drawable 转 bytes.
     *
     * @param drawable The drawable.
     * @return bytes
     */
    public static byte[] drawable2Bytes(@Nullable final Drawable drawable) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable));
    }

    /**
     * Drawable 转 bytes.
     *
     * @param drawable drawable.
     * @param format   位图的格式
     * @return bytes
     */
    public static byte[] drawable2Bytes(final Drawable drawable, final Bitmap.CompressFormat format, int quality) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format, quality);
    }

    /**
     * Bytes 转 drawable.
     *
     * @param bytes bytes.
     * @return drawable
     */
    public static Drawable bytes2Drawable(final byte[] bytes) {
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    /**
     * View 转 bitmap.
     *
     * @param view view.
     * @return bitmap
     */
    public static Bitmap view2Bitmap(final View view) {
        if (view == null) {
            return null;
        }
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (null == drawingCache || drawingCache.isRecycled()) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            drawingCache = view.getDrawingCache();
            if (null == drawingCache || drawingCache.isRecycled()) {
                bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
            } else {
                bitmap = Bitmap.createBitmap(drawingCache);
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;
    }

    /**
     * 返回 bitmap.
     *
     * @param file file.
     * @return bitmap
     */
    public static Bitmap getBitmap(final File file) {
        if (file == null) {
            return null;
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * 返回 bitmap.
     *
     * @param file      file.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final File file, final int maxWidth, final int maxHeight) {
        if (file == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }


    /**
     * 返回 bitmap.
     *
     * @param filePath 文件的路径。
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath) {
        if (DawnBridge.isSpace(filePath)) {
            return null;
        }
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 返回 bitmap.
     *
     * @param filePath  文件的路径.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath, final int maxWidth, final int maxHeight) {
        if (DawnBridge.isSpace(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 返回 bitmap.
     *
     * @param is 输入流。
     * @return bitmap
     */
    public static Bitmap getBitmap(final InputStream is) {
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 返回 bitmap.
     *
     * @param is        输入流.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final InputStream is, final int maxWidth, final int maxHeight) {
        if (is == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 返回 bitmap.
     *
     * @param data   data.
     * @param offset 偏移量。
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data, final int offset) {
        if (data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, offset, data.length);
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
    public static Bitmap getBitmap(final byte[] data,
                                   final int offset,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (data.length == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    /**
     * 返回 bitmap.
     *
     * @param resId resource id.
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes final int resId) {
        Drawable drawable = ContextCompat.getDrawable(DawnBridge.getApp(), resId);
        if (drawable == null) {
            return null;
        }
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 返回 bitmap.
     *
     * @param resId     resource id.
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes final int resId,
                                   final int maxWidth,
                                   final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = DawnBridge.getApp().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 返回 bitmap.
     *
     * @param fd 文件描述符
     * @return bitmap
     */
    public static Bitmap getBitmap(final FileDescriptor fd) {
        if (fd == null) {
            return null;
        }
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    /**
     * 返回 bitmap.
     *
     * @param fd        文件描述符
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final FileDescriptor fd,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (fd == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 返回具有指定颜色的位图。
     *
     * @param src   位图的来源。
     * @param color color.
     * @return 具有指定颜色的位图。
     */
    public static Bitmap drawColor(@NonNull final Bitmap src, @ColorInt final int color) {
        return drawColor(src, color, false);
    }

    /**
     * 返回具有指定颜色的位图。
     *
     * @param src     位图的来源
     * @param color   color.
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 具有指定颜色的位图。
     */
    public static Bitmap drawColor(@NonNull final Bitmap src,
                                   @ColorInt final int color,
                                   final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        Canvas canvas = new Canvas(ret);
        canvas.drawColor(color, PorterDuff.Mode.DARKEN);
        return ret;
    }

    /**
     * 返回缩放的位图。
     *
     * @param src       位图的来源
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @return 缩放的位图。
     */
    public static Bitmap scale(final Bitmap src, final int newWidth, final int newHeight) {
        return scale(src, newWidth, newHeight, false);
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
    public static Bitmap scale(final Bitmap src,
                               final int newWidth,
                               final int newHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回缩放的位图
     *
     * @param src         位图的来源
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 高度的比例。
     * @return 返回缩放的位图
     */
    public static Bitmap scale(final Bitmap src, final float scaleWidth, final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 返回缩放的位图
     *
     * @param src         位图的来源
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 高度的比例。
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 返回缩放的位图
     */
    public static Bitmap scale(final Bitmap src,
                               final float scaleWidth,
                               final float scaleHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
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
    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * 返回剪切的位图。
     *
     * @param src     位图的来源
     * @param x       第一个像素的 x 坐标。
     * @param y       第一个像素的 y 坐标。
     * @param width   width.
     * @param height  height.
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回剪切的位图。
     */
    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回倾斜的位图。
     *
     * @param src 位图的来源
     * @param kx  x 的偏斜系数。
     * @param ky  y 的偏斜系数。
     * @return 倾斜的位图。
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky) {
        return skew(src, kx, ky, 0, 0, false);
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
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final boolean recycle) {
        return skew(src, kx, ky, 0, 0, recycle);
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
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py) {
        return skew(src, kx, ky, px, py, false);
    }

    /**
     * 返回倾斜的位图。
     *
     * @param src     位图的来源
     * @param kx      x 的偏斜系数。
     * @param ky      y 的偏斜系数。
     * @param px      轴心点的 x 坐标。
     * @param py      轴心点的 y 坐标。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回倾斜的位图。
     */
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
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
    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * 返回旋转的位图。
     *
     * @param src     位图的来源
     * @param degrees 度数。
     * @param px      轴心点的 x 坐标。
     * @param py      轴心点的 y 坐标。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回旋转的位图。
     */
    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py,
                                final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        if (degrees == 0) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回旋转的度数。
     *
     * @param filePath 文件的路径。
     * @return 旋转的度数。
     */
    public static int getRotateDegree(final String filePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 返回圆形位图。
     *
     * @param src 位图的来源。
     * @return 圆形位图。
     */
    public static Bitmap toRound(final Bitmap src) {
        return toRound(src, 0, 0, false);
    }

    /**
     * 返回圆形位图
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回圆形位图
     */
    public static Bitmap toRound(final Bitmap src, final boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }

    /**
     * 返回圆形位图
     *
     * @param src         位图的来源
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @return 返回圆形位图
     */
    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }

    /**
     * 返回圆形位图
     *
     * @param src         位图的来源
     * @param recycle     True 回收位图的来源，否则为 false。
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @return 返回圆形位图
     */
    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor,
                                 final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        if (width != height) {
            matrix.preScale((float) size / width, (float) size / height);
        }
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回圆角位图。
     *
     * @param src    位图的来源
     * @param radius 圆角的半径。
     * @return 圆角位图。
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    /**
     * 返回圆角位图。
     *
     * @param src     位图的来源
     * @param radius  圆角的半径。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回圆角位图。
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       final boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
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
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @FloatRange(from = 0) float borderSize,
                                       @ColorInt int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
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
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float[] radii,
                                       @FloatRange(from = 0) float borderSize,
                                       @ColorInt int borderColor) {
        return toRoundCorner(src, radii, borderSize, borderColor, false);
    }

    /**
     * 返回圆角位图。
     *
     * @param src         位图的来源
     * @param radius      圆角的半径。
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 返回圆角位图。
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @FloatRange(from = 0) float borderSize,
                                       @ColorInt int borderColor,
                                       final boolean recycle) {
        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
        return toRoundCorner(src, radii, borderSize, borderColor, recycle);
    }

    /**
     * 返回圆角位图。
     *
     * @param src         位图的来源
     * @param radii       8 个值的数组，4 对 [X,Y] 半径
     * @param borderSize  边框的大小
     * @param borderColor 边框的颜色
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 返回圆角位图。
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float[] radii,
                                       @FloatRange(from = 0) float borderSize,
                                       @ColorInt int borderColor,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        Path path = new Path();
        path.addRoundRect(rectF, radii, Path.Direction.CW);
        canvas.drawPath(path, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawPath(path, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
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
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius) {
        return addBorder(src, borderSize, color, false, cornerRadius, false);
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
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color,
                                         final float[] radii) {
        return addBorder(src, borderSize, color, false, radii, false);
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
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color,
                                         final float[] radii,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, false, radii, recycle);
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
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle);
    }

    /**
     * 返回带边框的圆形位图。
     *
     * @param src        位图的来源
     * @param borderSize 边框的大小
     * @param color      边框的颜色
     * @return 返回带边框的圆形位图。
     */
    public static Bitmap addCircleBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color) {
        return addBorder(src, borderSize, color, true, 0, false);
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
    public static Bitmap addCircleBorder(final Bitmap src,
                                         @FloatRange(from = 1) final float borderSize,
                                         @ColorInt final int color,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, true, 0, recycle);
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
    private static Bitmap addBorder(final Bitmap src,
                                    @FloatRange(from = 1) final float borderSize,
                                    @ColorInt final int color,
                                    final boolean isCircle,
                                    final float cornerRadius,
                                    final boolean recycle) {
        float[] radii = {cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                cornerRadius, cornerRadius, cornerRadius, cornerRadius};
        return addBorder(src, borderSize, color, isCircle, radii, recycle);
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
    private static Bitmap addBorder(final Bitmap src,
                                    @FloatRange(from = 1) final float borderSize,
                                    @ColorInt final int color,
                                    final boolean isCircle,
                                    final float[] radii,
                                    final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        int width = ret.getWidth();
        int height = ret.getHeight();
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderSize);
        if (isCircle) {
            float radius = Math.min(width, height) / 2f - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        } else {
            RectF rectF = new RectF(0, 0, width, height);
            float halfBorderSize = borderSize / 2f;
            rectF.inset(halfBorderSize, halfBorderSize);
            Path path = new Path();
            path.addRoundRect(rectF, radii, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }
        return ret;
    }

    /**
     * 用反射返回位图。
     *
     * @param src              位图的来源
     * @param reflectionHeight 反射的高度
     * @return 用反射返回位图。
     */
    public static Bitmap addReflection(final Bitmap src, final int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }

    /**
     * 用反射返回位图。
     *
     * @param src              位图的来源
     * @param reflectionHeight 反射的高度
     * @param recycle          True 回收位图的来源，否则为 false。
     * @return 用反射返回位图。
     */
    public static Bitmap addReflection(final Bitmap src,
                                       final int reflectionHeight,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        final int REFLECTION_GAP = 0;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false);
        Bitmap ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient shader = new LinearGradient(
                0, srcHeight,
                0, ret.getHeight() + REFLECTION_GAP,
                0x70FFFFFF,
                0x00FFFFFF,
                Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, srcHeight + REFLECTION_GAP, srcWidth, ret.getHeight(), paint);
        if (!reflectionBitmap.isRecycled()) {
            reflectionBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
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
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final int textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y) {
        return addTextWatermark(src, content, textSize, color, x, y, false);
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
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final float textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y,
                                          final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回带有图像水印的位图。
     *
     * @param src       位图的来源
     * @param watermark 图片水印。
     * @param x         第一个像素的 x 坐标。
     * @param y         第一个像素的 y 坐标。
     * @param alpha     水印的alpha
     * @return 返回带有图像水印的位图。
     */
    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x, final int y,
                                           final int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
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
    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x,
                                           final int y,
                                           final int alpha,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回 alpha 位图
     *
     * @param src 位图的来源
     * @return 返回 alpha 位图
     */
    public static Bitmap toAlpha(final Bitmap src) {
        return toAlpha(src, false);
    }

    /**
     * 返回 alpha 位图
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回 alpha 位图
     */
    public static Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回灰色位图。
     *
     * @param src 位图的来源
     * @return 返回灰色位图。
     */
    public static Bitmap toGray(final Bitmap src) {
        return toGray(src, false);
    }

    /**
     * 返回灰色位图。
     *
     * @param src     位图的来源
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 返回灰色位图。
     */
    public static Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    /**
     * 返回模糊位图。
     * <p>zoom out, blur, zoom in</p>
     *
     * @param src    位图的来源
     * @param scale  比例（0...1）。
     * @param radius radius(0...25).
     * @return 模糊位图。
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius) {
        return fastBlur(src, scale, radius, false, false);
    }

    /**
     * 返回模糊位图。
     * <p>zoom out, blur, zoom in</p>
     *
     * @param src    位图的来源
     * @param scale  比例（0...1）。
     * @param radius radius(0...25).
     * @return 返回模糊位图。
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius,
                                  final boolean recycle) {
        return fastBlur(src, scale, radius, recycle, false);
    }

    /**
     * 返回模糊位图。
     * <p>zoom out, blur, zoom in</p>
     *
     * @param src           位图的来源
     * @param scale         比例（0...1）。
     * @param radius        radius(0...25).
     * @param recycle       True 回收位图的来源，否则为 false。
     * @param isReturnScale 返回比例模糊位图为真，否则为假。
     * @return 返回模糊位图
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius,
                                  final boolean recycle,
                                  final boolean isReturnScale) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaleBitmap =
                Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.scale(scale, scale);
        canvas.drawBitmap(scaleBitmap, 0, 0, paint);
        scaleBitmap = renderScriptBlur(scaleBitmap, radius, recycle);
        if (scale == 1 || isReturnScale) {
            if (recycle && !src.isRecycled() && scaleBitmap != src) {
                src.recycle();
            }
            return scaleBitmap;
        }
        Bitmap ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true);
        if (!scaleBitmap.isRecycled()) {
            scaleBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }


    /**
     * 使用渲染脚本返回模糊位图。
     *
     * @param src    位图的来源
     * @param radius radius(0...25).
     * @return 使用渲染脚本返回模糊位图。
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius) {
        return renderScriptBlur(src, radius, false);
    }

    /**
     * 使用渲染脚本返回模糊位图。
     *
     * @param src     位图的来源
     * @param radius  radius(0...25).
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用渲染脚本返回模糊位图。
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius,
                                          final boolean recycle) {
        RenderScript rs = null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        try {
            rs = RenderScript.create(DawnBridge.getApp());
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs,
                    ret,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(ret);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return ret;
    }

    /**
     * 使用堆栈返回模糊位图。
     *
     * @param src    位图的来源。
     * @param radius radius(0...25).
     * @return 使用堆栈返回模糊位图。
     */
    public static Bitmap stackBlur(final Bitmap src, final int radius) {
        return stackBlur(src, radius, false);
    }

    /**
     * 使用堆栈返回模糊位图
     *
     * @param src     位图的来源
     * @param radius  radius(0...25).
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用堆栈返回模糊位图
     */
    public static Bitmap stackBlur(final Bitmap src, int radius, final boolean recycle) {
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        if (radius < 1) {
            radius = 1;
        }
        int w = ret.getWidth();
        int h = ret.getHeight();

        int[] pix = new int[w * h];
        ret.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h);
        return ret;
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final Bitmap.CompressFormat format) {
        return save(src, filePath, format, 100, false);
    }

    /**
     * 保存位图。
     *
     * @param src    位图的来源
     * @param file   file.
     * @param format 图像的格式
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final File file, final Bitmap.CompressFormat format) {
        return save(src, file, format, 100, false);
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @param recycle  True 回收位图的来源，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final Bitmap.CompressFormat format,
                               final boolean recycle) {
        return save(src, filePath, format, 100, recycle);
    }

    /**
     * 保存位图。
     *
     * @param src     位图的来源
     * @param file    file.
     * @param format  图像的格式。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final File file,
                               final Bitmap.CompressFormat format,
                               final boolean recycle) {
        return save(src, file, format, 100, recycle);
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @param quality  提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final Bitmap.CompressFormat format,
                               final int quality) {
        return save(src, DawnBridge.getFileByPath(filePath), format, quality, false);
    }

    /**
     * 保存位图。
     *
     * @param src    位图的来源
     * @param file   file.
     * @param format 图像的格式。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final File file,
                               final Bitmap.CompressFormat format,
                               final int quality) {
        return save(src, file, format, quality, false);
    }

    /**
     * 保存位图。
     *
     * @param src      位图的来源
     * @param filePath 文件的路径。
     * @param format   图像的格式。
     * @param quality  提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle  True 回收位图的来源，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final Bitmap.CompressFormat format,
                               final int quality,
                               final boolean recycle) {
        return save(src, DawnBridge.getFileByPath(filePath), format, quality, recycle);
    }

    /**
     * 保存位图。
     *
     * @param src     位图的来源
     * @param file    file.
     * @param format  图像的格式。
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @param recycle True 回收位图的来源，否则为 false。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final File file,
                               final Bitmap.CompressFormat format,
                               final int quality,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) {
            Log.e("ImageUtils", "bitmap is empty.");
            return false;
        }
        if (src.isRecycled()) {
            Log.e("ImageUtils", "bitmap is recycled.");
            return false;
        }
        if (!DawnBridge.createFileByDeleteOldFile(file)) {
            Log.e("ImageUtils", "create or delete file <" + file + "> failed.");
            return false;
        }
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, quality, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    /**
     * 保存到相册
     *
     * @param src    位图的来源
     * @param format 图像的格式。
     * @return 文件保存成功，否则返回null。
     */
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final Bitmap.CompressFormat format) {
        return save2Album(src, "", format, 100, false);
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param format  图像的格式。
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 文件保存成功，否则返回null
     */
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final Bitmap.CompressFormat format,
                                  final boolean recycle) {
        return save2Album(src, "", format, 100, recycle);
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param format  图像的格式。
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @return 文件保存成功，否则返回null
     */
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final Bitmap.CompressFormat format,
                                  final int quality) {
        return save2Album(src, "", format, quality, false);
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
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final Bitmap.CompressFormat format,
                                  final int quality,
                                  final boolean recycle) {
        return save2Album(src, "", format, quality, recycle);
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @return 文件保存成功，否则返回null
     */
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final String dirName,
                                  final Bitmap.CompressFormat format) {
        return save2Album(src, dirName, format, 100, false);
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
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final String dirName,
                                  final Bitmap.CompressFormat format,
                                  final boolean recycle) {
        return save2Album(src, dirName, format, 100, recycle);
    }

    /**
     * 保存到相册
     *
     * @param src     位图的来源
     * @param dirName 目录的名称。
     * @param format  图像的格式
     * @param quality 提示压缩机，0-100。 0 表示压缩为小尺寸，100 表示压缩为最大质量。某些格式，例如无损的 PNG，将忽略质量设置
     * @return 文件保存成功，否则返回null
     */
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final String dirName,
                                  final Bitmap.CompressFormat format,
                                  final int quality) {
        return save2Album(src, dirName, format, quality, false);
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
    @Nullable
    public static File save2Album(final Bitmap src,
                                  final String dirName,
                                  final Bitmap.CompressFormat format,
                                  final int quality,
                                  final boolean recycle) {
        String safeDirName = TextUtils.isEmpty(dirName) ? DawnBridge.getApp().getPackageName() : dirName;
        String suffix = Bitmap.CompressFormat.JPEG.equals(format) ? "JPG" : format.name();
        String fileName = System.currentTimeMillis() + "_" + quality + "." + suffix;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!DawnBridge.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("ImageUtils", "save to album need storage permission");
                return null;
            }
            File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File destFile = new File(picDir, safeDirName + "/" + fileName);
            if (!save(src, destFile, format, quality, recycle)) {
                return null;
            }
            DawnBridge.notifySystemToScan(destFile);
            return destFile;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            Uri contentUri;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else {
                contentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            }
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + safeDirName);
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
            Uri uri = DawnBridge.getApp().getContentResolver().insert(contentUri, contentValues);
            if (uri == null) {
                return null;
            }
            OutputStream os = null;
            try {
                os = DawnBridge.getApp().getContentResolver().openOutputStream(uri);
                src.compress(format, quality, os);

                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                DawnBridge.getApp().getContentResolver().update(uri, contentValues, null, null);

                return DawnBridge.uri2File(uri);
            } catch (Exception e) {
                DawnBridge.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据文件名返回是否为图片
     *
     * @param file file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return isImage(file.getPath());
    }

    /**
     * 根据文件名返回是否为图片
     *
     * @param filePath 文件的路径。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final String filePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            return options.outWidth > 0 && options.outHeight > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回图像的类型。
     *
     * @param filePath 文件的路径。
     * @return 图像的类型。
     */
    public static ImageType getImageType(final String filePath) {
        return getImageType(DawnBridge.getFileByPath(filePath));
    }

    /**
     * 返回图像的类型
     *
     * @param file file.
     * @return 图像的类型。
     */
    public static ImageType getImageType(final File file) {
        if (file == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            ImageType type = getImageType(is);
            if (type != null) {
                return type;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static ImageType getImageType(final InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[12];
            return is.read(bytes) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ImageType getImageType(final byte[] bytes) {
        String type = DawnBridge.bytes2HexString(bytes).toUpperCase();
        if (type.contains("FFD8FF")) {
            return ImageType.TYPE_JPG;
        } else if (type.contains("89504E47")) {
            return ImageType.TYPE_PNG;
        } else if (type.contains("47494638")) {
            return ImageType.TYPE_GIF;
        } else if (type.contains("49492A00") || type.contains("4D4D002A")) {
            return ImageType.TYPE_TIFF;
        } else if (type.contains("424D")) {
            return ImageType.TYPE_BMP;
        } else if (type.startsWith("52494646") && type.endsWith("57454250")) {//524946461c57000057454250-12个字节
            return ImageType.TYPE_WEBP;
        } else if (type.contains("00000100") || type.contains("00000200")) {
            return ImageType.TYPE_ICO;
        } else {
            return ImageType.TYPE_UNKNOWN;
        }
    }

    private static boolean isJPEG(final byte[] b) {
        return b.length >= 2
                && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(final byte[] b) {
        return b.length >= 6
                && b[0] == 'G' && b[1] == 'I'
                && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(final byte[] b) {
        return b.length >= 8
                && (b[0] == (byte) 137 && b[1] == (byte) 80
                && b[2] == (byte) 78 && b[3] == (byte) 71
                && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(final byte[] b) {
        return b.length >= 2
                && (b[0] == 0x42) && (b[1] == 0x4d);
    }


    /**
     * 使用比例返回压缩位图。
     *
     * @param src       位图的来源。
     * @param newWidth  新的宽度。
     * @param newHeight 新的高度。
     * @return 使用比例返回压缩位图。
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight) {
        return scale(src, newWidth, newHeight, false);
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
    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight,
                                         final boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 使用比例返回压缩位图
     *
     * @param src         位图的来源。
     * @param scaleWidth  宽度的比例。
     * @param scaleHeight 宽度的比例。
     * @return 使用比例返回压缩位图
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
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
    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight,
                                         final boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 使用质量返回压缩数据。
     *
     * @param src     位图的来源
     * @param quality 质量
     * @return 使用质量返回压缩数据。
     */
    public static byte[] compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 使用质量返回压缩数据。
     *
     * @param src     位图的来源
     * @param quality 质量
     * @param recycle True 回收位图的来源，否则为 false。
     * @return 使用质量返回压缩数据。
     */
    public static byte[] compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }

    /**
     * 使用质量返回压缩数据。
     *
     * @param src         位图的来源
     * @param maxByteSize 字节的最大大小。
     * @return 使用质量返回压缩数据。
     */
    public static byte[] compressByQuality(final Bitmap src, final long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 使用质量返回压缩数据。
     *
     * @param src         位图的来源
     * @param maxByteSize 字节的最大大小。
     * @param recycle     True 回收位图的来源，否则为 false。
     * @return 使用质量返回压缩数据。
     */
    public static byte[] compressByQuality(final Bitmap src,
                                           final long maxByteSize,
                                           final boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                // find the best quality using binary search
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }

    /**
     * 使用样本大小返回压缩位图。
     *
     * @param src        位图的来源
     * @param sampleSize 样本量。
     * @return 使用样本大小返回压缩位图
     */

    public static Bitmap compressBySampleSize(final Bitmap src, final int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * 使用样本大小返回压缩位图。
     *
     * @param src        位图的来源
     * @param sampleSize 样本量。
     * @param recycle    True 回收位图的来源，否则为 false。
     * @return 使用样本大小返回压缩位图
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int sampleSize,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 使用样本大小返回压缩位图
     *
     * @param src       位图的来源
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 使用样本大小返回压缩位图
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
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
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    /**
     * 返回样本大小。
     *
     * @param options   options.
     * @param maxWidth  最大宽度。
     * @param maxHeight 最大高度。
     * @return 样本大小。
     */
    public static int calculateInSampleSize(final BitmapFactory.Options options,
                                            final int maxWidth,
                                            final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    /**
     * 返回位图的大小。
     *
     * @param filePath 文件路径
     * @return 位图的大小。
     */
    public static int[] getSize(String filePath) {
        return getSize(DawnBridge.getFileByPath(filePath));
    }

    /**
     * 返回位图的大小
     *
     * @param file The file.
     * @return 返回位图的大小
     */
    public static int[] getSize(File file) {
        if (file == null) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
