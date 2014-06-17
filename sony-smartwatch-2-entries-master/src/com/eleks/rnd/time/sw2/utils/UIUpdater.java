package com.eleks.rnd.time.sw2.utils;

import com.sonyericsson.extras.liveware.aef.control.Control;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created for easy reuse of SW2 view update logic.
 * @author ostap.andrusiv
 *
 */
public class UIUpdater {
    
    private View parent;
    private Bundle[] bundles;
    
    private UIUpdater(Bundle[] b) {
        this.bundles = b;
    }
    
    public static UIUpdater fromUIBundle(Bundle[] bundle) {
        return new UIUpdater(bundle); 
    }
    
    public UIUpdater with(View ctx) {
        this.parent = ctx;
        return this;
    }
    
    /**
     * Updates view and nulls all of its fields.
     */
    public void update() {
        for (Bundle bundle : bundles) {
            int resourceId = bundle.getInt(Control.Intents.EXTRA_LAYOUT_REFERENCE);
            String text = bundle.getString(Control.Intents.EXTRA_TEXT);
            
            View v = parent.findViewById(resourceId);
            if (v instanceof TextView) {
                ((TextView)v).setText(text);
            }
        }
        parent = null;
        bundles = null;
    }
}
