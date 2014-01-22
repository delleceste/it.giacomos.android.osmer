package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import it.giacomos.android.osmer.pro.preferences.Settings;
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
public class ScenarioDetailActivity extends FragmentActivity implements ReportConditionsAcceptedListener
{

	private boolean mConditionsAccepted;

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
			Intent i = getIntent();
			boolean forceShowTermsAndConditions = i != null && 
					i.getBooleanExtra(ScenarioDetailFragment.ARG_FORCE_SHOW_TERMS_AND_CONDITIONS, false);
			mConditionsAccepted = i != null && i.getBooleanExtra(ScenarioDetailFragment.ARG_CONDITIONS_ACCEPTED, false);
			
			arguments.putString(ScenarioDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(ScenarioDetailFragment.ARG_ITEM_ID));
			
			ScenarioDetailFragment fragment = new ScenarioDetailFragment();
			arguments.putBoolean(ScenarioDetailFragment.ARG_FORCE_SHOW_TERMS_AND_CONDITIONS, forceShowTermsAndConditions);
			arguments.putBoolean(ScenarioDetailFragment.ARG_CONDITIONS_ACCEPTED, mConditionsAccepted);
			fragment.setArguments(arguments);
			fragment.setReportConditionsAcceptedListener(this);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	public void onBackPressed()
//	{
//		Intent parentIntent = new Intent(this, ScenarioListActivity.class);
//		Log.e("ScenarioDetailActivity.onBackPressed", "putting extra conditions accepted: " + mConditionsAccepted);
//		parentIntent.putExtra(ScenarioDetailFragment.ARG_CONDITIONS_ACCEPTED, mConditionsAccepted);
//		NavUtils.navigateUpTo(this, parentIntent);
//	}

	@Override
	public void onReportConditionsAccepted(boolean accepted) 
	{
		Intent i = new Intent();
		i.putExtra("conditionsAccepted", accepted);
		this.setResult(RESULT_OK, i);
		mConditionsAccepted = accepted;
		new Settings(this).setReportConditionsAccepted(accepted);
//		if(accepted) /* back to list view */
//			onBackPressed();
	}
}
