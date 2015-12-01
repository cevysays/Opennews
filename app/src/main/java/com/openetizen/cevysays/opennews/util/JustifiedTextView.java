package com.openetizen.cevysays.opennews.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;

import com.openetizen.cevysays.opennews.R;


public class JustifiedTextView extends WebView {
 
    private String text;
 
    public JustifiedTextView(final Context context) {
        this(context, null, 0);
    }
 
    public JustifiedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public JustifiedTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
 
        if (attrs != null) {
            final TypedValue typedValue = new TypedValue();
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JustifiedTextView, defStyle, 0);
            if (typedArray != null) {
                typedArray.getValue(R.styleable.JustifiedTextView_text, typedValue);
 
                if (typedValue.resourceId > 0 && TextUtils.isEmpty(text)) {
                    text = context.getString(typedValue.resourceId);
                    text.replace("\n", "<br />");
                    loadDataWithBaseURL("file:///android_asset/", "<html><head>" +
                            "<link rel=\"stylesheet\" type=\"text/css\" href=\"justified_textview.css\" />" +
                            "</head><body>" + text + "</body></html>", "text/html", "UTF8", null);
                }
 
                setTransparentBackground();
            }
        }
    }
 
    public void setTransparentBackground() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } catch (final NoSuchMethodError e) {
            }
 
            setBackgroundColor(Color.TRANSPARENT);
            setBackgroundDrawable(null);
            setBackgroundResource(0);
        }
    }
 
    public String getText() {
        return text;
    }
 
    public void setText(String currentText) {
        this.text = currentText;
        text.replaceAll("\n", "<br />");
        loadDataWithBaseURL("file:///android_asset/", "<html><head>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"justified_textview.css\" />" +
                "</head><body>" + text + "</body></html>", "text/html", "UTF8", "UTF-8");
 
        setTransparentBackground();
    }
}