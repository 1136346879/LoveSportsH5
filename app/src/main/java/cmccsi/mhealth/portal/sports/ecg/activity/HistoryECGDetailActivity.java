package cmccsi.mhealth.portal.sports.ecg.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.DataECG;
import cmccsi.mhealth.portal.sports.bean.ECGListinfo;
import cmccsi.mhealth.portal.sports.bean.ECGSummary;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.portal.sports.ecg.fragment.ECGChartFragment;
import cmccsi.mhealth.portal.sports.ecg.utils.Range;
import cmccsi.mhealth.portal.sports.ecg.utils.RangeUtil;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.net.NetworkTool;
import cmccsi.mhealth.portal.sports.view.CustomProgressDialog;

public class HistoryECGDetailActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "HistoryECGDetailActivity";
	private FragmentStatePagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private CustomProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplicationContext(), "layout", "zyqt_activity_ecg_detail1"));
		initViews();
		Range semiAnnual = RangeUtil.getRange(RangeUtil.RANGE_TYPE_SEMIANNUAL);
		new DownloadEcgTask(semiAnnual.getStartDate(),semiAnnual.getEndDate()).execute();
	}

	private void initViews() {
		mViewPager = (ViewPager) findViewById(MResource.getIdByName(getApplicationContext(), "id", "pager"));
		mRadioGroup = (RadioGroup) findViewById(MResource.getIdByName(getApplicationContext(), "id", "radioGroup1"));

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// 滑动时选择对应单选框
						RadioButton radio = (RadioButton) mRadioGroup
								.getChildAt(position);
						radio.setChecked(true);
					}
				});
		OnRadioItemClickListener radioItemClickListener = new OnRadioItemClickListener();
		// 每一个单选框都设置ClickListener
		for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
			mRadioGroup.getChildAt(i)
					.setOnClickListener(radioItemClickListener);
		}
		//返回按钮
		ImageButton backButton = (ImageButton) findViewById(MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back"));
		backButton.setBackground(getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_my_button_back")));
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		//标题
		TextView textTitle = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "textView_title"));
		textTitle.setText("趋势分析");
		
	}

	/**
	 * 显示dialog
	 * 
	 * @param msg
	 * @param context
	 */
	protected void showProgressDialog(String msg) {
		if ((!isFinishing()) && (this.mProgressDialog == null)) {
			//this.mProgressDialog = new ProgressDialog(this);
			this.mProgressDialog = CustomProgressDialog.createDialog(this); 
		}
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}

	/**
	 * 取消 dialog
	 */
	protected void dismissProgressDialog() {
		if ((!isFinishing()) && (this.mProgressDialog != null)) {
			this.mProgressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {	
		int id = v.getId();

		if(id == MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back")){
			overridePendingTransition(MResource.getIdByName(getApplicationContext(), "anim", "zyqt_slide_in_left"),MResource.getIdByName(getApplicationContext(), "anim", "zyqt_silde_out_right"));
			this.finish();
		}
		
	}
	/**
	 * 
	 * @author Lianxw
	 * 
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ECGChartFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return String.valueOf(position);
		}
	}

	/**
	 * 处理单选按钮点击事件
	 * 
	 * @author Lianxw
	 * 
	 */
	private class OnRadioItemClickListener implements OnClickListener {
		private int currentId = MResource.getIdByName(getApplicationContext(), "id", "radio0");

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (currentId == id) {
				return;
			} else {
				currentId = id;
			}
			int pos = 0;

			if(id == MResource.getIdByName(getApplicationContext(), "id", "radio0")){
				pos = 0;
			}else if(id == MResource.getIdByName(getApplicationContext(), "id", "radio1")){
				pos = 1;
			}else if(id == MResource.getIdByName(getApplicationContext(), "id", "radio2")){
				pos = 2;
			}else if(id == MResource.getIdByName(getApplicationContext(), "id", "radio3")){
				pos = 3;
			}
			mViewPager.setCurrentItem(pos);
		}
	}

	private class DownloadEcgTask extends AsyncTask<String, Integer, Integer> {

		private String startTime;
		private String endTime;

		public DownloadEcgTask(String startTime, String endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getResources().getString(
					MResource.getIdByName(MapApplication.mContext, "string", "zyqt_text_wait")));
		}

		/**
		 * 成功更新数据 返回0，否则返回1
		 */
		@Override
		protected Integer doInBackground(String... arg0) {
			if (!NetworkTool.isOnline(getBaseContext())) {
				Log.d(TAG, "更新失败,无网络链接");
				return 1;
			}
			// 取数据库最晚时间
			String checkTime;
			DataECG tempdata = MHealthProviderMetaData.GetMHealthProvider(
					getBaseContext()).getlastEcgData();
			if (tempdata == null || tempdata.data.date == null) {
				checkTime = "2015-01-01 00:00:00";
			} else {
				checkTime = tempdata.data.date;
			}

			ECGListinfo ecglistInfo = new ECGListinfo();
			// 获取数据库最晚时间到当前时间的数据
			SharedPreferences info = getSharedPreferences(
					SharedPreferredKey.SHARED_NAME, 0);
			String userUid = info.getString(SharedPreferredKey.USERUID, null);
			int result = DataSyn.getInstance().getECGListData(ecglistInfo,
					startTime, endTime, userUid);

			SimpleDateFormat sfwithsecond = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			List<DataECG> ecgDataList = new ArrayList<DataECG>();
			System.out.println("-----ecglistInfo.datavalue-------"+ecglistInfo.datavalue.size());
			for (ECGSummary ecgSummary : ecglistInfo.datavalue) {
				try {
					DataECG tempEcg = new DataECG();
					long tempsecond = Long.parseLong(ecgSummary.date);
					Date date = new Date(tempsecond);
					ecgSummary.date = sfwithsecond.format(date);
					tempEcg.createtime = ecgSummary.date;
					tempEcg.data = ecgSummary;
					ecgDataList.add(tempEcg);
				} catch (NumberFormatException e) {
					continue;
				}
			}
			// 成功
			if (result == 0) {
				Date _checktime = new Date();
				try {
					_checktime = sfwithsecond.parse(checkTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// 保存新增数据
				System.out.println("--------保存新增数据---------"+ecgDataList.size());
				MHealthProviderMetaData.GetMHealthProvider(getBaseContext())
						.InsertECGData(ecgDataList, _checktime.getTime(), true);
				return 0;
			} else {
				Log.d(TAG, "从服务器获取数据失败");
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			for(Fragment f:fragments) {
				if(f instanceof ECGChartFragment){
					((ECGChartFragment)f).refresh();
				}
			}
			dismissProgressDialog();
		}
	}


}
