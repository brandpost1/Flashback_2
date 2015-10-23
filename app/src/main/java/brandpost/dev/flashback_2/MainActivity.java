package brandpost.dev.flashback_2;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;


import brandpost.dev.flashback_2.fragments.Fragment_Categories;
import brandpost.dev.flashback_2.fragments.Fragment_CurrentThreads;
import brandpost.dev.flashback_2.misc.autoupdater.AutoUpdater;
import brandpost.dev.flashback_2.misc.autoupdater.Downloader;


public class MainActivity extends BaseActivity {

	private ActionBarDrawerToggle drawerToggle;
	private TabLayout mTabLayout;
	private AutoUpdater mAutoUpdater;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
	    mDrawerLayout.setDrawerListener(drawerToggle);

	    if(savedInstanceState == null) {
		    // Show the categories
		    Fragment categories = new Fragment_Categories();
		    replaceFragment(categories);

			/**
			 * Setup AutoUpdater
			 */
			mAutoUpdater = new AutoUpdater(this);
			mAutoUpdater.checkForUpdate("https://api.github.com/repos/brandpost1/Flashback_2/releases/latest");


			/*
			* Setup TabLayout
			*/

			mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
			mTabLayout.setVisibility(View.VISIBLE);

			mTabLayout.addTab(mTabLayout.newTab().setText("Kategorier"));
			mTabLayout.addTab(mTabLayout.newTab().setText("Favoriter"));

			mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
				@Override
				public void onTabSelected(TabLayout.Tab tab) {
					switch (tab.getPosition()) {
						case 0:

							break;
						case 1:

							break;
					}
				}

				@Override
				public void onTabUnselected(TabLayout.Tab tab) {

				}

				@Override
				public void onTabReselected(TabLayout.Tab tab) {

				}
			});

			/*
			* Setup Navigation drawer
			*/
			final NavigationView navigationView = (NavigationView)findViewById(R.id.navview);
			navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
				final Handler drawerActionHandler = new Handler();

				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem) {
					String title = menuItem.getTitle().toString();

					findViewById(R.id.tab_layout).setVisibility(View.GONE);

					if(title.equals("Hem")) {
						setActivityTitle("");

						menuItem.setChecked(true);
						mDrawerLayout.closeDrawer(GravityCompat.START);

						mToolbar.setElevation(mToolbarDefaultElevation);

						drawerActionHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								Fragment categories = new Fragment_Categories();
								replaceFragment(categories);
							}
						}, 250);
						drawerActionHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
							}
						}, 550);

					} else if(title.equals("Aktuella \u00E4mnen")) {
						menuItem.setChecked(true);
						mDrawerLayout.closeDrawer(GravityCompat.START);

						drawerActionHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								setActivityTitle("Aktuella \u00E4mnen");

								Fragment currentThreads = new Fragment_CurrentThreads();
								replaceFragment(currentThreads);

								mToolbar.setElevation(0);
							}
						}, 250);
					} else if(title.equals("Nya \u00E4mnen")) {

					} else if(title.equals("Nya inl\u00E4gg")) {

					} else if(title.equals("Inst\u00E4llningar")) {

					} else if(title.equals("Logga in")) {

					}
					return false;
				}
			});
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
