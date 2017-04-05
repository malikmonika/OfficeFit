package edu.scu.mmalik1.photonotes;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by abhimanyusingh on 5/6/16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements ItemDragGandler{

    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        public TextView tv;
        public ImageView iv;
        public Notes n;
        NotesViewHolder(final View view) {
            super(view);
            tv = (TextView)view.findViewById(R.id.texttoshow);
            iv = (ImageView)view.findViewById(R.id.phototoshow);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), View_Photo_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("text", n.getText());
                    intent.putExtra("photo", n.getFilename());
                    intent.putExtra("lat",n.getlocLat());
                    intent.putExtra("lon",n.getLocLong());
                    intent.putExtra("voice",n.getRecVoice());
                    v.getContext().startActivity(intent);
                }
            });
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
        System.out.println("lat" + notesViewHolder.n.getlocLat());
        System.out.println("lon" + notesViewHolder.n.getLocLong());
        notesViewHolder.tv.setEllipsize(TextUtils.TruncateAt.END);
        notesViewHolder.tv.setText(notes.get(position).getText());
        String f = notes.get(position).getFilename();
        Bitmap bmp = BitmapFactory.decodeFile(f.substring(5,f.length()));
        Bitmap resized = ThumbnailUtils.extractThumbnail(bmp, 120, 120);
        notesViewHolder.iv.setImageBitmap(resized);
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup vg, int position) {
        View vw = LayoutInflater.from(mContext).inflate(R.layout.cardview, vg, false);
        NotesViewHolder nvh = new NotesViewHolder(vw);
        return nvh;
    }

    @Override
    public void onItemDismissed(int position)
    {
        Db_Helper dbHelper = new Db_Helper(mContext);
        dbHelper.open();
        String filename = notes.get(position).getFilename();
        dbHelper.removeNote(filename);
        dbHelper.close();
        notes.remove(position);
        notifyItemRemoved(position);
        File f = new File(filename.substring(5,filename.length()));
        f.delete();
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.parse(filename));
        mContext.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(notes, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Db_Helper dbHelper = new Db_Helper(mContext);
        dbHelper.open();
        String filenamenew = notes.get(toPosition).getFilename();
        String filenameold = notes.get(fromPosition).getFilename();
        dbHelper.moveNote(filenameold, filenamenew);
        dbHelper.close();
        return true;
    }

    public void changeItem(List<Notes> n)
    {
        this.notes = n;
    }

}