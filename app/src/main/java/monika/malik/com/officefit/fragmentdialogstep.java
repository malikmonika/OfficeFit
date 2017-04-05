package monika.malik.com.officefit;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class fragmentdialogstep extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        TextView t = (TextView)view.findViewById(R.id.congratstextholder);
        t.setText("Congratulations on Completing Goal Steps");
        return view;
    }

}