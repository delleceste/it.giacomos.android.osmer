package it.giacomos.android.osmer.guiHelpers;


import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener 
{
	public DrawerItemClickListener(OsmerActivity a)
	{
		super();
		mOsmerActivity = a;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		ListView drawerListView = mOsmerActivity.getDrawerListView();
		String[] drawerItems = mOsmerActivity.getDrawerItems();
		drawerListView.setItemChecked(position, true);
		mOsmerActivity.setTitle(drawerItems[position]);
		DrawerLayout drawerLayout = (DrawerLayout) mOsmerActivity.findViewById(R.id.drawer_layout);
		drawerLayout.closeDrawer(drawerListView);
		mOsmerActivity.getActionBarPersonalizer().drawerItemChanged(position);
		switch(position)
		{
		case 0:
			mOsmerActivity.switchView(ViewType.HOME);
			break;
		case 1:
			mOsmerActivity.switchView(ViewType.RADAR);
			break;
		case 2:
			mOsmerActivity.switchView(ViewType.DAILY_SKY);
			break;
		case 3:
			mOsmerActivity.switchView(ViewType.LATEST_SKY);
			break;
		case 4:
			mOsmerActivity.switchView(ViewType.WEBCAM);
		}
	}
	
	private OsmerActivity mOsmerActivity;
}
