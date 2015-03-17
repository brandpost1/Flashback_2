package brandpost.dev.flashback_2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import brandpost.dev.flashback_2.R;
import brandpost.dev.flashback_2.ThreadsActivity;
import brandpost.dev.flashback_2.misc.CategoryParser;
import brandpost.dev.flashback_2.misc.DocumentFetcher;

/**
 * Created by Viktor on 2014-12-26.
 */
public class Fragment_Forums extends Fragment {

	private CategoryParser.Category mContent;
	private ProgressBar mProgress;
	private View mContentView;

	private ArrayList<View> mHideableViews;
	private boolean isEditing = false;

	// Bundle arguments
	private String mUrl;
	private String mName;
	private String mColor;
	private String mId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		super.onPause();

		if(mProgress.getVisibility() == View.VISIBLE) {
			mProgress.setVisibility(View.GONE);
		}

		if(mContent != null && !mContent.mForums.isEmpty()) {
			if(mContentView != null)
				mContentView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("CategoryContent", mContent);
		outState.putString("Url", mUrl);
		outState.putString("Name", mName);
		outState.putString("Id", mId);
		outState.putString("Color", mColor);
	}


	@Override
	public void onResume() {
		super.onResume();
		if(mContent != null && mContent.mForums.isEmpty()) {
			if(mProgress != null)
				mProgress.setVisibility(View.VISIBLE);
		}
		if(mContent != null && !mContent.mForums.isEmpty()) {
			if(mContentView != null)
				mContentView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.forums_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.editBlocks:
				if(!item.isChecked()) {
					item.setChecked(true);
					for(View v : mHideableViews) {
						v.setVisibility(View.VISIBLE);
					}
				} else {
					item.setChecked(false);
					for(View v : mHideableViews) {
						v.setVisibility(View.GONE);
					}
				}
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		mHideableViews = new ArrayList<>();

		if(savedInstanceState == null) {

			// Get the arguments
			mUrl = getArguments().getString("Url");
			mColor = getArguments().getString("Color");
			mId = getArguments().getString("Id");
			mName = getArguments().getString("Name");

			// Set title
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(mName);

			DocumentFetcher.DocumentCallback callback = new DocumentFetcher.DocumentCallback<CategoryParser.Category>() {
				@Override
				public void onDocumentFetched(CategoryParser.Category document) {
					mContent = document;

					// Remove loading indicator
					if(mProgress != null) {
						if(mProgress.getVisibility() == View.VISIBLE) {
							mProgress.setVisibility(View.GONE);
						}
					}

					// Add the cards to the content view
					for(CategoryParser.Forum forum : mContent.mForums) {
						addForumCard(forum);
					}
				}
			};

			DocumentFetcher fetcher = new DocumentFetcher(callback, CategoryParser.class);
			fetcher.execute(mUrl);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate view
		mContentView = inflater.inflate(R.layout.fragment_forums, container, false);

		// Progressbar
		mProgress = (ProgressBar) mContentView.findViewById(R.id.progressbar);

		if(savedInstanceState != null) {
			// Restore state
			mName = (String)savedInstanceState.getSerializable("Name");
			mColor = (String)savedInstanceState.getSerializable("Color");
			mUrl = (String)savedInstanceState.getSerializable("Url");
			mId = (String)savedInstanceState.getSerializable("Id");

			// Restore "list"-items
			if(mContent == null) {
				mContent = (CategoryParser.Category) savedInstanceState.getSerializable("CategoryContent");
				if(!mContent.mForums.isEmpty()) {
					mProgress.setVisibility(View.GONE);
				}
			}

			// Restore the title
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(mName);

			// Add cards to contentview
			for(CategoryParser.Forum forum : mContent.mForums) {
				addForumCard(forum);
			}
		}

		return mContentView;
	}

	private void openForum(final CategoryParser.Forum forum) {
		Handler delay = new Handler();

		/**
		 * Short 300ms delay so that the click-animation has time to finish
		 */
		delay.postDelayed(new Runnable() {
			@Override
			public void run() {
				Bundle args = new Bundle();
				args.putString("Name", forum.mTitle);
				args.putString("Url", forum.mUrl);

				Intent intent = new Intent(getActivity(), ThreadsActivity.class);
				intent.putExtras(args);
				startActivity(intent);
			}
		}, 300);

	}

	private void addForumCard(final CategoryParser.Forum forum) {
		LinearLayout container = (LinearLayout)mContentView.findViewById(R.id.forumcard_container);
		View forumContainer = getActivity().getLayoutInflater().inflate(R.layout.forum_container, container, false);
		View subForumContainer = forumContainer.findViewById(R.id.subforum_container);

		forumContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openForum(forum);
			}
		});

		TextView forumTitle = (TextView)forumContainer.findViewById(R.id.forum_name);
		TextView forumInfo = (TextView)forumContainer.findViewById(R.id.forum_info);
		ImageButton blockForum = (ImageButton)forumContainer.findViewById(R.id.forum_block_button);
		forumTitle.setText(forum.mTitle);
		forumInfo.setText(forum.mInfo);
		blockForum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Blocking forum: " + forum.mTitle, Toast.LENGTH_SHORT).show();
			}
		});

		mHideableViews.add(blockForum);

		if(forum.mSubforums.isEmpty()) subForumContainer.setVisibility(View.GONE);

		for(int i = 0; i < forum.mSubforums.size(); i++) {
			final CategoryParser.Forum sub = forum.mSubforums.get(i);

			View subforum = getActivity().getLayoutInflater().inflate(R.layout.forum_container_subforum, (ViewGroup)subForumContainer, false);
			Button subforumButton = (Button)subforum.findViewById(R.id.subforum_name_button);
			ImageButton blockSubForum = (ImageButton)subforum.findViewById(R.id.subforum_block_button);
			subforumButton.setText(sub.mTitle);
			subforumButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openForum(sub);
				}
			});
			blockSubForum.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "Blocking forum: " + sub.mTitle, Toast.LENGTH_SHORT).show();
				}
			});
			mHideableViews.add(blockSubForum);

			((ViewGroup) subForumContainer).addView(subforum);
		}

		container.addView(forumContainer);
	}
}
