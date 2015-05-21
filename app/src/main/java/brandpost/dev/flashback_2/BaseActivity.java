package brandpost.dev.flashback_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Viktor on 2014-12-28.
 */
public class BaseActivity extends ActionBarActivity {

	private ListView mDrawerList;
	protected Toolbar mToolbar;
	protected DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);

		mDrawerLayout = (DrawerLayout)findViewById(R.id.my_drawer_layout);

		setSupportActionBar(mToolbar);

		mDrawerList = (ListView)findViewById(R.id.drawerList);
		mDrawerList.setAdapter(new DrawerAdapter(this));
	}

	protected void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content, fragment)
				.commit();
	}

    protected void setActivityTitle(String title) {
        ((TextView)mToolbar.findViewById(R.id.toolbar_title)).setText(title);
    }

	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(GravityCompat.START);
	}

	@Override
	public void onBackPressed() {
		if(isDrawerOpen()) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

}
