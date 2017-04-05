package monika.malik.com.officefit;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Reminder extends AppCompatActivity {

    int myear = 0;
    int mday = 0;
    int mmonth  = 0;
    CheckBox m,t,w,th,f,sa,s;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        //checkboxes
        m = (CheckBox)findViewById(R.id.mon);
        t = (CheckBox)findViewById(R.id.tue);
        w = (CheckBox)findViewById(R.id.wed);
        th = (CheckBox)findViewById(R.id.thur);
        f = (CheckBox)findViewById(R.id.fri);
        sa = (CheckBox)findViewById(R.id.sat);
        s = (CheckBox)findViewById(R.id.sun);
        name = (EditText)findViewById(R.id.editText);
    }


    public void setReminder(View v)
    {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker2);
        int hour = t.getHour();
        int min = t.getMinute();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,myear);
        cal.set(Calendar.MONTH,mmonth);
        cal.set(Calendar.DAY_OF_MONTH,mday);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,min);
        if(Calendar.getInstance().getTimeInMillis() > cal.getTimeInMillis())
        {
            Toast.makeText(getApplicationContext(), "Enter valid Time", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText text = (EditText)findViewById(R.id.editText);
        if(text.getText().toString().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Enter valid Reminder Name", Toast.LENGTH_SHORT).show();
            return;
        }
        setReminder(cal);
        finish();
    }

    public void setDate(View v)
    {
        final Calendar c = Calendar.getInstance();
        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        myear = year;
                        mday = dayOfMonth;
                        mmonth = monthOfYear;
                    }
                }, myear, mmonth, mday);
        dpd.show();
    }

    private void setReminder(Calendar cal)
    {
        Cursor cursor = null;
        try {
            cursor = getApplicationContext().getContentResolver()
                    .query(CalendarContract.Events.CONTENT_URI,
                            new String[]{CalendarContract.Calendars._ID}, null, null, null);
        }catch(SecurityException e)
        {

        }
        if(!cursor.moveToFirst())
        {
            System.out.println("No calendar");
        }
        long calID = cursor.getLong(0);
        long startMillis = 0;
        long endMillis = 0;
        startMillis = cal.getTimeInMillis();
        endMillis = cal.getTimeInMillis();
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, name.getText().toString());
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.HAS_ALARM, 1);
        values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        values.put(Events.SELF_ATTENDEE_STATUS,Events.STATUS_CONFIRMED);
        String str = "FREQ=DAILY;COUNT=10;BYDAY=";
        boolean st = false;
        if(m.isChecked())
        {
            st = true;
            str+="MO,";
        }
        if(t.isChecked())
        {
            st = true;
            str+="TU,";
        }
        if(w.isChecked())
        {
            st = true;
            str+="WE,";
        }
        if(th.isChecked())
        {
            st = true;
            str+="TH,";
        }
        if(f.isChecked())
        {
            st = true;
            str+="FR,";
        }
        if(sa.isChecked())
        {
            st = true;
            str+="SA,";
        }if(s.isChecked())
    {
        st = true;
        str+="SU,";
    }
        str = str.substring(0,str.length()-1);
        if(st)
        {
            str += ";WKST=MO";
            values.put(Events.RRULE,
                    str);
        }
        try
        {
            Uri uri = cr.insert(Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());
            values.clear();
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
            values.put(CalendarContract.Reminders.MINUTES, 0);
            getApplicationContext().getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
            System.out.println("new Uri "+eventID);
        } catch (SecurityException e)
        {

        }
    }
}
