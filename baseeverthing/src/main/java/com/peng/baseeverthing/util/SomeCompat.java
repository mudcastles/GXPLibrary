package com.peng.baseeverthing.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

public class SomeCompat {

    private interface CompatImpl {

        void setNavigationIcon(@NonNull Toolbar toolbar, @NonNull Context context, @DrawableRes int drawableId);

        Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableId);

        void polishDrawable(@NonNull Context context, @NonNull Drawable drawable, @ColorRes int color);
    }

    private static final CompatImpl IMPL;

    static {
        final int version = Build.VERSION.SDK_INT;

        if (version >= Build.VERSION_CODES.M) {
            IMPL = new MarshmallowCompatImpl();
        } else if (version >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new LollipopCompatImpl();
        } else {
            IMPL = new BaseCompatImpl();
        }
    }

    /**
     * Base
     */
    private static class BaseCompatImpl implements CompatImpl {

        @Override
        public void setNavigationIcon(@NonNull Toolbar toolbar, @NonNull Context context, @DrawableRes int drawableId) {
            toolbar.setNavigationIcon(context.getResources().getDrawable(drawableId));
        }

        @Override
        public Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableId) {
            return context.getResources().getDrawable(drawableId);
        }

        @Override
        public void polishDrawable(@NonNull Context context, @NonNull Drawable drawable, @ColorRes int color) {
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.mutate()), context.getResources().getColor(color));
        }
    }

    /**
     * Lollipop
     */
    private @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static class LollipopCompatImpl extends BaseCompatImpl {

        @Override
        public void setNavigationIcon(Toolbar toolbar, Context context, @DrawableRes int drawableId) {
            toolbar.setNavigationIcon(context.getResources().getDrawable(drawableId, context.getTheme()));
        }

        @Override
        public Drawable getDrawable(@NonNull Context context, @DrawableRes int drawable) {
            return context.getResources().getDrawable(drawable, context.getTheme());
        }
    }

    /**
     * Marshmallow
     */
    private @TargetApi(Build.VERSION_CODES.M)
    static class MarshmallowCompatImpl extends LollipopCompatImpl {
        @Override
        public void polishDrawable(@NonNull Context context, @NonNull Drawable drawable, @ColorRes int color) {
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.mutate()),
                    context.getResources().getColor(color, context.getTheme()));
        }
    }

    public static void setNavigationIcon(@NonNull Toolbar toolbar, @NonNull Context context,
                                         @DrawableRes int drawableId) {
        IMPL.setNavigationIcon(toolbar, context, drawableId);
    }

    @CheckResult
    @Nullable
    public static Drawable getImageDrawable(@NonNull Context context, @DrawableRes int drawable) {
        return IMPL.getDrawable(context, drawable);
    }

    public static void polishDrawable(@NonNull Context context, Drawable drawable, @ColorRes int color) {
        IMPL.polishDrawable(context, drawable, color);
    }
}
