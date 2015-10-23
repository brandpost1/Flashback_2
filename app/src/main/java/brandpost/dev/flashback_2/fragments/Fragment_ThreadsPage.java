package brandpost.dev.flashback_2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import brandpost.dev.flashback_2.BaseActivity;
import brandpost.dev.flashback_2.R;
import brandpost.dev.flashback_2.ThreadActivity;
import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.misc.DocumentFetcher;
import brandpost.dev.flashback_2.misc.ForumParser;

/**
 * Created by Viktor on 2014-12-28.
 */
public class Fragment_ThreadsPage extends Fragment {

	private MyScrollView.HeaderFooterProvider mCallback;

	private String mUrl;
	private int mPage;
	private ProgressBar mProgress;
	private ForumParser.ForumPage mContent;
	private View mContentView;
	private LayoutInflater mInflater;
	private MyScrollView mScrollView;

	private int mAddedItems = 0;

	/**
	 * Provides "Swipe to refresh"
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;

	/**
	 * Adds more views as you reach the bottom
	 */
	private MyScrollView.OnReachedBottomListener mReachedBottomListener = new MyScrollView.OnReachedBottomListener() {
		@Override
		public void onBottomReached() {
			final int start = mAddedItems;

			// Add the cards to the content view
			for (int i = start; i < start + 10; i++) {
				if(i <= mContent.mThreads.size()-1) {
					addThreadItem(mContent.mThreads.get(i));
				} else {
					return;
				}
			}
		}
	};

	private SwipeRefreshLayout.OnRefreshListener mOnrefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			refreshThreads();
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if(mScrollView != null) {
				mScrollView.showFooter(true);
                mScrollView.showHeader(true);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (MyScrollView.HeaderFooterProvider)activity;
		} catch (ClassCastException e) {
			System.out.println("Activity must implement the HeaderFooterProvider-interface.");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Url", mUrl);
		outState.putInt("Page", mPage);
		outState.putInt("AddedItems", mAddedItems);
		outState.putSerializable("Content", mContent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInflater = getActivity().getLayoutInflater();
		if(savedInstanceState == null) {
			mUrl = getArguments().getString("Url");
			mPage = getArguments().getInt("Page");

			getAddThreads();

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_threadspage, container, false);

		mProgress = (ProgressBar)mContentView.findViewById(R.id.progressbar);
		mScrollView = (MyScrollView)mContentView.findViewById(R.id.scroller);

        mScrollView.setHeaderView(mCallback.getHeader());

		for(View v : mCallback.getFooter()) {
			mScrollView.addFooterView(v);
		}

		mSwipeRefreshLayout = (SwipeRefreshLayout)mContentView.findViewById(R.id.swiperefresh);

		// Offset refresh-indicator by 72dp in pixels to account for padding
		Resources r = getResources();
		float paddingInPixelsStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, r.getDisplayMetrics());
		float paddingInPixelsEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 92, r.getDisplayMetrics());
		mSwipeRefreshLayout.setProgressViewOffset(true, (int)paddingInPixelsStart, (int)paddingInPixelsEnd);

		mSwipeRefreshLayout.setOnRefreshListener(mOnrefreshListener);

		mScrollView.setOnReachedBottomListener(mReachedBottomListener);

		if(savedInstanceState != null) {

			// Restore state
			mUrl = savedInstanceState.getString("Url");
			mPage = savedInstanceState.getInt("Page");
			mAddedItems = savedInstanceState.getInt("AddedItems");

			mContent = (ForumParser.ForumPage)savedInstanceState.getSerializable("Content");

			if(mContent != null) {
				if (!mContent.mThreads.isEmpty()) {
					// Remove loading indicator
					if (mProgress != null) {
						if (mProgress.getVisibility() == View.VISIBLE) {
							mProgress.setVisibility(View.GONE);
						}
					}
				}

				// Show footer views
				for(View v : mCallback.getFooter()) {
					v.setVisibility(View.VISIBLE);
				}

				int upto = (mAddedItems == 0 ? 10 : mAddedItems);

				mAddedItems = 0;
				// Add the cards to the content view
				for (int i = 0; i < upto; i++) {
					addThreadItem(mContent.mThreads.get(i));
				}
			}
		}
		return mContentView;
	}

	private void refreshThreads() {
		LinearLayout container = (LinearLayout)mContentView.findViewById(R.id.card_container);

		// Clear old views
		container.removeAllViews();

		// Hide footer views
		for(View v : mCallback.getFooter()) {
			v.setVisibility(View.GONE);
		}


		// Show loading indicator
		if (mProgress != null) {
			if (mProgress.getVisibility() == View.GONE) {
				mProgress.setVisibility(View.VISIBLE);
			}
		}

		// Reset the added-items counter
		mAddedItems = 0;

		// Get the threads and add them to the contentview
		getAddThreads();
	}

	private void getAddThreads() {
		DocumentFetcher.DocumentCallback<ForumParser.ForumPage> callback = new DocumentFetcher.DocumentCallback<ForumParser.ForumPage>() {
			@Override
			public void onDocumentFetched(ForumParser.ForumPage threads) {
				mContent = threads;

				// Remove loading indicator
				if (mProgress != null) {
					if (mProgress.getVisibility() == View.VISIBLE) {
						mProgress.setVisibility(View.GONE);
					}
				}
				// Hide the refresh-indicator
				if(mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.setRefreshing(false);
				}

				// Add threads to contentview
				addThreads();

				// Show Footer again
				for(View v : mCallback.getFooter()) {
					v.setVisibility(View.VISIBLE);
				}
			}
		};

		DocumentFetcher fetcher = new DocumentFetcher(callback, ForumParser.class);
		fetcher.execute(mUrl + "p" + mPage);
	}

	private void addThreads() {
		if(mContent != null) {
			if (mContent.mThreads.size() > 0) {
				int upto = (mAddedItems == 0 ? 10 : mAddedItems);
				// Add the cards to the content view
				for (int i = 0; i < upto; i++) {
					addThreadItem(mContent.mThreads.get(i));
				}
			}
		}
	}


	private void addThreadItem(final ForumParser.Thread thread) {
		if(mContentView != null) {
			LinearLayout container = (LinearLayout) mContentView.findViewById(R.id.card_container);
			View threadcontainer = mInflater.inflate(R.layout.thread_container, container, false);

			TextView title = (TextView) threadcontainer.findViewById(R.id.threadtitle);
			TextView author = (TextView) threadcontainer.findViewById(R.id.threadauthor);
			TextView lastactivity = (TextView) threadcontainer.findViewById(R.id.threadlastactivity);
			TextView replies = (TextView) threadcontainer.findViewById(R.id.threadreplies);
			TextView views = (TextView) threadcontainer.findViewById(R.id.threadviews);
			Button lastPage = (Button) threadcontainer.findViewById(R.id.thread_gotolast);

			title.setText(thread.mTitle);
			author.setText(thread.mAuthor);
			lastactivity.setText(thread.mLastActivity);
			replies.setText("Svar: " + thread.mNumReplies);
			views.setText("Visningar: " + thread.mNumViews);

			lastPage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openThread(thread, true);
				}
			});

			threadcontainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openThread(thread, false);
				}
			});

            if(mAddedItems == 0) mScrollView.setHeaderAnchor(threadcontainer);
			mAddedItems++;
			container.addView(threadcontainer);
		}
	}

	private void openThread(final ForumParser.Thread thread, final boolean lastPage) {
		Handler delay = new Handler();

		/**
		 * Short 300ms delay so that the click-animation has time to finish
		 */
		delay.postDelayed(new Runnable() {
			@Override
			public void run() {
				Bundle args = new Bundle();
				args.putString("Name", thread.mTitle);
				args.putString("Id", thread.mId);
				String page  = lastPage ? thread.mNumPages : "0";
                args.putString("Page", page);
				args.putInt("Num_Pages", Integer.valueOf(thread.mNumPages));

				Intent intent = new Intent(getActivity(), ThreadActivity.class);
				intent.putExtras(args);
				startActivity(intent);
			}
		}, 300);

	}
}
