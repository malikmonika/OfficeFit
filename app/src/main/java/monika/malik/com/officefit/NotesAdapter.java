package monika.malik.com.officefit;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{

    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        public TextView tv;
        public Notes n;
        NotesViewHolder(final View view) {
            super(view);
            tv = (TextView)view.findViewById(R.id.texttoshow);
        }


    }
    private static  List<Notes> notes;
    private static Context mContext;

    public NotesAdapter(List<Notes> notes, Context context)
    {
        this.notes = notes;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if(notes == null)
            return 0;
        return notes.size();
    }

    @Override
    public void onBindViewHolder(NotesViewHolder notesViewHolder, final int position) {
        notesViewHolder.n = notes.get(position);
        notesViewHolder.tv.setEllipsize(TextUtils.TruncateAt.END);
        notesViewHolder.tv.setText(notes.get(position).getText());
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View vw = LayoutInflater.from(mContext).inflate(R.layout.cardview, vg, false);
        NotesViewHolder nvh = new NotesViewHolder(vw);
        return nvh;
    }


}