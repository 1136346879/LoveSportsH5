package cmccsi.mhealth.portal.sports.activity;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import cmccsi.mhealth.portal.sports.adapter.GPSXListViewAdapter;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseFragment;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.GPSListBean;
import cmccsi.mhealth.portal.sports.bean.GPSListInfo;
import cmccsi.mhealth.portal.sports.bean.ListGPSData;
import cmccsi.mhealth.portal.sports.common.Logger;
import cmccsi.mhealth.portal.sports.common.ShowProgressDialog;
import cmccsi.mhealth.portal.sports.common.utils.ToastUtils;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.net.NetworkTool;
import cmccsi.mhealth.portal.sports.tabhost.MapFragment;
import cmccsi.mhealth.portal.sports.tabhost.TabBaseFragment;
import cmccsi.mhealth.portal.sports.view.CommonAskDialog;
import cmccsi.mhealth.portal.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.portal.sports.view.XListView;
import cmccsi.mhealth.portal.sports.view.XListView.IXListViewListener;

public class MapListGPSFragment extends BaseFragment implements OnItemClickListener
	,IXListViewListener{

	protected static final int SUCCESS = 0;
	private int page=1;
	private XListView xListView;
	private GPSXListViewAdapter myAdapter;
	private List<GPSListInfo> mListGPSInfo;
	private int unUploadSize=0;//未上传条数
	private View mBack;
	private RelativeLayout mRLayoutGetHistoryData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_activity_map_history_orbit"), container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmccsi.mhealth.portal.sports.basic.BaseFragment#findViews()
	 */
	@Override
	public void findViews() {
		mBack = findView(MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"));
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new backClick(new MapFragment()));
		mRLayoutGetHistoryData = findView(MResource.getIdByName(MapApplication.mContext, "id", "imageButton_title_add"));
		mRLayoutGetHistoryData.setVisibility(View.GONE);
	
		
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_my_button_back"));
		mTextViewTitle.setText("历史轨迹");
		xListView = (XListView) findView(MResource.getIdByName(MapApplication.mContext, "id", "map_history_activity"));
		
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(false);
//		xListView.setOnItemLongClickListener(new myOnItemlongClickListener());
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(this);
		if(buildData!=null&&buildData.size()>0)//从详细页面回来
		{
			if(unUploadSize>0)//有未上传的
			{
				List<GPSListInfo> nowData=getStarttimeList();
				if(nowData!=null&&nowData.size()<unUploadSize)//有补传的数据 重新加载
				{
					unUploadSize=nowData.size();
					mListGPSInfo.clear();
					page=1;
					myAdapter=null;
					buildData = new ArrayList<GPSListBean>();		
					myAdapter = new GPSXListViewAdapter(buildData, mActivity);
					mListGPSInfo=getStarttimeList();
					if(mListGPSInfo!=null){
						unUploadSize=mListGPSInfo.size();
					}
					downLoadData(page);
				}
			}else{
//				myAdapter = new GPSXListViewAdapter(buildData, mActivity);
				xListView.setAdapter(myAdapter);
				myAdapter.notifyDataSetChanged();
			}
		}else{//主页面进来
			mListGPSInfo=getStarttimeList();
			if(mListGPSInfo!=null){
				unUploadSize=mListGPSInfo.size();
			}
			downLoadData(page);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private List<GPSListInfo> getStarttimeList() {
		return MHealthProviderMetaData.GetMHealthProvider(mActivity).getUnUploadGpsData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TabBaseFragment fca = (TabBaseFragment) getActivity();
//		PreferencesUtils.putString(mActivity, SharedPreferredKey.HISTORY_START_TIME, mListGPSInfo.get(position-1));
		
		if(buildData.get(position-1).time == null||
				buildData.get(position-1).time.equals("null")){
			
			fca.switchContent(new MapViewOrbitFragment(buildData
					.get(position-1).listInfo));
		}
		
	}

	@Override
	public void onDestroy() {
//		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.HISTORY_START_TIME);
		super.onDestroy();
	}

	@Override
	public void clickListner() {
//		xListView.setXListViewListener(this);
	}

	@Override
	public void loadLogic() {
		
	}

	private void builderData() {
//		mListGPSInfo = getStarttimeList();//get date
		if(myAdapter==null){
			buildData = new ArrayList<GPSListBean>();		
			myAdapter = new GPSXListViewAdapter(buildData, mActivity);
			
		}
		Logger.d("cjz", "myAdapter==null:"+(myAdapter==null));
		fitData(buildData);
		mListGPSInfo.clear();
		xListView.setAdapter(myAdapter);
		myAdapter.notifyDataSetChanged();
	}
	private void fitData(List<GPSListBean> gpsListInfos) {
        //TODO
        if(mListGPSInfo.size() == 1){
            String date = mListGPSInfo.get(0).getStarttime().toString();
            GPSListBean listBean = new GPSListBean();
            listBean.time = date.split(" ")[0];
            gpsListInfos.add(listBean);
            listBean = new GPSListBean();
            listBean.listInfo = mListGPSInfo.get(0);
            gpsListInfos.add(listBean);
        }else{
            for (int i = 0; i < mListGPSInfo.size()-1; i++) {
                GPSListBean listBean = new GPSListBean();
                String date = mListGPSInfo.get(i).getStarttime().toString();
                String date1 = mListGPSInfo.get(i+1).getStarttime().toString();
                if(i==0){
                    listBean.time = date.split(" ")[0];
                    gpsListInfos.add(listBean);
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(0);
                    gpsListInfos.add(listBean);
                }
                if(!date.split(" ")[0].equals(date1.split(" ")[0])){
                    listBean = new GPSListBean();
                    listBean.time = date1.split(" ")[0];
                    gpsListInfos.add(listBean);
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(i+1);
                }else{
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(i+1);
                }
                gpsListInfos.add(listBean);
            }
        }
        
    }

	class myOnItemlongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			showAskDialog(position);
			return true;
		}
	}

	class backClick implements OnClickListener {

		BaseFragment to;

		public backClick(BaseFragment to) {
			super();
			this.to = to;
		}

		@Override
		public void onClick(View v) {
			getActivity().onBackPressed();
		}

	}

	protected void downLoadData(int currentPage) {
		if (NetworkTool.getNetworkState(mActivity) == 0) {
			handler.sendEmptyMessage(MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_INTERNET_NONE"));
			return;
		}
		ShowProgressDialog.showProgressDialog(getResources().getString(MResource.getIdByName(MapApplication.mContext, "string", "zyqt_text_wait")), getActivity());
		Thread thread = new Thread(new DownLoadData(currentPage));
		thread.start();
	}
	
	class DownLoadData implements Runnable{
		int mpage = 0;
        
		public DownLoadData(int page){
			this.mpage = page;
		}
		@Override
		public void run() {
			ListGPSData gpsData = new ListGPSData();
			try {
				// 摘要包
				int res = DataSyn.getInstance().getListGpsData(gpsData, mpage);
				if (res == 0) {
					page=gpsData.currentPage;
					boolean hasMore;
					if(gpsData.currentPage<gpsData.totalPage)
					{
						
						hasMore=true;
					}else{
						hasMore=false;
					}
					mListGPSInfo.addAll(gpsData.datavalue);
					Message msg=new Message();
					msg.what=MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_SYNCHRO_GPS_SUCCESS");
					Bundle data=new Bundle();
					data.putBoolean("hasMore", hasMore);
					msg.setData(data);
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_SYNCHRO_GPS_FAILED"));
				}
				//
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			ShowProgressDialog.dismiss();
			if(msg.what==MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_UPLOAD_GPS_SUCCESS")){
//				BaseToast("数据上传成功");
			}else if(msg.what==MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_GPS_NODATA")){
				BaseToast("您还没有数据，赶快开启运动吧！");
			}else if(msg.what==MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_SYNCHRO_GPS_SUCCESS")){
				Logger.i("MapListGPSFragment", "---数据同步成功！");
				if(msg.getData().getBoolean("hasMore")){
					xListView.setPullLoadEnable(true);
				}else{
					xListView.setPullLoadEnable(false);
				}
			}else if(msg.what==MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_SYNCHRO_GPS_FAILED")){
				xListView.setPullLoadEnable(false);
				BaseToast("网络异常，请检查网络设置");
			}else {
				BaseToast("数据同步失败");
				ToastUtils.showToast(MapApplication.mContext, MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_INTERNET_ERROR"));
			} 
			
			builderData();
		};
	};
	private List<GPSListBean> buildData;
	
	// 是否删除弹框
	private void showAskDialog(final int position){
		String[] buttons = { "确定", "", "取消" };
		CommonAskDialog mAskDialog = CommonAskDialog.create("确认删除？", buttons, false, true);
		mAskDialog.setAlertIconVisible(-1);
		mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
			@Override
			public void onClick(int which) {
				if (which == CommonAskDialog.BUTTON_OK) {
					//删除
					String starttime ;
					toast("删除成功！");
					if(buildData.get(position).time == null){
						starttime = buildData.get(position).listInfo.getStarttime();
					}else{
						starttime = buildData.get(position).time;
					}
					// delete data from database by id
					MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteGPSListData(starttime);
					builderData();
				}
			}
		});
		mAskDialog.show(mActivity.getSupportFragmentManager(), "CommonAskDialog");
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		page++;
		Logger.d("cjz", "loadmore page="+page);
		new Thread(new DownLoadData(page)).start();
	}
}
