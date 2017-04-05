package edu.scu.mmalik1.photonotes;

/**
 * Created by abhimanyusingh on 5/15/16.
 */

public interface ItemDragGandler {

    boolean onItemMoved(int fromPosition, int toPosition);
    void onItemDismissed(int position);
}
