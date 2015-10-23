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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.fragments.Fragment_ThreadPage;

/**
 * Created by Viktor on 2014-12-12.
 */
public class ThreadActivity extends BaseActivity implements MyScrollView.HeaderFooterProvider {

    private View mNewPostButton;
	private TextView mPageIndicator;
    private boolean newPostSent = false;

    /**
     * Provides pages for the ViewPager
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_layout);

        NUM_PAGES = getIntent().getExtras().getInt("Num_Pages");

        /**
         * Pageindicator button
         */
        mPageIndicator = (TextView)findViewById(R.id.pageIndicator);



        /**
         * ReSetup Toolbar
         */
        mToolbar = (Toolbar) findViewById(R.id.thread_toolbar);
        setActivityTitle(getIntent().getExtras().getString("Name"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /**
         * Setup ViewPager
         */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.thread_viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPageIndicator.setText(position + 1 + " / " + (NUM_PAGES == 0 ? "?" : NUM_PAGES));
            }
        });
        mPagerAdapter = new ScrollThreadPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mPagerAdapter);

        /**
         * Scroll to correct page in ViewPager
         */
        mViewPager.setCurrentItem(Integer.parseInt(getIntent().getExtras().getString("Page")), true);

        /**
         * Set page indicator text for page 1
         */
        if(getIntent().getExtras().getString("Page").equals("0")) mPageIndicator.setText("1 / " + NUM_PAGES);

        /**
         * Post reply-button stuff
         */

        mNewPostButton = findViewById(R.id.fab);

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
                                    .setStartDelay(0)   // Value carried over from "hide" animation delay in MyScrollView. Had to reset to 0 here
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(50);
                            return true;
                        case MotionEvent.ACTION_UP:
                            mNewPostButton.animate()
                                    .setStartDelay(0)   // Value carried over from "hide" animation delay in MyScrollView. Had to reset to 0 here
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(50);
                            return false;
                    }
                    return false;
                }
            });

            mPageIndicator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            mPageIndicator.animate()
                                    .translationZ(0)
                                    .setStartDelay(0)
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(50);
                            return true;
                        case MotionEvent.ACTION_UP:
                            mPageIndicator.animate()
                                    .translationZ(2)
                                    .setStartDelay(0)
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(50);

                            // Show overlay
                            View view = findViewById(R.id.overlay);
                            view.setVisibility(View.VISIBLE);

                            // Show pagepicker
                            LinearLayout pagepicker = (LinearLayout) findViewById(R.id.pagepicker);
                            pagepicker.setVisibility(View.VISIBLE);
                            pagepicker.animate()
                                    .translationY(-(findViewById(R.id.pagepicker).getHeight()))
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setDuration(300);

                            return false;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(newPostSent) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void dismissPagePicker(View view) {
            findViewById(R.id.pagepicker).animate()
                    .translationY(findViewById(R.id.pagepicker).getHeight())
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(300);

            findViewById(R.id.overlay).setVisibility(View.GONE);
    }

	@Override
	public View getHeader() {
		return mToolbar;
	}

	@Override
	public ArrayList<View> getFooter() {
        ArrayList<View> footer = new ArrayList<>();
        footer.add(mPageIndicator);
        footer.add(mNewPostButton);
        return footer;
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
            Bundle bundle = new Bundle();
            bundle.putString("Id", getIntent().getExtras().getString("Id"));
            bundle.putInt("Page", position + 1);
            Fragment_ThreadPage threadPage = new Fragment_ThreadPage();
            threadPage.setArguments(bundle);
            return threadPage;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
