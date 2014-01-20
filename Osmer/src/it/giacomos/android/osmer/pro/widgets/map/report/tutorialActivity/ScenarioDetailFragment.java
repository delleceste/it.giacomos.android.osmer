package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import it.giacomos.android.osmer.pro.preferences.Settings;

/**
 * A fragment representing a single Scenario detail screen. This fragment is
 * either contained in a {@link ScenarioListActivity} in two-pane mode (on
 * tablets) or a {@link ScenarioDetailActivity} on handsets.
 */
public class ScenarioDetailFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_FORCE_SHOW_TERMS_AND_CONDITIONS = "startedFromActivity";
	public static final String ARG_CONDITIONS_ACCEPTED = "conditionsAccepted";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ScenarioItem mItem;

	private ScenarioContent mContent;
	private ReportConditionsAcceptedListener mReportConditionsAcceptedListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ScenarioDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mContent = new ScenarioContent();
			mContent.addItem(new ScenarioItem("publishReport", getResources().getString(R.string.tutorial_publish_report)));
			mContent.addItem(new ScenarioItem("requestReport", getResources().getString(R.string.tutorial_publish_request)));
			mContent.addItem(new ScenarioItem("notificationService", getResources().getString(R.string.tutorial_notification_service)));
			mContent.addItem(new ScenarioItem("termsOfUse", getResources().getString(R.string.tutorial_terms_conditions)));
			
			mItem = mContent.itemMap.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_scenario_detail,
				container, false);

		Bundle extras = getArguments();
		boolean forceShowTermsAndConditions = extras != null && extras.getBoolean(ARG_FORCE_SHOW_TERMS_AND_CONDITIONS);
		boolean conditionsAccepted = extras != null && extras.getBoolean(ARG_CONDITIONS_ACCEPTED);
		Button btPrev = (Button) rootView.findViewById(R.id.btTutorialReportPrevious);
		Button btNext = (Button) rootView.findViewById(R.id.btTutorialReportNext);
		CheckBox cb = (CheckBox) rootView.findViewById(R.id.cbAcceptTermsAndConditions);
		btPrev.setOnClickListener(this);
		btNext.setOnClickListener(this);
		cb.setChecked(conditionsAccepted);
		cb.setOnCheckedChangeListener(this);
		btPrev.setEnabled(false);
		
		ViewFlipper vfMain = ((ViewFlipper) rootView.findViewById(R.id.vfMain));
		
		if (mItem != null) 
		{
			if(forceShowTermsAndConditions || mItem.id.compareTo("termsOfUse") == 0)
				vfMain.setDisplayedChild(3);
			else if(mItem.id.compareTo("publishReport") == 0)
				vfMain.setDisplayedChild(0);
			else if(mItem.id.compareTo("requestReport") == 0)
				vfMain.setDisplayedChild(1);
			else if(mItem.id.compareTo("notificationService") == 0)
				vfMain.setDisplayedChild(2);
		}

		Log.e("ScenarioDetailFragment.onCreateView", "flipper child " + vfMain.getDisplayedChild());
		return rootView;
	}

	@Override
	public void onClick(View v) 
	{
		Button b;
		if(v.getId() == R.id.btTutorialReportPrevious || v.getId() == R.id.btTutorialReportNext)
		{
			b = (Button) v;
			ViewFlipper vfMain = ((ViewFlipper) this.getView().findViewById(R.id.vfMain));
			ViewFlipper subVf = null;
			if(vfMain.getDisplayedChild() == 0) /* report */
				subVf = (ViewFlipper) this.getView().findViewById(R.id.vfTutorialReport);
			else if(vfMain.getDisplayedChild() == 1)
				subVf = (ViewFlipper) this.getView().findViewById(R.id.vfTutorialRequestReport);
			else if(vfMain.getDisplayedChild() == 2)
				subVf = (ViewFlipper) this.getView().findViewById(R.id.vfTutorialNotifications);
			
			if(v.getId() == R.id.btTutorialReportPrevious)
			{
				if(subVf.getDisplayedChild() > 0)
					subVf.setDisplayedChild(subVf.getDisplayedChild() - 1);
			}
			else if(v.getId() == R.id.btTutorialReportNext)
			{
				if(subVf.getDisplayedChild() < subVf.getChildCount() - 1)
					subVf.setDisplayedChild(subVf.getDisplayedChild() + 1);
			}
			b = (Button) getView().findViewById(R.id.btTutorialReportNext);
			b.setEnabled(subVf.getDisplayedChild() < subVf.getChildCount() - 1);
			b = (Button) getView().findViewById(R.id.btTutorialReportPrevious);
			b.setEnabled(subVf.getDisplayedChild() > 0);
					
		}
		
	}

	@Override
	public void onCheckedChanged(CompoundButton bt, boolean checked) 
	{
		if(bt.getId() == R.id.cbAcceptTermsAndConditions)
		{
			Log.e("ScenarioDetailFragment.onCheckedChanged", "conditions accepted: " + checked);
			/* There are two kinds of listeners: ScenarioDetailActivity, which sets into the setResult
			 * intent the value of this boolean, for the handset case, and the ScenarioListActivity,
			 * that, in case of a two pane device, directly sets the result to return to OsmerActivity.
			 */
			mReportConditionsAcceptedListener.onReportConditionsAccepted(checked);
		}
	}

	public void setReportConditionsAcceptedListener(ReportConditionsAcceptedListener l) 
	{
		mReportConditionsAcceptedListener = l;
	}
}
