package com.eleks.rnd.time.sw2;

import org.joda.time.Period;

import android.app.Activity;
import android.os.Bundle;

import com.eleks.rnd.time.sw2.api.Hardcoded;
import com.eleks.rnd.time.sw2.api.TimeEntry;
import com.eleks.rnd.time.sw2.utils.Time;
import com.eleks.rnd.time.sw2.utils.UIBundle;
import com.eleks.rnd.time.sw2.utils.UIUpdater;

public class TimeEntryDetailsActivity extends Activity {
    
    public static final String EXTRA_TIME_ENTRY_DETAILS_ID = "EXTRA_TIME_ENTRY_DETAILS_ID";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_entry);
        
        String entryId = getIntent().getExtras().getString(EXTRA_TIME_ENTRY_DETAILS_ID);
        TimeEntry mEntry = Hardcoded.DATA.getById(entryId);
        
        Bundle[] bundleData = UIBundle.with()
                .text(R.id.client, mEntry.getClient())
                .text(R.id.matter, mEntry.getMatter())
                .text(R.id.work_date, mEntry.getWorkDate().toString("MMM d, yyyy"))
                .text(R.id.hours, Time.HOURS.print(new Period(mEntry.getIntervalsDuration())))
                .text(R.id.narrative, mEntry.getNarrative())
                .bundle();

        UIUpdater.fromUIBundle(bundleData).with(findViewById(R.id.body)).update();
    }

}
