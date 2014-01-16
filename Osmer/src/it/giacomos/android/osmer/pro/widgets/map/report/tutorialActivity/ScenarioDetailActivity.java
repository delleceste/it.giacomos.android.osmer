package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * An activity representing a single Scenario detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link ScenarioListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ScenarioDetailFragment}.
 */
public class ScenarioDetailActivity extends FragmentActivity {

	private ScenarioContent mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenario_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) 
		{
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			boolean fromUpNavButton = getIntent() != null && 
					getIntent().getBooleanExtra(ScenarioDetailFragment.ARG_NAVIGATED_UP, false);
			
			arguments.putString(ScenarioDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(ScenarioDetailFragment.ARG_ITEM_ID));
			ScenarioDetailFragment fragment = new ScenarioDetailFragment();
			arguments.putBoolean(ScenarioDetailFragment.ARG_NAVIGATED_UP, fromUpNavButton);
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.scenario_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent parentIntent = new Intent(this, ScenarioListActivity.class);
			Log.e("onOptionsItemSelected", "putting extra");
			parentIntent.putExtra("navigatedUp", true);
			NavUtils.navigateUpTo(this, parentIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
