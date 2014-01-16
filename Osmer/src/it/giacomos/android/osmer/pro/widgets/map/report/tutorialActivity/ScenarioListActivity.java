package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity.ScenarioListFragment.Callbacks;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * An activity representing a list of Scenarios. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ScenarioDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ScenarioListFragment} and the item details (if present) is a
 * {@link ScenarioDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ScenarioListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ScenarioListActivity extends FragmentActivity implements
		ScenarioListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private boolean mConditionsAccepted;
	
	public static int SCENARIO_DETAIL_ACTIVITY_FOR_RESULT_ID = 10012;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_scenario_list);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		
		if (findViewById(R.id.scenario_detail_container) != null) 
		{
			Log.e("ScenarioListActivity.onCreate", "two pane");
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			ScenarioListFragment scenarioListFragment = (ScenarioListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.scenario_list);
			scenarioListFragment.setActivateOnItemClick(true);
		}
		else
			Log.e("ScenarioListActivity.onCreate", "NOT two pane");
		
		mConditionsAccepted = new Settings(this).reportConditionsAccepted();
		boolean fromUpNavButton = getIntent() != null && getIntent().getBooleanExtra("navigatedUp", false);
		if(!mConditionsAccepted && !fromUpNavButton)
		{
			Log.e("ScenarioListActivity.onCreate", "selecting termsOfUse " + mConditionsAccepted + " from nav " 
					+ fromUpNavButton);
			onItemSelected("termsOfUse");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			
			
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		
		Log.e("onOptionsItemSelected", "not putting extra");
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link ScenarioListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) 
	{
		boolean fromUpNavButton = getIntent() != null && getIntent().getBooleanExtra("navigatedUp", false);
		if (mTwoPane) 
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ScenarioDetailFragment.ARG_ITEM_ID, id);
			arguments.putBoolean(ScenarioDetailFragment.ARG_NAVIGATED_UP, fromUpNavButton);
			ScenarioDetailFragment fragment = new ScenarioDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.scenario_detail_container, fragment).commit();

		} 
		else 
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ScenarioDetailActivity.class);
			detailIntent.putExtra(ScenarioDetailFragment.ARG_ITEM_ID, id);
			detailIntent.putExtra(ScenarioDetailFragment.ARG_NAVIGATED_UP, fromUpNavButton);
			startActivityForResult(detailIntent, SCENARIO_DETAIL_ACTIVITY_FOR_RESULT_ID);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == SCENARIO_DETAIL_ACTIVITY_FOR_RESULT_ID)
		{
			Intent i = new Intent();
			boolean conditionsAccepted = (resultCode == Activity.RESULT_OK && 
					data.getExtras() != null && 
					data.getExtras().getBoolean("conditionsAccepted"));
			i.putExtra("conditionsAccepted", conditionsAccepted);
			setResult(Activity.RESULT_OK, i);
		}
	}
}
