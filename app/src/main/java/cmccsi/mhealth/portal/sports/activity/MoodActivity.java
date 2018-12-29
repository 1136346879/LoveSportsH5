package cmccsi.mhealth.portal.sports.activity;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.CombinedXYChart;
import org.achartengine.chart.ScatterChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.DataECG;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;

public class MoodActivity extends BaseActivity implements
		OnCheckedChangeListener {
	private ImageView mood_imageView;
	private TextView mood_time;
	private LinearLayout mood_graph;
	private RadioButton multiple_1;
	private RadioButton multiple_2;
	private RadioButton multiple_3;
	private TextView title;
	private ImageButton button_back;
	private PopupWindow popup;
	private ImageView arrow_img;
	private Dialog dialog;

	private LinearLayout layout_dialog;
	private TextView textview_mood_dialog;
	private TextView textView_datatime_dialog;
	private TextView textView_comment_dialog;

	private Intent intent;
	private Bundle bundle;

	private XYMultipleSeriesRenderer moodRenderer;
	private XYMultipleSeriesDataset mEcgStressData = new XYMultipleSeriesDataset();
	private String[] types;
	private int dataLength;
	private int mThre;
	private double maxMood;
	private double minMood;
	private String[] mDataTime;
	private String upTime;

	private XYChart xyChartMood;
	private GraphicalView mMoodChartView;

	private static int multiple;

	private LayoutParams lp;

	List<DataECG> ecgDataList = new ArrayList<DataECG>();
	private int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(MapApplication.mContext , "layout", "zyqt_hr_activity"));
		initialView();
		initDialog();
		loadView();
		lp = (LayoutParams) mood_graph.getLayoutParams();
		multiple = lp.width;
	}

	private void loadView() {
		// TODO Auto-generated method stub
		
		if(count<20){
			multiple_1.setVisibility(View.GONE);
			multiple_2.setVisibility(View.GONE);
			multiple_3.setVisibility(View.GONE);
		}else if(count<40){
			multiple_3.setVisibility(View.GONE);
		}
		
		mood_graph.removeAllViews();
		moodRenderer.setPointSize(8);
		moodRenderer.setLabelsTextSize(Common.dip2px(this, 13));
		moodRenderer.setYLabels(Common.dip2px(this, 3));
		moodRenderer.setXLabels(0);
		moodRenderer.setXAxisMax(dataLength + 1);
		moodRenderer.setYAxisMax(maxMood + 10);
		moodRenderer.setYAxisMin(minMood - 10);
		moodRenderer.setShowGrid(true);
		float density = Common.getDensity(this);
		if (density <= 1.5f) {
			moodRenderer.setMargins(new int[] { Common.dip2px(this, 5),
					Common.dip2px(this, 10), Common.dip2px(this, 32),
					Common.dip2px(this, 5) });
		} else if (density >= 2.0f) {
			// renderer.setLegendHeight(80);
			moodRenderer.setMargins(new int[] { Common.dip2px(this, 15),
					Common.dip2px(this, 10), Common.dip2px(this, 30),
					Common.dip2px(this, 5) });
		}
		if (dataLength <= mThre) {
			xyChartMood = new ScatterChart(mEcgStressData, moodRenderer);
			mMoodChartView = new GraphicalView(this, xyChartMood);
		} else {
			xyChartMood = new CombinedXYChart(mEcgStressData, moodRenderer,
					types);
			mMoodChartView = new GraphicalView(this, xyChartMood);
		}
		mood_graph.addView(mMoodChartView);
		mMoodChartView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				PopupDismiss();
				return false;
			}
		});
		mMoodChartView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PopupDismiss();
				SeriesSelection seriesSelection = mMoodChartView
						.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {
					dialog.dismiss();
					PopupDismiss();
				} else {
					int[] location = new int[2];
					mMoodChartView.getLocationOnScreen(location);
					int x = location[0];
					int y = location[1];

					double[] point = new double[] {
							seriesSelection.getXValue(),
							seriesSelection.getValue() };
					double[] dest = xyChartMood.toScreenPoint(point);

					textview_mood_dialog.setText(" "
							+ (int) seriesSelection.getValue());
					textView_datatime_dialog.setText("测量于 "
							+ mDataTime[seriesSelection.getPointIndex()]);

					if (seriesSelection.getValue() < 50) {
						layout_dialog.setBackground(getResources().getDrawable(
								MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_stress_normal")));
						textView_comment_dialog.setText("近期压力水平正常，情绪较平静放松");
						getPopupInstance(MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_arrow_normal"));
					} else {
						layout_dialog.setBackground(getResources().getDrawable(
								MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_stress_abnormal")));

						textView_comment_dialog.setText("近期压力较高，情绪较紧张或激动");

						getPopupInstance(MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_arrow_error"));
					}
					popup.showAtLocation(
							mMoodChartView,
							Gravity.NO_GRAVITY,
							(int) dest[0] + x
									- DiptoPx(getApplicationContext(), 6),
							(int) dest[1] + y
									- DiptoPx(getApplicationContext(), 26));
					dialog.show();
				}
			}
		});
	}

	private void initDialog() {
		dialog = new Dialog(this, MResource.getIdByName(MapApplication.mContext , "style", "zyqt_dialog_fullscreen") );
		dialog.setContentView(MResource.getIdByName(MapApplication.mContext , "layout", "zyqt_ecg_dialog"));
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.TOP);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);

		layout_dialog = (LinearLayout) dialog.findViewById(MResource.getIdByName(MapApplication.mContext , "id", "layout_dialog"));
		textview_mood_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(MapApplication.mContext , "id", "textview_hr_dialog"));
		textView_datatime_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(MapApplication.mContext , "id", "textView_datatime_dialog"));
		textView_comment_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(MapApplication.mContext , "id", "textView_comment_dialog"));
	}

	private void getPopupInstance(int img) {
		// TODO Auto-generated method stub
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
			return;
		} else {
			initWindow(img);
		}
	}

	private void PopupDismiss() {
		// TODO Auto-generated method stub
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
		}
	}

	private void initWindow(int img) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(MResource.getIdByName(MapApplication.mContext , "layout", "zyqt_popup_arrow"), null);
		popup = new PopupWindow(view, -2, -2);
		arrow_img = (ImageView) view.findViewById(MResource.getIdByName(MapApplication.mContext , "id", "arrow_img"));
		arrow_img.setBackgroundResource(img);
	}

	private int DiptoPx(Context context, int dpValue) {
		// TODO Auto-generated method stub
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private void initialView() {
		// TODO Auto-generated method stub

		intent = getIntent();
		bundle = intent.getExtras();

		moodRenderer = (XYMultipleSeriesRenderer) bundle
				.getSerializable("moodRenderer");

		types = intent.getStringArrayExtra("types");
		mThre = intent.getIntExtra("mThre", 0);
		upTime=intent.getStringExtra("upTime");

		mood_imageView = findView(MResource.getIdByName(MapApplication.mContext , "id", "hr_imageView4"));
		mood_time = findView(MResource.getIdByName(MapApplication.mContext , "id", "hr_time"));
		mood_time.setText(upTime);
		mood_graph = findView(MResource.getIdByName(MapApplication.mContext , "id", "hr_graph"));
		multiple_1 = findView(MResource.getIdByName(MapApplication.mContext , "id", "multiple_1"));
		multiple_2 = findView(MResource.getIdByName(MapApplication.mContext , "id", "multiple_2"));
		multiple_3 = findView(MResource.getIdByName(MapApplication.mContext , "id", "multiple_3"));

		multiple_1.setOnCheckedChangeListener(this);
		multiple_2.setOnCheckedChangeListener(this);
		multiple_3.setOnCheckedChangeListener(this);

		title = (TextView) findViewById(MResource.getIdByName(MapApplication.mContext , "id", "textView_title"));
		title.setText("情绪趋势");
		button_back = (ImageButton) findViewById(MResource.getIdByName(MapApplication.mContext , "id", "button_input_bg_back"));
		button_back.setBackgroundDrawable(getResources().getDrawable(
				MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_my_button_back")));
		button_back.setVisibility(View.VISIBLE);
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_slide_in_left"),
		        		MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_silde_out_right"));
			}
		});
		requestData();
	}
	private void requestData() {
		// TODO Auto-generated method stub
		ecgDataList = MHealthProviderMetaData.GetMHealthProvider(this)
				.getAllEcgData();
		dataLength = ecgDataList.size();
		if (dataLength > 40) {
			dataLength = 40;
		}
		if (dataLength > 0) {
			double[] moodData = new double[dataLength];
			mDataTime = new String[dataLength];
			for (int i = 0; i < dataLength; i++) {

				DataECG tempECG = ecgDataList.get(i);
				moodData[dataLength - i - 1] = Double
						.parseDouble(tempECG.data.mood);
				mDataTime[dataLength - i - 1] = tempECG.data.date;
			}
			double res[] = calStatics(moodData, dataLength);
			minMood = res[0];
			maxMood = res[1];
			
			XYSeries mMoodSeries = new XYSeries("情绪");
			for (int i = 0; i < dataLength; i++) {
				// 心率
				mMoodSeries.add(i + 1, moodData[i]);
				count = mMoodSeries.getItemCount();
			}
			mEcgStressData.addSeries(mMoodSeries);
		}
	}
	public double[] calStatics(double[] src, int length) {
		int i = 0;
		double minSrc = 1000, maxSrc = 0, avaSrc = 0;
		for (i = 0; i < length; i++) {
			avaSrc += src[i];

			if (minSrc > src[i]) {
				minSrc = src[i];
			}
			if (maxSrc < src[i]) {
				maxSrc = src[i];
			}
		}
		avaSrc /= length;
		double[] res = new double[3];
		res[0] = minSrc;
		res[1] = maxSrc;
		res[2] = avaSrc;
		return res;
	}
	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		// TODO Auto-generated method stub
		PopupDismiss();
		if(button.getId()==MResource.getIdByName(this, "id", "multiple_1")){
			if (isChecked) {
				lp.width = multiple;
				mood_graph.setLayoutParams(lp);
			}
		}else if(button.getId()==MResource.getIdByName(this, "id", "multiple_2")){
			if (isChecked) {
				lp.width = multiple * 4;
				mood_graph.setLayoutParams(lp);
			}
		}else if(button.getId()==MResource.getIdByName(this, "id", "multiple_3")){
			if (isChecked) {
				lp.width = multiple * 8;
				mood_graph.setLayoutParams(lp);
			}
		}
		
	}
}
