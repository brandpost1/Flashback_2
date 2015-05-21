package brandpost.dev.flashback_2;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import org.jsoup.Connection;

import java.util.ArrayList;

import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.fragments.Fragment_ThreadPage;

/**
 * Created by Viktor on 2014-12-12.
 */
public class ThreadActivity extends BaseActivity implements MyScrollView.HeaderFooterProvider {

    private View mNewPostButton;
	private View mPageIndicator;

    /**
     * Provides pages for the ViewPager
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_layout);

        /**
         * ReSetup Toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.thread_toolbar);
        setActivityTitle("Rubriken på tråden");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /**
         * Setup ViewPager
         */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.thread_viewpager);
        mPagerAdapter = new ScrollThreadPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        /**
         * Post reply-button stuff
         */

        mNewPostButton = findViewById(R.id.addButton);

        // Lollipop specific
        if(android.os.Build.VERSION.SDK_INT >= 21) {
            CircleOutlineProvider mCircleOutline = new CircleOutlineProvider();

            mNewPostButton.setOutlineProvider(mCircleOutline);
            mNewPostButton.setClipToOutline(true);

            mNewPostButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            mNewPostButton.animate()
                                    .translationZ(0)
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(50);
                            return true;
                        case MotionEvent.ACTION_UP:
                            mNewPostButton.animate()
                                    .translationZ(5)
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(50);
                            return false;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public View getHeader() {
		return mToolbar;
	}

	@Override
	public ArrayList<View> getFooter() {
		return null;
	}


	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class CircleOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                System.out.println("Pressed home button");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private int NUM_PAGES = 1;

    private class ScrollThreadPagerAdapter extends FragmentStatePagerAdapter {
        public ScrollThreadPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment_ThreadPage threadPage = new Fragment_ThreadPage();
            return threadPage;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
