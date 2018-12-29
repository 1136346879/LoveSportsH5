package cmccsi.mhealth.portal.sports.common;

import cmccsi.mhealth.portal.sports.view.CustomProgressDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ShowProgressDialog {
	private static CustomProgressDialog mProgressDialog;

	public static void showProgressDialog(String msg, final Context context) {
		showProgressDialog(msg, context, true);
	}

	public static void showProgressDialog(String msg, final Context context, boolean cancelAble) {
		mProgressDialog = CustomProgressDialog.createDialog(context, cancelAble);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
	}

	/**
	 * 取消 dialog
	 */
	public static void dismiss() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
}
