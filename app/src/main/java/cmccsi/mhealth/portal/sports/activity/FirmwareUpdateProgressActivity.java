package cmccsi.mhealth.portal.sports.activity;

import com.neurosky.ble.TGBleManager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.SaveDeviceToken;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.common.Logger;
import cmccsi.mhealth.portal.sports.common.PreferencesUtils;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;
import cmccsi.mhealth.portal.sports.common.utils.ToastUtils;
import cmccsi.mhealth.portal.sports.device.DeviceConstants;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.pedo.TgbleManagerNeuro;
import cmccsi.mhealth.portal.sports.view.DownLoadApkProgress;

public class FirmwareUpdateProgressActivity extends BaseActivity {

	private static final String TAG = "FirmwareUpdateProgressActivity";
	private static final int REQUEST_ENABLE_BT = 8001;

	private String mStrURL = "";
	private String mFileNa = "";
	private String mFilePath = "";
	private DownLoadApkProgress mMyProgress = null;
	private TextView mTvMessage;
	private TextView mTvPrcent;

	private FirmwareUpdateProgressActivity mFirmwareUpdateProgressActivity = this;
	private String mDeviceVersion;
	private String mDeviceveId;

	private TgbleManagerNeuro tgBleManager = null;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TGBleManager.MSG_FW_TRANSFER_REPORT:

				String toastStr = "";
				boolean successed = false;
				switch (Integer.valueOf(msg.arg1)) {
				case 0:
					ToastUtils.showToast(getApplicationContext(), "固件升级成功");
					updateToken();
					successed = true;
					break;
				case 1:
					toastStr = "固件升级失败";
					successed = false;
					break;
				case 2:
					toastStr = "手环连接断开";
					successed = false;
					break;
				case 3:
					toastStr = "未找到手环固件";
					successed = false;
					break;
				case 4:
					toastStr = "手环电量过低，请先充电";
					successed = false;
					break;
				case 5:
					toastStr = "固件升级失败";
					successed = false;
					break;
				case 6:
					toastStr = "固件升级失败";
					successed = false;
					break;
				case 7:
					toastStr = "更新手环固件超时";
					successed = false;
					break;
				case 8:
					toastStr = "固件文件无效";
					successed = false;
					break;
				}
				if (!successed) {
					ToastUtils.showToast(getApplicationContext(), toastStr);
					finishSelf(false);
				}
				break;
			case TGBleManager.MSG_FW_TRANSFER_PERCENT:
				Logger.i(TAG, "--- PED MSG_FW_TRANSFER_PERCENT: " + msg.arg1);
				Integer progress_value = Integer.valueOf(msg.arg1);
				if (progress_value >= 100) {
					mMyProgress.setProgress(100);
					mTvPrcent.setText("100%");
				} else {
					mMyProgress.setProgress(progress_value);
					mTvPrcent.setText(progress_value + "%");
				}

				break;
			case TgbleManagerNeuro.MSG_CONNECTED: // 神念手环连接成功
				mHandlerTemp.removeCallbacks(_connect_timeout);
				mTvMessage.setText("更新中，当前进度");
				mMyProgress.setMax(100);
				mTvPrcent.setText("0%");
				checkUpdate(mDeviceVersion);
				break;
			case TgbleManagerNeuro.MSG_EXCEPT: // 神念手环连接失败
				mHandlerTemp.removeCallbacks(_connect_timeout);
				break;
			}

			super.handleMessage(msg);
		};
	};
	// 连接设备超时
	private Runnable _connect_timeout = new Runnable() {
		@Override
		public void run() {
			mHandlerTemp.removeCallbacks(_connect_timeout);
			Logger.i(TAG, "---连接设备超时");
			ToastUtils.showToast(getApplicationContext(), "连接超时");
			finishSelf(false);
		}
	};
	private Handler mHandlerTemp = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_activity_progress_new"));

		// 将Activity设置成diglog窗口德形式，点击空白处是不会消失的
		FirmwareUpdateProgressActivity.this.setFinishOnTouchOutside(false);

		findView();

		mFilePath = getIntent().getStringExtra("fileurl");
		mStrURL = getIntent().getStringExtra("updateurl");
		Logger.i(TAG, "--- " + mFilePath);
		Logger.i(TAG, "--- " + mStrURL);

		mFileNa = mStrURL.substring(mStrURL.lastIndexOf("/") + 1);
		mDeviceVersion = getIntent().getStringExtra("deviceversion");
		mDeviceveId = getIntent().getStringExtra("deviceveId");
		Logger.i(TAG, "--- mDeviceveId" + mDeviceveId);
		mTvMessage.setText("正在连接设备，请稍等");
		connectDevice(mDeviceveId);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.i(TAG, "--- onDestroy ");
		if (tgBleManager != null) {
			tgBleManager.close();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, requestCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			Logger.i(TAG, "---onActivityResult" + resultCode);
			if (resultCode == RESULT_OK) {
				connectDevice(mDeviceveId);
			} else if (resultCode == RESULT_CANCELED) {
				finishSelf(false);
			}
			return;
		}
	}

	private void findView() {
		//
		mMyProgress = (DownLoadApkProgress) findViewById(MResource.getIdByName(MapApplication.mContext, "id", "pgsBar"));
		mTvMessage = (TextView) findViewById(MResource.getIdByName(MapApplication.mContext, "id", "tv_progress_message"));
		mTvPrcent = (TextView) findViewById(MResource.getIdByName(MapApplication.mContext, "id", "tv_progress_prcent"));
	}

	private void checkUpdate(String deviceVersion) {
		Logger.i(TAG, "--- deviceVersion " + deviceVersion);
		Logger.i(TAG, "--- tgBleManager.getFwVersion() " + tgBleManager.getFwVersion());
		String[] arr = tgBleManager.getFwVersion().split("\\.");
		String[] vArr = deviceVersion.split("\\.");
		if (arr.length >= 3) {
			int arrH = Integer.valueOf(arr[0]);
			int arrM = Integer.valueOf(arr[1]);
			int arrL = Integer.valueOf(arr[2]);

			int vArrH = Integer.valueOf(vArr[0]);
			int vArrM = Integer.valueOf(vArr[1]);
			int vArrL = Integer.valueOf(vArr[2]);

			if (arrH > vArrH) {
				ToastUtils.showToast(this, "当前固件版本已是最新");
				updateToken();
			} else if ((arrH == vArrH) && (arrM > vArrM)) {
				ToastUtils.showToast(this, "当前固件版本已是最新");
				updateToken();
			} else if ((arrH == vArrH) && (arrM == vArrM) && (arrL >= vArrL)) {
				ToastUtils.showToast(this, "当前固件版本已是最新");
				updateToken();
			} else {
				tgBleManager.updateFirmware(mFilePath, mFileNa);

			}
		}
	}

	private void updateToken() {
		Logger.i(TAG, "--- updateToken ");
		new Thread() {
			public void run() {
				SaveDeviceToken saveBack = new SaveDeviceToken();
				String userId = PreferencesUtils.getString(getApplicationContext(), SharedPreferredKey.USERUID, "");
				int back = DataSyn.getInstance().saveDeviceToken(userId, mDeviceveId, tgBleManager.getHwSerialNumber(),
						tgBleManager.getBondToken(), mDeviceVersion, saveBack);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						finishSelf(true);
					}
				});
			};
		}.start();
	}

	private void finishSelf(boolean isupdate) {
		if (isupdate) {
			Intent intent = new Intent();
			intent.putExtra("result", "update");
			setResult(RESULT_OK, intent);
		}
		if (tgBleManager != null) {
			tgBleManager.close();
		}
		mFirmwareUpdateProgressActivity.finish();
		overridePendingTransition(MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_slide_in_left"),
				MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_silde_out_right"));
	}

	private void connectDevice(String deviceId) {
		// 检查蓝牙是否开启
		if (!deviceId.isEmpty()) {
			if (Build.VERSION.SDK_INT >= 18) {
				if (checkBlueEnabled()) {
					// 连接当前设备
					mTvMessage.setText("正在连接手环，请稍等");
					tgBleManager = TgbleManagerNeuro.getSingleInstance(getBaseContext());
					tgBleManager.setHandle(mHandler);
					tgBleManager.setRealActivitiy(false);
					mHandlerTemp.postDelayed(_connect_timeout, 1000 * 20);
					tgBleManager.content(mDeviceveId);
				}
			} else {
				ToastUtils.showToast(this, "您的Android系统版本过低，暂不支持蓝牙手环");
				finishSelf(false);
			}

		} else {
			// 设备ID为空
			ToastUtils.showToast(this, "连接失败");
			finishSelf(false);
		}
	}

	/**
	 * 判断蓝牙是否开启
	 * 
	 * @return boolean
	 */
	private boolean checkBlueEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}
}