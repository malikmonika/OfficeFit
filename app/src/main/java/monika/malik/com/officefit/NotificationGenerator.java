package monika.malik.com.officefit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Calendar;


public class NotificationGenerator {

    private Context mContext;
    private int ResW,ResP,ResS,ResE,ResL;
    private String mName,extra;
    private DBHelper mDBHelper;
    private int NUM = 0;
    private int MORNING = 8;
    private int LATEMORNING = 10;
    private int NOON = 12;
    private int LATENOON = 14;
    private int EVENING =16;
    private int LATEVENING = 18;
    private int NIGHT = 20;
    private int LATENIGHT = 22;

    public NotificationGenerator(Context context,int wt, int stp,int prs,int l,int e)
    {
        mContext = context;
        ResW = wt;
        ResP = prs;
        ResS = stp;
        ResL = l;
        ResE = e;
        mDBHelper = new DBHelper(context);
        extra = "";
    }

    private void pushNottodb(String action)
    {
        mDBHelper.updateorInsertValue(action,"na");
    }

    public void createNotification()
    {
        boolean sent = true;

        /////// STEP NOTIFICATION START
        NUM = 1;
        String step = mDBHelper.retriveValue("steps");
        String stepgoal = mDBHelper.retriveValue("stepgoal");
        int progress = (int)((Integer.valueOf(step) * 100.0f) /Integer.valueOf(stepgoal));
        Calendar cal = Calendar.getInstance();
        int hour  = cal.get(Calendar.HOUR_OF_DAY);
        int pr = 0;
        if(hour>=MORNING || hour <=LATENIGHT) {

            if ((hour > MORNING && hour < LATEMORNING) && (mDBHelper.retriveValue("s1") == null)) {
                pr = 15;
                pushNottodb("s1");
                sent = false;

            } else if ((hour >= LATEMORNING && hour < NOON) && (mDBHelper.retriveValue("s2") == null)) {
                pr = 30;
                pushNottodb("s2");
                sent = false;
            } else if ((hour >= NOON && hour < LATENOON) && (mDBHelper.retriveValue("s3") == null)) {
                pr = 45;
                pushNottodb("s3");
                sent = false;
            } else if ((hour >= LATENOON && hour < EVENING) && (mDBHelper.retriveValue("s4") == null)) {
                pr = 60;
                pushNottodb("s4");
                sent = false;
            } else if ((hour >= EVENING && hour < LATEVENING) && (mDBHelper.retriveValue("s5") == null)) {
                pr = 80;
                pushNottodb("s5");
                sent = false;
            } else if ((hour >= LATEVENING && hour < NIGHT) && (mDBHelper.retriveValue("s6") == null)) {
                pr = 100;
                pushNottodb("s6");
                sent = false;
            }
            else if ((hour >= NIGHT && hour < LATENIGHT) && (mDBHelper.retriveValue("s7") == null)) {
                pr = 101;
                pushNottodb("s7");
                sent = false;
            }
            if(pr == 101)
            {
                if(progress <100)
                {
                    extra = mDBHelper.retriveValue("name") + " Steps Taken Today: "+step;
                }
                else
                {
                    extra = mDBHelper.retriveValue("name") + " Congrats you met your goal";
                }

            }
            else if (progress < pr) {
                if (pr - progress > 60) {
                    extra = mDBHelper.retriveValue("name") + " Get some walk ";
                } else if (pr - progress > 40) {
                    extra = mDBHelper.retriveValue("name") + " You need to keep up ";
                } else if (pr - progress > 20) {
                    extra = mDBHelper.retriveValue("name") + " You are behind you goal ";

                } else {
                    extra = mDBHelper.retriveValue("name") + " You are on track ";
                }
            } else {
                extra = mDBHelper.retriveValue("name") + " Good you are keeping up ";
            }

            mName = "ACTIVITY TRACKER";
            String proceed = mDBHelper.retriveValue("stepswitch");
            if (!proceed.equals("no") && !sent) {
                createAndSend();
            }
        }
        /////// STEP NOTIFICATION END

        /////// WATER NOTIFICATION START

        NUM = 2;
        pr = 0;
        sent = true;
        if(hour>MORNING || hour <=LATENIGHT) {

            if ((hour > MORNING && hour < LATEMORNING) && (mDBHelper.retriveValue("w1") == null)) {
                pr = 1;
                pushNottodb("w1");
                sent = false;

            } else if ((hour >= LATEMORNING && hour < NOON) && (mDBHelper.retriveValue("w2") == null)) {
                pr = 3;
                pushNottodb("w2");
                sent = false;
            } else if ((hour >= NOON && hour < LATENOON) && (mDBHelper.retriveValue("w3") == null)) {
                pr = 4;
                pushNottodb("w3");
                sent = false;
            } else if ((hour >= LATENOON && hour < EVENING) && (mDBHelper.retriveValue("w4") == null)) {
                pr = 5;
                pushNottodb("w4");
                sent = false;
            } else if ((hour >= EVENING && hour < LATEVENING) && (mDBHelper.retriveValue("w5") == null)) {
                pr = 7;
                pushNottodb("w5");
                sent = false;
            } else if ((hour >= LATEVENING && hour < NIGHT) && (mDBHelper.retriveValue("w6") == null)) {
                pr = 8;
                pushNottodb("w6");
                sent = false;
            }
            else if ((hour >= NIGHT && hour < LATENIGHT) && (mDBHelper.retriveValue("w7") == null)) {
                pr = 9;
                pushNottodb("w7");
                sent = false;
            }
            progress = Integer.valueOf(mDBHelper.retriveValue("water"));
            if(pr == 9)
            {
                if(progress <8)
                {
                    extra = mDBHelper.retriveValue("name") + " You missed your goal Water Taken: "+progress;
                }
                else
                {
                    extra = mDBHelper.retriveValue("name") + " Congrats you met your goal";
                }
            }
            else if (progress < pr) {
                if (pr - progress > 3) {
                    extra = mDBHelper.retriveValue("name") + " Get some water";
                } else if (pr - progress > 1) {
                    extra = mDBHelper.retriveValue("name") + " You need to keep up";
                } else {
                    extra = mDBHelper.retriveValue("name") + " You are on track";
                }
            } else {
                extra = mDBHelper.retriveValue("name") + " Good you are keeping up";
            }
            mName = "HYDRATE YOURSELF";
            String proceed = mDBHelper.retriveValue("waterswitch");
            if (!proceed.equals("no") && !sent)
                createAndSend();
        }
        /////// WATER NOTIFICATION END

        /////// LUNCH NOTIFICATION START
        NUM = 3;
        mName="LUNCH TIME";
        extra = "It is an important meal of the day";
        if(hour >=NOON && hour <= LATENOON) {
            String proceed = mDBHelper.retriveValue("luntchswitch");
            if (!proceed.equals("no") && (mDBHelper.retriveValue("l1") == null)) {
                pushNottodb("l1");
                createAndSend();
            }
        }
        /////// LUNCH NOTIFICATION END

        /////// EYEWASH NOTIFICATION START
        NUM = 4;
        sent = true;
        if(hour >MORNING && hour <=LATENIGHT) {
            if ((hour > MORNING && hour < NOON) && (mDBHelper.retriveValue("e1") == null)) {
                pushNottodb("e1");
                sent = false;
            } else if ((hour >= NOON && hour < EVENING) && (mDBHelper.retriveValue("e2") == null)) {
                pushNottodb("e2");
                sent = false;
            } else if ((hour >= EVENING && hour < NIGHT) && (mDBHelper.retriveValue("e3") == null)) {
                pushNottodb("e3");
                sent = false;
            } else if ((hour >= NIGHT && hour < LATENIGHT) && (mDBHelper.retriveValue("e4") == null)) {
                pushNottodb("e4");
                sent = false;
            }
            mName = "TAKE A BREAK";
            extra = "Washing eyes helps keep them healthy";
            String proceed = mDBHelper.retriveValue("generalswitch");
            if (!proceed.equals("no") && !sent)
                createAndSend();
        }
        /////// EYEWASH NOTIFICATION END

        /////// STRETCH NOTIFICATION START
        NUM = 5;
        sent = true;
        if(hour >MORNING && hour <=LATENIGHT) {
            if ((hour > MORNING && hour < NOON) && (mDBHelper.retriveValue("sr1") == null)) {
                pushNottodb("sr1");
                sent = false;
            } else if ((hour >= NOON && hour < LATENOON) && (mDBHelper.retriveValue("sr2") == null)) {
                pushNottodb("sr2");
                sent = false;
            } else if ((hour >= LATENOON && hour < LATENIGHT) && (mDBHelper.retriveValue("sr3") == null)) {
                pushNottodb("sr3");
                sent = false;
            }
            mName = "TAKE A BREAK";
            extra = "Lets try some office stretches";
            String proceed = mDBHelper.retriveValue("generalswitch");
            if (!proceed.equals("no") && !sent)
                createAndSend();
        }
        /////// STRETCH NOTIFICATION END
    }

    private void createAndSend()
    {
        Intent intent;
        if(NUM == 5) {
            Uri uri = Uri.parse("http://www.google.com/#q=office+stretches");
            intent = new Intent(Intent.ACTION_VIEW, uri);
        }
        else {
            intent = new Intent(mContext, MainActivity.class);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setContentTitle(mName)
                        .setContentText(extra);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_ONE_SHOT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        if(NUM ==1 )
        mBuilder.setSmallIcon(ResS);
        else if (NUM ==2)
            mBuilder.setSmallIcon(ResW);
        else if (NUM==3)
            mBuilder.setSmallIcon(ResL);
        else if(NUM == 4)
            mBuilder.setSmallIcon(ResE);
        else if(NUM ==5)
            mBuilder.setSmallIcon(ResP);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(NUM, mBuilder.build());
    }

}
