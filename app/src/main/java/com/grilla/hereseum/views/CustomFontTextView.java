package com.grilla.hereseum.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by bill on 8/8/15.
 */
public class CustomFontTextView extends TextView {
    private static final String FONT = "fonts/Goku.ttf";

    private static Typeface sCustomFont;

    private static synchronized Typeface getFont(Context context) {
        if (sCustomFont == null) {
            sCustomFont = Typeface.createFromAsset(context.getAssets(), FONT);
        }

        return sCustomFont;
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTypeface(getFont(context));
    }
}
