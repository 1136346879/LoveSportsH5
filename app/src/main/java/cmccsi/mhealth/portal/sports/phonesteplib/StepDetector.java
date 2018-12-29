package cmccsi.mhealth.portal.sports.phonesteplib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 陀螺仪的实例，记步使用
 *
 * @author luckchoudog
 */
public class StepDetector implements SensorEventListener {
	/**
	 * 陀螺仪单例
	 */
	private static StepDetector stepDetector = null;
	/**
	 * 陀螺仪的实例，从系统获取
	 */
	private SensorManager mSensorManager;
	/**
	 * 陀螺仪的三个方向（xyz）的偏移值
	 */
	private float[] mAcc;
	/**
	 * 陀螺仪是否在使用
	 */
	private boolean isStepDetectorRun = false;

	private StepDetector(Context context) {
		mAcc = new float[3];
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	public static StepDetector getStepDetetorInstance(Context context) {
		if (null == stepDetector) {
			stepDetector = new StepDetector(context);
		}
		return stepDetector;
	}

	/**
	 * 开始使用陀螺仪（比如说开始记步啦）
	 */
	public void startStepDetector() {
		isStepDetectorRun = true;
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * 停止监听陀螺仪变化
	 */
	public void stopStepDetector() {
		isStepDetectorRun = false;
		mSensorManager.unregisterListener(this);
	}

	/**
	 * 获得三轴加速度
	 */
	public List<Integer> getAcc() {
		List<Integer> result = new ArrayList<Integer>();
		float[] temp = mAcc.clone();
		for (int i = 0; i < 3; i++) {
			int b = (int) (temp[i] / 9.8 / 2 * 128);
			result.add(b);
		}
		return result;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 去除无效偏移量，如果偏移量很小将忽略
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		for (int i = 0; i < 3; i++) {
			if (values[i] > 2 * 9.8) {
				mAcc[i] = (float) (2 * 9.8);
			} else if (values[i] < -2 * 9.8) {
				mAcc[i] = (float) (-2 * 9.8);
			} else {
				mAcc[i] = values[i];
			}
		}
	}
	public boolean getIsStepDetectorRun(){
		return isStepDetectorRun;
	}
}
