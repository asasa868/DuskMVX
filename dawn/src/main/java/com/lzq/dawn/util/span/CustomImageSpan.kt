package com.lzq.dawn.util.span

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.lzq.dawn.DawnBridge.app

/**
 * @Name :CustomImageSpan
 * @Time :2022/8/30 14:30
 * @Author :  Lzq
 * @Desc :
 */
internal class CustomImageSpan : CustomDynamicDrawableSpan {
    private var mDrawable: Drawable? = null
    private var mContentUri: Uri? = null
    private var mResourceId = 0

    constructor(b: Bitmap?, verticalAlignment: Int) : super(verticalAlignment) {
        mDrawable = BitmapDrawable(app.resources, b)
        mDrawable?.setBounds(
            0, 0, mDrawable?.intrinsicWidth ?:0, mDrawable?.intrinsicHeight ?:0
        )
    }

    constructor(d: Drawable?, verticalAlignment: Int) : super(verticalAlignment) {
        mDrawable = d
        mDrawable?.setBounds(
            0, 0, mDrawable?.intrinsicWidth?:0, mDrawable?.intrinsicHeight?:0
        )
    }

    constructor(uri: Uri?, verticalAlignment: Int) : super(verticalAlignment) {
        mContentUri = uri
    }

    constructor(@DrawableRes resourceId: Int, verticalAlignment: Int) : super(verticalAlignment) {
        mResourceId = resourceId
    }

    override val drawable: Drawable?
        get() {
            var drawable: Drawable? = null
            if (mDrawable != null) {
                drawable = mDrawable
            } else if (mContentUri != null) {
                val bitmap: Bitmap
                try {
                    val `is` = app.contentResolver.openInputStream(
                        mContentUri!!
                    )
                    bitmap = BitmapFactory.decodeStream(`is`)
                    drawable = BitmapDrawable(app.resources, bitmap)
                    drawable.setBounds(
                        0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                    )
                    `is`?.close()
                } catch (e: Exception) {
                    Log.e("sms", "Failed to loaded content $mContentUri", e)
                }
            } else {
                try {
                    drawable = ContextCompat.getDrawable(app, mResourceId)
                    drawable!!.setBounds(
                        0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight
                    )
                } catch (e: Exception) {
                    Log.e("sms", "Unable to find resource: $mResourceId")
                }
            }
            return drawable
        }
}