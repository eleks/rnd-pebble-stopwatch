package com.eleks.rnd.time.sw2.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.eleks.rnd.time.sw2.AdvancedLayoutsExtensionService;
import com.eleks.rnd.time.sw2.R;
import com.eleks.rnd.time.sw2.api.Hardcoded;
import com.eleks.rnd.time.sw2.api.TimeEntry;
import com.eleks.rnd.time.sw2.utils.UIBundle;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

public class TimeEntryControl extends ManagedControlExtension {

    public final static String EXTRA_ENTRY_ID = "EXTRA_ENTRY_DETAILS";

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

        String id = getIntent().getStringExtra(EXTRA_ENTRY_ID);
        TimeEntry entry = Hardcoded.DATA.getById(id);
        mEntry = entry == null ? TimeEntry.EMPTY : entry;

        updateLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        getIntent().putExtra(EXTRA_ENTRY_ID, mEntry.getId());
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

        Bundle[] bundleData = UIBundle.with()
                .text(R.id.client, mEntry.getClient())
                .text(R.id.matter, mEntry.getMatter())
                .text(R.id.narrative, mEntry.getNarrative())
                .text(R.id.work_date, mEntry.getWorkDate().toString("MMM d, yyyy"))
                .text(R.id.hours, "00.25")
                .bundle();

        showLayout(R.layout.time_entry_details, bundleData);
    }

}