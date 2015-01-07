package it.giacomos.android.osmer.pager;


import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
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
		// Log.e("onItemClick drawer listener", "clicekd at pos " + id);
		ListView drawerListView = mOsmerActivity.getDrawerListView();
		String[] drawerItems = mOsmerActivity.getDrawerItems();
		drawerListView.setItemChecked(position, true);
		mOsmerActivity.setTitle(drawerItems[position]);

		DrawerLayout drawerLayout = (DrawerLayout) mOsmerActivity.findViewById(R.id.drawer_layout);
		drawerLayout.closeDrawer(drawerListView);
		/* calls switchView on OsmerActivity with the position passed */
		mOsmerActivity.getActionBarManager().drawerItemChanged(position);
	}

	private OsmerActivity mOsmerActivity;
}
