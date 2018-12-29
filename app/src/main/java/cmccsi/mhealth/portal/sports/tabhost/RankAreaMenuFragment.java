package cmccsi.mhealth.portal.sports.tabhost;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.activity.RankingActivity;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseFragment;
import cmccsi.mhealth.portal.sports.basic.MapApplication;

public class RankAreaMenuFragment extends BaseFragment  implements OnClickListener{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_activity_rank_areamenu"), container,
				false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}
	
	@Override
	public void findViews() {
		ImageButton mBack = findView(MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"));
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_my_button_back"));
		mBack.setOnClickListener(this);
		
		mTextViewTitle = (TextView) findView(MResource.getIdByName(MapApplication.mContext, "id", "textView_title"));
		mTextViewTitle.setText("区域");
		
		LinearLayout mll_area_rank=findView(MResource.getIdByName(MapApplication.mContext, "id", "ll_area_rank"));
		mll_area_rank.setOnClickListener(this);
	}

	@Override
	public void clickListner() {

	}

	@Override
	public void loadLogic() {

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back")) {
			getActivity().finish();
		}else if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "ll_area_rank")) {
			Intent it=new Intent();
			it.putExtra(RankingActivity.ISAREARANK, RankingActivity.RANK_AREA);
			it.setClass(getActivity(), RankingActivity.class);
	        startActivity(it);
		}
	}

}
