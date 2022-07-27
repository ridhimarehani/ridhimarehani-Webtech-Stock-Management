package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallbackFavorite extends ItemTouchHelper.Callback {
    private final ItemMoveCallbackFavorite.ItemTouchHelperContract mAdapter;


    public ItemMoveCallbackFavorite(ItemMoveCallbackFavorite.ItemTouchHelperContract mAdapter) {
        this.mAdapter =  mAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof FavoriteRecyclerAdapter.FavoriteViewHolder) {
                FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder=
                        (FavoriteRecyclerAdapter.FavoriteViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof FavoriteRecyclerAdapter.FavoriteViewHolder) {
            FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder=
                    (FavoriteRecyclerAdapter.FavoriteViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder);
        void onRowClear(FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder);

    }
}
