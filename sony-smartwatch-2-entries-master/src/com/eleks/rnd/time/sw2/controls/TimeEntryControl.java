package com.eleks.rnd.time.sw2.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.eleks.rnd.time.sw2.AdvancedLayoutsExtensionService;
import com.eleks.rnd.time.sw2.R;
import com.eleks.rnd.time.sw2.api.TimeEntry;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

public class TimeEntryControl extends ManagedControlExtension {

    public final static String EXTRA_ENTRY_DETAILS = "EXTRA_ENTRY_DETAILS";

    private TimeEntry mEntry = null;

    /**
     * @see ManagedControlExtension#ManagedControlExtension(Context, String,
     *      ControlManagerCostanza, Intent)
     */
    public TimeEntryControl(Context context, String hostAppPackageName, ControlManagerSmartWatch2 controlManager, Intent intent) {
        super(context, hostAppPackageName, controlManager, intent);
    }

    @Override
    public void onResume() {
        Log.d(AdvancedLayoutsExtensionService.LOG_TAG, "onResume");

        TimeEntry entry = (TimeEntry) getIntent().getSerializableExtra(EXTRA_ENTRY_DETAILS);
        mEntry = entry == null ? TimeEntry.EMPTY : entry;

        updateLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        getIntent().putExtra(EXTRA_ENTRY_DETAILS, mEntry);
    }

    @Override
    public void onTouch(ControlTouchEvent event) {
        super.onTouch(event);

        Log.d(AdvancedLayoutsExtensionService.LOG_TAG, "onTouch: TimeEntryControl " + " - " + event.getX() + ", " + event.getY());
    }

    /**
     * This is an example of how to update the entire layout and some of the
     * views. For each view, a bundle is used. This bundle must have the layout
     * reference, i.e. the view ID and the content to be used. This method
     * updates an ImageView and a TextView.
     * 
     * @see Control.Intents#EXTRA_DATA_XML_LAYOUT
     * @see Registration.LayoutSupport
     */
    private void updateLayout() {

        // Client data
        Bundle headerBundle = new Bundle();
        headerBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.client);
        headerBundle.putString(Control.Intents.EXTRA_TEXT, mEntry.getClient());

        // Matter data
        Bundle bodyBundle = new Bundle();
        bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.matter);
        bodyBundle.putString(Control.Intents.EXTRA_TEXT, mEntry.getMatter());

        // Narrative data
        Bundle narrativeBundle = new Bundle();
        bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.narrative);
        bodyBundle.putString(Control.Intents.EXTRA_TEXT, mEntry.getNarrative());

        Bundle[] bundleData = new Bundle[3];
        bundleData[0] = headerBundle;
        bundleData[1] = bodyBundle;
        bundleData[2] = narrativeBundle;

        showLayout(R.layout.time_entry_details, null);
    }

}