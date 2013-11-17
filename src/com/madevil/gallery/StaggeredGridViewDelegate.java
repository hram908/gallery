package com.madevil.gallery;

import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;
import android.view.View;
import com.origamilabs.library.views.StaggeredGridView;

public class StaggeredGridViewDelegate implements ViewDelegate {
    @Override
    public boolean isReadyForPull(View view, float x, float y) {
        StaggeredGridView absListView = (StaggeredGridView) view;
        if (absListView.getFirstPosition() == 0) {
            return true;
        }
//        } else if (absListView.getFirstVisiblePosition() == 0) {
//            final View firstVisibleChild = absListView.getChildAt(0);
//            return firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
//        }
        return false;
    }
}
