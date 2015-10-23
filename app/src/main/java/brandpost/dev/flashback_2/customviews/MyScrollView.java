package brandpost.dev.flashback_2.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

import java.util.ArrayList;

import brandpost.dev.flashback_2.R;

/**
 * Created by Viktor on 2014-12-19.
 */
public class MyScrollView extends ScrollView {

    /**
     * The View at the top you want to hide
     */
    private View mHeader;

    /**
     * The view(s) at the bottom you want to hide
     */
    private ArrayList<View> mFooter = new ArrayList<>();

    /**
     * Should be the first "list"-item within the ScrollView
     * */
    private View mAnchor;

    /**
     * Header hidden or not?
     */
    private boolean headerHidden = false;

	/**
	 * Footer hidden or not?
	 */
	private boolean footerHidden = false;

    /**
     * Sensitivity for hiding the header and footer.
     * Lower value for higher sensitivity
     */
    private final int HIDE_THRESHOLD = 5;

	private OnReachedBottomListener mListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeaderView(View v) {
        mHeader = v;
    }

    public void addFooterView(View v) {
        mFooter.add(v);
    }

    public void setHeaderAnchor(View v) {
        mAnchor = v;
    }

    public void showHeader(boolean fast) {
        if(mHeader != null && mAnchor != null) {

            // Show Header
            headerHidden = false;

            mHeader.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(fast ? 50 : 300);

        }
    }

    public void hideHeader(boolean fast) {
        if(mHeader != null && mAnchor != null) {

            // Show Header
            headerHidden = true;

            mHeader.animate()
                    .translationY(-mHeader.getHeight())
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(fast ? 50 : 300);
        }
    }

	public void hideFooter(boolean fast) {
		if(mFooter != null) {
			footerHidden = true;

			for(int i = 0; i < mFooter.size(); i++) {
				mFooter.get(i).animate()
						.translationY(2 * mFooter.get(i).getHeight())
						.setStartDelay(i > 0 ? 100 : 0)
						.setInterpolator(new DecelerateInterpolator())
						.setDuration(fast ? 50 : 300);
			}
		}
	}

	public void showFooter(boolean fast) {
		if(mFooter != null) {
			footerHidden = false;

			for(int i = 0; i < mFooter.size(); i++) {
				mFooter.get(i).animate()
						.translationY(0)
						.setStartDelay(i > 0 ? 100 : 0)
						.setInterpolator(new DecelerateInterpolator())
						.setDuration(fast ? 50 : 300);
			}
		}
	}

	public void setOnReachedBottomListener(OnReachedBottomListener listener) {
		mListener = listener;
	}


    @Override
    protected void onScrollChanged(int l, int currentVertScroll, int oldl, int oldVertScroll) {
        super.onScrollChanged(l, currentVertScroll, oldl, oldVertScroll);

	    // If bottom has been reached
	    if(!canScrollVertically(1)) {
		    if(mListener != null)
		        mListener.onBottomReached();
	    }

	    int direction = oldVertScroll - currentVertScroll;

	    if(mFooter != null) {
		    if(direction > HIDE_THRESHOLD && footerHidden) {
			    showFooter(false);
		    }

		    if(direction < -HIDE_THRESHOLD && !footerHidden) {
			    hideFooter(false);
		    }
	    }

        if(mHeader != null && mAnchor != null) {
	        int paddingtop = mAnchor.getTop() + getPaddingTop();
	        int scrolly = getScrollY();
	        int distance = paddingtop - scrolly;

            // If we have scrolled beyond the Toolbar
            if (distance < 0) {
                // If scrolldirection is greater than a threshold and Toolbar is hidden, show it.
                if (direction > HIDE_THRESHOLD && headerHidden) {
                    showHeader(false);
                }

                if (direction < -HIDE_THRESHOLD && !headerHidden) {
                    hideHeader(false);
                }
            }
        }
    }

	public interface OnReachedBottomListener {
		void onBottomReached();
	}

    public interface HeaderFooterProvider {
        View getHeader();
        ArrayList<View> getFooter();
    }
}
