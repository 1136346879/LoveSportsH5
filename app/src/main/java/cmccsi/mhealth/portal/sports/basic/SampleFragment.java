package cmccsi.mhealth.portal.sports.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;

public class SampleFragment extends BaseFragment implements OnClickListener{
	protected static final String TAG = "SampleFragment";
	
	protected TextView mTextViewTitle;// 标题栏
	protected ImageView mBack;// 侧滑按钮
	protected String mTitle;
	
	protected String mPhoneNum; //手机号
	protected String mPassword;//密码

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(container == null || container.getTag() == null || !"inflated".equals(container.getTag().toString())){
			container = (ViewGroup) inflater.inflate(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_sample_fragment_blank"), container, false);
		}
		super.onCreateView(inflater, container, savedInstanceState);
		return container;
	}

	private void initView() {
		mTextViewTitle = findView(MResource.getIdByName(MapApplication.mContext, "id", "textView_title"));
		mBack = findView(MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"));
		mBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"):
//			//showMenu();
//			break;
//		}
		
	}
	
	@Override 
	public void findViews() {
		initView();
		loadNessesaryInfo();
	};
	
	private void loadNessesaryInfo() {
		mPhoneNum = sp.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
	}

	@Override
	public void clickListner() {
		mBack.setOnClickListener(this);
	}
	@Override
	public void loadLogic() {
	}
}
