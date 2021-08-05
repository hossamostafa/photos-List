package com.dev.testproject.views;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private boolean includeedges;

    public RecyclerDecoration(int spanCount, int spacing, boolean includeedges) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeedges = includeedges;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position=parent.getChildAdapterPosition(view);
        int column=position%spanCount;

        if (includeedges) {
            outRect.left=spacing-column*spacing/spanCount;
            outRect.right=(column+1)*spacing/spanCount;
            if (position<spanCount){
                outRect.top=spacing;
            }
            outRect.bottom=spacing;}
        else {
            outRect.left=column*spacing/spanCount;
            outRect.right=spacing-(column+1)*spacing/spanCount;
            if (position>=spanCount){
                outRect.top=spacing;
            }
        }
    }

}
