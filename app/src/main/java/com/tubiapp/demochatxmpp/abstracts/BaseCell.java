package com.tubiapp.demochatxmpp.abstracts;

import android.content.Context;
import android.view.View;

/**
 * Created by Binh CAO on 6/13/2015.
 */
public abstract class BaseCell {
    public static int reUseIdentifier = 0;
    protected Context context;

    public BaseCell(Context context, View rootView) {
        super();
        this.context = context;
        initUiComponents(rootView);
    }

    public BaseCell(Context context) {
        this.context = context;
        initUiComponents(null);
    }

    protected abstract void initUiComponents(View rootView);

}
