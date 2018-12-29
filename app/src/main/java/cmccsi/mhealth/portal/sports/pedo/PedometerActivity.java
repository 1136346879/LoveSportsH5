package cmccsi.mhealth.portal.sports.pedo;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.portal.sports.activity.MainFragmentActivity2;
import cmccsi.mhealth.portal.sports.appversion.HistorySportActivity;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.basic.SendMsgBroadCast;
import cmccsi.mhealth.portal.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.portal.sports.bean.DeviceInfo;
import cmccsi.mhealth.portal.sports.bean.DeviceListInfo;
import cmccsi.mhealth.portal.sports.bean.GoalInfo;
import cmccsi.mhealth.portal.sports.bean.GoalNetInfo;
import cmccsi.mhealth.portal.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.portal.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.portal.sports.bean.PedometorListInfo;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.common.Config;
import cmccsi.mhealth.portal.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.portal.sports.common.Logger;
import cmccsi.mhealth.portal.sports.common.PreferencesUtils;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;
import cmccsi.mhealth.portal.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.portal.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.portal.sports.common.utils.PedometorUtils;
import cmccsi.mhealth.portal.sports.common.utils.ToastUtils;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.portal.sports.device.BaseDeviceInterface.BaseCallBack;
import cmccsi.mhealth.portal.sports.device.DeviceConstants;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.phonesteplib.StepController;
import cmccsi.mhealth.portal.sports.service.StepService;
import cmccsi.mhealth.portal.sports.service.StepService_GPS;
import cmccsi.mhealth.portal.sports.tabhost.TabBaseFragment;
import cmccsi.mhealth.portal.sports.view.CenterRollingBall;
import cmccsi.mhealth.portal.sports.view.CustomProgressDialog;
import cmccsi.mhealth.portal.sports.view.DownFlashView;
import cmccsi.mhealth.portal.sports.view.DownFlashView.RefreshListener;
import cmccsi.mhealth.portal.sports.view.PedoCalProcess;
import cmccsi.mhealth.portal.sports.view.PopMenu;

import com.cmcc.bracelet.lsjx.libs.JWDeviceManager;

public class PedometerActivity extends BaseActivity implements OnClickListener, RefreshListener {

	private final String UPDATERESULT = "updateresult";
	private final int REQUEST_OPEN_BT_CODE = 99;

	private TextView mTv_Duration;// 运动时长
	private TextView mTv_distance;// 距离
	private TextView mTv_stepnumofday;// 步数
	private TextView mTv_percentstep;// 步数百分比
	private TextView mTv_currentEquipment; // 当前设备
	private TextView mTv_displaydate;// 当前显示日期
	private TextView tv_showGoal;// 距离成就

	private ImageView mIv_leftday;// 左翻页
	private ImageView mIv_rightday;// 右翻页
	private ImageView iv_showGoal;// 成就图片
	private ImageView mIv_reset;
	private ImageView mIv_bloothDisconnect;// 手环断开连接
	private ImageView zyqt_icon_history;// 手环断开连接
	private GestureDetector mGestureDetector;

	private Button mBtn_cal_achievement;// 卡路里成就切换
	private Button mBtn_distance_achievement;// 距离成就切换

	private RelativeLayout mRl_distance_achievement;// 距离成就
	private RelativeLayout mRelativeLayoutProgress;// 圆形进度图片

	private LinearLayout mLl_cal_achievement;// 卡路里成就显示
	private LinearLayout mTopLayout;

	private PopMenu mPopmenu_more;// 更多按钮
	private PedoCalProcess mPedoCalProcess;// 卡路里成就
	private DownFlashView mRefreshableView;

	private String mDeviceId = "";// 设备id
	private String mDevicePara = "";// 设备型号
	private int mDeviceType = 0;// 设备类型
	private int mTargetStep = 10000;// 目标步数

	private PedometorDataInfo currentPedo;// 当前页面显示的数据
	private PedometorDataInfo bluetoothPedo;// 当前页面显示的数据
	private Date mDisplayDate;// 展示日期
	private Date mToday;// 当天日期 为了判断跨天
	private MHealthStepReceiver mMHStepReceiver;// 手机计步接收

	private int mlastCal = 0;// 手机计步卡路里成就

	private boolean isupdate = true;// 下拉是否刷新 专用于手环

	private boolean isRealTime = true;// 手环实时显示

	private TgbleManagerNeuro tgble = null;

	private JWDeviceManager jwdManager = null;

	private TimerUpdateECGPedo mTimerUpdateECGPedo;

	private boolean hasBleException = false;

	private int bluetoothState = 0;// 蓝牙设备连接状态 0不是蓝牙设备 1已连接 2已断开
	private String userUid;
	private int msgId;
	private boolean hasDownUserInfo=false;//区分是否需要下载用户信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplicationContext(), "layout", "zyqt_activity_pedo"));
		// -----------------
		Intent intent = getIntent();
		userUid = intent.getStringExtra("userUid");
		msgId = intent.getIntExtra("msgId", 0);
		if (null != userUid) {
			dialog = CustomProgressDialog.createDialog(PedometerActivity.this, true);
			dialog.show();
			new Thread(new InitAccountRunnable()).start();
		} else if (0 != msgId) {
			dialog = CustomProgressDialog.createDialog(PedometerActivity.this, true);
			dialog.show();
			new Thread(new InitAccountRunnable()).start();
		}else{
			hasDownUserInfo=true;
		}
		// -----------------

		initView();
		// getGuideState();
		mDisplayDate = new Date(System.currentTimeMillis());
		mToday = new Date(System.currentTimeMillis());
		setPercentPedoData(0, 1000);
		String mUserId = PreferencesUtils.getString(PedometerActivity.this, SharedPreferredKey.USERUID, "");
		// if (null!=userUid && userUid.equals(mUserId)) {

		// }
		if (null == userUid) {
			mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, -1);
			// String deviceName = PreferencesUtils.getString(this,
			// SharedPreferredKey.DEVICE_NAME, null);
			mDevicePara = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_MODEL, null);
			showCurrentEquipment(mDeviceType);
			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				// mIv_bloothDisconnect.setVisibility(View.VISIBLE);
				bluetoothState = 2;
				setBloothDisconnectState(false, true);
			}else{
				setBloothDisconnectState(false, false);
			}
			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				mTimerUpdateECGPedo = new TimerUpdateECGPedo(3600);
			}
			mRefreshableView.startRefreshDirectly();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Logger.d("cjz", "PedometerActivity onStart");
		try {
			if (!DateFormatUtils.isToday(mToday)) {
				mDisplayDate = new Date(System.currentTimeMillis());
				mToday = new Date(System.currentTimeMillis());
				displayPedoData(null);
			}
			setLeftAndRightDisplayStatus();
			if (mDeviceType != PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0)) {
				isupdate = true;
				Logger.e("cjz", "设备 changed");
				mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0);
				// String deviceName = PreferencesUtils.getString(this,
				// SharedPreferredKey.DEVICE_NAME, null);
				mDevicePara = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_MODEL, null);
				mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
				showCurrentEquipment(mDeviceType);
				// 切换设备后重新查询
				currentPedo = queryPedometor(mDeviceId, new Date(System.currentTimeMillis()));
				displayPedoData(currentPedo);
				if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP)// 手机
				{
					if (!StepService.isRunning)// 开关没有打开并且手机计步没有运行																		// 开启手机计步
					{
						PreferencesUtils.putBoolean(PedometerActivity.this, SharedPreferredKey.IS_STEPOPEN, true);
						startStepService();
					}
				}
			}
			// 距离成就
			GoalInfo info = GoalInfo.getInstance(this);
			iv_showGoal.setImageResource(info.type.getImgRes());
			tv_showGoal.setText(info.getGoalReportInTime(this));
			new LoadAchievementTask().execute();
			
			if(hasDownUserInfo){
				startDevice();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			ToastUtils.showToast(MapApplication.mContext, MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_INTERNET_ERROR"));

		}

	}
	
	/**
	 * 开始设备连接 原来放在onstart 
	 */
	private void startDevice(){
		if (mDeviceType != DeviceConstants.DEVICE_BRACLETE_BEATBAND && mDeviceType != DeviceConstants.DEVICE_BRACLETE_JW
				&& mDeviceType != DeviceConstants.DEVICE_BRACLETE_JW201 && DateFormatUtils.isToday(mDisplayDate)) {
			bluetoothState = 0;
			setBloothDisconnectState(true, false);
			bluetoothPedo = null;
		}
		switch (mDeviceType) {
		case DeviceConstants.DEVICE_MOBILE_STEP:// 手机
			displayPedoData(currentPedo);
			registerStepReceiver();
			// 判断是否为手环设备，是则开启定时器（间隔1小时执行一次）
			if(tgble!=null){
				tgble.cancleUploadTime();
			}
			break;
		case DeviceConstants.DEVICE_BRACLETE_BEATBAND:// 神念手环
			if (isVersionUseable()) {

				startBle();
				tgble.startUploadTimer();
			}
			break;
		case DeviceConstants.DEVICE_BRACLETE_JW:// 叮当手环
			if (isVersionUseable()) {
				startJWBle();
			} else {
				mRefreshableView.finishRefresh();
			}
			// 判断是否为手环设备，是则开启定时器（间隔1小时执行一次）
			if(tgble!=null){
				tgble.cancleUploadTime();
			}
			break;
		case DeviceConstants.DEVICE_BRACLETE_JW201:// 叮当手环
			if (isVersionUseable()) {
				startJWBle();
			} else {
				mRefreshableView.finishRefresh();
			}
			// 判断是否为手环设备，是则开启定时器（间隔1小时执行一次）
			if(tgble!=null){
				tgble.cancleUploadTime();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		// isupdate=true;
		try {
			switch (mDeviceType) {
			case DeviceConstants.DEVICE_MOBILE_STEP:// 手机
				unregisterStepReceiver();
				break;
			case DeviceConstants.DEVICE_BRACLETE_BEATBAND:// 神念手环
				mHandler.removeCallbacks(bleDisconnectRunnable);
				if (isRealTime && tgble != null) {
					tgble.setRealActivitiy(false);
					tgble.stopRealTimeSport();
					tgble.close();
				}
				break;
			case DeviceConstants.DEVICE_BRACLETE_JW:// 叮当手环
				mHandler.removeCallbacks(bleDisconnectRunnable);
				if (jwdManager != null) {
					jwdManager.stopRealTime();
				}
				if (bluetoothPedo != null) {
					bluetoothPedo.deviceId = mDeviceId;
					bluetoothPedo.deviceType = mDevicePara;
					// PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(bluetoothPedo,
					// false);
				}
				break;
			case DeviceConstants.DEVICE_BRACLETE_JW201:// 叮当手环
				mHandler.removeCallbacks(bleDisconnectRunnable);
				if (jwdManager != null) {
					jwdManager.stopRealTime();
				}
				if (bluetoothPedo != null) {
					bluetoothPedo.deviceId = mDeviceId;
					bluetoothPedo.deviceType = mDevicePara;
					// PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(bluetoothPedo,
					// false);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int downX, downY;

	/**
	 * 初始化页面控件 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:22:22
	 */
	private void initView() {
		zyqt_icon_history = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "zyqt_icon_history_l"));
		zyqt_icon_history.setOnClickListener(this);
		ConstantsBitmaps.initRunPics(this);// 圆形进度相关图片
		mRelativeLayoutProgress = (RelativeLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"Progress_center_rote1"));
		// 数据展示
		mTv_Duration = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_Duration"));
		mTv_distance = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_distance"));
		mTv_stepnumofday = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_stepnumofday"));
		mTv_percentstep = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_percentstep"));
		mTv_currentEquipment = (TextView) findViewById(MResource
				.getIdByName(getApplicationContext(), "id", "tv_currentEquipment"));
		mTv_displaydate = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_displaydate"));
		// 左右翻页
		mIv_leftday = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_leftday"));
		mIv_leftday.setOnClickListener(this);
		mIv_rightday = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_rightday"));
		mIv_rightday.setOnClickListener(this);
		mIv_reset = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_reset"));// 回到当日页面
		mIv_reset.setVisibility(View.INVISIBLE);
		mIv_reset.setOnClickListener(this);

		// 运动成就
		mBtn_cal_achievement = (Button) findViewById(MResource.getIdByName(getApplicationContext(), "id", "btn_cal_achievement"));
		mBtn_cal_achievement.setOnClickListener(this);
		mBtn_distance_achievement = (Button) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"btn_distance_achievement"));
		mBtn_distance_achievement.setOnClickListener(this);
		mRl_distance_achievement = (RelativeLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"rl_distance_achievement"));
		mLl_cal_achievement = (LinearLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"ll_cal_achievement"));

		mIv_bloothDisconnect = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "ig_disconnect"));
		mIv_bloothDisconnect.setOnClickListener(this);
		// 返回键
		ImageButton mImageButtonBack = (ImageButton) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"button_input_bg_back"));
		mImageButtonBack.setBackgroundResource(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_my_button_back"));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(this);
		// 标题
		TextView tv_Title = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "textView_title"));
		// tv_Title.setText(getResources().getString(
		// MResource.getIdByName(getApplicationContext(), "string",
		// "zyqt_pedometer_title")));
		tv_Title.setText("爱动力 · 走一走");
		// 更多键
		mTopLayout = (LinearLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id", "ll_pedomain"));
		initMorePopmenu();
		ImageButton mImageButtonUpdate = (ImageButton) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"imageButton_title"));
		mImageButtonUpdate.setBackgroundResource(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_menu_more"));
		mImageButtonUpdate.setVisibility(View.VISIBLE);
		mImageButtonUpdate.setOnClickListener(this);
		// 距离成就
		iv_showGoal = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_showGoal"));
		iv_showGoal.setOnClickListener(this);
		tv_showGoal = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_showGoal"));
		// 卡路里成就
		initPedoCalProcess();

		mRefreshableView = (DownFlashView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "refresh_root"));
		mRefreshableView.setRefreshListener(this);

		FrameLayout mFl_pedoprogress = (FrameLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id",
				"fl_pedoprogress"));
		mGestureDetector = new GestureDetector(getBaseContext(), new PedoGestureListener());
		mFl_pedoprogress.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = (int) ev.getX();
					downY = (int) ev.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					int y = (int) (ev.getY() - downY);
					if (y > 30) {
						if (!mRefreshableView.isRefreshing) {
							isDown = true;
							mRefreshableView.doMovement(y / 3);
							return false;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if (isDown) {
						mRefreshableView.startRefreshDirectly();
						isDown = false;
						return false;
					}
					break;
				default:
					break;
				}
				return mGestureDetector.onTouchEvent(ev);
			}
		});

	}

	/**
	 * 初始化更多菜单列表 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午10:09:34
	 */
	private void initMorePopmenu() {
		List<CommonBottomMenuItem> menulist = new ArrayList<CommonBottomMenuItem>();
		menulist.add(new CommonBottomMenuItem(1, "分享运动简报", MResource.getIdByName(getApplicationContext(), "drawable",
				"zyqt_menuitem_share")));
		menulist.add(new CommonBottomMenuItem(1, "切换设备", MResource.getIdByName(getApplicationContext(), "drawable",
				"zyqt_menuitem_changequipment")));
		mPopmenu_more = new PopMenu(this, menulist);
		mPopmenu_more.setOnItemClickListener(menuItemClick);
	}

	/**
	 * 初始化卡路里成就进度 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:16:44
	 */
	private void initPedoCalProcess() {
		mPedoCalProcess = (PedoCalProcess) findViewById(MResource
				.getIdByName(getApplicationContext(), "id", "pcp_pedocalprocess"));
		mPedoCalProcess.setProcess(0);
		mPedoCalProcess.setExplainText(PedometorUtils.calorieToFoodDescription(0));
		mPedoCalProcess.setProcessPicture(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_archive_process"));
		int[] CoordinateX = { 25, 45, 65, 85 };
		mPedoCalProcess.setCoordinateX(CoordinateX);
		int[] normalPictures = { MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve1_normal"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve2_normal"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve3_normal"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve4_normal") };
		mPedoCalProcess.setNormalMilePictures(normalPictures);
		int[] overPictures = { MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve1_press"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve2_press"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve3_press"),
				MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve4_press"), };
		mPedoCalProcess.setOverMilePictures(overPictures);
		mPedoCalProcess
				.setExplainPictures(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_archive_explain"));
	}

	/**
	 * 更新运动数据 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:36:50
	 */
	private void updatePedoData() {
		new Thread() {
			public void run() {
				// 取当前数据库最后一条
				PedometorDataInfo data = PedoController.GetPedoController(getBaseContext()).getLatestPedometer(mDeviceId);

				String startDate = "";
				String endDate = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot);
				if (data == null)// 数据库没有数据则更新n天
				{
					startDate = DateFormatUtils.AddDays(endDate, -30, FormatType.DateShot);
				} else// 有数据则更新数据库时间到当前时间的数据
				{
					startDate = DateFormatUtils.ChangeFormat(data.createtime, FormatType.DateLong, FormatType.DateShot);
				}

				PedometorListInfo reqData = new PedometorListInfo();
				int result = DataSyn.getInstance().getPedoInfoByTimeArea(null, null, startDate, endDate, reqData);
				if (result == 0) {
					PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(reqData);
				}
				Bundle bdl = new Bundle();
				bdl.putInt(UPDATERESULT, result);
				Message msg = new Message();
				msg.what = 0;
				msg.setData(bdl);
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * ui线程处理
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Log.e("mytest", "msg.what : " + msg.what);
				switch (msg.what) {
				case 0:// 更新完毕
					Log.e("mytest", "msg.getData().getInt(UPDATERESULT) : " + msg.getData().getInt(UPDATERESULT));
					if (msg.getData().getInt(UPDATERESULT) == 0)// 成功
					{
						// Toast.makeText(getBaseContext(),
						// getResources().getString(R.string.phonestep_uploadsuccess),
						// Toast.LENGTH_SHORT).show();
						Log.e("mytest", "mDeviceId : " + mDeviceId);
						if ("".equals(mDeviceId)) {
							mDeviceId = PreferencesUtils.getString(MapApplication.mContext, SharedPreferredKey.DEVICE_ID, null);
						}
						currentPedo = queryPedometor(mDeviceId, new Date());
						if ((mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
								|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)
								&& bluetoothPedo != null) {
							displayPedoData(bluetoothPedo);
						} else {
							displayPedoData(currentPedo);
						}
					} else {
						currentPedo = queryPedometor(mDeviceId, new Date());
						displayPedoData(currentPedo);
						Toast.makeText(
								getBaseContext(),
								getResources().getString(
										MResource.getIdByName(getApplicationContext(), "string", "zyqt_phonestep_uploadfailed")),
								Toast.LENGTH_SHORT).show();
					}
					if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP)// 手机
					{
						boolean isStepServiceOpen = PreferencesUtils.getBoolean(getBaseContext(), SharedPreferredKey.IS_STEPOPEN, true);
						if (isStepServiceOpen && !StepService.isRunning)// 开关没有打开并且手机计步没有运行
																			// 开启手机计步
						{
							startStepService();
						}
					}
					mRefreshableView.finishRefresh();
					break;
				case 1:
					// mIv_bloothDisconnect.setAnimation(null);
					setBloothDisconnectState(true, true);
					mRefreshableView.finishRefresh();
					break;
				case 1000:
					// mDeviceType =
					// PreferencesUtils.getInt(PedometerActivity.this,
					// SharedPreferredKey.DEVICE_TYPE, -1);
					setAlert();
					restoreDeviceInfo();
					showCurrentEquipment(mDeviceType);
					startDevice();
					if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
						mTimerUpdateECGPedo = new TimerUpdateECGPedo(3600);
					}
					mRefreshableView.startRefreshDirectly();
					PreferencesUtils.putBoolean(PedometerActivity.this, "newLogin", true);
					if (msgId == 1) {
						Intent intent = new Intent();
						intent.putExtra("intent", 9);
						intent.setClass(PedometerActivity.this, TabBaseFragment.class);
						startActivity(intent);
						finish();
					}
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	/**
	 * 展示运动数据 TODO
	 * 
	 * @param data
	 *            运动实体
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:18:48
	 */
	private void displayPedoData(PedometorDataInfo data) {
		try {
			String displayDate = DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithDiagonalNoYear);
			displayDate = displayDate + "(" + Common.GetWeekStr(mDisplayDate.getDay()) + ")";
			mTv_displaydate.setText(displayDate);

			mTargetStep = Integer.parseInt(PreferencesUtils.getString(this, SharedPreferredKey.TARGET_STEP, "10000"));

			String today = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateWithUnderline);
			if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning
					&& DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline).equals(today))// 手机计步开启状态从service中取数
			{

				String stepTime = formatHHMMfromSec(StepService.todayTotalTime);
				setNomalPedoData(stepTime, "0", String.valueOf(StepService.todayTotalCal),
						String.valueOf(StepService.todayTotalDistance));
				setPercentPedoData(StepService.todayTotalStep, mTargetStep);
				setCalArchivement(StepService.todayTotalCal);
				if(currentPedo==null)
				{
					currentPedo=new PedometorDataInfo();
					currentPedo.createtime=DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateLong);
					currentPedo.date=DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateWithUnderline);
					currentPedo.deviceId=mDeviceId;
				}
				currentPedo.stepNum=String.valueOf(StepService.todayTotalStep);
				currentPedo.cal=String.valueOf(StepService.todayTotalCal);
				currentPedo.distance=String.valueOf(StepService.todayTotalDistance);
			} else {
				setNomalPedoData(data);
				if (data != null) {
					String stepSum = data.stepNum;
					try {
						setPercentPedoData(Integer.parseInt(stepSum), mTargetStep);
						setCalArchivement(Integer.parseInt(data.cal));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
					setPercentPedoData(0, mTargetStep);
					setCalArchivement(0);
				}

			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示基本运动数据 TODO
	 * 
	 * @param data
	 *            运动数据实体
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:12:16
	 */
	private void setNomalPedoData(PedometorDataInfo data) {
		if (data != null) {
			// int intStepTime = getStepTimeFromPedometor(data);
			data.strength3 = data.strength3 == "" ? "0" : data.strength3;
			data.strength4 = data.strength4 == "" ? "0" : data.strength4;
			int tempstepTime = Integer.parseInt(data.strength2) + Integer.parseInt(data.strength3)
					+ Integer.parseInt(data.strength4);
			String strStepTime = formatHHMMfromSec(tempstepTime);
			setNomalPedoData(strStepTime, data.yxbssum, data.cal, data.distance);
		} else {
			mTv_Duration.setText("00:00");
			mTv_distance.setText("0");
		}
	}

	/**
	 * 显示基本运动数据 TODO
	 * 
	 * @param steptime
	 *            时长
	 * @param yxbs
	 *            有效步数
	 * @param cal
	 *            卡路里
	 * @param distance
	 *            距离
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午3:32:29
	 */
	private void setNomalPedoData(String steptime, String yxbs, String cal, String distance) {
		mTv_Duration.setText(steptime);
		mTv_distance.setText(distance);

	}

	/**
	 * 设置运动百分比圆环与步数 TODO
	 * 
	 * @param value
	 *            步数
	 * @param max
	 *            最大步数
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午3:35:20
	 */
	private void setPercentPedoData(int value, int max) {
		CenterRollingBall mCenterRollingBall = new CenterRollingBall(this);
		mCenterRollingBall.setPics(ConstantsBitmaps.mBitmapBgCenterRound, ConstantsBitmaps.mBitmapPointRound);
		mCenterRollingBall.setMaxScore(max);
		mCenterRollingBall.setScore(value);
		mCenterRollingBall.showEndBall(true);
		mCenterRollingBall.showFrontArc(true);
		mCenterRollingBall.setCenterOffest(20);
		mCenterRollingBall.setAngelOffest(-120);
		mCenterRollingBall.invalidate();
		if (mCenterRollingBall != null) {
			mRelativeLayoutProgress.removeAllViews();
			mRelativeLayoutProgress.addView(mCenterRollingBall);
		}
		int val = 0;
		if (max != 0) {
			val = value * 100 / max;
		}
		mTv_percentstep.setText(val + "%");
		mTv_stepnumofday.setText(String.valueOf(value));
	}

	/**
	 * 查询运动数据 TODO
	 * 
	 * @param deviceId
	 *            设备ID
	 * @param date
	 *            日期
	 * @return null
	 * @return DataPedometor 运动数据
	 * @author jiazhi.cao
	 * @time 下午3:46:39
	 */
	private PedometorDataInfo queryPedometor(String deviceId, Date date) {
		PedometorDataInfo data = null;
		data = PedoController.GetPedoController(this).getPedometerByDay(deviceId, date);
		return data;
	}

	/**
	 * 运动时间展示格式化 TODO
	 * 
	 * @param sec
	 *            秒
	 * @return
	 * @return String HH:MM格式
	 * @author jiazhi.cao
	 * @time 下午3:17:23
	 */
	private String formatHHMMfromSec(int sec) {
		String result = "";
		try {
			int stepTime = (int) Math.floor(sec / 60);
			result = String.format("%1$02d:%2$02d", (int) Math.floor(stepTime / 60), (int) stepTime % 60);
		} catch (NullPointerException enull) {
			enull.printStackTrace();
		} catch (IllegalFormatException eill) {
			eill.printStackTrace();
		}
		return result;
	}

	/**
	 * 取运动时长 TODO
	 * 
	 * @param data
	 *            运动实体
	 * @return
	 * @return String HH:MM格式
	 * @author jiazhi.cao
	 * @time 下午3:21:12
	 */
	private int getStepTimeFromPedometor(PedometorDataInfo data) {
		int result = 0;
		try {
			result = Integer.parseInt(data.strength2 == null ? "0" : data.strength2)
					+ Integer.parseInt(data.strength3 == null ? "0" : data.strength3)
					+ Integer.parseInt(data.strength4 == null ? "" : data.strength4);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 显示设备信息 TODO
	 * 
	 * @param deviceName
	 *            设备名称
	 * @param devicePara
	 *            设备型号
	 * @param deviceID
	 *            设备ID
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午6:03:11
	 */
	private void showCurrentEquipment(String deviceName, String devicePara, String deviceID) {
		if (mDeviceType == 0)// 手机
		{
			String phoneModel = Build.MODEL;
			mTv_currentEquipment.setText(phoneModel + "手机计步");

		} else if (mDeviceType == 2)// 手环
		{
			mTv_currentEquipment.setText(deviceName + "计步");

		} else// 计步器
		{
			mTv_currentEquipment.setText(deviceName + "计步");

		}
	}

	/**
	 * 显示设备信息 TODO
	 *
	 * @param deviceType
	 *            设备类型
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午6:03:11
	 */
	private void showCurrentEquipment(int deviceType) {
		String discription = Common.getDeviceDisplayName(deviceType);
		if (!discription.equals("")) {
			mTv_currentEquipment.setText(discription + "计步");
		} else {
			mTv_currentEquipment.setText("");
		}
	}

	/**
	 * 开启手机计步 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:10:42
	 */
	private void startStepService() {
		StepController mStepController = new StepController();
		mStepController.setContext(getBaseContext());
		mStepController.startStepService(Config.SC_ACTION);
	}

	/**
	 * 设置显示运动数据的左箭头右箭头状态 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午5:05:11
	 */
	private void setLeftAndRightDisplayStatus() {
		// 当前显示数据日期小于系统日期显示右箭头
		String now = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateWithUnderline);
		String displayday = DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline);
		if (DateFormatUtils.compare_date(displayday, now) == 1) {
			mIv_rightday.setVisibility(View.VISIBLE);
			mIv_reset.setVisibility(View.VISIBLE);
		} else {
			mIv_rightday.setVisibility(View.INVISIBLE);
			mIv_reset.setVisibility(View.INVISIBLE);
		}
		// 当前显示数据日期大于本地库最早日期 显示左箭头
	}

	/**
	 * 设置卡路里成就 TODO
	 * 
	 * @param cal
	 *            卡路里
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:19:56
	 */
	private void setCalArchivement(int cal) {
		int calprocess = PedometorUtils.getCalpersent(mPedoCalProcess.getCoordinateX(), cal);
		Logger.i("cjz", "cal:" + cal + "  calprocess:" + calprocess);
		String calmessage = PedometorUtils.calorieToFoodDescription(cal);
		mPedoCalProcess.setExplainText(calmessage);
		mPedoCalProcess.setProcess(calprocess);
		mPedoCalProcess.invalidate();
	}

	@Override
	public void onRefresh(DownFlashView view) {
		if (DateFormatUtils.isToday(mDisplayDate)) {
			if (isupdate) {
				if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
						|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201
						|| mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP) {
					isupdate = false;
				}
				updatePedoData();
			} else {
				if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND)// 神念
				{
					//延时关闭下拉
//					hasBleException=false;
					mHandler.postDelayed(bleDisconnectRunnable, 40*1000);
					
					mRefreshableView.setText("开始同步数据");
					if (tgble != null) {
						tgble.getPedometerData();
					}
				} else if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
						|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)// 叮当
				{
					if (jwdManager != null && jwdManager.getDeviceStatus() == 2) {
						mRefreshableView.setText("开始同步数据");
						String startDate = PreferencesUtils.getString(getBaseContext(), "BRACLETE_JW_UPLOAD", "");
						String endDate = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateLong);
						// 补传 没有"上次获取手环数据时间"时，判断手机端昨天是否有数据；有则只上传当天数据；没有则补传两天数据。
						if (startDate.equals("")) {
							Date yesterday = DateFormatUtils.AddDays(new Date(System.currentTimeMillis()), -1);
							PedometorDataInfo data = PedoController.GetPedoController(this).getPedometerByDay(mDeviceId,
									yesterday);
							if (data != null) {
								startDate = DateFormatUtils.DateToString(yesterday, FormatType.DateWithUnderline) + " 00:00:00";
							} else {
								startDate = DateFormatUtils.AddDays(endDate, -2, FormatType.DateWithUnderline) + " 00:00:00";
								;
							}
						}
						// 不补传
						// startDate=DateFormatUtils.DateToString(new Date(),
						// FormatType.DateWithUnderline)+" 00:00:00";
						Logger.d("cjz", "丁当手环下拉时间 startDate" + startDate);
						jwdManager.syncData(startDate, endDate);
						String uploadDate = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()),
								FormatType.DateWithUnderline) + " 00:00:00";
						PreferencesUtils.putString(getBaseContext(), "BRACLETE_JW_UPLOAD", uploadDate);
					} else {
						mRefreshableView.finishRefresh();
					}
				} else if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP) {
					Intent it = new Intent(Config.PHONESTEP_UPLOAD_ACTION);
					sendBroadcast(it);
					mRefreshableView.setText("正在上传手机计步数据");
				}
			}
		} else {
			mRefreshableView.finishRefresh();
		}
	}

	Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TgbleManagerNeuro.MSG_EXCEPT:
				hasBleException = true;
				Bundle edata = msg.getData();
				if (tgble != null) {
					tgble.close();
				}
				if (null != edata && edata.getString("MSG_EXCEPT") != null && !edata.getString("MSG_EXCEPT").equals("")) {
					ToastUtils.showToast(PedometerActivity.this, edata.getString("MSG_EXCEPT"));
				}

				mRefreshableView.finishRefresh();
				bluetoothState = 2;
				Logger.d("cjz", "---------MSG_EXCEPT--------");
				setBloothDisconnectState(true, true);
				break;
			case TgbleManagerNeuro.MSG_RESET:
				hasBleException = true;
				if (tgble != null) {
					tgble.close();
				}
				Bundle edatareset = msg.getData();
				if (null != edatareset && edatareset.getString("MSG_RESET") != null
						&& !edatareset.getString("MSG_RESET").equals("")) {
					ToastUtils.showToast(PedometerActivity.this, edatareset.getString("MSG_RESET"));
				}
				mRefreshableView.finishRefresh();
				bluetoothState = 2;
				Logger.d("cjz", "---------MSG_RESET--------");
				setBloothDisconnectState(true, true);
				break;
			case TgbleManagerNeuro.MSG_LOST:
				hasBleException = true;
				mRefreshableView.finishRefresh();
				bluetoothState = 2;
				Logger.d("cjz", "---------MSG_LOST--------");
				setBloothDisconnectState(true, true);
				break;
			case TgbleManagerNeuro.MSG_CONNECTED:
				hasBleException = true;
				// ToastUtils.showToast(getBaseContext(), "手环连接成功");
				bluetoothState = 1;
				setBloothDisconnectState(true, false);
				// tgble.startRealTimeSport();
				break;
			case TgbleManagerNeuro.MSG_TRANSPORT:
				Bundle data = msg.getData();
				mRefreshableView.setText(data.getString("MSG_TRANSPORT"));
				break;
			case TgbleManagerNeuro.MSG_SUCCESS:
				mHandler.removeCallbacks(bleDisconnectRunnable);
				mRefreshableView.finishRefresh();
				break;
			case TgbleManagerNeuro.MSG_BUSY:
				mHandler.removeCallbacks(bleDisconnectRunnable);
				mRefreshableView.finishRefresh();
				break;
			case TgbleManagerNeuro.MSG_STATUS:
				Bundle data1 = msg.getData();
				Toast.makeText(getBaseContext(), data1.getString("MSG_STATUS"), Toast.LENGTH_SHORT).show();
				mRefreshableView.setText(data1.getString("MSG_STATUS"));
				break;
			case TgbleManagerNeuro.MSG_POWER:
				Bundle data6 = msg.getData();
				int PowerPercent = data6.getInt("MSG_POWER");
				Toast.makeText(getBaseContext(), "手环电量：" + PowerPercent + "，电量较低", Toast.LENGTH_SHORT).show();
				mRefreshableView.finishRefresh();
				break;

			case TgbleManagerNeuro.MSG_FAILED:
				mHandler.removeCallbacks(bleDisconnectRunnable);
				if(mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND){
					ToastUtils.showToast(getBaseContext(), msg.getData().getString("MSG_FAILED"));
				}
				mRefreshableView.finishRefresh();
				break;
			case TgbleManagerNeuro.MSG_CURRENTCOUNT:
				Bundle data10 = msg.getData();
				if (bluetoothPedo == null) {
					bluetoothPedo = new PedometorDataInfo();
					bluetoothPedo.createtime = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()),
							FormatType.DateLong);
					bluetoothPedo.date = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()),
							FormatType.DateWithUnderline);
					bluetoothPedo.deviceId = mDeviceId;
				}
				if (!data10.getString("stepSum").equals(bluetoothPedo.stepNum)) {

					bluetoothPedo.stepNum = data10.getString("stepSum");
					bluetoothPedo.cal = data10.getString("calSum");
					bluetoothPedo.distance = data10.getString("distanceSum");
					bluetoothPedo.strength2 = data10.getString("stepTime");
					// bluetoothPedo.strength2=(Integer.parseInt(bluetoothPedo.stepNum)
					// * 0.55)+"";
					Logger.d("cjz", "已实时 步数：" + bluetoothPedo.stepNum);
					Logger.d("cjz", "已实时 时长：" + bluetoothPedo.strength2);
					if (DateFormatUtils.isToday(mDisplayDate)) {
						displayPedoData(bluetoothPedo);
						currentPedo=bluetoothPedo;
					}
				}
				break;
			default:
				Logger.d("cjz", "---------MSG_default--------" + msg.what);
				setBloothDisconnectState(true, true);
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back")) {
			backToMain();
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "imageButton_title")) {
			mPopmenu_more.showAsDropDown(v);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "btn_cal_achievement")) {
			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(
					getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_cal")),
					null, null, null);
			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(

					getResources().getDrawable(
							MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_distance_normal")), null,
					null, null);
			mBtn_cal_achievement.setTextColor(getResources().getColor(
					MResource.getIdByName(getApplicationContext(), "color", "zyqt_coffee")));
			mBtn_distance_achievement.setTextColor(getResources().getColor(
					MResource.getIdByName(getApplicationContext(), "color", "zyqt_gray0")));
			mRl_distance_achievement.setVisibility(View.GONE);
			mLl_cal_achievement.setVisibility(View.VISIBLE);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "btn_distance_achievement")) {
			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(

			getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_cal_normal")),
					null, null, null);
			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(

			getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_distance")),
					null, null, null);
			mBtn_cal_achievement.setTextColor(getResources().getColor(
					MResource.getIdByName(getApplicationContext(), "color", "zyqt_gray0")));
			mBtn_distance_achievement.setTextColor(getResources().getColor(
					MResource.getIdByName(getApplicationContext(), "color", "zyqt_coffee")));
			mRl_distance_achievement.setVisibility(View.VISIBLE);
			mLl_cal_achievement.setVisibility(View.GONE);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "iv_showGoal")) {
			Intent intent = new Intent();
			intent.putExtra("intent", 13);
			intent.setClass(getBaseContext(), TabBaseFragment.class);
			startActivity(intent);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "iv_leftday")) {
			displayPedoDataAdd(-1);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "iv_rightday")) {
			displayPedoDataAdd(1);
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "iv_reset")) {
			mDisplayDate = new Date(System.currentTimeMillis());
			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
			if ((mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)
					&& bluetoothPedo != null) {
				displayPedoData(bluetoothPedo);
			} else {
				displayPedoData(currentPedo);
			}
			showCurrentEquipment(mDeviceType);
			setLeftAndRightDisplayStatus();
			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				if (bluetoothState == 2) {
					setBloothDisconnectState(true, true);
				} else {
					setBloothDisconnectState(true, false);
				}
			}
		} else if (v.getId() == MResource.getIdByName(getApplicationContext(), "id", "ig_disconnect")) {
			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				startBle();
			} else if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
				startJWBle();
			}
		}else if (v.getId() ==MResource.getIdByName(getApplicationContext(), "id", "zyqt_icon_history_l")) {
			Intent intent = new Intent();
			intent.setClass(getBaseContext(), HistorySportActivity.class);
			startActivity(intent);
		}

	}

	/**
	 * 运动数据翻页 TODO
	 * 
	 * @param diff
	 *            天数差
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:01:18
	 */
	private void displayPedoDataAdd(int diff) {
		if (DateFormatUtils.DateToString(mDisplayDate, FormatType.DateShot).equals(
				DateFormatUtils.DateToString(new Date(), FormatType.DateShot))
				&& diff > 0)// 滑动到尽头不允许向后滑动
		{
			return;
		}
		mDisplayDate = DateFormatUtils.AddDays(mDisplayDate, diff);
		if (DateFormatUtils.isToday(mDisplayDate)) {
			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
			showCurrentEquipment(mDeviceType);
		} else {
			// 显示最后一条记录不论设备
			currentPedo = PedoController.GetPedoController(this).getLatestPedometerOfAllByDay(mDisplayDate);
			if (currentPedo != null) {
				showCurrentEquipment(Common.getDeviceType(currentPedo.deviceId, currentPedo.deviceType));
			} else {
				showCurrentEquipment(mDeviceType);
			}
		}

		if ((mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)
				&& DateFormatUtils.isToday(mDisplayDate) && bluetoothPedo != null) {
			displayPedoData(bluetoothPedo);
		} else {
			displayPedoData(currentPedo);
		}
		setLeftAndRightDisplayStatus();
		if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
				|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
			if (bluetoothState == 2) {
				setBloothDisconnectState(true, true);
			} else {
				setBloothDisconnectState(true, false);
			}
		}
	}

	private void setBloothDisconnectState(boolean isStop, boolean isShow) {

		if (isStop) {
			mIv_bloothDisconnect.setAnimation(null);
		} else {
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getBaseContext(),
					MResource.getIdByName(getApplicationContext(), "anim", "zyqt_progess_round"));
			// 使用ImageView显示动画
			mIv_bloothDisconnect.startAnimation(hyperspaceJumpAnimation);
		}
		if (isShow && DateFormatUtils.isToday(mDisplayDate)) {
			mIv_bloothDisconnect.setVisibility(View.VISIBLE);
		} else {
			mIv_bloothDisconnect.setVisibility(View.GONE);
		}

	}

	/**
	 * TODO 返回键监听
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 * @return boolean
	 * @author zhangfengjuan
	 * @time 下午2:08:47
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backToMain();
			return false;
		}
		return false;
	}

	/**
	 * TODO 跳转进入MainFragmentActivity
	 * 
	 * @return void
	 * @author zhangfengjuan
	 * @time 下午2:11:50
	 */
	private void backToMain() {
		System.out.println("-----isInLauncher()----" + isInLauncher());
		if (isInLauncher()) {
			// Intent intent = new Intent(this, MainFragmentActivity.class);
			// // Intent intent = new Intent(this, MainFragmentActivity2.class);
			// startActivity(intent);
			// overridePendingTransition(R.anim.slide_in_left,
			// R.anim.silde_out_right);
			finish();
		} else {
			// Intent intent = new Intent(this, MainFragmentActivity.class);
			Intent intent = new Intent(this, MainFragmentActivity2.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(MResource.getIdByName(getApplicationContext(), "anim", "zyqt_slide_in_left"),
					MResource.getIdByName(getApplicationContext(), "anim", "zyqt_silde_out_right"));
			this.finish();
		}
	}

	/**
	 * TODO 栈中是否包含MainFragmentActivity
	 * 
	 * @return
	 * @return boolean
	 * @author zhangfengjuan
	 * @time 下午2:08:14
	 */
	private boolean isInLauncher() {
		// ActivityManager mAm = (ActivityManager)
		// this.getSystemService(Context.ACTIVITY_SERVICE);
		// List<RunningTaskInfo> taskList = mAm.getRunningTasks(100);
		// for (RunningTaskInfo rti : taskList) {
		// String name = rti.baseActivity.getClassName();
		// String name2 = rti.topActivity.getClassName();
		// if
		// (name.equals("cmccsi.mhealth.portal.sports.activity.MainFragmentActivity")
		// ||
		// name2.equals("cmccsi.mhealth.portal.sports.activity.MainFragmentActivity"))
		// {
		// return true;
		// }
		// // if
		// (name.equals("cmccsi.mhealth.portal.sports.activity.MainFragmentActivity2")
		// // ||
		// name2.equals("cmccsi.mhealth.app.sports.activity.MainFragmentActivity2"))
		// {
		// // return true;
		// // }
		// }
//		for (Activity i : BaseActivity.allActivity) {
//			System.out.println("-----------------" + i.getClass() + "------------" + MainFragmentActivity2.class);
//			if (i.getClass().equals(MainFragmentActivity2.class)) {
//				return true;
//			}
//		}
		if (BaseActivity.allActivity.contains(MainFragmentActivity2.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 手机计步数据接收器
	 * 
	 * @type MHealthStepReceiver TODO
	 * @author jiazhi.cao
	 * @time 2015-3-14下午9:41:19
	 */
	private class MHealthStepReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Config.STEP_SENDING_ACTION.equals(intent.getAction())) {

				if (DateFormatUtils.isToday(mDisplayDate)) {
					Bundle data = intent.getExtras();
					String steptime = formatHHMMfromSec(data.getInt("DURATION_ALL_DAY"));
					setNomalPedoData(steptime, "0", String.valueOf(data.getInt("CALORIE_ALL_DAY")),
							String.valueOf(data.getInt("DISTANCE_ALL_DAY")));
					setPercentPedoData(data.getInt("STEP_ALL_DAY"), mTargetStep);
					int tempcal = data.getInt("CALORIE_ALL_DAY");
					if ((tempcal - mlastCal) != 0)// 卡路里有变化或者重置卡路里
					{
						mlastCal = tempcal;
						setCalArchivement(tempcal);
					}
					//更新current 为分享准备
					if(currentPedo==null)
					{
						currentPedo=new PedometorDataInfo();
						currentPedo.createtime=DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateLong);
						currentPedo.date=DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateWithUnderline);
						currentPedo.deviceId=mDeviceId;
					}
					currentPedo.stepNum= String.valueOf(data.getInt("STEP_ALL_DAY"));
					currentPedo.cal= String.valueOf(data.getInt("CALORIE_ALL_DAY"));
					currentPedo.distance= String.valueOf(data.getInt("DISTANCE_ALL_DAY"));
					currentPedo.strength2= String.valueOf(data.getInt("DURATION_ALL_DAY"));
				}
			} else if (intent.getAction().equals(Config.UPLOADSTATUS_ACTION)) {
				Logger.i("cjz", " 手机计步结束完了");
				mRefreshableView.finishRefresh();
			}
		}
	}

	/**
	 * 注册计步数据接收器
	 */
	private void registerStepReceiver() {
		if (mMHStepReceiver == null) {
			mMHStepReceiver = new MHealthStepReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.STEP_SENDING_ACTION);
		filter.addAction(Config.UPLOADSTATUS_ACTION);
		filter.addAction(Config.PHONESTEP_STARTUPLOAD_ACTION);
		registerReceiver(mMHStepReceiver, filter);
	}

	/**
	 * 注销计步数据接收器
	 */
	private void unregisterStepReceiver() {
		try {
			if (mMHStepReceiver != null) {
				unregisterReceiver(mMHStepReceiver);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 更多菜单点击事件
	 */
	private OnItemClickListener menuItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:// 分享
				Intent preShareIntent = new Intent(PedometerActivity.this,SharePagerActivity.class);
				Bundle mBundle = new Bundle();  
		        mBundle.putParcelable("currentPedo", currentPedo);  
		        preShareIntent.putExtras(mBundle);  
		        preShareIntent.putExtra("mDisplayDate", DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline));
				startActivity(preShareIntent);
				break;
			case 1:// 设备切换
					// 存一下实时蓝牙数据便于上传
				if (bluetoothPedo != null) {
					PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(bluetoothPedo, false);
				}
				Intent tempit = new Intent(PedometerActivity.this,
						cmccsi.mhealth.portal.sports.ecg.activity.DeviceSettingActivityTest.class);
				tempit.putExtra("sampletitle",
						getString(MResource.getIdByName(getApplicationContext(), "string", "zyqt_textview_binddevice")));
				tempit.putExtra(SharedPreferredKey.PASSWORD, PreferencesUtils.getPhonePwd(getBaseContext()));
				tempit.putExtra(SharedPreferredKey.PHONENUM, PreferencesUtils.getPhoneNum(getBaseContext()));
				startActivity(tempit);
				break;
			default:
				break;
			}
			mPopmenu_more.dismiss();
		}
	};
	private boolean isDown = false;

	class PedoGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// DisplayMetrics dm = new DisplayMetrics();
			// WindowManager windowManager = (WindowManager)
			// PedometerActivity.this.getSystemService(Context.WINDOW_SERVICE);
			// windowManager.getDefaultDisplay().getMetrics(dm);
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			// //限制必须得划过屏幕的1/4才能算划过
			// float x_limit = dm.widthPixels / 7;
			// float y_limit = dm.heightPixels / 7;
			float x_abs = Math.abs(x);
			float y_abs = Math.abs(y);
			if (x_abs >= y_abs) {
				// 左滑动
				if ((e1.getX() - e2.getX()) > 20 && Math.abs(velocityX) > 0) {
					displayPedoDataAdd(1);
				}// 右滑动
				else if ((e2.getX() - e1.getX()) > 20 && Math.abs(velocityX) > 0) {
					displayPedoDataAdd(-1);
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}

	// /*****************以下为手环代码*************************************
	// //TODO Auto-generated catch block

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND)// 是蓝牙
		{
			if (isRealTime) {
				// tgble.stopRealTimeSport();
				// PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(bluetoothPedo,
				// false);
				if (bluetoothPedo != null) {
					bluetoothPedo.deviceId = mDeviceId;
					bluetoothPedo.deviceType = mDevicePara;
					PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(bluetoothPedo, false);
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						UploadManager.uploadBlePedo(bluetoothPedo);
					}
				}).start();
			}
		} else if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)// 是蓝牙
		{
			if (bluetoothPedo != null) {
				bluetoothPedo.deviceId = mDeviceId;
				bluetoothPedo.deviceType = mDevicePara;
				PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(bluetoothPedo, false);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					UploadManager.uploadBlePedo(bluetoothPedo);
				}
			}).start();
			if (jwdManager != null) {
				jwdManager.disConnect();
			}
			if (mTimerUpdateECGPedo != null) {
				mTimerUpdateECGPedo.cancle();
				mTimerUpdateECGPedo = null;
			}
		} else if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP) {
			Intent it = new Intent(Config.PHONESTEP_UPLOAD_ACTION);
			sendBroadcast(it);
			try {
				unregisterReceiver(mMHStepReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private class LoadAchievementTask extends AsyncTask<String, Integer, GoalInfo> {

		@Override
		protected GoalInfo doInBackground(String... params) {
			GoalNetInfo goalNetInfo = new GoalNetInfo();
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			int res = DataSyn.getInstance().getGoalData("2014-10-01", dateFormatter.format(date), goalNetInfo);
			GoalInfo goalInfo = null;
			if (res == 0) {
				goalInfo = GoalInfo.getInstanse(goalNetInfo);
				restoreGoalInfo2SP(goalNetInfo);
			} else {
				goalInfo = GoalInfo.getInstance(getBaseContext());
			}
			return goalInfo;
		}

		/**
		 * 把goal数据存储到SharedPreferences中
		 * 
		 * @param goalNetInfo
		 */
		private void restoreGoalInfo2SP(GoalNetInfo goalNetInfo) {
			PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.GOAL_TYPE, goalNetInfo.goalinfo.goal);
			PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.LATEST_RATE, goalNetInfo.goalinfo.rate);
			PreferencesUtils.putString(getBaseContext(), SharedPreferredKey.CURRENT_DISTANCE, goalNetInfo.goalinfo.distance);
		}

		@Override
		protected void onPostExecute(GoalInfo result) {
			super.onPostExecute(result);
			iv_showGoal.setImageResource(result.type.getImgRes());
			tv_showGoal.setText(result.getGoalReportInTime(getBaseContext()));
		}

	}

	/**
	 * 是否可用的版本 （4.3）
	 * 
	 * @return 4.3及以上为真
	 */
	private boolean isVersionUseable() {
		if (Build.VERSION.SDK_INT < 18) {
			Toast.makeText(getBaseContext(), "蓝牙智能手环要求Android4.3以上版本，您的手机版本过低！", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OPEN_BT_CODE) {
			switch (resultCode) {
			case RESULT_OK:
				if (isVersionUseable()) {
					if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
						startBle();
					} else if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
							|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
						startJWBle();
					}
				}
				break;
			case RESULT_CANCELED:
				setBloothDisconnectState(true, true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 开始神念手环连接 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:06:13
	 */
	private void startBle() {
		setBloothDisconnectState(false, true);

		hasBleException = false;
		mHandler.postDelayed(bleDisconnectRunnable, 30 * 1000);
		tgble = TgbleManagerNeuro.getSingleInstance(getApplicationContext());
		tgble.setHandle(mbleAlertHandler);
		tgble.setRealActivitiy(true);
		// 调用系统API去打开蓝牙
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) // 未打开蓝牙，才需要打开蓝牙
		{
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
			// 会以Dialog样式显示一个Activity ， 我们可以在onActivityResult()方法去处理返回值
			return;
		}

		tgble.startRealTimeSport();

	}

	/**
	 * 开始手环连接 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:06:13
	 */
	private void startJWBle() {
		if ("".equals(mDeviceId)) {
			mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
		}
		setBloothDisconnectState(false, true);
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) // 未打开蓝牙，才需要打开蓝牙
		{
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_OPEN_BT_CODE);
			// 会以Dialog样式显示一个Activity ， 我们可以在onActivityResult()方法去处理返回值
			return;
		}
		hasBleException = false;
		mHandler.postDelayed(bleDisconnectRunnable, 30 * 1000);
		if (jwdManager == null) {
			jwdManager = JWDeviceManager.getInstance(this);
		}
		jwdManager.setCallBack(jwdCallBack);
		if (jwdManager.getDeviceStatus() == 0) {
			jwdManager.connect(mDeviceId.substring(2));
		} else if (jwdManager.getDeviceStatus() == 2) {
			hasBleException = true;
			setBloothDisconnectState(true, false);
			jwdManager.startRealTime();
		}
	}

	private Runnable bleDisconnectRunnable = new Runnable() {
		@Override
		public void run() {
			if (!hasBleException) {
				Message msg = new Message();
				msg.what=1;
				mHandler.sendMessage(msg);
				ToastUtils.showToast(getBaseContext(), "手环连接失败，请稍后再试");
			}
		}
	};

	BaseCallBack jwdCallBack = new BaseCallBack() {

		@Override
		public void realTimeEKGDataReceived(int key, Object data) {

		}

		@Override
		public void realTimeDataReceived(PedometorDataInfo data) {
			Message msg = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_CURRENTCOUNT);
			Bundle mBundle = new Bundle();
			mBundle.putString("stepSum", data.stepNum);
			mBundle.putString("calSum", data.cal);
			mBundle.putString("distanceSum", data.distance);
			mBundle.putString("stepTime", Integer.valueOf(data.strength2) * 60 + "");
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void pedoDataReceived(PedometorListInfo data, List<PedoDetailInfo> detail) {
			Logger.d("cjz", "pedoDataReceived called");
			if (data == null) {
				Message msgdisconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_SUCCESS);
				msgdisconnect.sendToTarget();
				return;
			}
			for (PedometorDataInfo tempdata : data.datavalue) {
				tempdata.deviceId = mDeviceId;
				tempdata.deviceType = mDevicePara;
			}
			PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(data);

			int isSucess = 0;// 0成功 -1失败 1没有数据
			try {
				for (PedometorDataInfo pedo : data.datavalue) {
					Logger.d("cjz", pedo.toString());
					if (pedo.createtime.substring(0, 10).equals(
							DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline))
							&& bluetoothPedo != null) {
						bluetoothPedo.deviceId = mDeviceId;
						bluetoothPedo.deviceType = mDevicePara;
						UploadManager.uploadBlePedo(bluetoothPedo);
					} else {
						if (!UploadManager.uploadBlePedo(pedo)) {
							isSucess = -1;
						}
					}

				}

				for (PedoDetailInfo pedoDetailInfo : detail) {
					MHealthProviderMetaData.GetMHealthProvider(PedometerActivity.this).updatePedoDetailData(pedoDetailInfo);
					if (!UploadManager.uploadBlePedoDetail(pedoDetailInfo, mDeviceId, mDevicePara)) {
						isSucess = -1;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				// BaseToast("上传"+e.getMessage());
				isSucess = -1;
			}
			if (isSucess == 0)// 成功后清楚数据
			{
				Message msgdisconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_SUCCESS);
				msgdisconnect.sendToTarget();
				// jwdManager.clearDeviceData();
			} else {
				Message msgdisconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_FAILED);
				msgdisconnect.sendToTarget();
			}
		}

		@Override
		public void pedoDataPercent(int percent) {
			Message msg = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_TRANSPORT);
			Bundle mBundle = new Bundle();
			mBundle.putString("MSG_TRANSPORT", "传输进度:" + percent + "%");
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void exception(int code, String msg) {
			Logger.d("cjz", "-------exception---------" + msg);
			Message msgdisconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_EXCEPT);
			Bundle mBundle = new Bundle();
			mBundle.putString("MSG_EXCEPT", msg);
			msgdisconnect.setData(mBundle);
			msgdisconnect.sendToTarget();
		}

		@Override
		public void ekgStop(int result, int finalHR) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ekgDataReceived(int key, Object data) {

		}

		@Override
		public void disConnected() {
			Logger.d("cjz", "-------disconnect---------");
			Message msgdisconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_EXCEPT);
			// Bundle mBundle=new Bundle();
			// mBundle.putString("MSG_EXCEPT", "手环断开连接");
			// msgdisconnect.setData(mBundle);
			msgdisconnect.sendToTarget();
		}

		@Override
		public void connected(int code, String msg) {
			Logger.d("cjz", "-------connected-------");
			if (code == DeviceConstants.CONNECTED_SUCCESS) {
				Message msgconnect = Message.obtain(mbleAlertHandler, TgbleManagerNeuro.MSG_CONNECTED);
				msgconnect.sendToTarget();
				jwdManager.startRealTime();
			}
		}
	};

	/**
	 * 手环数据定时发送
	 * 
	 * @type TimerUpdateECGPedo TODO
	 * @author shaoting.chen
	 * @time 2015年4月3日下午2:37:27
	 */
	public class TimerUpdateECGPedo {
		Timer timer;
		TimerUpdateAskTask timerTask;

		public TimerUpdateECGPedo(int seconds) {

			timer = new Timer();
			timerTask = new TimerUpdateAskTask(seconds);
			timer.schedule(timerTask, 60 * 1000, 60 * 1000);
		}

		public void cancle() {
			timer.cancel();
			timerTask.cancel();
		}

		class TimerUpdateAskTask extends TimerTask {
			int mSpace = 0;

			public TimerUpdateAskTask(int space) {
				mSpace = space;
			}

			public void run() {
				Logger.i("-----", "Timer TimerUpdateAskTask!");
				if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW || mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201)// 手环上传数据
				{
					try {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(System.currentTimeMillis());
						if (calendar.get(Calendar.MINUTE) % 5 == 0) {
							Logger.d("cjz", "DEVICE_BRACLETE_JW 存数据");
							if (bluetoothPedo != null) {
								bluetoothPedo.deviceId = mDeviceId;
								bluetoothPedo.deviceType = mDevicePara;
								PedoController.GetPedoController(PedometerActivity.this).insertOrUpdatePedometer(bluetoothPedo,
										false);
							}
						}
						if ((calendar.get(Calendar.HOUR_OF_DAY) % 2 == 1 && calendar.get(Calendar.MINUTE) == mSpace)
								|| (calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) == 59)) {
							PedometorDataInfo data = PedoController.GetPedoController(PedometerActivity.this).getPedometerByDay(
									mDeviceId, new Date(System.currentTimeMillis()));
							UploadManager.uploadBlePedo(data);
						}

					} catch (Exception e) {

					}
				}
			}
		}
	}

	// -------------------------------------------------------------
	private CustomProgressDialog dialog;
	private static final int WHAT_LOAD_FINISHED = 1000;

	/**
	 * 初始化user
	 * 
	 * @version 1.0.0
	 * @author Xiao
	 */
	private class InitAccountRunnable implements Runnable {

		@Override
		public void run() {
			Context context = PedometerActivity.this;
			String versionName = getVersion();
			if (Config.ISALONE) {
				userUid = PreferencesUtils.getString(context, SharedPreferredKey.NICK_NAME, "");
				System.out.println("------------2userUid------------" + userUid);
			} else {
				System.out.println("----------1userUid-------------" + userUid);
				System.out.println("----------1userUid1-------------"
						+ PreferencesUtils.getString(context, SharedPreferredKey.USERUID, ""));
				if (!PreferencesUtils.getString(context, SharedPreferredKey.USERUID, "").equals(userUid)) {
					SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					Editor editorShare = info.edit();
					editorShare.clear();
					editorShare.commit();
					clearServise();
					Common.clearDatabases(PedometerActivity.this);
				}
				// 版本升级删除数据库数据不删除运动数据
				if (!PreferencesUtils.getString(context, SharedPreferredKey.APPVERNAME, "").equals(versionName)) {
					PreferencesUtils.putString(context, SharedPreferredKey.APPVERNAME, versionName);
					clearServise();
					Common.clearDatabasesWithoutPedo(PedometerActivity.this);
				}

			}

			DataSyn dataSyn = DataSyn.getInstance();
			Log.d("cmccsi.mhealth.portal.sports", "userUid = " + userUid);
			dataSyn.setUserUid(userUid);
			dataSyn.loadServerInfo(context);
			dataSyn.loadUserInfoNotInThread(context);
			// dataSyn.loadClubId(context);
			DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
			dataSyn.getDeviceListData(deviceListInfo);
			hasDownUserInfo=true;
			mHandler.sendEmptyMessage(WHAT_LOAD_FINISHED);
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private void clearServise() {
		// if (Common.isServiceRunning(this, Constants.SERVICE_RUNNING_NAME)) {
		if (StepService_GPS.isServiceStart) {

			stopService(new Intent().setClass(this, StepService_GPS.class));
		}
		if (Common.isStepServiceRunning(this)) {
			Intent it = new Intent(Config.PHONESTEP_STOP_NOSAVE_ACTION);
			sendBroadcast(it);
		}
	}

	private void setAlert() {
		System.out.println("-----etwet--------");
		String strDate = getStringDateShort() + " 22:00:00";
		Date time = strToDateLong(strDate);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);// 获取系统提示服务
		Intent intent = new Intent(this, SendMsgBroadCast.class);// 一般用广播连用
		PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
		if (time.getTime() > System.currentTimeMillis()) {
			am.set(AlarmManager.RTC, time.getTime(), operation);// 开始设置闹铃
		}
	}

	/**
	 * 获取现在短时间字符串
	 * 
	 * @return 返回短时间字符串格式：年-月-日
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间字符串转换为时间类型 yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 返回时间类型长字符串时间 yyyy-MM-dd HH:mm:ss
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 保存设备信息到 sharedpreference中
	 */
	private void restoreDeviceInfo() {
		String Address = "";
		DeviceListInfo deviceListInfo = DeviceListInfo.getInstance();
		DeviceInfo deviceInfo_now = null;
		boolean have_bracelet_device = false;
		for (DeviceInfo deviceInfo : deviceListInfo.datavalue) {
			// 判断是否有手环设备
			if (Common.getDeviceType(deviceInfo.deviceSerial, deviceInfo.productPara) == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
				have_bracelet_device = true;
				Address = deviceInfo.deviceSerial.substring(2);
				if (deviceInfo != null) {
					Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					editor.putString(SharedPreferredKey.DEVICE_TOKEN, deviceInfo.deviceToken);
					editor.putString(SharedPreferredKey.DEVICE_NUMBER, deviceInfo.deviceNumber);
					editor.putString(SharedPreferredKey.DEVICE_VERSION, deviceInfo.deviceVersion);
					editor.commit();
				}
			}
			// 获取当前绑定设备
			if (Integer.valueOf(deviceInfo.isUsed) == 1) {
				deviceInfo_now = deviceInfo;
			}
		}
		PreferencesUtils.putBoolean(this, SharedPreferredKey.HAVE_BRACELET_DEVICE, have_bracelet_device);
		PreferencesUtils.putString(this, SharedPreferredKey.DEVICE_ADDRESS, Address);
		if (deviceInfo_now != null) {
			mDeviceType = Common.getDeviceType(deviceInfo_now.deviceSerial, deviceInfo_now.productPara);
			Editor editor = this.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			editor.putString(SharedPreferredKey.DEVICE_ID, deviceInfo_now.deviceSerial);
			System.out.println("---------DEVICE_ID------------" + deviceInfo_now.deviceSerial);
			editor.putString(SharedPreferredKey.DEVICE_NAME, deviceInfo_now.productName);
			editor.putString(SharedPreferredKey.DEVICE_MODEL, deviceInfo_now.productPara);
			editor.putInt(SharedPreferredKey.DEVICE_TYPE, mDeviceType);
			editor.commit();
		} else {
			// Editor editor =
			// this.getSharedPreferences(SharedPreferredKey.SHARED_NAME,
			// Context.MODE_PRIVATE).edit();
			// editor.putString(SharedPreferredKey.DEVICE_ID, null);
			// editor.putString(SharedPreferredKey.DEVICE_NAME, null);
			// editor.putString(SharedPreferredKey.DEVICE_MODEL, null);
			// editor.putInt(SharedPreferredKey.DEVICE_TYPE,
			// Common.getDeviceType(null));
			// editor.commit();
			backToMain();
		}
	}
}
