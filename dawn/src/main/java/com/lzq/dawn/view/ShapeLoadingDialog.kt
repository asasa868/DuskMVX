package com.lzq.dawn.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.lzq.dawn.R

/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
class ShapeLoadingDialog private constructor(val builder: Builder) : Dialog(
    builder.mContext, R.style.dawn_custom_dialog
) {
    private var mLoadingView: LoadingView? = null

    init {
        setCancelable(builder.mCancelable)
        setCanceledOnTouchOutside(builder.mCanceledOnTouchOutside)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dawn_layout_dialog)
        mLoadingView = findViewById(R.id.dawn_loading_view)
        mLoadingView?.delay = builder.mDelay
        mLoadingView?.loadingText = builder.mLoadText
        setOnDismissListener { mLoadingView?.visibility = View.GONE }
    }

    override fun show() {
        super.show()
        mLoadingView!!.visibility = View.VISIBLE
    }

    class Builder(val mContext: Context) {
        var mDelay = 80
        var mLoadText: CharSequence? = null
        var mCancelable = true
        var mCanceledOnTouchOutside = true
        fun delay(delay: Int): Builder {
            mDelay = delay
            return this
        }

        fun loadText(loadText: CharSequence?): Builder {
            mLoadText = loadText
            return this
        }

        fun loadText(resId: Int): Builder {
            mLoadText = mContext.getString(resId)
            return this
        }

        fun setContentTxt(loadText: CharSequence?): Builder {
            mLoadText = loadText
            return this
        }

        fun cancelable(cancelable: Boolean): Builder {
            mCancelable = cancelable
            mCanceledOnTouchOutside = cancelable
            return this
        }

        fun canceledOnTouchOutside(canceledOnTouchOutside: Boolean): Builder {
            mCanceledOnTouchOutside = canceledOnTouchOutside
            return this
        }

        fun build(): ShapeLoadingDialog {
            return ShapeLoadingDialog(this)
        }
    }
}