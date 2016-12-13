package it.giacomos.android.osmer.widgets.map.report.tutorialActivity;

import it.giacomos.android.osmer.R;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuickStartNotificationsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.tutorial_notifications, container,
				false);
		
		return rootView;
	}
	
}
