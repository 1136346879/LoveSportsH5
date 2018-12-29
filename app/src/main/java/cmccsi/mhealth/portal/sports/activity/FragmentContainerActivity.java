package cmccsi.mhealth.portal.sports.activity;

import android.os.Bundle;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;

public class FragmentContainerActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(MResource.getIdByName(this, "layout", "zyqt_frament_activity"));
		MapStartRunningFragment fragment = new MapStartRunningFragment(
                "map");
		 getSupportFragmentManager().beginTransaction()
         .replace(MResource.getIdByName(this, "id", "frament_activity"), fragment).commit();
	}
}
