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

public class RankCompanyMenuFragment extends BaseFragment implements OnClickListener{
	
	private ImageButton mBack;
	private LinearLayout mll_company_rank;
	private LinearLayout mll_rank_activite;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		 
		 View view = inflater.inflate(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_activity_rank_companymenu"), container,
					false);
		 super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		 return view;
	}

	@Override
	public void findViews() {
		//返回键
		ImageButton mImageButtonBack = (ImageButton)findView(MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_my_button_back"));
		mImageButtonBack.setOnClickListener(this);
		
		mTextViewTitle = (TextView) findView(MResource.getIdByName(MapApplication.mContext, "id", "textView_title"));
		mTextViewTitle.setText("企业");
		
		mll_company_rank=findView(MResource.getIdByName(MapApplication.mContext, "id", "ll_company_rank"));
		mll_rank_activite=findView(MResource.getIdByName(MapApplication.mContext, "id", "ll_rank_activite"));
		mll_company_rank.setOnClickListener(this);
		mll_rank_activite.setOnClickListener(this);
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
		}else if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "ll_company_rank")) {
			Intent it=new Intent();
			it.putExtra(RankingActivity.ISAREARANK, RankingActivity.RANK_GROUP);
			it.setClass(getActivity(), RankingActivity.class);
	        startActivity(it);
		}else if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "ll_rank_activite")) {
			Intent itent=new Intent();
			itent.putExtra("intent", 5);
			itent.setClass(getActivity(), TabBaseFragment.class);
	        startActivity(itent);
		}
	}

}
