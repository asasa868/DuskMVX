package com.lzq.dawn.view

import android.content.Context
import android.util.AttributeSet

/** 保留原加载视图的类名、构造器和继承的公开行为，兼容既有布局与调用方。 */
class LoadingView : DawnLoadingView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    companion object {}
}
