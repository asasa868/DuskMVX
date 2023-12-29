package com.lzq.dawn.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.lzq.dawn.R;


/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
public class ShapeLoadingDialog extends Dialog {

    private LoadingView mLoadingView;

    private final Builder mBuilder;

    private ShapeLoadingDialog(Builder builder) {
        super(builder.mContext, R.style.dawn_custom_dialog);
        mBuilder = builder;
        setCancelable(mBuilder.mCancelable);
        setCanceledOnTouchOutside(mBuilder.mCanceledOnTouchOutside);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dawn_layout_dialog);

        mLoadingView = findViewById(R.id.loadView);

        mLoadingView.setDelay(mBuilder.mDelay);
        mLoadingView.setLoadingText(mBuilder.mLoadText);

        setOnDismissListener(dialog -> mLoadingView.setVisibility(View.GONE));
    }

    @Override
    public void show() {
        super.show();
        mLoadingView.setVisibility(View.VISIBLE);
    }


    public Builder getBuilder() {
        return mBuilder;
    }

    public static class Builder {

        private final Context mContext;

        private int mDelay = 80;

        private CharSequence mLoadText;

        private boolean mCancelable = true;

        private boolean mCanceledOnTouchOutside = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder delay(int delay) {
            mDelay = delay;
            return this;
        }

        public Builder loadText(CharSequence loadText) {
            mLoadText = loadText;
            return this;
        }

        public Builder loadText(int resId) {
            mLoadText = mContext.getString(resId);
            return this;
        }

        public Builder setContentTxt(CharSequence loadText) {
            mLoadText = loadText;
            return this;
        }


        public Builder cancelable(boolean cancelable) {
            mCancelable = cancelable;
            mCanceledOnTouchOutside = cancelable;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public ShapeLoadingDialog build() {
            return new ShapeLoadingDialog(this);
        }

    }
}