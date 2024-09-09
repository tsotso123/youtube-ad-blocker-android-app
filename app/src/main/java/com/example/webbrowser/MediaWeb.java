package com.example.webbrowser;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class MediaWeb extends WebView {

    public MediaWeb(Context context) {
        super(context);
    }

    public MediaWeb(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaWeb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility != View.GONE) super.onWindowVisibilityChanged(View.VISIBLE);
    }
}