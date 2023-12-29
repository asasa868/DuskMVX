package com.lzq.dawn.util.snackbar;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * @Name :SnackbarUtils
 * @Time :2022/8/30 11:43
 * @Author :  Lzq
 * @Desc : Snackbar
 */
public final class SnackbarUtils {

    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final int COLOR_SUCCESS = 0xFF2BB600;
    private static final int COLOR_WARNING = 0xFFFFC100;
    private static final int COLOR_ERROR = 0xFFFF0000;
    private static final int COLOR_MESSAGE = 0xFFFFFFFF;

    private static WeakReference<Snackbar> sWeakSnackbar;

    private View view;
    private CharSequence message;
    private int messageColor;
    private int bgColor;
    private int bgResource;
    private int duration;
    private CharSequence actionText;
    private int actionTextColor;
    private View.OnClickListener actionListener;
    private int bottomMargin;

    private SnackbarUtils(final View parent) {
        setDefault();
        this.view = parent;
    }

    private void setDefault() {
        message = "";
        messageColor = COLOR_DEFAULT;
        bgColor = COLOR_DEFAULT;
        bgResource = -1;
        duration = LENGTH_SHORT;
        actionText = "";
        actionTextColor = COLOR_DEFAULT;
        bottomMargin = 0;
    }

    /**
     * 设置要从中查找父级的视图。
     *
     * @param view 从中查找父级的视图。
     * @return {@link SnackbarUtils} instance
     */
    public static SnackbarUtils with(@NonNull final View view) {
        return new SnackbarUtils(view);
    }

    /**
     * 设置消息。
     *
     * @param msg message.
     * @return {@link SnackbarUtils} instance
     */
    public SnackbarUtils setMessage(@NonNull final CharSequence msg) {
        this.message = msg;
        return this;
    }

    /**
     * 设置消息的颜色。
     *
     * @param color color
     * @return {@link SnackbarUtils} instance
     */
    public SnackbarUtils setMessageColor(@ColorInt final int color) {
        this.messageColor = color;
        return this;
    }

    /**
     * 设置背景颜色。
     *
     * @param color color
     * @return{@link SnackbarUtils} instance
     */
    public SnackbarUtils setBgColor(@ColorInt final int color) {
        this.bgColor = color;
        return this;
    }

    /**
     * 设置背景资源。
     *
     * @param bgResource 背景资源。
     * @return {@link SnackbarUtils} instance
     */
    public SnackbarUtils setBgResource(@DrawableRes final int bgResource) {
        this.bgResource = bgResource;
        return this;
    }

    /**
     * 设置持续时间。
     *
     * @param duration 持续时间
     *                 <ul>
     *                 <li>{@link Duration#LENGTH_INDEFINITE}</li>
     *                 <li>{@link Duration#LENGTH_SHORT     }</li>
     *                 <li>{@link Duration#LENGTH_LONG      }</li>
     *                 </ul>
     * @return {@link SnackbarUtils} instance
     */
    public SnackbarUtils setDuration(@Duration final int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 设置动作。
     *
     * @param text     text.
     * @param listener 单击侦听器。
     * @return {@link SnackbarUtils} instance
     */
    public SnackbarUtils setAction(@NonNull final CharSequence text,
                                   @NonNull final View.OnClickListener listener) {
        return setAction(text, COLOR_DEFAULT, listener);
    }

    /**
     * 设置动作
     *
     * @param text     text.
     * @param color    文本的颜色。
     * @param listener 单击侦听器
     * @return {@link SnackbarUtils} instance
     */

    public SnackbarUtils setAction(@NonNull final CharSequence text,
                                   @ColorInt final int color,
                                   @NonNull final View.OnClickListener listener) {
        this.actionText = text;
        this.actionTextColor = color;
        this.actionListener = listener;
        return this;
    }

    /**
     * 设置底部边距。
     *
     * @param bottomMargin 底部边距的大小，以像素为单位。
     */
    public SnackbarUtils setBottomMargin(@IntRange(from = 1) final int bottomMargin) {
        this.bottomMargin = bottomMargin;
        return this;
    }

    /**
     * 显示snackbar。
     */
    public Snackbar show() {
        return show(false);
    }

    /**
     * 显示snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示 snackbar，否则为false。
     */
    public Snackbar show(boolean isShowTop) {
        View view = this.view;
        if (view == null) {
            return null;
        }
        if (isShowTop) {
            ViewGroup suitableParent = findSuitableParentCopyFromSnackbar(view);
            View topSnackBarContainer = suitableParent.findViewWithTag("topSnackBarCoordinatorLayout");
            if (topSnackBarContainer == null) {
                CoordinatorLayout topSnackBarCoordinatorLayout = new CoordinatorLayout(view.getContext());
                topSnackBarCoordinatorLayout.setTag("topSnackBarCoordinatorLayout");
                topSnackBarCoordinatorLayout.setRotation(180);
                // bring to front
                topSnackBarCoordinatorLayout.setElevation(100);
                suitableParent.addView(topSnackBarCoordinatorLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                topSnackBarContainer = topSnackBarCoordinatorLayout;
            }
            view = topSnackBarContainer;
        }
        if (messageColor != COLOR_DEFAULT) {
            SpannableString spannableString = new SpannableString(message);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(messageColor);
            spannableString.setSpan(
                    colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, spannableString, duration));
        } else {
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, message, duration));
        }
        final Snackbar snackbar = sWeakSnackbar.get();
        final Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        if (isShowTop) {
            for (int i = 0; i < snackbarView.getChildCount(); i++) {
                View child = snackbarView.getChildAt(i);
                child.setRotation(180);
            }
        }
        if (bgResource != -1) {
            snackbarView.setBackgroundResource(bgResource);
        } else if (bgColor != COLOR_DEFAULT) {
            snackbarView.setBackgroundColor(bgColor);
        }
        if (bottomMargin != 0) {
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
            params.bottomMargin = bottomMargin;
        }
        if (actionText.length() > 0 && actionListener != null) {
            if (actionTextColor != COLOR_DEFAULT) {
                snackbar.setActionTextColor(actionTextColor);
            }
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
        return snackbar;
    }


    /**
     * 以成功风格显示snackbar。
     */
    public void showSuccess() {
        showSuccess(false);
    }

    /**
     * 以成功风格显示snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    public void showSuccess(boolean isShowTop) {
        bgColor = COLOR_SUCCESS;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * 显示带有警告样式的snackbar。
     */
    public void showWarning() {
        showWarning(false);
    }

    /**
     * 显示带有警告样式的snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    public void showWarning(boolean isShowTop) {
        bgColor = COLOR_WARNING;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * 显示带有错误样式的snackbar。
     */
    public void showError() {
        showError(false);
    }

    /**
     * 显示带有错误样式的snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    public void showError(boolean isShowTop) {
        bgColor = COLOR_ERROR;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Dismiss snackbar.
     */
    public static void dismiss() {
        if (sWeakSnackbar != null && sWeakSnackbar.get() != null) {
            sWeakSnackbar.get().dismiss();
            sWeakSnackbar = null;
        }
    }

    /**
     * 返回snackbar的视图。
     *
     * @return view
     */
    public static View getView() {
        Snackbar snackbar = sWeakSnackbar.get();
        if (snackbar == null) {
            return null;
        }
        return snackbar.getView();
    }

    /**
     * 将视图添加到snackbar。
     * <p>在{@link #show（）}<p>之后调用它
     *
     * @param layoutId 布局的id。
     * @param params   params.
     */
    public static void addView(@LayoutRes final int layoutId,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) view;
            View child = LayoutInflater.from(view.getContext()).inflate(layoutId, null);
            layout.addView(child, -1, params);
        }
    }

    /**
     * 将视图添加到snackbar。
     * <p>在{@link #show（）}<p>之后调用它
     *
     * @param child view
     * @param params  params.
     */
    public static void addView(@NonNull final View child,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) view;
            layout.addView(child, params);
        }
    }

    private static ViewGroup findSuitableParentCopyFromSnackbar(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            }

            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                }

                fallback = (ViewGroup) view;
            }

            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        return fallback;
    }
}
