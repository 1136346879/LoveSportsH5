package cmccsi.mhealth.portal.sports.pedo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.portal.sports.bean.GoalInfo;
import cmccsi.mhealth.portal.sports.bean.GoalNetInfo;
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
import cmccsi.mhealth.portal.sports.device.DeviceConstants;
import cmccsi.mhealth.portal.sports.device.DeviceManagerService;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.phonesteplib.StepController;
import cmccsi.mhealth.portal.sports.service.StepService;
import cmccsi.mhealth.portal.sports.view.CenterRollingBall;
import cmccsi.mhealth.portal.sports.view.DownFlashView;
import cmccsi.mhealth.portal.sports.view.DownFlashView.RefreshListener;
import cmccsi.mhealth.portal.sports.view.PedoCalProcess;
import cmccsi.mhealth.portal.sports.view.PopMenu;


public class PedometerActivityTest extends Activity implements OnClickListener, RefreshListener {
	private final String TAG = "PedometerActivityTest";
	private final String UPDATERESULT = "updateresult";
	private final int REQUEST_OPEN_BT_CODE=99;

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
	private ImageView mIv_bloothDisconnect;//手环断开连接
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
	private int mDeviceType = 0;// 设备类型
	private int mTargetStep = 10000;// 目标步数

	private PedometorDataInfo currentPedo;// 当前页面显示的数据
	private PedometorDataInfo bluetoothPedo;// 当前页面显示的数据
	private Date mDisplayDate;// 展示日期
	
	private boolean isupdate=true;//下拉是否刷新 专用于手环
	
	private DeviceManagerService mDeviceManagerService;
	
	
//	private boolean hasBleException=false;
	
	private int bluetoothState=0;//蓝牙设备连接状态 0不是蓝牙设备 1已连接 2已断开
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplicationContext(), "layout", "zyqt_activity_pedo"));

		initView();
		//getGuideState();
		Logger.d("cjz", "-------PedometerActivityTest start------");
		mDisplayDate = new Date(System.currentTimeMillis());
		setPercentPedoData(0, 1000);
		
		mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0);
		mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
		showCurrentEquipment(mDeviceType);
		if(mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
		{
			bluetoothState=2;
			setBloothDisconnectState(false,true);
		}
		
		mRefreshableView.startRefreshDirectly();
	}
	public PedometerActivityTest() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onStart() {
		super.onStart();
		Logger.d("cjz", "PedometerActivityTest onStart");
		try {
			setLeftAndRightDisplayStatus();
			if(mDeviceType!=PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0))
			{
				isupdate=true;
				Logger.e("cjz", "设备 changed");				
				mDeviceType = PreferencesUtils.getInt(this, SharedPreferredKey.DEVICE_TYPE, 0);
				mDeviceId = PreferencesUtils.getString(this, SharedPreferredKey.DEVICE_ID, null);
				showCurrentEquipment(mDeviceType);				
				//切换设备后重新查询
				currentPedo = queryPedometor(mDeviceId, new Date(System.currentTimeMillis()));
				displayPedoData(currentPedo);
				//开启设备管理服务
				Intent _intent = new Intent(this, DeviceManagerService.class);
				startService(_intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(mDeviceType!=DeviceConstants.DEVICE_BRACLETE_BEATBAND
				&&mDeviceType!=DeviceConstants.DEVICE_BRACLETE_JW
				&&mDeviceType!=DeviceConstants.DEVICE_BRACLETE_JW201
				&&DateFormatUtils.isToday(mDisplayDate))
		{
			bluetoothState=0;
			setBloothDisconnectState(true,false);
			bluetoothPedo=null;
		}
//		mDeviceManagerService.connect(mDeviceId);

		// 距离成就
		GoalInfo info = GoalInfo.getInstance(this);
		iv_showGoal.setImageResource(info.type.getImgRes());
		tv_showGoal.setText(info.getGoalReportInTime(this));
		new LoadAchievementTask().execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.i("PedometerActivity", "---onResume");
		//绑定Service  
		Intent _intent = new Intent(this, DeviceManagerService.class);
		bindService(_intent, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mDeviceManagerService.stopRealTime();		
	}

	/**
	 * 初始化页面控件 TODO
	 * 
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:22:22
	 */
	private void initView() {
		ConstantsBitmaps.initRunPics(this);// 圆形进度相关图片
		mRelativeLayoutProgress = (RelativeLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id", "Progress_center_rote1"));
		// 数据展示
		mTv_Duration = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_Duration"));
		mTv_distance = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_distance"));
		mTv_stepnumofday = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_stepnumofday"));
		mTv_percentstep = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_percentstep"));
		mTv_currentEquipment = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_currentEquipment"));
		mTv_displaydate = (TextView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "tv_displaydate"));
		// 左右翻页
		mIv_leftday = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_leftday"));
		mIv_leftday.setOnClickListener(this);
		mIv_rightday = (ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_rightday"));
		mIv_rightday.setOnClickListener(this);
		mIv_reset=(ImageView) findViewById(MResource.getIdByName(getApplicationContext(), "id", "iv_reset"));// 回到当日页面
		mIv_reset.setVisibility(View.INVISIBLE);
		mIv_reset.setOnClickListener(this);
		
		// 运动成就
		mBtn_cal_achievement = (Button) findViewById(MResource.getIdByName(getApplicationContext(), "id", "btn_cal_achievement"));
		mBtn_cal_achievement.setOnClickListener(this);
		mBtn_distance_achievement = (Button) findViewById(MResource.getIdByName(getApplicationContext(), "id", "btn_distance_achievement"));
		mBtn_distance_achievement.setOnClickListener(this);
		mRl_distance_achievement = (RelativeLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id", "rl_distance_achievement"));
		mLl_cal_achievement = (LinearLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id", "ll_cal_achievement"));
		
		mIv_bloothDisconnect=(ImageView)findViewById(MResource.getIdByName(getApplicationContext(), "id", "ig_disconnect"));
		mIv_bloothDisconnect.setOnClickListener(this);
		// 返回键
		ImageButton mImageButtonBack = (ImageButton) findViewById(MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back"));
		mImageButtonBack.setBackgroundResource(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_my_button_back"));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(this);
		//标题
		TextView tv_Title=(TextView)findViewById(MResource.getIdByName(getApplicationContext(), "id", "textView_title"));
//		tv_Title.setText(getResources().getString(MResource.getIdByName(getApplicationContext(), "string", "zyqt_pedometer_title")));
		tv_Title.setText("爱动力 · 走一走");
		// 更多键
		mTopLayout = (LinearLayout) findViewById(MResource.getIdByName(getApplicationContext(), "id", "ll_pedomain"));
		initMorePopmenu();
		ImageButton mImageButtonUpdate = (ImageButton) findViewById(MResource.getIdByName(getApplicationContext(), "id", "imageButton_title"));
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
		
		FrameLayout mFl_pedoprogress=(FrameLayout)findViewById(MResource.getIdByName(getApplicationContext(), "id", "fl_pedoprogress"));
		mGestureDetector=new GestureDetector(getBaseContext(),new PedoGestureListener());
		mFl_pedoprogress.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				return mGestureDetector.onTouchEvent(event);
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
		menulist.add(new CommonBottomMenuItem(1, "分享运动简报", MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_menuitem_share")));
		menulist.add(new CommonBottomMenuItem(1, "切换设备", MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_menuitem_changequipment")));
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
		mPedoCalProcess = (PedoCalProcess) findViewById(MResource.getIdByName(getApplicationContext(), "id", "pcp_pedocalprocess"));
		mPedoCalProcess.setProcess(0);
		mPedoCalProcess.setExplainText(PedometorUtils.calorieToFoodDescription(0));
		mPedoCalProcess.setProcessPicture(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_archive_process"));
        int[] CoordinateX={25,45,65,85};
        mPedoCalProcess.setCoordinateX(CoordinateX);
        int[] normalPictures={MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve1_normal"),MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve2_normal")
        		,MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve3_normal"),MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve4_normal")}	;
        mPedoCalProcess.setNormalMilePictures(normalPictures);
        int[] overPictures={MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve1_press"),MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve2_press")
        		,MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve3_press"),MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_achieve4_press"),}	;
        mPedoCalProcess.setOverMilePictures(overPictures);
        mPedoCalProcess.setExplainPictures(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_cal_archive_explain"));
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
				PedometorDataInfo data = PedoController.GetPedoController(getBaseContext()).getLatestPedometer(
						mDeviceId);

				String startDate = "";
				String endDate = DateFormatUtils
						.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot);
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
			switch (msg.what) {
			case 0:// 更新完毕
				if (msg.getData().getInt(UPDATERESULT) == 0)// 成功
				{
//					Toast.makeText(getBaseContext(), getResources().getString(R.string.phonestep_uploadsuccess),
//							Toast.LENGTH_SHORT).show();
					currentPedo = queryPedometor(mDeviceId, new Date());
					if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
							||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
							||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
							&&bluetoothPedo!=null)
					{
						displayPedoData(bluetoothPedo);
					}
					else
					{
						displayPedoData(currentPedo);
					}
				} else {
					currentPedo = queryPedometor(mDeviceId, new Date());
					displayPedoData(currentPedo);
					Toast.makeText(getBaseContext(), getResources().getString(MResource.getIdByName(getApplicationContext(), "string", "zyqt_phonestep_uploadfailed")),
							Toast.LENGTH_SHORT).show();
				}
				if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP)// 手机
				{
					boolean isStepServiceOpen = PreferencesUtils.getBoolean(getBaseContext(), "StepService", false);
					if (!isStepServiceOpen && !StepService.isRunning)// 开关没有打开并且手机计步没有运行
																		// 开启手机计步
					{
						startStepService();
					}
				}
				mRefreshableView.finishRefresh();
				break;
			case 1:
//				mIv_bloothDisconnect.setAnimation(null);
				setBloothDisconnectState(true,true);
				break;
			default:
				break;
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

			String today = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()),
					FormatType.DateWithUnderline);
			if (mDeviceType == DeviceConstants.DEVICE_MOBILE_STEP && StepService.isRunning
					&& DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithUnderline).equals(today))// 手机计步开启状态从service中取数
			{
				
				String stepTime = formatHHMMfromSec(StepService.todayTotalTime);
				setNomalPedoData(stepTime, "0", String.valueOf(StepService.todayTotalCal),
						String.valueOf(StepService.todayTotalDistance));
				setPercentPedoData(StepService.todayTotalStep, mTargetStep);
				setCalArchivement(StepService.todayTotalCal);
				Logger.d("cjz", "todayTotalStep"+StepService.todayTotalStep);
			} else {
				setNomalPedoData(data);
				if (data != null) {
					String stepSum = data.stepNum;
					try
					{
						setPercentPedoData(Integer.parseInt(stepSum), mTargetStep);
						setCalArchivement(Integer.parseInt(data.cal));
					}
					catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
				} 
				else
				{
					setPercentPedoData(0,mTargetStep);
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
//			int intStepTime = getStepTimeFromPedometor(data);
			data.strength3=data.strength3==""?"0":data.strength3;
			data.strength4=data.strength4==""?"0":data.strength4;
			int tempstepTime=Integer.parseInt(data.strength2)+Integer.parseInt(data.strength3)+Integer.parseInt(data.strength4);
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
		if(mCenterRollingBall!=null)
		{
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
	 * 显示设备信息 TODO
	 *
	 * @param deviceType
	 *            设备类型
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午6:03:11
	 */
	private void showCurrentEquipment(int deviceType) {
		String discription=Common.getDeviceDisplayName(deviceType)+"计步";
		mTv_currentEquipment.setText(discription);

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
	private void setCalArchivement(int cal)
	{
		int calprocess=PedometorUtils.getCalpersent(mPedoCalProcess.getCoordinateX(),cal);
		Logger.i("cjz", "cal:"+cal+"  calprocess:"+calprocess);
		String calmessage=PedometorUtils.calorieToFoodDescription(cal);
		mPedoCalProcess.setExplainText(calmessage);
		mPedoCalProcess.setProcess(calprocess);
		mPedoCalProcess.invalidate();
	}

	@Override
	public void onRefresh(DownFlashView view) {
		if(DateFormatUtils.isToday(mDisplayDate))
		{
			if(isupdate)
			{
				if(mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
						||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
						||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201
						||mDeviceType==DeviceConstants.DEVICE_MOBILE_STEP)
				{
					isupdate=false;
				}
				updatePedoData();
			}
			else
			{
				mRefreshableView.setText("开始同步数据");
//				PedometorDataInfo data = PedoController.GetPedoController(getBaseContext()).getLatestPedometer(
//						mDeviceId);
				String startDate="";
				String endDate=DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
				//补传
//				if(data!=null){
//					startDate=data.createtime;
//				}else{
//					startDate=DateFormatUtils.DateToString(DateFormatUtils.AddDays(new Date(),-3), FormatType.DateLong);
//				}
				//不补传
				startDate=DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline)+" 00:00:00";
				mDeviceManagerService.syncData(startDate, endDate);
				
			}
		}
		else
		{
			mRefreshableView.finishRefresh();
		}
	}
	
	private Handler mbleAlertHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DeviceConstants.CONNECTED_SUCCESS:
//				ToastUtils.showToast(getBaseContext(), "手环连接成功");
				setBloothDisconnectState(true,false);
				mDeviceManagerService.startRealTime();
				break;
			case DeviceConstants.CONNECTED_FAIL:
				Bundle edataconectfail = msg.getData();
				if(null!=edataconectfail&&edataconectfail.getString("CONNECTED_FAIL")!=null
						&&!edataconectfail.getString("CONNECTED_FAIL").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edataconectfail.getString("CONNECTED_FAIL"));
				}
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.DEVICE_RESET:
				Bundle edatareset = msg.getData();
				if(null!=edatareset&&!edatareset.getString("MSG_EXCEPT").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edatareset.getString("MSG_RESET"));
				}
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.EXCEPTION_CONNECT:
				Bundle edata = msg.getData();
				if(edata!=null&&edata.getString("EXCEPTION_CONNECT")!=null
						&&!edata.getString("EXCEPTION_CONNECT").equals("")){
					ToastUtils.showToast(PedometerActivityTest.this, edata.getString("EXCEPTION_CONNECT"));
				}
				
				mRefreshableView.finishRefresh();
				setBloothDisconnectState(true,true);
				break;
			case DeviceConstants.TRANSPORT_PERCENT:
				Bundle data = msg.getData();
				mRefreshableView.setText(data.getString("MSG_TRANSPORT"));
				break;
				
			case DeviceConstants.NOMAL_MESSAGE:
				Bundle data1 = msg.getData();
				mRefreshableView.setText(data1.getString("MSG_STATUS"));
				break;
				
			case DeviceConstants.DEVICE_POWER:
				Bundle data6 = msg.getData();
				int PowerPercent = data6.getInt("MSG_POWER");
				ToastUtils.showToast(getBaseContext(), "手环电量：" + PowerPercent + "，电量较低");
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.UPLOAD_SUCCESS:
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.UPLOAD_FAIL:
				ToastUtils.showToast(getBaseContext(), "数据上传失败");
				mRefreshableView.finishRefresh();
				break;
			case DeviceConstants.REALTIME_PEDO:
				Bundle data10 = msg.getData();
				if(bluetoothPedo==null)
				{
					bluetoothPedo=new PedometorDataInfo();
					bluetoothPedo.createtime=DateFormatUtils.DateToString(new Date(), FormatType.DateLong);
					bluetoothPedo.date=DateFormatUtils.DateToString(new Date(), FormatType.DateWithUnderline);
					bluetoothPedo.deviceId=mDeviceId;
				}
				if(!data10.getString("STEP").equals(bluetoothPedo.stepNum))
				{
					
					bluetoothPedo.stepNum=data10.getString("STEP");
					bluetoothPedo.cal=data10.getString("CAL");
					bluetoothPedo.distance=data10.getString("DISTANCE");
					bluetoothPedo.strength2=data10.getString("STEPTIME");
					Logger.d("cjz", "已实时 步数："+bluetoothPedo.stepNum);
					if(DateFormatUtils.isToday(mDisplayDate))
					{
						displayPedoData(bluetoothPedo);
					}
				}
				break;
			default:
				setBloothDisconnectState(true,true);
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back"):// 返回
//			backToMain();
//
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "imageButton_title"):// 更多
//			mPopmenu_more.showAsDropDown(v);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "btn_cal_achievement")://卡路里成就
//			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(null, null, null,getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_cal")) );
//			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(null, null, null,getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_icon_pedo_distance_normal")) );
//			mBtn_cal_achievement.setTextColor(getResources().getColor(R.color.zyqt_coffee));
//			mBtn_distance_achievement.setTextColor(getResources().getColor(R.color.zyqt_gray0));
//			mRl_distance_achievement.setVisibility(View.GONE);
//			mLl_cal_achievement.setVisibility(View.VISIBLE);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "btn_distance_achievement")://距离成就
//			mBtn_cal_achievement.setCompoundDrawablesWithIntrinsicBounds(null, null, null,getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_")) );
//			mBtn_distance_achievement.setCompoundDrawablesWithIntrinsicBounds(null, null, null,getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "icon_pedo_cal_normalzyqt_icon_pedo_distance")) );
//			mBtn_cal_achievement.setTextColor(getResources().getColor(R.color.zyqt_gray0));
//			mBtn_distance_achievement.setTextColor(getResources().getColor(R.color.zyqt_coffee));
//			mRl_distance_achievement.setVisibility(View.VISIBLE);
//			mLl_cal_achievement.setVisibility(View.GONE);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "iv_showGoal"):// 距离成就
//			Intent intent = new Intent();
//			intent.putExtra("intent", 13);
//			intent.setClass(getBaseContext(), TabBaseFragment.class);
//			startActivity(intent);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "iv_leftday"):// 左翻页
//			displayPedoDataAdd(-1);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "iv_rightday"):// 右翻页
//			displayPedoDataAdd(1);
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "iv_reset"):// 回到当日
//			mDisplayDate = new Date(System.currentTimeMillis());
//			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
//			if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
//					||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
//					||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
//					&&bluetoothPedo!=null)
//			{
//				displayPedoData(bluetoothPedo);
//			}
//			else
//			{
//				displayPedoData(currentPedo);
//			}
//			showCurrentEquipment(mDeviceType);
//			setLeftAndRightDisplayStatus();
//			if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
//					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
//					|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
//				if(bluetoothState==2){
//					setBloothDisconnectState(true, true);
//				}else{
//					setBloothDisconnectState(true, false);
//				}
//			}
//			break;
//		case MResource.getIdByName(getApplicationContext(), "id", "ig_disconnect"):
//			mDeviceManagerService.connect(mDeviceId);
//			break;
//		default:
//			break;
//		}

	}
	
	/**
	 * 运动数据翻页
	 * TODO
	 * @param diff 天数差
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午11:01:18
	 */
	private void displayPedoDataAdd(int diff)
	{
		if(DateFormatUtils.DateToString(mDisplayDate, FormatType.DateShot)
				.equals(DateFormatUtils.DateToString(new Date(), FormatType.DateShot))
				&&diff>0)//滑动到尽头不允许向后滑动
		{
			return;
		}
		mDisplayDate = DateFormatUtils.AddDays(mDisplayDate, diff);
		if(DateFormatUtils.isToday(mDisplayDate))
		{
			currentPedo = queryPedometor(mDeviceId, mDisplayDate);
			showCurrentEquipment(mDeviceType);
		}
		else
		{
			//显示最后一条记录不论设备
			currentPedo=PedoController.GetPedoController(this).getLatestPedometerOfAllByDay(mDisplayDate);
			if(currentPedo!=null)
			{
				showCurrentEquipment(Common.getDeviceType(currentPedo.deviceId, currentPedo.deviceType));
			}else{
				showCurrentEquipment(mDeviceType);
			}
		}

		if((mDeviceType==DeviceConstants.DEVICE_BRACLETE_BEATBAND
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW
				||mDeviceType==DeviceConstants.DEVICE_BRACLETE_JW201)
				&&DateFormatUtils.isToday(mDisplayDate)&&bluetoothPedo!=null)
		{
			displayPedoData(bluetoothPedo);
		}
		else
		{
			displayPedoData(currentPedo);
		}
		setLeftAndRightDisplayStatus();
		if (mDeviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND
				|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW
				|| mDeviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
			if(bluetoothState==2){
				setBloothDisconnectState(true, true);
			}else{
				setBloothDisconnectState(true, false);
			}
		}
	}
	
	private void setBloothDisconnectState(boolean isStop, boolean isShow) {

		if (isStop) {
			mIv_bloothDisconnect.setAnimation(null);
		} else {
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getBaseContext(), MResource.getIdByName(getApplicationContext(), "anim", "zyqt_progess_round"));
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
		if (isInLauncher()) {
//			Intent intent = new Intent(this, MainFragmentActivity.class);
//			Intent intent = new Intent(this, MainFragmentActivity2.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
			finish();
		} else {
//			Intent intent = new Intent(this, MainFragmentActivity.class);
			Intent intent = new Intent(this, MainFragmentActivity2.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			overridePendingTransition(MResource.getIdByName(getApplicationContext(), "anim", "zyqt_slide_in_left"),MResource.getIdByName(getApplicationContext(), "anim", "zyqt_silde_out_right"));
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
		ActivityManager mAm = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = mAm.getRunningTasks(100);
		for (RunningTaskInfo rti : taskList) {
			String name = rti.baseActivity.getClassName();
			String name2 = rti.topActivity.getClassName();
//			if (name.equals("cmccsi.mhealth.app.sports.activity.MainFragmentActivity")
//					|| name2.equals("cmccsi.mhealth.app.sports.activity.MainFragmentActivity")) {
//				return true;
//			}
			if (name.equals("cmccsi.mhealth.portal.sports.activity.MainFragmentActivity2")
					|| name2.equals("cmccsi.mhealth.portal.sports.activity.MainFragmentActivity2")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 更多菜单点击事件
	 */
	private OnItemClickListener menuItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:// 分享
//				String shareDate = DateFormatUtils.DateToString(mDisplayDate, FormatType.DateWithDiagonalNoYear);
				Logger.d("cjz", "截图"+ (mTopLayout==null));
				ShareWeiXin share = new ShareWeiXin(PedometerActivityTest.this, null, mTopLayout);
				share.Shared();
				break;
			case 1:// 设备切换
				//存一下实时蓝牙数据便于上传
				if(bluetoothPedo!=null)
				{
					PedoController.GetPedoController(getBaseContext()).insertOrUpdatePedometer(bluetoothPedo, false);
				}
				Intent tempit = new Intent(PedometerActivityTest.this,
						cmccsi.mhealth.portal.sports.ecg.activity.DeviceSettingActivity.class);
				tempit.putExtra("sampletitle", getString(MResource.getIdByName(getApplicationContext(), "string", "zyqt_textview_binddevice")));
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
	
	class PedoGestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {

			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			//左滑动
			if((e1.getX()- e2.getX()) > 20 && Math.abs(velocityX) > 0) { 
				displayPedoDataAdd(1);  
			}//右滑动
			else if((e2.getX() - e1.getX()) > 20 && Math.abs(velocityX) > 0) { 
				displayPedoDataAdd(-1);
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
	
	///*****************以下为手环代码*************************************
	////TODO Auto-generated catch block

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDeviceManagerService = null;
		unbindService(conn);
	}
	
	
	
	private class LoadAchievementTask extends AsyncTask<String, Integer, GoalInfo>{

        @Override
        protected GoalInfo doInBackground(String... params) {
            GoalNetInfo goalNetInfo = new GoalNetInfo();
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            int res = DataSyn.getInstance().getGoalData("2014-10-01",dateFormatter.format(date), goalNetInfo);
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
		if(requestCode==REQUEST_OPEN_BT_CODE){
			switch (resultCode) {
			case RESULT_OK:
				if(isVersionUseable()){
					//连接当前设备
					mDeviceManagerService.connect(mDeviceId);
				}
				break;
			case RESULT_CANCELED:
				setBloothDisconnectState(true,true);
				break;
			default:
				break;
			}
		}
	}
	
	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.i(TAG, "---onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logger.i(TAG, "---onServiceConnected");
			// 返回一个MsgService对象
			mDeviceManagerService = ((DeviceManagerService.DeviceBind) service).getService(mbleAlertHandler);

			// 当前设备如果是手环，检查蓝牙是否开启
			if (mDeviceId.substring(0, 2).equals("01")) {
				if(Build.VERSION.SDK_INT >= 18){
					if(checkBlueEnabled()){
						//连接当前设备
						mDeviceManagerService.connect(mDeviceId);
					}
				}else{
					ToastUtils.showToast(PedometerActivityTest.this, "您的Android系统版本过低，暂不支持蓝牙手环");
				}
				
			}else{
				//连接当前设备
				mDeviceManagerService.connect(mDeviceId);
			}	
		}
	};
	
	/**
	 * 判断蓝牙是否开启
	 * @return boolean 
	 */
	private boolean checkBlueEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_OPEN_BT_CODE);
			return false;
		}
		return true;
	}
}
