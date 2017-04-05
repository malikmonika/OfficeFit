package monika.malik.com.officefit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class notifications extends AppCompatActivity {

    DBHelper mDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otifications);
        mDBHelper = new DBHelper(getApplicationContext());
        Switch waterSwitch = (Switch)findViewById(R.id.water);
        String ret = mDBHelper.retriveValue("waterswitch");
        if(ret != null)
        {
            if (ret.equals("yes"))
                waterSwitch.setChecked(true);
            else if(ret.equals("no"))
                waterSwitch.setChecked(false);
        }
        waterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDBHelper.updateorInsertValue("waterswitch","yes");
                else
                    mDBHelper.updateorInsertValue("waterswitch","no");
            }
        });

        Switch stepSwitch = (Switch)findViewById(R.id.step);
        ret = mDBHelper.retriveValue("stepswitch");
        if(ret != null)
        {
            if (ret.equals("yes")) {
                stepSwitch.setChecked(true);
            }
            else if(ret.equals("no"))
                stepSwitch.setChecked(false);
        }
        stepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDBHelper.updateorInsertValue("stepswitch","yes");
                else
                    mDBHelper.updateorInsertValue("stepswitch","no");
            }
        });

        Switch generalSwitch = (Switch)findViewById(R.id.general);
        ret = mDBHelper.retriveValue("generalswitch");
        if(ret != null)
        {
            if (ret.equals("yes"))
                generalSwitch.setChecked(true);
            else if(ret.equals("no"))
                generalSwitch.setChecked(false);
        }
        generalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDBHelper.updateorInsertValue("generalswitch","yes");
                else
                    mDBHelper.updateorInsertValue("generalswitch","no");
            }
        });

        Switch luntchSwitch = (Switch)findViewById(R.id.lunch);
        ret = mDBHelper.retriveValue("luntchswitch");
        if(ret != null)
        {
            if (ret.equals("yes"))
                luntchSwitch.setChecked(true);
            else if(ret.equals("no"))
                luntchSwitch.setChecked(false);
        }
        luntchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDBHelper.updateorInsertValue("luntchswitch","yes");
                else
                    mDBHelper.updateorInsertValue("luntchswitch","no");
            }
        });
    }
}
