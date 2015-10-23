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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import java.util.ArrayList;

import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.fragments.Fragment_ThreadsPage;
import brandpost.dev.flashback_2.misc.DocumentFetcher;
import brandpost.dev.flashback_2.misc.ForumParser;

/**
 * Created by Viktor on 2014-12-28.
 */
public class ThreadsActivity extends BaseActivity implements MyScrollView.HeaderFooterProvider {

	private int mNumberOfPages = 0;
	private String mForumName = "";
	private String mForumUrl = "";
	private View mNewThreadButton;
	private TextView mPageIndicator;
	private Bundle activityExtras;
	private String mPageIndicatorText;

	/**
	 * Provides pages for the ViewPager
	 */
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Name", mForumName);
		outState.putInt("Pages", mNumberOfPages);
		outState.putString("Url", mForumUrl);
		outState.putString("PageIndicatorText", mPageIndicatorText);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_layout);

		/**
		 * Pageindicator button
		 */
		mPageIndicator = (TextView)findViewById(R.id.pageIndicator);

		/**
		 * Setup ViewPager
		 */
		final ViewPager mViewPager = (ViewPager) findViewById(R.id.thread_viewpager);
		mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mPageIndicator.setText(position + 1 + " / " + (mNumberOfPages == 0 ? "?" : mNumberOfPages));
			}
		});
        mViewPager.setOffscreenPageLimit(1);
		mPagerAdapter = new ForumPagePagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);

		/**
		 * Post new thread button
		 */
		mNewThreadButton = findViewById(R.id.fab);

		if(savedInstanceState == null) {
			activityExtras = getIntent().getExtras();

			// Get the url from the arguments
			mForumUrl = activityExtras.getString("Url");

			// Get the threadname
			mForumName = activityExtras.getString("Name");

			DocumentFetcher.DocumentCallback<ForumParser.ForumPage> callback = new DocumentFetcher.DocumentCallback<ForumParser.ForumPage>() {
				@Override
				public void onDocumentFetched(ForumParser.ForumPage data) {
					mNumberOfPages = data.mNumPages;

					String indicatorCurrentPage = mPageIndicator.getText().toString();
					mPageIndicatorText = (indicatorCurrentPage.isEmpty() ? "1" : indicatorCurrentPage) + " / " + mNumberOfPages;

					mPageIndicator.setText(mPageIndicatorText);
					// Notify the adapter so it will start supplying pages
					mPagerAdapter.notifyDataSetChanged();
				}
			};

			// Get the document for that url
			DocumentFetcher fetcher = new DocumentFetcher(callback, ForumParser.class);
			fetcher.execute(mForumUrl);
		} else {
			mForumName = savedInstanceState.getString("Name");
			mForumUrl = savedInstanceState.getString("Url");
			mPageIndicatorText = savedInstanceState.getString("PageIndicatorText");
			mNumberOfPages = savedInstanceState.getInt("Pages");
			mPagerAdapter.notifyDataSetChanged();
			mPageIndicator.setText(mPageIndicatorText);

		}

		/**
		 * ReSetup Toolbar
		 */
		mToolbar = (Toolbar) findViewById(R.id.forum_toolbar);
		setSupportActionBar(mToolbar);
        setActivityTitle(mForumName);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Lollipop specific
		if(android.os.Build.VERSION.SDK_INT >= 21) {
			CircleOutlineProvider mCircleOutline = new CircleOutlineProvider();

			mNewThreadButton.setOutlineProvider(mCircleOutline);
			mNewThreadButton.setClipToOutline(true);

			mNewThreadButton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getActionMasked()) {
						case MotionEvent.ACTION_DOWN:
							mNewThreadButton.animate()
									.setStartDelay(0)   // Value carried over from "hide" animation delay in MyScrollView. Had to reset to 0 here
									.scaleX(0.95f)
									.scaleY(0.95f)
									.setDuration(50);
							return true;
						case MotionEvent.ACTION_UP:
							mNewThreadButton.animate()
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
							return false;
					}
					return false;
				}
			});
		}
	}

	@Override
	public View getHeader() {
		return mToolbar;
	}

	@Override
	public ArrayList<View> getFooter()
    {
		ArrayList<View> footer = new ArrayList<>();
		footer.add(mPageIndicator);
		footer.add(mNewThreadButton);

		return footer;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private class CircleOutlineProvider extends ViewOutlineProvider {
		@Override
		public void getOutline(View view, Outline outline) {
			outline.setOval(0, 0, view.getWidth(), view.getHeight());
		}
	}

	private class ForumPagePagerAdapter extends FragmentStatePagerAdapter {
		public ForumPagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment_ThreadsPage threadPage = new Fragment_ThreadsPage();
			threadPage.setUserVisibleHint(false);
			Bundle bundle = new Bundle();
			bundle.putString("Url", mForumUrl);
			bundle.putInt("Page", position + 1);
			threadPage.setArguments(bundle);
			return threadPage;
		}

		@Override
		public int getCount() {
			return mNumberOfPages;
		}
	}
}
