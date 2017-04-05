package edu.scu.mmalik1.photonotes;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by abhimanyusingh on 5/15/16.
 */
public class TouchHelper extends ItemTouchHelper.Callback {

    private ItemDragGandler itdg;

    public TouchHelper(ItemDragGandler handler)
    {
        itdg =  handler;
    }

    @Override
    public int getMovementFlags(RecyclerView view, RecyclerView.ViewHolder holder)
    {
        int drag = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
        int swipe = ItemTouchHelper.LEFT;
        return makeMovementFlags(drag,swipe);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int dir)
    {
        System.out.println("swiped");
        itdg.onItemDismissed(holder.getAdapterPosition());
    }

    @Override
    public boolean onMove(RecyclerView view, RecyclerView.ViewHolder old,
                          RecyclerView.ViewHolder latest) {
        System.out.println("moved");
        itdg.onItemMoved(old.getAdapterPosition(), latest.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
