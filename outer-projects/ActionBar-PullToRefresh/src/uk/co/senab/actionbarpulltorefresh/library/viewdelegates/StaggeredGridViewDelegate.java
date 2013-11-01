package uk.co.senab.actionbarpulltorefresh.library.viewdelegates;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.view.View;

import com.origamilabs.library.views.StaggeredGridView;

public class StaggeredGridViewDelegate extends
		PullToRefreshAttacher.ViewDelegate {

	public static final Class SUPPORTED_VIEW_CLASS = StaggeredGridView.class;

	@Override
	public boolean isReadyForPull(View view, float x, float y) {
		StaggeredGridView absListView = (StaggeredGridView) view;
		if (absListView.getFirstPosition() == 0) {
			return true;
		}
//		} else if (absListView.getFirstVisiblePosition() == 0) {
//			final View firstVisibleChild = absListView.getChildAt(0);
//			return firstVisibleChild != null && firstVisibleChild.getTop() >= 0;
//		}
		return false;
	}
}
