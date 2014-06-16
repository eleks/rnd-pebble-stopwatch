package com.eleks.rnd.time.sw2.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.sonyericsson.extras.liveware.aef.control.Control;

public class UIBundle {
    
    private List<Bundle> bundles = new ArrayList<Bundle>();
    
    private UIBundle() {}
    
    public static UIBundle with() {
        return new UIBundle();
    }
    
    public UIBundle text(int id, String value) {
        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, id);
        bundle.putString(Control.Intents.EXTRA_TEXT, value);

        bundles.add(bundle);
        return this;
    }
    
    public UIBundle uri(int id, String value) {
        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, id);
        bundle.putString(Control.Intents.EXTRA_DATA_URI, value);

        bundles.add(bundle);
        return this;
    }
    
    public Bundle[] bundle() {
        return bundles.toArray(new Bundle[bundles.size()]);
    }
    
}
