package brandpost.dev.flashback_2;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;


import brandpost.dev.flashback_2.fragments.Fragment_Categories;


public class MainActivity extends BaseActivity {

	private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
	    mDrawerLayout.setDrawerListener(drawerToggle);

	    if(savedInstanceState == null) {
		    // Show the categories
		    Fragment categories = new Fragment_Categories();
		    replaceFragment(categories);
	    }
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
}
