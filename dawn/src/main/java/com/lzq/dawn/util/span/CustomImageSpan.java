package com.lzq.dawn.util.span;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.lzq.dawn.DawnBridge;

import java.io.InputStream;

/**
 * @Name :CustomImageSpan
 * @Time :2022/8/30 14:30
 * @Author :  Lzq
 * @Desc :
 */
 class CustomImageSpan extends CustomDynamicDrawableSpan {
    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;

    public CustomImageSpan(final Bitmap b, final int verticalAlignment) {
        super(verticalAlignment);
        mDrawable = new BitmapDrawable(DawnBridge.getApp().getResources(), b);
        mDrawable.setBounds(
                0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()
        );
    }

    public CustomImageSpan(final Drawable d, final int verticalAlignment) {
        super(verticalAlignment);
        mDrawable = d;
        mDrawable.setBounds(
                0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()
        );
    }

    public CustomImageSpan(final Uri uri, final int verticalAlignment) {
        super(verticalAlignment);
        mContentUri = uri;
    }

    public CustomImageSpan(@DrawableRes final int resourceId, final int verticalAlignment) {
        super(verticalAlignment);
        mResourceId = resourceId;
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;
        if (mDrawable != null) {
            drawable = mDrawable;
        } else if (mContentUri != null) {
            Bitmap bitmap;
            try {
                InputStream is =
                        DawnBridge.getApp().getContentResolver().openInputStream(mContentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(DawnBridge.getApp().getResources(), bitmap);
                drawable.setBounds(
                        0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                );
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                Log.e("sms", "Failed to loaded content " + mContentUri, e);
            }
        } else {
            try {
                drawable = ContextCompat.getDrawable(DawnBridge.getApp(), mResourceId);
                drawable.setBounds(
                        0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                );
            } catch (Exception e) {
                Log.e("sms", "Unable to find resource: " + mResourceId);
            }
        }
        return drawable;
    }
}
