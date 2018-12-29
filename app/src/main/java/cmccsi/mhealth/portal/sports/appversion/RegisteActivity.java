package cmccsi.mhealth.portal.sports.appversion;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.portal.sports.activity.PreLoadForgetPwdActivity;
import cmccsi.mhealth.portal.sports.activity.PreLoadResetPwdActivity;
import cmccsi.mhealth.portal.sports.activity.WebViewActivity;
import cmccsi.mhealth.portal.sports.basic.AuthUtl;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.common.utils.ToastUtils;
import cmccsi.mhealth.portal.sports.net.TestBase;

public class RegisteActivity extends BaseActivity {
	private String userName;
	private String password;
	private String password2;
	private String telephoneNum;
	private String identifyingCode;
	private EditText userNameView;
	private EditText passwordView;
	private EditText password2View;
	private EditText telephoneNumView;
	private EditText identifyingCodeView;
	private TextView node1;
	private TextView node2;
	private RadioButton radioButton;
	private Button sendIdentifyingCodeView;
	private Button okView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(getApplicationContext(), "layout", "zyqt_activity_registe"));
		initeView();
	}

	private void initeView() {
		ImageButton back = findView(MResource.getIdByName(getApplicationContext(), "id", "button_input_bg_back"));
		back.setVisibility(View.VISIBLE);
		back.setBackground(getResources().getDrawable(MResource.getIdByName(getApplicationContext(), "drawable", "zyqt_my_button_back")));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisteActivity.this.finish();
			}
		});
		TextView title = findView(MResource.getIdByName(getApplicationContext(), "id", "textView_title"));
		title.setText("注册");
		userNameView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_edittext_username"));
		userNameView.setHint(setHintSize("长度为6-18，请以英文字母开头"));
		passwordView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_edittext_password"));
		passwordView.setHint(setHintSize("请输入密码"));
		password2View = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_edittext_password2"));
		password2View.setHint(setHintSize("请再次输入密码"));
		telephoneNumView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_edittext_phonenum"));
		telephoneNumView.setHint(setHintSize("请输入11位手机号"));
		identifyingCodeView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_edittext_identifyingcode"));
		identifyingCodeView.setHint(setHintSize("请输入6位验证码"));
		sendIdentifyingCodeView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_button_sendidentifyingcode"));
		node1 = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_textview_node"));
		node2 = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_textview_node2"));
		radioButton = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_RadioButton"));
		radioButton.setChecked(true);
		node1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				radioButton.setChecked(!radioButton.isChecked());
			}
		});
		node2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url ="file:///android_asset/register.htm";
				Intent intent1 = new Intent();
				intent1.putExtra("UserInfo", url);
	            intent1.putExtra("title", "注册须知");
				intent1.setClass(getApplicationContext(), WebViewActivity.class);
				startActivityForResult(intent1, 300);
			}
		});
		okView = findView(MResource.getIdByName(getApplicationContext(), "id", "registe_button_ok"));
		okView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkUserName() && checkPassWord() && checkIsNull()) {
					if (radioButton.isChecked()) {
						registeUser();
					} else {
						Toast.makeText(RegisteActivity.this, "请阅读《注册须知》，并勾选同意", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		sendIdentifyingCodeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkUserName() && checkPassWord()) {
					telephoneNum = telephoneNumView.getText().toString();
					if (StringUtils.isEmpty(telephoneNum)) {
						Toast.makeText(RegisteActivity.this, "请输入手机号", Toast.LENGTH_LONG).show();
					} else {
						Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0,5-9]))\\d{8}$");
						Matcher m = p.matcher(telephoneNum);
						if (!m.matches()) {
							Toast.makeText(RegisteActivity.this, "手机格式不正确", Toast.LENGTH_LONG).show();
						} else {
							sendPwdMessage();
//							timeHandler.sendEmptyMessage(timeHandlerInt);
						}
					}
				}
			}
		});

	}
	protected void registeUser() {
		new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					// "uid", "password", "phoneNumber", "username","verifyCode"
					HashMap<String, String> arg = new HashMap<String, String>();
					arg.put("uid", userName);
					arg.put("password", TestBase.calculateUserPassword(AuthUtl.HASH_METHOD_SHA, password));
					System.out.println("------加密串--------"+TestBase.calculateUserPassword(AuthUtl.HASH_METHOD_SHA, password));
					arg.put("phoneNumber", telephoneNum);
					arg.put("username", userName);
					arg.put("verifyCode", identifyingCode);
					String json1 = WebServiceManage.post(arg, 5);
					System.out.println("-----注册-----"+json1);
					if (null == json1) {
						timeHandler.sendEmptyMessage(error2);
						return;
					}
					JSONObject jsonObject = new JSONObject(json1);
					String s = jsonObject.getString("status");
					if (s == null || s.equals("FAILURE")) {
						Message message = new Message();
						message.what = error3;
						message.obj = jsonObject.getString("message");
						timeHandler.sendMessage(message);
						return;
					} else if (s.equals("SUCCESS")) {
						finish();
						timeHandler.sendEmptyMessage(error4);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void sendPwdMessage(){
		new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					HashMap<String, String> arg = new HashMap<String, String>();
					arg.put("uid", userName);
					arg.put("phoneNumber", telephoneNum);
					String json1 = WebServiceManage.get(arg, 4);
					System.out.println("-----------发送注册短信验证码+++:" + json1);
					if (null == json1) {
						timeHandler.sendEmptyMessage(error1);
						return;
					}
					JSONObject jsonObject = new JSONObject(json1);
					String s = jsonObject.getString("status");
					if (s == null || "FAILURE".equals(s)) {
						Message message = new Message();
						message.what = error5;
						message.obj = jsonObject.getString("message");
						timeHandler.sendMessage(message);
						return;
					} else if ("SUCCESS".equals(s)) {
						timeHandler.sendEmptyMessage(timeHandlerInt);
					} else {
						timeHandler.sendEmptyMessage(error1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	private boolean checkPassWord() {

		password = passwordView.getText().toString();
		password2 = password2View.getText().toString();
		if (password.length() < 6 || password.length() > 18) {
			Toast.makeText(this, "密码长度为6-18位", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!password.equals(password2)) {
			Toast.makeText(this, "两次密码不相同，请重新输入", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean checkIsNull() {
		telephoneNum = telephoneNumView.getText().toString();
		identifyingCode = identifyingCodeView.getText().toString();
		if (StringUtils.isEmpty(telephoneNum)) {
			Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
			return false;
		} else {
			Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(telephoneNum);
			if (!m.matches()) {
				Toast.makeText(this, "手机格式不正确", Toast.LENGTH_LONG).show();
				return false;
			}
			if (StringUtils.isEmpty(identifyingCode)) {
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
				return false;
			}
			if (identifyingCode.length() != 6) {
				Toast.makeText(this, "请输入6位验证码", Toast.LENGTH_LONG).show();
				return false;
			}
			return true;
		}
	}

	private boolean checkUserName() {
		userName = userNameView.getText().toString().trim();
		if (StringUtils.isEmpty(userName) || userName.length() < 6 || userName.length() > 18) {
			Toast.makeText(this, "用户名长度应在6-18位", Toast.LENGTH_LONG).show();
			return false;
		}
		Pattern p = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
		Matcher m = p.matcher(userName);
		if (!m.matches()) {
			Toast.makeText(this, "用户名只能包含数字字母下划线", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

//	private static final int timeHandlerInt = 10001;
//	private int timeCount = 60;
//	@SuppressLint("HandlerLeak")
//	private Handler timeHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case timeHandlerInt:
//				if (--timeCount > 0) {
//					System.out.println("-----------:" + timeCount);
//					sendIdentifyingCodeView.setText(timeCount + "秒后重试");
//					sendIdentifyingCodeView.setClickable(false);
//					sendIdentifyingCodeView.setBackgroundColor(Color.rgb(193, 222, 131));
//					timeHandler.sendEmptyMessageDelayed(timeHandlerInt, 1000);
//				} else {
//					sendIdentifyingCodeView.setText("获取验证码");
//					sendIdentifyingCodeView.setClickable(true);
//					sendIdentifyingCodeView.setBackgroundColor(Color.rgb(212, 207, 43));
//					timeCount = 60;
//				}
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
	private static final int timeHandlerInt = 10000;
	private static final int error1 = 10001;
	private static final int error2 = 10002;
	private static final int error3 = 10003;
	private static final int error4 = 10004;
	private static final int ok = 10005;
	private static final int error5 = 10006;
	private int timeCount = 60;
	private Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case timeHandlerInt:
				if (--timeCount > 0) {
					sendIdentifyingCodeView.setText(timeCount + "秒后重试");
					sendIdentifyingCodeView.setClickable(false);
					sendIdentifyingCodeView.setBackgroundColor(Color.rgb(193, 222, 131));
					timeHandler.sendEmptyMessageDelayed(timeHandlerInt, 1000);
				} else {
					sendIdentifyingCodeView.setText("获取验证码");
					sendIdentifyingCodeView.setClickable(true);
					sendIdentifyingCodeView.setBackgroundColor(Color.rgb(212, 207, 43));
					timeCount = 60;
				}
				break;
			case error1:
				Toast.makeText(RegisteActivity.this, "获取验证码失败，请稍后再试", Toast.LENGTH_LONG).show();
			case error2:
				ToastUtils.showToast(MapApplication.mContext, MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_INTERNET_ERROR"));
				break;
			case error3:
				if (null == msg.obj) {
					Toast.makeText(RegisteActivity.this, "验证码验证失败", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(RegisteActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			case error4:
				Toast.makeText(RegisteActivity.this, "注册成功", Toast.LENGTH_LONG).show();
				break;
			case error5:
				if (null == msg.obj) {
					Toast.makeText(RegisteActivity.this, "获取验证码错误", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(RegisteActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	private SpannedString setHintSize(String str) {
		SpannableString ss = new SpannableString(str);
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return new SpannedString(ss);

	}
}
