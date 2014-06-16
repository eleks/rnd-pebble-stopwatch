package com.eleks.rnd.time.sw2.controls;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.eleks.rnd.time.sw2.R;
import com.eleks.rnd.time.sw2.api.Hardcoded;
import com.eleks.rnd.time.sw2.api.TimeEntry;
import com.eleks.rnd.time.sw2.utils.UIBundle;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

public class TimeEntryControl extends ManagedControlExtension {

    public final static String EXTRA_ENTRY_ID = "EXTRA_ENTRY_DETAILS";
    public final static int LIST_VIEW_HEIGHT_PX = 92;
    public final static int LIST_VIEW_WIDTH_PX = 220 - 6 - 6; // - padLeft - padRight

    private static final String TAG = "TEC";

    private TimeEntry mEntry = null;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    /**
     * @see ManagedControlExtension#ManagedControlExtension(Context, String,
     *      ControlManagerCostanza, Intent)
     */
    public TimeEntryControl(Context context, String hostAppPackageName, ControlManagerSmartWatch2 controlManager, Intent intent) {
        super(context, hostAppPackageName, controlManager, intent);
        Log.d(TAG, "TimeEntryControl constructor");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        String id = getIntent().getStringExtra(EXTRA_ENTRY_ID);
        TimeEntry entry = Hardcoded.DATA.getById(id);
        mEntry = entry == null ? TimeEntry.EMPTY : entry;

        updateLayout();
        renderTextToCanvas();
    }

    @Override
    public void onPause() {
        super.onPause();
        getIntent().putExtra(EXTRA_ENTRY_ID, mEntry.getId());
    }

    @Override
    public void onTouch(ControlTouchEvent event) {
        super.onTouch(event);

        Log.d(TAG, "onTouch: TimeEntryControl " + " - " + event.getX() + ", " + event.getY());
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
//                .text(R.id.narrative, mEntry.getNarrative())
                .text(R.id.work_date, mEntry.getWorkDate().toString("MMM d, yyyy"))
                .text(R.id.hours, "00.25")
                .bundle();

        showLayout(R.layout.time_entry_details, bundleData);
    }

    private void renderTextToCanvas() {
        int canvasWidth = LIST_VIEW_WIDTH_PX;
        String text = mEntry.getNarrative();
        
        int MY_DIP_VALUE = 9; //9dp
        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                      MY_DIP_VALUE, mContext.getResources().getDisplayMetrics());
        TextPaint mTextPaint=new TextPaint();
        mTextPaint.setTextSize(pixel);
        mTextPaint.setAntiAlias(true);
        
        // layout text and measure it
        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, canvasWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        Log.d(TAG+":SL", "width " + mTextLayout.getWidth() + " height " + mTextLayout.getHeight());

        int textWidth = mTextLayout.getWidth();
        int textHeight = mTextLayout.getHeight();
        if (textHeight % LIST_VIEW_HEIGHT_PX > 0) {
            int screenCount = textHeight / LIST_VIEW_HEIGHT_PX + 1;
            textHeight = screenCount * LIST_VIEW_HEIGHT_PX;
        }
        
        // create bitmap of the size of a text
        mBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888);
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        mCanvas = new Canvas(mBitmap);
        mCanvas.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        // draw text to memory
        mCanvas.save();
        float textX = 0;
        float textY = 0;
        mCanvas.translate(textX, textY);
        mTextLayout.draw(mCanvas);
        mCanvas.restore();

        int pages = textHeight / LIST_VIEW_HEIGHT_PX;
        sendListCount(R.id.narrative, pages);
    }

    @Override
    public void onRequestListItem(final int layoutReference, final int listItemPosition) {
        Log.d(TAG, "NARRATIVE onRequestListItem() - position " + listItemPosition + " lay.ref: " + layoutReference);
        if (layoutReference != -1 && listItemPosition != -1 && layoutReference == R.id.narrative) {
            ControlListItem item = createControlListItem(listItemPosition);
            if (item != null) {
                sendListItem(item);
            }
        }
    }

    protected ControlListItem createControlListItem(int position) {
        Log.d(TAG, "narrative page request: " + position);
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, LIST_VIEW_HEIGHT_PX * position, LIST_VIEW_WIDTH_PX, LIST_VIEW_HEIGHT_PX);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        ControlListItem item = new ControlListItem();
        item.layoutReference = R.id.narrative;
        item.dataXmlLayout = R.layout.item_scrollable_narrative;
        item.listItemPosition = position;
        item.listItemId = position;

        Bundle[] bundles = UIBundle.with()
                .data(R.id.text_page, byteArrayOutputStream.toByteArray())
                .bundle();
        
        item.layoutData = bundles;

        return item;
    }

}