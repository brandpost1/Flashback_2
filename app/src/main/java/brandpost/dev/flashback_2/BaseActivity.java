package brandpost.dev.flashback_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Viktor on 2014-12-28.
 */
public class BaseActivity extends AppCompatActivity {

	//private ListView mDrawerList;
	protected Toolbar mToolbar;
	protected float mToolbarDefaultElevation;
	protected DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_main);
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		mToolbarDefaultElevation = mToolbar.getElevation();
		setSupportActionBar(mToolbar);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.my_drawer_layout);
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
