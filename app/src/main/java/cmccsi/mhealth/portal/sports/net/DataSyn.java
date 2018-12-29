package cmccsi.mhealth.portal.sports.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import cmccsi.mhealth.portal.sports.bean.AcceptFriendRequestInfo;
import cmccsi.mhealth.portal.sports.bean.ActivityDetailData_new;
import cmccsi.mhealth.portal.sports.bean.ActivityDetailMessageInfo;
import cmccsi.mhealth.portal.sports.bean.ActivityInfo;
import cmccsi.mhealth.portal.sports.bean.ActivityMedalInfo;
import cmccsi.mhealth.portal.sports.bean.AreaListInfo;
import cmccsi.mhealth.portal.sports.bean.AuthUser;
import cmccsi.mhealth.portal.sports.bean.BackInfo;
import cmccsi.mhealth.portal.sports.bean.BaseNetItem;
import cmccsi.mhealth.portal.sports.bean.CampaignRankListInfo;
import cmccsi.mhealth.portal.sports.bean.ClubData;
import cmccsi.mhealth.portal.sports.bean.ClubListInfo;
import cmccsi.mhealth.portal.sports.bean.ContactListInfo;
import cmccsi.mhealth.portal.sports.bean.ContectData;
import cmccsi.mhealth.portal.sports.bean.ContectGroupData;
import cmccsi.mhealth.portal.sports.bean.ContectGroupInfo;
import cmccsi.mhealth.portal.sports.bean.ContectInfo;
import cmccsi.mhealth.portal.sports.bean.DetailGPSData;
import cmccsi.mhealth.portal.sports.bean.DeviceInfo;
import cmccsi.mhealth.portal.sports.bean.DeviceListInfo;
import cmccsi.mhealth.portal.sports.bean.DeviceTypeInfo;
import cmccsi.mhealth.portal.sports.bean.DeviceTypeListInfo;
import cmccsi.mhealth.portal.sports.bean.ECGListinfo;
import cmccsi.mhealth.portal.sports.bean.FindFriendInfo;
import cmccsi.mhealth.portal.sports.bean.FriendPedometorSummary;
import cmccsi.mhealth.portal.sports.bean.FriendsInfo;
import cmccsi.mhealth.portal.sports.bean.GPSListInfo;
import cmccsi.mhealth.portal.sports.bean.GoalNetInfo;
import cmccsi.mhealth.portal.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.portal.sports.bean.GroupIdInfo;
import cmccsi.mhealth.portal.sports.bean.GroupMemberPkInfo;
import cmccsi.mhealth.portal.sports.bean.GroupPkInfo;
import cmccsi.mhealth.portal.sports.bean.GroupRankUpdateVersion;
import cmccsi.mhealth.portal.sports.bean.JsonResult;
import cmccsi.mhealth.portal.sports.bean.ListGPSData;
import cmccsi.mhealth.portal.sports.bean.LoginInfo;
import cmccsi.mhealth.portal.sports.bean.MultAddFriendsBackInfo;
import cmccsi.mhealth.portal.sports.bean.OrgnizeMemberPKInfo;
import cmccsi.mhealth.portal.sports.bean.OrgnizeMemberSum;
import cmccsi.mhealth.portal.sports.bean.OrgnizememSeq;
import cmccsi.mhealth.portal.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.portal.sports.bean.PedoRankListInfo;
import cmccsi.mhealth.portal.sports.bean.PedometorInfo;
import cmccsi.mhealth.portal.sports.bean.PedometorListInfo;
import cmccsi.mhealth.portal.sports.bean.RaceInfo;
import cmccsi.mhealth.portal.sports.bean.RaceMemberInfo;
import cmccsi.mhealth.portal.sports.bean.RankingDate;
import cmccsi.mhealth.portal.sports.bean.RequestListInfo;
import cmccsi.mhealth.portal.sports.bean.Response;
import cmccsi.mhealth.portal.sports.bean.SaveAreaInfo;
import cmccsi.mhealth.portal.sports.bean.SaveDeviceToken;
import cmccsi.mhealth.portal.sports.bean.ServersInfo;
import cmccsi.mhealth.portal.sports.bean.TempCodeInfo;
import cmccsi.mhealth.portal.sports.bean.UpdatePasswordInfo;
import cmccsi.mhealth.portal.sports.bean.UpdateVersionJson;
import cmccsi.mhealth.portal.sports.bean.UserCompanyInfo;
import cmccsi.mhealth.portal.sports.bean.UserRegInfo;
import cmccsi.mhealth.portal.sports.bean.VitalSignInfo;
import cmccsi.mhealth.portal.sports.bean.VitalSignInfoDataBean;
import cmccsi.mhealth.portal.sports.bean.VitalSignUploadState;
import cmccsi.mhealth.portal.sports.bean.WeightInfo;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.common.Config;
import cmccsi.mhealth.portal.sports.common.Constants;
import cmccsi.mhealth.portal.sports.common.HttpUtils;
import cmccsi.mhealth.portal.sports.common.Logger;
import cmccsi.mhealth.portal.sports.common.PreferencesUtils;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class DataSyn {
	public static String TAG = "DataSyn";
	public static String strHttpURL;
	public static String avatarHttpURL;

	public static String contactFileUrl;
	public static String contactGroupFileUrl;
	JsonResult<AuthUser> jsonResult;
	// gengqi 达人url前缀
	public static String strAccountHttpURL;

	// public static String strHttpURL =
	// "http://phr.cmri.cn/sport/openClientApi.do?action=";
	// public static String strHttpURL =
	// "http://218.206.179.71/jk/openClientApi.do?action=";
	// public static String strHttpURL =
	// "http://218.206.179.193/CmccPhr/openClientApi.do?action=";

	private static DataSyn instance;
	private HttpClient mHttpClient;
	private String muserid = null;
	public String muserUid = null;
	private String getclubname;
	private int getclubid;

	// private ThreadPoolManager threadPoolManager;

	private DataSyn() {
		/*
		 * SchemeRegistry schemeRegistry = new SchemeRegistry();
		 * PoolingClientConnectionManager cm = new
		 * PoolingClientConnectionManager(schemeRegistry); // Increase max total
		 * connection to 200 cm.setMaxTotal(200); // Increase default max
		 * connection per route to 20 cm.setDefaultMaxPerRoute(20); // Increase
		 * max connections for localhost:80 to 50 HttpHost localhost = new
		 * HttpHost("locahost", 80); cm.setMaxPerRoute(new HttpRoute(localhost),
		 * 50);
		 */

		/*
		 * HttpParams params = new BasicHttpParams(); HttpProtocolParamBean
		 * paramsBean = new HttpProtocolParamBean(params);
		 * paramsBean.setVersion(HttpVersion.HTTP_1_1);
		 * paramsBean.setContentCharset("UTF-8");
		 * paramsBean.setUseExpectContinue(true);
		 */
		// threadPoolManager = ThreadPoolManager.getInstance();
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			// 设置8秒请求超时
			mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		}

	}

	// public void getRunable(String url,BaseNetItem bni){
	// BaseTask baseTask = new BaseTask(url,bni);
	// threadPoolManager.addTask(baseTask);
	// }
	//
	// class BaseTask implements Runnable{
	//
	// private String mUrl;
	// public BaseNetItem baseNetItem;
	//
	// public BaseTask(String queryStr, BaseNetItem reqData){
	// this.mUrl = queryStr;
	// this.baseNetItem = reqData;
	// }
	//
	// @Override
	// public void run() {
	// if(getDataFromNet(mUrl,baseNetItem) == 0){
	//
	// }
	// }
	// }

	public static void setStrHttpURL(String strHttpURL) {
		DataSyn.strHttpURL = strHttpURL;
		// TODO 测试
	}

	public static void setAvatarHttpURL(String avatarHttpURL) {
		DataSyn.avatarHttpURL = avatarHttpURL;
		// TODO 测试
	}

	public static void setstrAccountHttpURL(String strAccountHttpURL) {
		DataSyn.strAccountHttpURL = strAccountHttpURL;
		// TODO 测试
	}

	public synchronized static DataSyn getInstance() {
		if (null == instance) {
			instance = new DataSyn();
			strHttpURL = "http://" + Config.SERVER_NAME + "openClientApi.do?action=";
			avatarHttpURL = "http://" + Config.SERVER_NAME + "UserAvatar/";
			strAccountHttpURL = "Http://" + Config.SERVER_NAME + "account.do?action=";
		}

		return instance;
	}

	// g
	private HttpEntity httpClientExecuteGet(String queryStr) {
		Logger.i(TAG, queryStr);
		HttpGet httpget = new HttpGet(queryStr);
		HttpEntity entity = null;

		try {
			HttpResponse response = mHttpClient.execute(httpget);
			entity = response.getEntity();
		} catch (UnknownHostException unknown) {
			Logger.e(TAG, "超时");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			httpget.abort();
		}

		return entity;
	}

	public int getDataFromNet(String queryStr, BaseNetItem reqData, InputStream instream) {

		// if (!queryStr.startsWith(Config.SERVER_DESTINY)) {
		// if (queryStr.split("action=").length == 1) {
		// Logger.e(TAG, "URL is NULL");
		// return -1;
		// }
		// }
		// synchronized (mHttpClient) {
		// HttpEntity entity = httpClientExecuteGet(queryStr);
		//
		// if (null == entity) {
		// Logger.e(TAG, "entity is null");
		// return -1;
		// }
		//
		// InputStream instream = null;
		try {
			// instream = entity.getContent();
			if (null == instream) {
				Logger.e(TAG, "instream is null");
				return -1;
			}
			Logger.i("gengqi", instream.toString() + "xxxxxx");
			// *****************************************
			BufferedReader read = new BufferedReader(new InputStreamReader(instream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = read.readLine()) != null) {
					sb.append(line + "/n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			Log.e("yd", queryStr + "  data :" + sb.toString());
			InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

			// *************************************************************
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			// reader.beginObject();
			Gson gson = new Gson();
			BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
			// reader.endObject();

			reader.close();

			if (null == rawData) {
				Logger.e(TAG, "rawData error");
				return 1;
			} else {
				// Logger.e(TAG, "status:" + rawData.status + "\tReason:" +
				// rawData.Reason);
				if ("FAILURE".equals(rawData.status) && "No User Or Password is Worng".equals(rawData.reason)) {
					return 500;
				}
			}

			// if (!rawData.isValueData(rawData)) {
			// Logger.e(TAG, "valueData is null");
			// return 1;
			// }

			reqData.setValue(rawData);

			// if (!rawData.status.equals("SUCCESS"))
			// return 1;

			Logger.d(TAG, rawData.toString());

			reqData.initialDate();

		} catch (JsonSyntaxException ex) {
			ex.printStackTrace();
			return 1;
		} catch (IOException ex) {
			// In case of an IOException the connection will be released
			// back to the connection manager automatically
			// throw ex;
			ex.printStackTrace();
			return -1;
		} finally {
			// Closing the input stream will trigger connection release
			try {
				instream.close();
			} catch (Exception ignore) {
				ignore.printStackTrace();
				return -1;
			}
		}
		// }
		return 0;
	}

	public int getDataFromNet(String queryStr, BaseNetItem reqData) {

		if (!queryStr.startsWith(Config.SERVER_DESTINY)) {
			if (queryStr.split("action=").length == 1) {
				Logger.e(TAG, "URL is NULL");
				return -1;
			}
		}
		synchronized (mHttpClient) {
			HttpEntity entity = httpClientExecuteGet(queryStr);

			if (null == entity) {
				Logger.e(TAG, "entity is null");
				return -1;
			}

			InputStream instream = null;
			try {
				instream = entity.getContent();
				if (null == instream) {
					Logger.e(TAG, "instream is null");
					return -1;
				}
				Logger.i("gengqi", instream.toString() + "xxxxxx");

				// ******************************
				BufferedReader read = new BufferedReader(new InputStreamReader(instream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = read.readLine()) != null) {
					sb.append(line + "/n");
				}
				Log.e("yd", queryStr + "  +数据  " + sb.toString());
				InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
				// ******************************
				JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
				// reader.beginObject();
				Gson gson = new Gson();
				BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
				// reader.endObject();

				reader.close();

				if (null == rawData) {
					Logger.e(TAG, "rawData error");
					return 1;
				} else {
					Logger.d(TAG, rawData.toString());
					reqData.initialDate();
					reqData.setValue(rawData);
					if ("FAILURE".equals(rawData.status) ) {
						if("No User Or Password is Worng".equals(rawData.reason)){
							return 500;
						}
						else{
							return -2;
						}
					}
				}
				// if (!rawData.isValueData(rawData)) {
				// Logger.e(TAG, "valueData is null");
				// return 1;
				// }
				// if (!rawData.status.equals("SUCCESS"))
				// return 1;
				Logger.d(TAG, rawData.toString());
				reqData.initialDate();
				reqData.setValue(rawData);

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				Log.i("gengqi", "json解析异常");
				return 1;
			} catch (IOException ex) {
				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				// throw ex;
				ex.printStackTrace();
				return -1;
			} catch (NullPointerException ex) {
				ex.printStackTrace();
				return -1;
			} finally {
				// Closing the input stream will trigger connection release
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}

	public int sendTempCode(String phoneNum, TempCodeInfo resultreqData, Context context) {
		TempCodeInfo reqData = new TempCodeInfo();
		String strinner = "http://" + Config.SERVER_NAME + "openClientApi.do?action=sendTempCode&userid=" + phoneNum;

		int dataFromNet = getDataFromNet(strinner, reqData);
		Logger.d(TAG, "reqData.status == " + reqData.status);
		if (dataFromNet == 0 && "SUCCESS".equals(reqData.status)) {
			reqData.selectserver = Config.SERVER_NAME;

			servernamesavetosp(context, 0);
			return dataFromNet;
		}
		if ("对同一手机号码每天最多请求3次短信验证码".equals(reqData.result)) {
			return 5;
		}
		return -1;
	}

	/**
	 * function: company: CMCC-CMRI
	 * 
	 * @author Gaofei 2013-9-3 下午4:57:54 version 1.0 describe: 获取群组更新版本号
	 * @param phoneNum
	 * @param Pwd
	 * @param reqData
	 * @return
	 */
	public int getGroupRankUpdateVersion(String phoneNum, String Pwd, int ClubId, GroupRankUpdateVersion reqData) {
		String str = strHttpURL
		// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getPrepareDataVersion&userid=" + muserid
				// + "&psw=" + Pwd
				+ "&clubid=" + getclubid;
		return getDataFromNet(str, reqData);
	}

	public int passwordReset(String phoneNum, String tempCode, String newPwd, UpdatePasswordInfo reqData) {
		String str = strHttpURL + "passwordreset&userid=" + phoneNum + "&tempcode=" + tempCode + "&new_psw=" + newPwd;
		return getDataFromNet(str, reqData);
	}

	public int verifyTempCode(String phoneNum, String tempCode, String newPwd, UpdatePasswordInfo reqData, String selectedserver) {
		String str;
		if (selectedserver == null || "".equals(selectedserver)) {
			str = strHttpURL + "passwordreset&userid=" + phoneNum + "&tempcode=" + tempCode;
		} else {
			str = "http://" + selectedserver + "openClientApi.do?action=passwordreset&userid=" + phoneNum + "&tempcode="
					+ tempCode;
			Logger.i(TAG, str);
		}
		if ("".equals(strHttpURL)) {
			strHttpURL = "http://" + selectedserver + "openClientApi.do?action=";
			avatarHttpURL = "http://" + selectedserver + "UserAvatar/";
			strAccountHttpURL = "Http://" + Config.SERVER_NAME + "account.do?action=";
		}
		return getDataFromNet(str, reqData);
	}

	public ArrayList<ContectData> getContactList(Context context, String PhoneNum, String Password, int clubid) {
		return getContactList(context, clubid);
	}

	private ArrayList<ContectData> getContactList(Context context, int clubid) {
		ContectInfo ci = new ContectInfo();
		String queryStr = strHttpURL + "getclubmember&userid=" + muserid
		// + "&psw=" + Password
				+ "&clubid=" + getclubid;
		getDataFromNet(queryStr, ci);
		if (ci.datavalue.size() > 0) {
			return ci.datavalue;
		}
		return null;
	}

	public ArrayList<ContectGroupData> getContactGroupList(Context context, String PhoneNum, String Password, int clubid) {
		return getContactGroupList(context, clubid);
	}

	private ArrayList<ContectGroupData> getContactGroupList(Context context, int clubid) {
		ContectGroupInfo cgi = new ContectGroupInfo();
		String queryStr = strHttpURL + "getclubgroup&userid=" + muserid
		// + "&psw=" + Password
				+ "&clubid=" + getclubid;
		getDataFromNet(queryStr, cgi);
		if (cgi.datavalue.size() > 0) {
			return cgi.datavalue;
		}
		return null;
	}

	/**
	 * gengqi 保存服务器信息的方法，此方法很重要
	 * 
	 * 设置服务器地址
	 * */
	private void servernamesavetosp(Context context, Integer userId) {

		PreferencesUtils.putString(context, SharedPreferredKey.SERVER_NAME, Config.SERVER_NAME);
		Log.i("gengqi", "servernamesavetosp" + userId + "String.valueOf(userId):" + Config.SERVER_NAME);
		PreferencesUtils.putString(context, SharedPreferredKey.USERID, String.valueOf(userId));
		strHttpURL = "http://" + Config.SERVER_NAME + "openClientApi.do?action=";
		avatarHttpURL = "http://" + Config.SERVER_NAME + "UserAvatar/";
		strAccountHttpURL = "Http://" + Config.SERVER_NAME + "account.do?action=";
	}

	/**
	 * 获取muserid
	 * 
	 * @return
	 */
	public String forSetting() {
		return muserid;

	}

	/**
	 * 修改密码
	 * 
	 * @param phoneNum
	 * @param oldPassword
	 * @param newPassword
	 * @param newPasswordAg
	 * @param reqData
	 * @return int 0/1
	 */
	public int updatePassWord(String phoneNum, String oldPassword, String newPassword, String newPasswordAg,
			UpdatePasswordInfo reqData) {
		// 创建GET方法的实例
		String str = strHttpURL + "passwordchange&userid=" + phoneNum // 13810411683
				+ "&old_psw=" + oldPassword + "&new_psw=" + newPassword + "&new_psw_ag=" + newPasswordAg;

		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取版本信息（独立版）
	 * TODO
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午1:57:45
	 */
	public int updateVersion(UpdateVersionJson reqData) {
		String str = strHttpURL + "updateClient" + "&type=" + "android";
		return getDataFromNet(str, reqData);
	}
	/**
	 * 获取设备类型列表（独立版）
	 * TODO
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午2:05:51
	 */
	public int getDeviceTypeList(DeviceTypeListInfo reqData){
		String str = strHttpURL + "getDeviceTypeList";
		return getDataFromNet(str, reqData);
		
//		reqData.datavalue.add(new DeviceTypeInfo("SMARTPHONE_BT", "BeatBand手环",
//				"http://health.10086.cn/portal/pub/icon_device/SMARTPHONE_BT.png", "爱动力",
//				"监测用户的运动数据，包括步数、运动时长、卡路里等; 监测用户心率等健康数据", "1", "BeatBand"));
//		reqData.datavalue.add(new DeviceTypeInfo("SMARTPHONE_BT_LS_IW-", "丁当手环",
//				"http://health.10086.cn/portal/pub/icon_device/SMARTPHONE_BT_LS_IW-106.png", "爱动力",
//				"监测用户的运动数据，包括步数、运动时长、卡路里等; 监测用户心率等健康数据", "1", "DD"));
//		reqData.datavalue.add(new DeviceTypeInfo("SMARTPHONE_DEVICE", "智能手机设备",
//				"http://health.10086.cn/portal/pub/icon_device/SMARTPHONE_DEVICE.png", "爱动力",
//				"监测用户的运动数据，包括步数、运动时长、卡路里等; 监测用户心率等健康数据", "0", ""));
//		reqData.datavalue.add(new DeviceTypeInfo("WS-JBQ-001", "爱动力计步器",
//				"http://health.10086.cn/portal/pub/icon_device/WS-JBQ-001.png", "爱动力",
//				"监测用户的运动数据，包括步数、运动时长、卡路里等; 监测用户心率等健康数据", "0", ""));
//
//		return 0;
	}
	/**
	 * 添加新计步设备（独立版）
	 * TODO
	 * @param deviceSerial 设备ID
	 * @param productPara 设备类型
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午2:09:45
	 */
	public int addDevice(String deviceSerial, String productPara, BackInfo reqData){
		String str = strHttpURL + "addDevice&userid=" + muserUid + "&deviceSerial=" + deviceSerial + "&productPara=" + productPara;
		return getDataFromNet(str, reqData);
	}
	/**
	 * 删除计步设备（独立版）
	 * TODO
	 * @param deviceSerial
	 * @param productPara
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午2:11:22
	 */
	public int deleteDevice(String deviceSerial, String productPara, BackInfo reqData){
		String str = strHttpURL + "deleteDevice&userid=" + muserUid + "&deviceSerial=" + deviceSerial + "&productPara=" + productPara;
		return getDataFromNet(str, reqData);
	}

	public String getUserIfoByUid() {
		String str = strHttpURL + "getuserinfobyuid&uid=" + muserUid;// 13810411683
		return str;
	}

	/**
	 * 获取计步器上传数据
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	public int getPedoInfo(String PhoneNum, String Password, String date, PedometorInfo reqData) {
		if (date != null) {
			if (date.length() < 8) {
				date = "20130122";
			}
		} else {
			date = "20130122";
		}

		// String str = strHttpURL + "queryUploadDetail&userid=" + PhoneNum//
		// 13810411683
		// String str = strHttpURL + "pedometer&userid=" + PhoneNum//
		// 13810411683
		// + "&psw=" + Password// wxf
		// + "&date=" + date;// 20121002
		String str = strHttpURL + "pedometer&userid=" + muserUid// 13810411683
				+ "&psw=" + Password// wxf
				+ "&date=" + date;

		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取计步器上传数据(时间区域)
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param startTime
	 *            起始时间
	 * @param endTime
	 *            截止时间
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	public int getPedoInfoByTimeArea(String PhoneNum, String Password, String startTime, String endTime, PedometorListInfo reqData) {

		// String str = strHttpURL + "pedometer_multi&userid=" + muserUid//
		// 13810411683
		String str = strHttpURL + "pedometer_multi&userid=" + muserUid// 13810411683
				+ "&psw=" + Password// wxf
				+ "&startTime=" + startTime + "&endTime=" + endTime;

		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取计步器详细数据
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	public int getPedoInfoDetail(String PhoneNum, String Password, String fromHour, String toHour, String date,
			PedoDetailInfo reqData) {
		if (date != null) {
			if (date.length() < 8) {
				date = "20130122";
			}
		} else {
			date = "20130122";
		}
		// gengqi old
		// String str = strHttpURL + "pedalldata&userid=" + PhoneNum//
		// 13810411683
		// + "&psw=" + Password// wxf
		// + "&fromHour=" + fromHour + "&toHour=" + toHour + "&date=" + date;//
		// 20121002

		String str = strHttpURL + "pedalldata&userid=" + muserUid// 13810411683
				+ "&psw=" + Password// wxf
				+ "&fromHour=" + fromHour + "&toHour=" + toHour + "&date=" + date;
		return getDataFromNet(str, reqData);
	}

	/**
	 * getOrgnizeMembersPkInfoBySpan(获取某天公司内全部成员信息--通过班组ID获取成员基本信息)
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param reqData
	 *            返回的班组内成员基本信息
	 * @param @return 设定文件
	 * @return -1 网络错误， 1 json数据错误， 2 输入日期格式错误， 0 正确结果
	 */
	// public int getOrgnizeMembersPkInfoBySpan(String PhoneNum, String
	// Password,
	// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData,
	// String strStartDate, String strEndDate) {
	//
	// // 组织查询字符串
	// String str = strHttpURL + "pedorgnizememberbyspan&userid=" + PhoneNum
	// + "&psw=" + Password + "&startseqnum=" + startseqnum
	// + "&endseqnum=" + endseqnum + "&startdate=";
	//
	// try {
	// str += Common.Formatyyyy_MM_dd(strStartDate) + "&enddate="
	// + Common.Formatyyyy_MM_dd(strEndDate);
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "日期解析出错");
	// return 2;
	// }
	// return getDataFromNet(str, reqData);
	// }

	public int getOrgnizeMembersPkInfoYestoday(String PhoneNum, String Password, int ClubId, int startseqnum, int endseqnum,
			OrgnizeMemberPKInfo reqData, String strStartDate, String strEndDate) {

		return getOrgnizeMembersPkInfoYestoday(startseqnum, endseqnum, reqData);
	}

	public int getOrgnizeMembersPkInfoYestoday(int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData) {
		// 组织查询字符串
		String str = strHttpURL
		// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getuserreadydata&userid=" + muserid
				// + "&psw=" + Password
				+ "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid=" + getclubid;
		return getDataFromNet(str, reqData);
	}

	public int getOrgnizeMembersPkInfo7Day(String PhoneNum, String Password, int ClubId, int startseqnum, int endseqnum,
			OrgnizeMemberPKInfo reqData) {
		// public int getOrgnizeMembersPkInfo7Day(String PhoneNum, String
		// Password,
		// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData,
		// String strStartDate, String strEndDate) {

		// 组织查询字符串
		String str = strHttpURL
		// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getuserreadydata&userid=" + muserid
				// + "&psw=" + Password
				+ "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid=" + getclubid;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取公司内全部成员信息--通过班组ID获取成员基本信息
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param reqData
	 *            返回的班组内成员基本信息
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	// public int getOrgnizeMembersPkInfo(String PhoneNum, String Password,
	// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData) {
	// // 创建GET方法的实例
	// String str = strHttpURL + "pedorgnizemember&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&startseqnum=" + startseqnum
	// + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * 获取特定班组的成员信息--通过班组ID获取成员基本信息
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param groupid
	 *            需要访问的班组ID
	 * @param reqData
	 *            返回的班组内成员基本信息
	 * @return -1 网络错误， 1 数据错误， 0 正确结果 by gaofei
	 */
	// public int getGroupMembersPkInfoBySpan(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// SimpleDateFormat df_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	// SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	//
	// String str = "";
	// try {
	// Date dateTmp = df_yyyyMMdd.parse(strStartDate);
	// String strStartDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// dateTmp = df_yyyyMMdd.parse(strEndDate);
	// String strEndDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// // 创建GET方法的实例
	// str = strHttpURL
	// + "pedgroupmemberbyspan&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid
	// + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum
	// + "&startdate=" + strStartDateV2 + "&enddate="
	// + strEndDateV2;
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "日期解析出错");
	// return 2;
	// }
	//
	// return getDataFromNet(str, reqData);
	// }

	public int getGroupMembersPkInfoYestoday(String PhoneNum, String Password, int ClubId, String groupid, int startseqnum,
			int endseqnum, GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
		// 创建GET方法的实例
		String str = strHttpURL
				// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getuseringroupreadydata&userid="
				+ muserid // 13810411683
				// + "&psw=" + Password
				+ "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid="
				+ getclubid;
		return getDataFromNet(str, reqData);
	}

	public int getGroupMembersPkInfo7Day(String PhoneNum, String Password, int ClubId, String groupid, int startseqnum,
			int endseqnum, GroupMemberPkInfo reqData) {
		// public int getGroupMembersPkInfo7Day(String PhoneNum, String
		// Password,
		// String groupid, int startseqnum, int endseqnum,
		// GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
		// 创建GET方法的实例
		String str = strHttpURL
				// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getuseringroupreadydata&userid="
				+ muserid // 13810411683
				// + "&psw=" + Password
				+ "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid="
				+ getclubid;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取特定班组的成员信息--通过班组ID获取成员基本信息
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param groupid
	 *            需要访问的班组ID
	 * @param reqData
	 *            返回的班组内成员基本信息
	 * @return -1 网络错误， 1 数据错误， 0 正确结果 by gaofei
	 */
	// public int getGroupMembersPkInfo(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupMemberPkInfo reqData) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// // 创建GET方法的实例
	// String str = strHttpURL + "pedgroupmember&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid + "&startseqnum="
	// + startseqnum + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * 获取比赛中的各个班组信息--通过自身所在班组ID获取比赛中的各个班组基本信息
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param groupid
	 *            访问者所在的班组ID
	 * @param reqData
	 *            返回比赛中的其他班组基本信息
	 * @return -1 网络错误， 1 数据错误， 0 正确结果 by gaofei
	 */
	// public int getGroupPkInfoBySpan(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupPkInfo reqData, String strStartDate, String strEndDate) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	// // 构造HttpClient的实例
	// String str = "";
	// try {
	//
	// SimpleDateFormat df_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	// SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	//
	// Date dateTmp = df_yyyyMMdd.parse(strStartDate);
	// String strStartDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// dateTmp = df_yyyyMMdd.parse(strEndDate);
	// String strEndDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// // 创建GET方法的实例
	// str = strHttpURL
	// + "pedgrouppkbyspan&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid
	// + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum
	// + "&startdate=" + strStartDateV2 + "&enddate="
	// + strEndDateV2;
	//
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "日期解析出错");
	// return 2;
	// }
	//
	// return getDataFromNet(str, reqData);
	// }

	public int getGroupPkInfoYestoday(String PhoneNum, String Password, int ClubId, String groupid, int startseqnum,
			int endseqnum, GroupPkInfo reqData, String strStartDate, String strEndDate) {
		String str = strHttpURL
				// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getgroupreadydata&userid="
				+ muserid // 13810411683
				// + "&psw=" + Password
				+ "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid="
				+ getclubid;

		// Logger.e(TAG, str);
		return getDataFromNet(str, reqData);

	}

	public int getGroupPkInfo7(String PhoneNum, String Password, int ClubId, String groupid, int startseqnum, int endseqnum,
			GroupPkInfo reqData) {
		String str = strHttpURL
				// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "getgroupreadydata&userid="
				+ muserid // 13810411683
				// + "&psw=" + Password
				+ "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid="
				+ getclubid;

		// Logger.e(TAG, str);

		return getDataFromNet(str, reqData);
	}

	/**
	 * 通过班组ID获取班组排名基本信息
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param groupid
	 * @param startseqnum
	 * @param endseqnum
	 * @param reqData
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	// public int getGroupPkInfo(String PhoneNum, String Password, String
	// groupid,
	// int startseqnum, int endseqnum, GroupPkInfo reqData) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// // 创建GET方法的实例
	// String str = strHttpURL + "pedgrouppk&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid + "&startseqnum="
	// + startseqnum + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * 获取所在的班组ID--通过自身账号获取所在的班组ID
	 * 
	 * @param PhoneNum
	 *            访问名
	 * @param Password
	 *            访问密码
	 * @param reqData
	 *            返回所在的班组ID
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	public int getGroupIdInfo(String PhoneNum, String Password, int ClubId, GroupIdInfo reqData) {
		// PhoneNum = "13811029472";
		// Password = "123456";
		// 创建GET方法的实例
		String str = strHttpURL
		// "http://10.111.120.103:8080/data_new/openClientApi.do?action="
				+ "pedgroupid&userid=" + muserid // 13810411683
				// + "&psw=" + Password
				+ "&clubid=" + getclubid;

		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取所有成员排名的总条数
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return
	 */
	public int getPedorgnizeMemberSum(String PhoneNum, String Password, int ClubId, OrgnizeMemberSum reqData) {
		// 创建GET方法的实例
		String str = strHttpURL
		// "http://kk/data_new/openClientApi.do?action="
				+ "pedorgnizemembersum&userid=" + muserid // 13810411683
				// + "&psw=" + Password
				+ "&clubid=" + getclubid;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取某个范围内个人排名的名次
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return 0为正常
	 */
	// public int getPedOrgnizeMemberSeqBySpan(String PhoneNum, String Password,
	// OrgnizememSeq reqData, String strStartDate, String strEndDate) {
	// // 创建GET方法的实例
	// String str = strHttpURL + "pedorgnizememberseq&userid=" + PhoneNum //
	// 13810411683
	// + "&psw=" + Password;
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * 获取个人排名的名次
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return 0为正常
	 */
	public int getPedOrgnizeMemberSeq(String PhoneNum, String Password, OrgnizememSeq reqData) {
		// 创建GET方法的实例
		String str = strHttpURL + "pedorgnizememberseq&userid=" + muserid // 13810411683
				+ "&psw=" + Password;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取活动列表
	 * 
	 * @param phoneNum
	 *            访问名
	 * @param password
	 *            访问密码
	 * @param reqData
	 * 
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 */
	public int getActivityInfo(String phoneNum, String password, int ClubId, ActivityInfo reqData) {
		// PhoneNum = "13811029472";
		// Password = "232304";
		// 创建GET方法的实例
		String str = strHttpURL + "activityinfo&userid=" + muserid // 13810411683
				+ "&psw=" + password + "&clubid=" + ClubId;
		Log.e("yd", "获取活动列表 :" + str);
		return getDataFromNet(str, reqData);
	}
	
	/**
	 * 获取活动列表
	 * TODO
	 * @param type 活动类型0：已结束1：进行中 2：敬请期待 3：所有
	 * @param orgId 用户企业id
	 * @param page 页码（默认一页10条数据）
	 * @param reqData
	 * @return
	 * @return int
	 * @author jiazhi.cao
	 * @time 下午2:42:34
	 */
	public int getActivityInfo_new(int type, int orgId, int page, ActivityInfo reqData) {
		// PhoneNum = "13811029472";
		// Password = "232304";
		// 创建GET方法的实例
		String str = strHttpURL + "activityinfo_new&uid=" + muserUid // 13810411683
				+ "&type=" + type + "&orgId=" + orgId + "&page=" + page;
		Log.e("yd", "获取活动列表 :" + str);
		return getDataFromNet(str, reqData);
	}

	public int getActivityDetailMessage(String phoneNum, String password, String activityid, ActivityDetailMessageInfo reqData) {
		// String str =
		// strHttpURL+"activitydetail&userid=13552019273&psw=123456&activityid=6";
		// String str =
		// "http://phr.cmri.cn/datav2/openClientApi.do?action=activitydetail&userid=13552019273&psw=123456&activityid=6";
		String str = strHttpURL + "activitydetail&userid=" + phoneNum // 13810411683
				+ "&psw=" + password + "&activityid=" + activityid;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 
	 * 获取特定活动中的奖牌信息
	 * 
	 * @param PhoneNum
	 *            ,Password,GroupMemberSum
	 * @return int 0为正常
	 */
	public int getAvtivityMedalInfo(String userId, String userNm, String PhoneNum, String Password, int ClubId, ActivityMedalInfo reqData,
			String activityID, String page) {
		// 创建GET方法的实例
		String str = strHttpURL + "memberactivityrank&userid=" + userId // 13810411683
				+ "&activityid=" + activityID + "&clubid=" + ClubId + "&page=" + page;
		Log.e("yd", "获取特定活动中的奖牌信息 :" + str);
		return getDataFromNet(str, reqData);
	}
	
	/**
	 * 
	 * 获取特定活动中的信息
	 * 
	 * @param PhoneNum
	 *            ,Password,GroupMemberSum
	 * @return int 0为正常
	 */
	public int getAvtivityDetailInfo(String userId,String activityID, ActivityDetailData_new reqData) {
		// 创建GET方法的实例
		String str = strHttpURL + "getActivityRank&userId=" + userId // 13810411683
				+ "&activityId=" + activityID ;
		
		return getDataFromNet(str, reqData);
	}

	/**
	 * 
	 * getPedGroupSeq(这里用一句话描述这个方法的作用) 获取名次 (这里描述这个方法适用条件 – 可选)
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param ClubId
	 * @param reqData
	 * @return int
	 * @exception
	 * @since 1.0.0
	 * @author Gao
	 * @addtime 2014-11-18 下午3:00:13
	 */
	public int getPedGroupSeq(String PhoneNum, String Password, int ClubId, RankingDate reqData) {
		return getPedGroupSeq(reqData);
	}

	public int getPedGroupSeq(RankingDate reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "pedgroupseq&userid=" + muserid // 13810411683
				// + "&psw=" + Password
				+ "&clubid=" + getclubid;
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 上传体重数据并获取最新体重数据
	 */
	public int getLastestWeightInfo(String PhoneNum, String Password, String weight, double height, int agecode, int gender,
			String changetime, WeightInfo reqData) {
		String queryStr = strHttpURL + "personprofile&userid=" + PhoneNum + "&psw=" + Password + "&weight=" + weight + "&height="
				+ height + "&agecode=" + agecode + "&gender=" + gender + "&changetime=" + changetime;
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 获取服务器最新体重数据
	 */
	public int getLastestWeightInfoWithOutParams(String PhoneNum, String Password, WeightInfo reqData) {
		String queryStr = strHttpURL + "personprofile&userid=" + PhoneNum + "&psw=" + Password;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 得到目标对象的排名信息
	 */
	public int getSeqsBySearchNumber(String PhoneNum, String Password, int ClubId, String targetNumber, RankingDate reqData) {
		String queryStr = strHttpURL + "pedgroupseq&userid=" + PhoneNum + "&psw=" + Password + "&targetnumber=" + targetNumber
				+ "&clubid=" + getclubid;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 得到目标群组的排名信息
	 */
	public int getGroupSeqsBySearchNumber(String PhoneNum, String Password, int ClubId, String targetNumber, RankingDate reqData) {
		String queryStr = strHttpURL + "pedtargetgroupseq&userid=" + PhoneNum + "&psw=" + Password + "&targetgroupid="
				+ targetNumber + "&clubid=" + getclubid;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 获取服务器上，上次手机端下拉时间之后的所有数据
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param lastDownloadSuccessTime
	 *            long值时间参数
	 * @param reqData
	 * @return
	 */
	public int getVitalSignAfterLastDownloadSuccessTime(String PhoneNum, String Password, long lastDownloadSuccessTime,
			VitalSignInfo reqData) {
		String queryStr = strHttpURL
				// "http://10.111.111.111:8080/data_new/openClientApi.do?action="
				+ "downloadvitalsign&userid=" + muserUid + "&nativetime=" + Common.getDateFromLongToStr(new Date().getTime())
				+ "&newesdownload=" + Common.getDateFromLongToStr(lastDownloadSuccessTime);
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 获取服务器上，上次手机端下拉时间之后的所有数据
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param lastDownloadSuccessTime
	 *            String值时间 格式为 yyyy-MM-dd_HH:mm:ss
	 * @param reqData
	 * @return
	 */
	public int getVitalSignAfterLastDownloadSuccessTime(String PhoneNum, String Password, String lastDownloadSuccessTime,
			VitalSignInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.111:8080/data_new/openClientApi.do?action="
				+ "downloadvitalsign&userid=" + muserUid + "&starttime=" + lastDownloadSuccessTime;
		Log.i("gengqi", "starttime=" + lastDownloadSuccessTime);
		Log.i("gengqi", "downloadvitalsign-userId=" + PhoneNum);
		return getDataFromNet(queryStr, reqData);
		// return 1;
	}

	/**
	 * 上传距上次上传成功时间之后的生理数据
	 */
	public int postVitalSign(String PhoneNum, String Password, List<VitalSignInfoDataBean> listofweight,
			VitalSignUploadState reqData, String datetypeStr) {
		StringBuilder sb = new StringBuilder();
		if (listofweight == null || listofweight.size() == 0) {
			return 10;
		}
		for (VitalSignInfoDataBean vitalSignBean : listofweight) {
			// sb.append("@" + datetypeStr + "#" +
			// vitalSignBean.getMeasureDate()
			// + "#"
			// + Common.getDateFromLongToStr(vitalSignBean.getEditTime())
			// + "#" + vitalSignBean.getValue());
			sb.append(vitalSignBean.getValue());
		}
		String vitalstring = sb.substring(0);

		String queryStr = strHttpURL
		// "http://10.111.111.111:8080/data_new/openClientApi.do?action="
				+ "uploadvitalsign";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("userid", muserUid));
		list.add(new BasicNameValuePair("vitalsignarray", vitalstring));
		Logger.d("VitalSignMetaData", vitalstring);
		return postDataFromNet(queryStr, reqData, list);
	}

	// =======
	// 好友的接口
	// ========
	// 删除好友
	public int deleteFriend(String PhoneNum, String Password, String friendid, AcceptFriendRequestInfo reqData) {
		return deleteFriend(friendid, reqData);
	}

	public int deleteFriend(String friendid, AcceptFriendRequestInfo reqData) {
		String queryStr = strHttpURL + "deletefriend&userid=" + muserid + "&friendid=" + friendid;// +
																									// "&psw="
																									// +
																									// Password
		return getDataFromNet(queryStr, reqData);
	}

	// 同意好友请求
	public int acceptRequest(String PhoneNum, String Password, String friendid, BackInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "acceptfriend&userid=" + muserid + "&friendid=" + friendid;
		Log.e("yd", "同意好友请求  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// 获得好友请求列表
	public int getFriendRequestList(String PhoneNum, String Password, RequestListInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "getfriendrequestlist&userid=" + muserid;// +
															// "&psw="
															// +
															// Password;
		Log.e("yd", "获得好友请求列表  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// 获得好友列表
	public int getFriendsList(String PhoneNum, String Password, FriendsInfo reqData) {
		return getFriendsList(reqData);
	}

	public int getFriendsList(FriendsInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "getfriendslist&userid=" + muserid + "&clubid=" + getclubid;
		// + "&psw=" + Password;
		Log.e("yd", "获得好友列表  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// 获取特定好友的简报信息
	public int getFriendInfo(String PhoneNum, String Password, String friendid, FriendPedometorSummary reqData) {
		return getFriendInfo(friendid, reqData);
	}

	public int getFriendInfo(String friendid, FriendPedometorSummary reqData) {
		String queryStr = strHttpURL + "getfriendsinfo&userid=" + muserid + "&friendid=" + friendid; // +
																										// "&psw="
																										// +
		// Password
		// ;
		Log.e("yd", "获取特定好友的简报信息  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// 查找好友
	public int findFriendById(String PhoneNum, String Password, String friendid, FindFriendInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "findfriendbyid&userid=" + muserid + "&friendid=" + friendid;
		Log.e("yd", "查找好友  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// 添加好友

	public int addFriendById(String PhoneNum, String Password, String friendid, BackInfo reqData) {
		return addFriendById(friendid, reqData);
	}

	public int addFriendById(String friendid, BackInfo reqData) {
		String queryStr = strHttpURL
		// "http://10.111.111.131:8080/data_new/openClientApi.do?action="
				+ "addfriendbyid&userid=" + muserid + "&friendid=" + friendid;
		Log.e("yd", "添加好友  :" + queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	// ========
	// 获取公司列表
	public int getClubList(Context context, String PhoneNum, String Password, ClubListInfo reqData) {
		String queryStr = strHttpURL + "getclublist&userid=" + muserUid + "&psw=" + Password;
		int i = getDataFromNet(queryStr, reqData);
		if (reqData.clublist.size() > 0 && i == 0) {
			getclubid = reqData.clublist.get(0).getClubid();
			getclubname = reqData.clublist.get(0).getClubname();
			PreferencesUtils.putInt(context, SharedPreferredKey.CLUB_ID, getclubid);
			Log.i("gengqi", "getclubid:" + getclubid);
			Log.i("gengqi", "getclubname:" + getclubname);
		}
		Log.e(TAG + " +yd", queryStr);
		return i;
	}

	private int getClubList(String userUid, ClubListInfo reqData) {
		String queryStr = strHttpURL + "getclublist&userid=" + userUid;
		int i = getDataFromNet(queryStr, reqData);
		if (reqData.clublist.size() > 0 && i == 0) {
			getclubid = reqData.clublist.get(0).getClubid();
			getclubname = reqData.clublist.get(0).getClubname();
		}

		Logger.d(TAG, queryStr);
		return i;
	}

	// ========
	// ========

	// 同意比赛邀请
	public int acceptRaceInvitingRequest(String PhoneNum, String Password, String raceid, String teamname, BackInfo reqData) {
		String queryStr = strHttpURL + "acceptrace&userid=" + muserid + "&raceid=" + raceid + "&teamname=" + teamname;
		return getDataFromNet(queryStr, reqData);
	}

	// 获得比赛请求列表
	public int getRaceInvitedRequestList(String PhoneNum, String Password, RequestListInfo reqData) {
		String queryStr = strHttpURL + "getracerequestlist&userid=" + muserid;
		// + "&psw=" + Password;
		return getDataFromNet(queryStr, reqData);
	}

	// 获取比赛列表
	public int getRaceList(String PhoneNum, String Password, int state, int num, int startid, RaceInfo reqData) {
		String queryStr = strHttpURL + "getracelist&userid=" + muserid
		// + "&psw=" + Password
				+ "&state=" + state + "&num=" + num + "&startid=" + startid;
		return getDataFromNet(queryStr, reqData);
	}

	// 获取比赛队伍排名列表
	public int getRaceInfo(String PhoneNum, String Password, String raceid, RaceMemberInfo reqData) {
		String queryStr = strHttpURL + "getraceinfo&userid=" + muserid
		// + "&psw=" + Password
				+ "&raceid=" + raceid;
		return getDataFromNet(queryStr, reqData);
	}

	// 获取比赛队伍排名列表
	public int getSearchedRace(String PhoneNum, String Password, String keyword, int type, RaceInfo reqData) {
		String queryStr = strHttpURL + "searchrace";
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		listvp.add(new BasicNameValuePair("userid", muserid));
		// listvp.add(new BasicNameValuePair("psw", Password));
		listvp.add(new BasicNameValuePair("type", type + ""));
		listvp.add(new BasicNameValuePair("keyword", keyword));
		return postDataFromNet(queryStr, reqData, listvp);
	}

	// 创建比赛
	public int createRace(String PhoneNum, String Password, List<NameValuePair> listvp, BackInfo reqData) {
		String queryStr = strHttpURL + "createrace";
		Log.e("yd", "创建比赛 " + muserid);
		listvp.add(new BasicNameValuePair("userid", muserid));
		// listvp.add(new BasicNameValuePair("psw", Password));
		Log.e("yd", "listvp   " + listvp.toString());
		return postDataFromNet(queryStr, reqData, listvp);
	}

	// 参加比赛
	public int joinRace(String PhoneNum, String Password, String raceid, BackInfo reqData, String team) {
		String queryStr = strHttpURL + "joinrace&userid=" + muserid
		// + "&psw="+ Password
				+ "&raceid=" + raceid + "&teamname=" + team;
		return getDataFromNet(queryStr, reqData);
	}

	// 退出比赛
	public int resignRace(String PhoneNum, String Password, String raceid, BackInfo reqData) {
		String queryStr = strHttpURL + "resignrace&userid=" + muserid
		// + "&psw=" + Password
				+ "&raceid=" + raceid;
		return getDataFromNet(queryStr, reqData);
	}

	// 剔除成员
	public int kickRaceMember(String PhoneNum, String Password, String raceid, String memberid, BackInfo reqData) {
		String queryStr = strHttpURL + "kickracemember&userid=" + muserid
		// + "&psw=" + Password
				+ "&raceid=" + raceid + "&memberid=" + memberid;
		return getDataFromNet(queryStr, reqData);
	}

	// 邀请成员
	public int inviteRaceMember(String PhoneNum, String Password, String raceid, String memberid, BackInfo reqData) {
		String queryStr = strHttpURL + "inviterace&userid=" + muserid
		// + "&psw=" + Password
				+ "&raceid=" + raceid + "&memberid=" + memberid;
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 上传成就类型
	 * 
	 * @param goalType
	 *            成就类型0-5
	 * @param reqData
	 * @return
	 */
	public int uploadGoalType(String goalType, BackInfo reqData) {
		String queryStr = strHttpURL + "setusergoal&useruid=" + muserUid
		// + "&psw=" + Password
				+ "&goal=" + goalType;
		return getDataFromNet(queryStr, reqData);
	}

	// ========

	/**
	 * 一开始获取User个人信息
	 * 
	 * @return
	 */
	public int getUserInfo(String PhoneNum, String Password, UserRegInfo info) {
		String queryStr = // "http://10.111.111.107:8080/data_new/openClientApi.do?action=getuserinfobyuid&uid=wangbaofeng";
		strHttpURL + "getuserinfobyuid&uid=" + muserUid;
		// gengqi String queryStr =
		// "http://111.11.29.83:8099/data_new/openClientApi.do?action=" +
		// "getpersonprofile&userid=" + PhoneNum + "&psw=" + Password;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, info);
	}

	private int getUserInfoWithUid(UserRegInfo info) {
		String queryStr = strHttpURL + "getuserinfobyuid&uid=" + muserUid;
		// String queryStr =
		// "http://112.33.1.160:81/CmccPhr/openClientApi.do?action=" +
		// "getuserinfobyuid&uid="
		// + muserUid;

		return getDataFromNet(queryStr, info);
	}

	/**
	 * 运动轨迹数据上传
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param gpsListInfo
	 * @param gpsInfoDetails
	 * @return
	 */
	public int UpLoadMapData(String PhoneNum, String Password, GPSListInfo gpsListInfo, List<GpsInfoDetail> gpsInfoDetails) {
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		String queryStr = strHttpURL + "uploadtrackevent";
		listvp.add(new BasicNameValuePair("userid", muserid));
		listvp.add(new BasicNameValuePair("phone", PhoneNum));
		listvp.add(new BasicNameValuePair("psw", "111"));
		listvp.add(new BasicNameValuePair("starttime", gpsListInfo.getStarttime()));
		listvp.add(new BasicNameValuePair("stepNum", gpsListInfo.getStepNum()));
		listvp.add(new BasicNameValuePair("distance", gpsListInfo.getDistance() + ""));
		listvp.add(new BasicNameValuePair("sporttype", gpsListInfo.getSporttype() + ""));
		listvp.add(new BasicNameValuePair("duration", gpsListInfo.getDuration()));
		listvp.add(new BasicNameValuePair("cal", gpsListInfo.getCal() + ""));
		listvp.add(new BasicNameValuePair("speed", gpsListInfo.getSpeed() + ""));
		listvp.add(new BasicNameValuePair("durationperkm", gpsListInfo.getDurationperkm()));
		listvp.add(new BasicNameValuePair("speedmax", gpsListInfo.getSpeedmax()));
		listvp.add(new BasicNameValuePair("speedmin", gpsListInfo.getSpeedmin()));
		listvp.add(new BasicNameValuePair("climbsum", gpsListInfo.getClimbsum()));
		StringBuilder sb = new StringBuilder();
		for (GpsInfoDetail gpsInfoDetail : gpsInfoDetails) {
			sb.append("@" + gpsInfoDetail.toString());
		}
		if ("".equals(sb) || 0 == sb.length()) {
			return -1;
		} else {
			listvp.add(new BasicNameValuePair("gpstrackarray", sb.substring(1)));
			Logger.i(TAG, sb.substring(1));
		}
		Logger.i("cjz", "运动轨迹数据上传:" + listvp.toString());
		return postDataFromNet(queryStr, gpsListInfo, listvp);
	}

	/**
	 * 获取历史轨迹摘要
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param gpsData
	 * @param endtime
	 * @param recordnum
	 *            末尾时间往前推N条
	 * @return
	 */
	public int getListGpsData(ListGPSData gpsData, String endtime, String recordnum, Context context) {
		String PhoneNum = PreferencesUtils.getPhoneNum(context);
		String Password = PreferencesUtils.getPhonePwd(context);
		endtime = endtime.replaceAll(" ", "%20");
		String queryStr = strHttpURL + "gettrackevent&userid=" + muserid + "&psw=" + Password + "&endtime=" + endtime
				+ "&recordnum=" + recordnum;
		return getDataFromNet(queryStr, gpsData);
	}
	
	/**
	 * 获取历史轨迹摘要
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param gpsData
	 * @param endtime
	 * @param recordnum
	 *            末尾时间往前推N条
	 * @return
	 */
	public int getListGpsData(ListGPSData gpsData, int page) {
		String queryStr = strHttpURL + "gettrackevent_new&userid=" + muserid + "&page=" + page ;
		return getDataFromNet(queryStr, gpsData);
	}

	/**
	 * 获取成就数据
	 * 
	 * @param startTime
	 *            格式为2014-10-01
	 * @param endTime
	 *            格式为2014-10-01
	 * @param goalInfo
	 * @return
	 */
	public int getGoalData(String startTime, String endTime, GoalNetInfo goalInfo) {
		String str = strHttpURL + "getusergoal&useruid=" + muserUid // 13810411683
				+ "&starttime=" + startTime + "&endtime=" + endTime;
		return getDataFromNet(str, goalInfo);
	}

	/**
	 * 获取历史轨迹详细
	 * 
	 * @param gpsInfoDetail
	 * @param starttime
	 * @param context
	 * @return
	 */
	public int getDetailsGpsData(DetailGPSData gpsInfoDetail, String starttime, Context context) {
		String PhoneNum = PreferencesUtils.getPhoneNum(context);
		String Password = PreferencesUtils.getPhonePwd(context);
		starttime = starttime.replaceAll(" ", "%20");
		String queryStr = strHttpURL + "gettrackeventdetail&userid=" + PhoneNum + "&psw=" + Password + "&starttime=" + starttime;
		return getDataFromNet(queryStr, gpsInfoDetail);
	}

	/**
	 * 获取绑定的设备列表
	 * 
	 * @param listInfo
	 * @return
	 */
	public int getDeviceListData(DeviceListInfo listInfo) {
		String queryStr = strHttpURL + "getUserIDeviceList&userid=" + muserUid;
		return getDataFromNet(queryStr, listInfo);
	}

	/**
	 * 获取心境数据
	 * 
	 * @param listinfo
	 * @param user
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getECGListData(ECGListinfo listinfo, String startTime, String endTime, String userUid) {
		String queryStrToday = strHttpURL + "getHrinfo&userid=" + userUid + "&starttime=" + startTime + "&endtime=" + endTime
				+ "&timetype=" + "today";
		String queryStrSevenday = strHttpURL + "getHrinfo&userid=" + userUid + "&starttime=" + startTime + "&endtime=" + endTime
				+ "&timetype=" + "sevenday";
		String queryStrOnemonth = strHttpURL + "getHrinfo&userid=" + userUid + "&starttime=" + startTime + "&endtime=" + endTime
				+ "&timetype=" + "onemonth";
		String queryStrSixmonth = strHttpURL + "getHrinfo&userid=" + userUid + "&starttime=" + startTime + "&endtime=" + endTime
				+ "&timetype=" + "sixmonth";
		int Today = getDataFromNet(queryStrToday, listinfo);
		int Sevenday = getDataFromNet(queryStrSevenday, listinfo);
		int Onemonth = getDataFromNet(queryStrOnemonth, listinfo);
		int Sixmonth = getDataFromNet(queryStrSixmonth, listinfo);
		if (Today == 0 && Sevenday == 0 && Onemonth == 0 && Sixmonth == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 绑定设备
	 * 
	 * @param oldDevice
	 * @param newDevice
	 * @param backInfo
	 * @return
	 */
	public int uploadBindDeviceInfo(DeviceInfo oldDevice, DeviceInfo newDevice, BackInfo backInfo) {
		String queryStr = strHttpURL + "bindUserDevice&userid=" + muserUid + "&deviceSerialOld=" + oldDevice.deviceSerial
				+ "&productParaOld=" + oldDevice.productPara + "&deviceSerial=" + newDevice.deviceSerial + "&productPara="
				+ newDevice.productPara;
		return getDataFromNet(queryStr, backInfo);
	}

	public int uploadBindBtDevice(String address, boolean change, Response backInfo) {
		// String pre
		// ="http://10.110.110.100/data_new/openClientApi.do?action=";
		String queryStr = strHttpURL + "bindUserBtDevice&userid=" + muserUid + "&deviceSerial=01" + address + "&isUsed="
				+ (change ? "1" : "0");
		int rst = getDataFromNet(queryStr, backInfo);
		return rst;
	}

	public int postDataFromNet(String queryStr, BaseNetItem reqData, List<NameValuePair> list) {
		synchronized (mHttpClient) {

			HttpEntity entity = httpClientExecutePost(queryStr, list);

			if (null == entity) {
				Logger.e(TAG, "entity is null");
				return -1;
			}

			InputStream instream = null;
			try {
				instream = entity.getContent();
				// JsonReader reader = new JsonReader(new InputStreamReader(
				// instream, "UTF-8"));
				// log
				if (null == instream) {
					Logger.e(TAG, "instream is null");
					return -1;
				}
				// *****************************************
				BufferedReader read = new BufferedReader(new InputStreamReader(instream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				try {
					while ((line = read.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				Log.e("yd", queryStr + "  data :" + sb.toString());

				Gson gson = new Gson();
				BaseNetItem rawData = gson.fromJson(sb.toString(), reqData.getClass());
				// gson.fromJson(reader, reqData.getClass());
				// reader.endObject();
				// reader.close();

				if (null == rawData) {
					Logger.e(TAG, "rawData error");
					return 1;
				}
				Logger.e(TAG, rawData.reason + "rawData.reason");
				if (!rawData.isValueData(rawData)) {
					Logger.e(TAG, "valueData is null");
					return 1;
				}

				reqData.setValue(rawData);

				reqData.initialDate();

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				return 1;
			} catch (IOException ex) {
				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				// throw ex;
				ex.printStackTrace();
				return -1;
			} finally {
				// Closing the input stream will trigger connection release
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}

	private HttpEntity httpClientExecutePost(String queryStr, List<NameValuePair> list) {
		Logger.i(TAG, queryStr);
		HttpEntity httpEntity = null;
		HttpPost httpPost = new HttpPost(queryStr);
		try {
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(list, "UTF-8");

			httpPost.setEntity(requestHttpEntity);
			// Log.e("yd", "创建比赛 ： " + httpPost);
			HttpResponse response = mHttpClient.execute(httpPost);
			httpEntity = response.getEntity();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			// In case of an unexpected exception you may want to abort
			// the HTTP request in order to shut down the underlying
			// connection immediately.
			ex.printStackTrace();
			httpPost.abort();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return httpEntity;
	}

	/**
	 * 获取服务器列表
	 * 
	 * @param userUid
	 * @return int
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-18 下午5:30:20
	 */
	public void loadServerInfo(Context context) {
		servernamesavetosp(context, 0);
	}

	/**
	 * 设置userid
	 * 
	 * @param userId
	 *            void
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-18 下午5:38:13
	 */
	public void setUserId(String userId) {
		muserid = userId;
	}

	/**
	 * 设置userUid
	 * 
	 * @param userUid
	 *            void
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-21 下午2:42:15
	 */
	public void setUserUid(String userUid) {
		muserUid = userUid;
	}

	public void setClubid(int clubid) {
		getclubid = clubid;
	}

	/**
	 * 加载clubId
	 * 
	 * @param userId
	 * @param context
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-19 上午9:53:26
	 */
	public void loadClubId(Context context) {

		Editor editorShare = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();

		/* 登录成功并且首次安装则清楚数据保存登录信息 */
		SharedPreferences info = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String strServerIP = info.getString(SharedPreferredKey.SERVER_NAME, null);
		String strServerVersion = info.getString(SharedPreferredKey.SERVER_VERSION, "2");
		loadContect(strServerVersion, context); // 更新班组联系人数据库
		// editorShare.clear();
		editorShare.putString(SharedPreferredKey.SERVER_NAME, strServerIP);
		editorShare.putString(SharedPreferredKey.SERVER_VERSION, strServerVersion);
		editorShare.commit();
	}

	private void loadContect(final String strServerVersion, final Context context) {
		new Thread() {
			@Override
			public void run() {
				ClubListInfo clireqData = new ClubListInfo();
				int suc = 0;
				if ("2".equals(strServerVersion)) {
					suc = getClubList(muserUid, clireqData);
				} else {
					suc = 0;
					clireqData.clublist.add(new ClubData(0, "公司"));
				}

			}
		}.start();
	}

	/**
	 * 加载用户数据
	 * 
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-26 上午11:03:43
	 */
	public void loadUserInfo(Context context) {
		final SharedPreferences sp = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		new Thread() {
			@Override
			public void run() {
				String queryStr = strHttpURL + "getuserinfobyuid&uid=" + muserUid;
				UserRegInfo userRegInfo = new UserRegInfo();
				Logger.i(TAG, queryStr);
				int res = DataSyn.getInstance().getUserInfoWithUid(userRegInfo);
				if (res == 0) {
					restoreUserInfo(sp, userRegInfo);
				}
			}

		}.start();

	}

	/**
	 * restoreUserInfo 存储用户个人数据
	 * 
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-12-2 下午3:17:13
	 */
	public void restoreUserInfo(SharedPreferences sp, final UserRegInfo userRegInfo) {
		Editor editor = sp.edit();
		try {

			muserid = userRegInfo.personprofile.userid + "";
			editor.putString(SharedPreferredKey.USERID, muserid);
			editor.putString(SharedPreferredKey.USERUID, muserUid);
			editor.putString(SharedPreferredKey.NAME, userRegInfo.personprofile.name);
			editor.putString(SharedPreferredKey.NICK_NAME, userRegInfo.personprofile.nickname);
			editor.putString(SharedPreferredKey.WEIGHT, userRegInfo.personprofile.weight);
			editor.putString(SharedPreferredKey.HEIGHT, userRegInfo.personprofile.height);
			editor.putString(SharedPreferredKey.GENDER, userRegInfo.personprofile.gender);
			editor.putString(SharedPreferredKey.BIRTHDAY, userRegInfo.personprofile.birthday);
			editor.putString(SharedPreferredKey.SCORE, userRegInfo.personprofile.score);
			editor.putString(SharedPreferredKey.AVATAR, userRegInfo.personprofile.avarta);
			editor.putString(SharedPreferredKey.TARGET_WEIGHT, userRegInfo.personprofile.targetweight);
			editor.putString(SharedPreferredKey.TARGET_STEP, userRegInfo.personprofile.targetstep);
			editor.putString(SharedPreferredKey.PHONENUM, userRegInfo.personprofile.phonenum);
			editor.putInt(SharedPreferredKey.CLUB_ID, userRegInfo.personprofile.clubarray.get(0).clubid);
			editor.putInt(SharedPreferredKey.ORG_ID, userRegInfo.personprofile.clubarray.get(0).orgid);
			if (userRegInfo.personprofile.locationInfo != null) {
				editor.putInt(SharedPreferredKey.COUNTY_ID, userRegInfo.personprofile.locationInfo.countyId);
				editor.putInt(SharedPreferredKey.CITY_ID, userRegInfo.personprofile.locationInfo.cityId);
				editor.putInt(SharedPreferredKey.PROVINCE_ID, userRegInfo.personprofile.locationInfo.provinceId);
				editor.putString(SharedPreferredKey.COUNTY_NAME, userRegInfo.personprofile.locationInfo.countyName);
				editor.putString(SharedPreferredKey.CITY_NAME, userRegInfo.personprofile.locationInfo.cityName);
				editor.putString(SharedPreferredKey.PROVINCE_NAME, userRegInfo.personprofile.locationInfo.provinceName);
			}
			// Logger.i(TAG, userRegInfo.personprofile.clubarray.size() +
			// " ==");
			int size = userRegInfo.personprofile.clubarray.size();
			for (int i = 0; i < size; i++) {
				if (i < 2) {
					UserCompanyInfo userCompanyInfo = userRegInfo.personprofile.clubarray.get(i);
					restoreCorpInfo(editor, userCompanyInfo);
				}
			}
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
			editor.commit();
		}
	}

	/**
	 * restoreCorpInfo存储公司信息 void
	 * 
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-12-2 下午3:39:23
	 */
	public void restoreCorpInfo(Editor editor, UserCompanyInfo userCompanyInfo) {
		editor.putString(SharedPreferredKey.GROUP_NAME, userCompanyInfo.groupname);
		editor.putString(SharedPreferredKey.CORPORATION, userCompanyInfo.corporation);
		editor.putInt(SharedPreferredKey.CLUB_ID, (userCompanyInfo.clubid == 0 ? Constants.DEFAULT_CLUBID
				: userCompanyInfo.clubid));
	};

	/**
	 * 加载用户数据
	 * 
	 * @exception
	 * @since 1.0.0
	 * @author Xiao
	 * @addtime 2014-11-26 上午11:03:43
	 */
	public void loadUserInfoNotInThread(Context context) {
		final SharedPreferences sp = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);

		String queryStr = strHttpURL + "getuserinfobyuid&uid=" + muserUid;
		// String queryStr =
		// "http://112.33.1.160:81/CmccPhr/openClientApi.do?action=" +
		// "getuserinfobyuid&uid=" + muserUid;
		UserRegInfo userRegInfo = new UserRegInfo();
		Logger.i(TAG, queryStr);
		int res = DataSyn.getInstance().getUserInfoWithUid(userRegInfo);
		if (res == 0) {
			restoreUserInfo(sp, userRegInfo);
		}

	}

	/**
	 * 获取地区排名信息列表
	 * 
	 * TODO
	 * 
	 * @param userId
	 *            当前用户ID
	 * @param orgId
	 *            地区Id，只传最后一级县的Id，不传省市
	 * @param dayCount
	 *            传“1”或“7”，表示获取1天或7天排名
	 * @param type
	 *            传“user”或“group”，表示获取个人排名或团队排名
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 上午11:19:21
	 */
	public int getPedoRankListInfo(String userId, String orgId, String dayCount, String type, int rankGroup,
			PedoRankListInfo reqData, String page) {

		String str;
		if (rankGroup == 0) {
			str = strHttpURL + "getAreaRank&userId=" + userId + "&orgId=" + orgId + "&dayCount=" + dayCount + "&type=" + type + "&page=" + page;
		} else {
			str = strHttpURL + "getCompanyRank&userId=" + userId + "&orgId=" + orgId + "&dayCount=" + dayCount + "&type=" + type + "&page=" + page;
		}

		return getDataFromNet(str, reqData);
	}
	
	/**
	 * 获取地区排名信息列表
	 * 
	 * TODO
	 * 
	 * @param userId
	 *            当前用户ID
	 * @param orgId
	 *            地区Id，只传最后一级县的Id，不传省市
	 * @param dayCount
	 *            传“1”或“7”，表示获取1天或7天排名
	 * @param type
	 *            传“user”或“group”，表示获取个人排名或团队排名
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 上午11:19:21
	 */
	public int getPedoRankListInfo(String userId, String orgId, String dayCount, String type, int rankGroup,
			PedoRankListInfo reqData) {

		String str;
		if (rankGroup == 0) {
			str = strHttpURL + "getAreaRank&userId=" + userId + "&orgId=" + orgId + "&dayCount=" + dayCount + "&type=" + type ;
		} else {
			str = strHttpURL + "getCompanyRank&userId=" + userId + "&orgId=" + orgId + "&dayCount=" + dayCount + "&type=" + type ;
		}

		return getDataFromNet(str, reqData);
	}

	/**
	 * 获取指定区域内所有子地区信息
	 * 
	 * @param orgId
	 *            为空时查询所有省信息
	 * @return -1 网络错误， 1 数据错误， 0 正确结果
	 * 
	 */
	public int getAreaInfo(String orgId, AreaListInfo reqData) {

		// String str =
		// "http://112.33.1.160:81/CmccPhr/openClientApi.do?action=" +
		// "queryArea&orgId=" + orgId;
		String str = strHttpURL + "queryArea&orgId=" + orgId;

		return getDataFromNet(str, reqData);
	}

	/**
	 * 保存区域信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param orgId
	 *            最后一级的区域ID
	 * @param odlOrgId
	 *            当前用户最后一级的ID（没有传空值""）
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 上午9:31:49
	 */
	public int saveAreaInfo(String userId, String orgId, String oldOrgId, SaveAreaInfo reqData) {

		if (oldOrgId.equals("1") || oldOrgId.equals("0")) {
			oldOrgId = "";
		}
		String queryStr = strHttpURL + "saveArea_new&userId=" + userId + "&orgId=" + orgId + "&oldOrgId=" + oldOrgId;

		return getDataFromNet(queryStr, reqData);
	}
	
	public int saveTargetStep(String targetStep, SaveAreaInfo reqData) {

		String queryStr = strHttpURL + "settargetvalue&userid=" + muserid + "&value=" + targetStep + "&type=target_step";

		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * 保存蓝牙设备令牌
	 * 
	 * @param userId
	 *            用户ID
	 * @param deviceId
	 *            蓝牙设备的设备ID
	 * @param deviceNumber
	 *            蓝牙设备的SN
	 * @param deviceToken
	 *            蓝牙设备的令牌
	 * @param deviceVersion
	 *            蓝牙设备的版本号
	 * @param reqData
	 * @return
	 * @return int
	 * @author yuqi.lin
	 * @time 2015-04-14
	 */
	public int saveDeviceToken(String userId, String deviceId, String deviceNumber, String deviceToken, String deviceVersion,
			SaveDeviceToken reqData) {

		String str = strHttpURL + "saveDeviceToken&userId=" + userId + "&deviceId=" + deviceId + "&deviceNumber=" + deviceNumber
				+ "&deviceToken=" + deviceToken + "&deviceVersion=" + deviceVersion;

		return getDataFromNet(str, reqData);
	}
	
	/**
	 * 
	 * TODO 	对比联系人
	 * 
	 * @param userid
	 * @param phonenumbers
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午3:15:58
	 */
	public int checkPhonenumbers(String userid,String phonenumbers, ContactListInfo reqData) {

		String queryStr = strHttpURL + "checkPhonenumbers";
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		listvp.add(new BasicNameValuePair("userid", userid));
		listvp.add(new BasicNameValuePair("phonenumbers", phonenumbers));

		return postDataFromNet(queryStr, reqData, listvp);
	}
	/**
	 *  批量添加好友
	 * TODO
	 * @param userid
	 * @param ids
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午2:11:57
	 */
	public int addFriendsByPhonenumbers(String userid,String ids, MultAddFriendsBackInfo reqData) {

		String str = strHttpURL + "addFriendsByPhonenumbers&userid=" + userid + "&ids=" + ids;

		return getDataFromNet(str, reqData);
	}
	/**
     * 登陆验证 gengqi 耿琦
     * 
     * @param phone
     *            登陆的手机号
     * @param password
     *            登陆的密码
     * @param data
     *            登陆的时间
     * @return 0 登陆成功;-1 异常;1 数据错误 2 账号或密码错误 3未激活 4账号错误 5密码错误 6获取服务器列表失败
     * @throws Exception
     * @throws IOException
     */
    public int loginAuth(String phoneNum, String password, Context context) {
        long after_day = new Date().getTime() + (1000L * 60 * 60 * 24); // 后移一天
        String date = Common.getDateAsYYYYMMDD(after_day);
        SharedPreferences info = context.getSharedPreferences(
                SharedPreferredKey.SHARED_NAME, 0);
        String selectedserver = info.getString(SharedPreferredKey.SERVER_NAME,
                "");

        if ("".equals(selectedserver)) {

            BasicNameValuePair value0 = new BasicNameValuePair("appKey", "YDJK");
            BasicNameValuePair value1 = new BasicNameValuePair("invokeKey",
                    "[YDJK-S]0MBrtpyk0z");
            BasicNameValuePair value2 = new BasicNameValuePair("salt",
                    TestBase.salt);
            BasicNameValuePair value3 = new BasicNameValuePair("loginName",
                    phoneNum);
            BasicNameValuePair value4 = new BasicNameValuePair("loginPwd",
                    TestBase.getPassWord(password));
//            String strinner = "http://jiankang.10086.cn/healthcare_service_restful/s/auth/verifyLogin";
//            String strinner = "http://112.33.1.160:81/healthcare_service_restful/s/auth/verifyLogin";
            String strinner = Config.SERVER_NAME_LOGIN;
            String result = HttpUtils.postByHttpClient(
                    strinner, value0, value1, value2, value3, value4);

            JSONObject jsonObject = JSON.parseObject(result);
            String jsonObjectStr = jsonObject.getString("jsonResult");
            System.out.println(jsonObjectStr);
            jsonResult = JSON.parseObject(jsonObjectStr,
                    new TypeReference<JsonResult<AuthUser>>() {
                    });
            if (jsonResult.getResultCode().equals("0")) {
                muserid = jsonResult.getData().getUserId().toString();
                muserUid = jsonResult.getData().getUserUid();
                Log.i("gengqi", "getUserId" + jsonResult.getData().getUserId());
                Log.i("gengqi", "getUserUId:" + muserUid);
                Editor edit = info.edit();
				edit.putString(SharedPreferredKey.USERUID, muserUid);
				edit.commit();
                Log.i("gengqi", "登陆请求接口" + strinner);

                Log.i("gengqi", jsonResult.getResultCode() + "getResultCode");

                servernamesavetosp(info, null, jsonResult.getData()
                        .getUserId());
                loadUserInfo(context);
                return 0;
            } else if (jsonResult.getResultCode().equals("1")) {
                return 7;
            }
            return -1;
        } else {
            LoginInfo reqDataElse = new LoginInfo();
            // TODO 测试
            strHttpURL = "http://" + selectedserver
                    + "openClientApi.do?action=";
            avatarHttpURL = "http://" + selectedserver + "UserAvatar/";
            String strinner = strHttpURL + "pedometer&userid=" + phoneNum
                    + "& psw=" + password + "&date=" + date;
            int result = getDataFromNet(strinner, reqDataElse);
            if (reqDataElse.status.equals("FAILURE")
                    && reqDataElse.reason.equals("NOACCOUNT OR ERRPASSWORD"))
                result = 2;
            if (reqDataElse.status.equals("FAILURE")
                    && reqDataElse.reason.equals("NOACTIVITY"))
                result = 3;
            if (reqDataElse.status.equals("FAILURE")
                    && reqDataElse.reason.equals("Worng User")) {
                result = 7;
            }
            Logger.i(TAG, "using old server -- > " + selectedserver);
            return result;
        }
    }
    /**
     * gengqi 保存服务器信息的方法，此方法很重要
     * */
    private void servernamesavetosp(SharedPreferences info,
            ServersInfo serversInfo, Integer userId) {
        servernamesavetosp(info, userId);
    }

    private void servernamesavetosp(SharedPreferences info, Integer userId) {
        Editor edit = info.edit();
        edit.putString(SharedPreferredKey.SERVER_NAME,
                Config.SERVER_NAME);
        Log.i("gengqi", "servernamesavetosp" + userId
                + "String.valueOf(userId):" + Config.SERVER_NAME);
        edit.putString("userId", String.valueOf(userId));
        edit.commit();
        strHttpURL = "http://" + Config.SERVER_NAME
                + "openClientApi.do?action=";
        avatarHttpURL = "http://" + Config.SERVER_NAME + "UserAvatar/";
        strAccountHttpURL = "Http://" + Config.SERVER_NAME
                + "account.do?action=";
    }
    
	/**
	 * 分页获取活动排名信息
	 * TODO
	 * @param activityId
	 * @param page
	 * @param level
	 * @param reqData
	 * @return
	 * @return int
	 * @author shaoting.chen
	 * @time 下午3:23:51
	 */
	public int getActivityUserRankByLevel(String activityId, String page, String level, CampaignRankListInfo reqData) {

		String str = strHttpURL + "getActivityUserRankByLevel&userId=" + muserUid + "&activityId=" + activityId + "&page=" + page + "&level=" + level;

		return getDataFromNet(str, reqData);
	}
}
