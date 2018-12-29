package cmccsi.mhealth.portal.sports.view;

import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static boolean isBackExit = true;
	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	/**
	 * 获取dialog实例
	 * 
	 * @param context
	 * @param cancelAble
	 *            点击返回键是否取消dialog
	 * @return
	 */
	public static CustomProgressDialog createDialog(Context context, boolean cancelAble) {
		customProgressDialog = new CustomProgressDialog(context, MResource.getIdByName(context, "style",
				"zyqt_CustomProgressDialog"));
		customProgressDialog.setContentView(MResource.getIdByName(context, "layout", "zyqt_custom_progress_dialog"));
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(cancelAble);// 不可以用“返回键”取消
		isBackExit = cancelAble;
		return customProgressDialog;
	}

	/**
	 * 获取dialog实例 (默认点击返回键不可取消dialog)
	 * 
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog createDialog(Context context) {
		return createDialog(context, true);
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) customProgressDialog.findViewById(MResource.getIdByName(context, "id",
				"loadingImageView"));
		// AnimationDrawable animationDrawable = (AnimationDrawable)
		// imageView.getBackground();
		// animationDrawable.start();
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
				MResource.getIdByName(context, "anim", "zyqt_progess_round"));
		// 使用ImageView显示动画
		imageView.startAnimation(hyperspaceJumpAnimation);
	}

	/**
	 * 
	 * [Summary] setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 *
	 */
	public CustomProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 *
	 */
	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog.findViewById(MResource.getIdByName(context, "id", "id_tv_loadingmsg"));

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (isBackExit) {
			BaseActivity.allActivity.get(BaseActivity.allActivity.size() - 1).finish();
		}
	}

}
