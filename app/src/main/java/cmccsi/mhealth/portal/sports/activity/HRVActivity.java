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
import cmccsi.mhealth.portal.sports.bean.DataECG;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;

public class HRVActivity extends BaseActivity implements
		OnCheckedChangeListener {
	private ImageView hrv_imageView;
	private TextView hrv_time;
	private LinearLayout hrv_graph;
	private RadioButton multiple_1;
	private RadioButton multiple_2;
	private RadioButton multiple_3;
	private TextView title;
	private ImageButton button_back;
	private PopupWindow popup;
	private ImageView arrow_img;
	private Dialog dialog;

	private LinearLayout layout_dialog;
	private TextView textview_hr_dialog;
	private TextView textView_datatime_dialog;
	private TextView textView_comment_dialog;

	private Intent intent;
	private Bundle bundle;

	private XYMultipleSeriesRenderer hRVRenderer;
	private XYMultipleSeriesDataset mEcgHrvData = new XYMultipleSeriesDataset();
	private String[] types;
	private int dataLength;
	private int mThre;
	private double maxHRV;
	private double minHRV;
	private String[] mDataTime;
	private String upTime;

	private XYChart xyChartHRV;
	private GraphicalView mHRVChartView;

	private static int multiple;

	private LayoutParams lp;

	List<DataECG> ecgDataList = new ArrayList<DataECG>();
	private int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(this, "layout", "zyqt_hr_activity"));

		initialView();
		initDialog();
		loadView();
		lp = (LayoutParams) hrv_graph.getLayoutParams();
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
		
		hrv_graph.removeAllViews();
		hRVRenderer.setPointSize(8);
		hRVRenderer.setLabelsTextSize(Common.dip2px(this, 13));
		hRVRenderer.setYLabels(Common.dip2px(this, 3));
		hRVRenderer.setXLabels(0);
		hRVRenderer.setXAxisMax(dataLength + 1);
		hRVRenderer.setYAxisMax(maxHRV + 10);
		hRVRenderer.setYAxisMin(minHRV - 10);
		hRVRenderer.setShowGrid(true);
		float density = Common.getDensity(this);
		if (density <= 1.5f) {
			hRVRenderer.setMargins(new int[] { Common.dip2px(this, 5),
					Common.dip2px(this, 10), Common.dip2px(this, 32),
					Common.dip2px(this, 5) });
		} else if (density >= 2.0f) {
			// renderer.setLegendHeight(80);
			hRVRenderer.setMargins(new int[] { Common.dip2px(this, 15),
					Common.dip2px(this, 10), Common.dip2px(this, 30),
					Common.dip2px(this, 5) });
		}
		if (dataLength <= mThre) {
			xyChartHRV = new ScatterChart(mEcgHrvData, hRVRenderer);
			mHRVChartView = new GraphicalView(this, xyChartHRV);
		} else {
			xyChartHRV = new CombinedXYChart(mEcgHrvData, hRVRenderer, types);
			mHRVChartView = new GraphicalView(this, xyChartHRV);
		}
		hrv_graph.addView(mHRVChartView);
		mHRVChartView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				PopupDismiss();
				return false;
			}
		});
		mHRVChartView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PopupDismiss();
				SeriesSelection seriesSelection = mHRVChartView
						.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {
					dialog.dismiss();
					PopupDismiss();
				} else {
					int[] location = new int[2];
					mHRVChartView.getLocationOnScreen(location);
					int x = location[0];
					int y = location[1];

					double[] point = new double[] {
							seriesSelection.getXValue(),
							seriesSelection.getValue() };
					double[] dest = xyChartHRV.toScreenPoint(point);

					textview_hr_dialog.setText(" "
							+ (int) seriesSelection.getValue());
					textView_datatime_dialog.setText("测量于 "
							+ mDataTime[seriesSelection.getPointIndex()]);
					layout_dialog.setBackground(getResources().getDrawable(
							MResource.getIdByName(HRVActivity.this, "drawable", "zyqt_hrv_normal")));
					textView_comment_dialog.setText("心率变异率反映一段时间内的心脏健康指数，平均值越高心脏越健康");
					getPopupInstance(MResource.getIdByName(HRVActivity.this, "drawable", "zyqt_arrow_normal"));
					popup.showAtLocation(
							mHRVChartView,
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
		dialog = new Dialog(this,MResource.getIdByName(this, "style", "zyqt_dialog_fullscreen") );
		dialog.setContentView(MResource.getIdByName(this, "layout", "zyqt_ecg_dialog"));
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.TOP);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);

		layout_dialog = (LinearLayout) dialog.findViewById(MResource.getIdByName(this, "id", "layout_dialog"));
		textview_hr_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(this, "id", "textview_hr_dialog"));
		textView_datatime_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(this, "id", "textView_datatime_dialog"));
		textView_comment_dialog = (TextView) dialog
				.findViewById(MResource.getIdByName(this, "id", "textView_comment_dialog"));
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
		View view = inflater.inflate(MResource.getIdByName(this, "layout", "zyqt_popup_arrow"), null);
		popup = new PopupWindow(view, -2, -2);
		arrow_img = (ImageView) view.findViewById(MResource.getIdByName(this, "id", "arrow_img"));
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

		hRVRenderer = (XYMultipleSeriesRenderer) bundle
				.getSerializable("hRVRenderer");
		types = intent.getStringArrayExtra("types");
		mThre = intent.getIntExtra("mThre", 0);
		upTime=intent.getStringExtra("upTime");

		hrv_imageView = findView(MResource.getIdByName(this, "id", "hr_imageView4"));
		hrv_time = findView(MResource.getIdByName(this, "id", "hr_time"));
		hrv_time.setText(upTime);
		hrv_graph = findView(MResource.getIdByName(this, "id", "hr_graph"));
		multiple_1 = findView(MResource.getIdByName(this, "id", "multiple_1"));
		multiple_2 = findView(MResource.getIdByName(this, "id", "multiple_2"));
		multiple_3 = findView(MResource.getIdByName(this, "id", "multiple_3"));

		multiple_1.setOnCheckedChangeListener(this);
		multiple_2.setOnCheckedChangeListener(this);
		multiple_3.setOnCheckedChangeListener(this);

		title = (TextView) findViewById(MResource.getIdByName(this, "id", "textView_title"));
		title.setText("心率变异率趋势");
		button_back = (ImageButton) findViewById(MResource.getIdByName(this, "id", "button_input_bg_back"));
		button_back.setBackgroundDrawable(getResources().getDrawable(
				MResource.getIdByName(this, "drawable", "zyqt_my_button_back")));
		button_back.setVisibility(View.VISIBLE);
		button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(MResource.getIdByName(HRVActivity.this, "anim", "zyqt_slide_in_left"),
						MResource.getIdByName(HRVActivity.this, "anim", "zyqt_silde_out_right"));
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
			double[] hrvData = new double[dataLength];
			mDataTime = new String[dataLength];
			for (int i = 0; i < dataLength; i++) {

				DataECG tempECG = ecgDataList.get(i);
				hrvData[dataLength - i - 1] = Double
						.parseDouble(tempECG.data.hrv);
				mDataTime[dataLength - i - 1] = tempECG.data.date;
			}
			double res[] = calStatics(hrvData, dataLength);
			minHRV = res[0];
			maxHRV = res[1];
			
			XYSeries mHRVSeries = new XYSeries("心率变异率");
			for (int i = 0; i < dataLength; i++) {
				// 心率
				mHRVSeries.add(i + 1, hrvData[i]);
				count=mHRVSeries.getItemCount();
			}
			mEcgHrvData.addSeries(mHRVSeries);
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
				hrv_graph.setLayoutParams(lp);
			}
		}else if(button.getId()==MResource.getIdByName(this, "id", "multiple_2")){
			if (isChecked) {
				lp.width = multiple * 4;
				hrv_graph.setLayoutParams(lp);
			}
		}else if(button.getId()==MResource.getIdByName(this, "id", "multiple_3")){
			if (isChecked) {
				lp.width = multiple * 8;
				hrv_graph.setLayoutParams(lp);
			}
		}
	}
}
