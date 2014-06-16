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

        Bundle[] bundleData = UIBundle.with().text(R.id.client, mEntry.getClient()).text(R.id.matter, mEntry.getMatter())
                .text(R.id.narrative, mEntry.getNarrative()).text(R.id.work_date, mEntry.getWorkDate().toString("MMM d, yyyy")).text(R.id.hours, "00.25")
                .bundle();

        showLayout(R.layout.time_entry_details, bundleData);
    }

    /*private void renderTextToCanvas() {
        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight * SCREEN_PAGES, Bitmap.Config.ARGB_8888);
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        mCanvas = new Canvas(mBitmap);
        mCanvas.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        TextPaint tp = new TextPaint();
        tp.setColor(Color.WHITE);
        tp.setTextSize(18);

        String text = mNote.textContent;

        if (text == null) {
            Log.d(TAG, "Empty text ...");
            text = mContext.getString(R.string.empty_note);
            tp.setTextSkewX(-0.25f); // Italics
        }

        StaticLayout sl = new StaticLayout(text, tp, mScreenWidth, Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, false);

        mCanvas.save();
        sl.draw(mCanvas);
        mCanvas.restore();
    }

    @Override
    public void onRequestListItem(final int layoutReference, final int listItemPosition) {
        Log.d(TAG, "onRequestListItem() - position " + listItemPosition);
        if (layoutReference != -1 && listItemPosition != -1 && layoutReference == R.id.listView) {
            ControlListItem item = createControlListItem(listItemPosition);
            if (item != null) {
                sendListItem(item);
            }
        }
    }

    protected ControlListItem createControlListItem(int position) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, mScreenHeight * position, mScreenWidth, mScreenHeight);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        ControlListItem item = new ControlListItem();
        item.layoutReference = R.id.listView;
        item.dataXmlLayout = R.layout.note_content_item;
        item.listItemPosition = position;
        item.listItemId = position;

        Bundle imageBundle = new Bundle();
        imageBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.imageView);
        imageBundle.putByteArray(Control.Intents.EXTRA_DATA, byteArrayOutputStream.toByteArray());

        item.layoutData = new Bundle[] { imageBundle };

        return item;
    }*/

}