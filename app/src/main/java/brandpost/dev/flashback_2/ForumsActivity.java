package brandpost.dev.flashback_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import brandpost.dev.flashback_2.fragments.Fragment_Forums;

/**
 * Created by Viktor on 2014-12-28.
 */
public class ForumsActivity extends BaseActivity {

	private String mForumTitle;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Name", mForumTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forums_layout);
		mToolbar = (Toolbar) findViewById(R.id.forums_toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if(savedInstanceState == null) {

			Bundle fragmentArgs = getIntent().getExtras();
			mForumTitle = fragmentArgs.getString("Name");

			Fragment forumsFragment = new Fragment_Forums();
			forumsFragment.setArguments(fragmentArgs);

			replaceFragment(forumsFragment);
		} else {
			mForumTitle = savedInstanceState.getString("Name");
		}

        setActivityTitle(mForumTitle);
	}

}
