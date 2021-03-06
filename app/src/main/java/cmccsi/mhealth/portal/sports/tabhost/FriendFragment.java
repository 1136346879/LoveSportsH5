package cmccsi.mhealth.portal.sports.tabhost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.activity.HandAddContactActivity;
import cmccsi.mhealth.portal.sports.activity.MatchContactActivity;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseFragment;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.AcceptFriendRequestInfo;
import cmccsi.mhealth.portal.sports.bean.BackInfo;
import cmccsi.mhealth.portal.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.portal.sports.bean.FriendPedometorSummary;
import cmccsi.mhealth.portal.sports.bean.FriendsInfo;
import cmccsi.mhealth.portal.sports.bean.OrgnizeMemberInfo;
import cmccsi.mhealth.portal.sports.bean.OrgnizeMemberPKInfo;
import cmccsi.mhealth.portal.sports.bean.RankingDate;
import cmccsi.mhealth.portal.sports.common.CatchUserDialog;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.portal.sports.common.ImageUtil;
import cmccsi.mhealth.portal.sports.common.SharedPreferredKey;
import cmccsi.mhealth.portal.sports.common.utils.ToastUtils;
import cmccsi.mhealth.portal.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.view.CommonAskDialog;
import cmccsi.mhealth.portal.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.portal.sports.view.PopMenu;
import cmccsi.mhealth.portal.sports.view.RoundAngleImageView;
import cmccsi.mhealth.portal.sports.view.ScoreBarView;
import cmccsi.mhealth.portal.sports.view.XListView;
import cmccsi.mhealth.portal.sports.view.XListView.IXListViewListener;

public class FriendFragment extends BaseFragment implements OnClickListener,
        IXListViewListener {
    private final static String TAG = "FriendFragment";

    private ImageView mBack;
    private TextView mTextViewTitle;
    private CommonAskDialog mAskDialog = null;

    private boolean mRefresh = false;
    private SharedPreferences mSharedInfo;
    private String mPhoneNum;
    // private String mPassword;
    private FriendsInfo mOrgnizeMemberPKInfo;
    private MHealthProviderMetaData mMHealthProvider;

    private XListView mMainListView;
    private MySimpleAdapter adapter;
    private TextView mTextViewTimeShowing;

    private RankingDate mRankingDate;
    private String mAvaterName;
    private String mMembername;
    private String mSex;
    private List<OrgnizeMemberInfo> myFriends;
    private boolean hasnoFriend;
    private RelativeLayout mAddFriend;

    private String fp;
    private String membername;
    private FriendPedometorSummary fpsReqData;
    private int mPosition;

    private CatchUserDialog mCatchDialog;
    
    private PopMenu mPopmenu_more;// 添加好友按钮

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater
                .inflate(MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_activity_friend"), container, false);
        super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
        return view;
    }

    private void initView() {
        hasnoFriend = false;

        mMHealthProvider = MHealthProviderMetaData
                .GetMHealthProvider(mActivity);

        mBack = findView(MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back"));
        mBack.setVisibility(View.VISIBLE);
        mBack.setBackgroundResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_my_button_back"));
        mBack.setOnClickListener(this);

        mTextViewTitle = findView(MResource.getIdByName(MapApplication.mContext, "id", "textView_title"));
        mTextViewTitle.setText(MResource.getIdByName(MapApplication.mContext, "string", "zyqt_friend_title"));

        mMainListView = findView(MResource.getIdByName(MapApplication.mContext, "id", "af_listview_rank"));
        mMainListView.setXListViewListener(this);
        mMainListView.setPullLoadEnable(false);

        mTextViewTimeShowing = findView(MResource.getIdByName(MapApplication.mContext, "id", "af_timeshowingtext"));

        String frt = sp.getString("friend_reflesh_time", "还未刷新");
        mTextViewTimeShowing.setText(frt);
        
        // bigface相关：
        mFaceRL = findView(MResource.getIdByName(MapApplication.mContext, "id", "af_listview_rl_face"));
        mFaceLL = findView(MResource.getIdByName(MapApplication.mContext, "id", "af_listview_ll_face"));
        mFaceIV = findView(MResource.getIdByName(MapApplication.mContext, "id", "af_listview_iv_face"));
        mFaceRL.setVisibility(View.INVISIBLE);
        mFaceRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mFaceRL.setVisibility(View.GONE);
                isFaceShowing = false;
            }
        });

        mAddFriend = (RelativeLayout) findView(MResource.getIdByName(MapApplication.mContext, "id", "imageButton_title_add"));
        mAddFriend.setOnClickListener(this);
        mAddFriend.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.findViewById(MResource.getIdByName(MapApplication.mContext, "id", "textview_title_add"))
                            .setBackgroundResource(
                            		MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_addfriendbutton_orange"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.findViewById(MResource.getIdByName(MapApplication.mContext, "id", "textview_title_add"))
                            .setBackgroundResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_addfriendbutton"));
                }
                return false;
            }
        });
        
        List<CommonBottomMenuItem> menulist = new ArrayList<CommonBottomMenuItem>();
		menulist.add(new CommonBottomMenuItem(1, "匹配通讯录", MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_add_f_contact")));
		menulist.add(new CommonBottomMenuItem(1, "手动添加好友", MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_add_f_auto")));
		mPopmenu_more = new PopMenu(getActivity(), menulist);
		mPopmenu_more.setOnItemClickListener(menuItemClick);
    }

    @Override
    public void onResume() {
        if (mAddFriend != null) {
            mAddFriend.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private void autoUpdate() {
        if (mRefresh) {
            return;
        }
        new AsyncTask<Integer, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRefresh = true;
            }

            protected String doInBackground(Integer... params) {
                updateGroupData();
//                getMySeq();
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Editor edit = sp.edit();
                edit.putLong(SharedPreferredKey.FRIEND_GETTIME, Common
                        .getCurrentDayFirstTimeMills(new Date().getTime()));
                edit.commit();
                getFriendFromDB();
                showDatas();
                if (hasnoFriend) {
                    BaseToast("您还没有好友，快添加好友吧！", 5);
                }
                mRefresh = false;
            }
        }.execute();
    }

    public void updateGroupData() {
        getFriendInfos();
    }

    private void getFriendInfos() {
        mOrgnizeMemberPKInfo = new FriendsInfo();
        try {
            int res = DataSyn.getInstance()
                    .getFriendsList(mOrgnizeMemberPKInfo);
            if (res == 0) {
                mMHealthProvider.deleteMyFriend();
                mMHealthProvider
                        .FriendInsertValue(mOrgnizeMemberPKInfo.friendslist);
                hasnoFriend = mOrgnizeMemberPKInfo.friendslist.size() == 0;
            } else {
                hasnoFriend = mMHealthProvider.getFriendCount() == 0;
                handle.sendEmptyMessage(NET_PROBLEM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void findViews() {
        initView();
        getMySharedInfo();
        int friendNum = getFriendFromDB();
        showDatas();
        if (friendNum == 0
                || new Date().getTime()
                        - sp.getLong(SharedPreferredKey.FRIEND_GETTIME, 0) > 86400000l) {
            mMainListView.startLoading(Common.getDensity(mActivity) * 60);
        }
    }

    @Override
    public void clickListner() {
    }

    @Override
    public void loadLogic() {
    }

    @Override
    public void onClick(View v) {
    	if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "button_input_bg_back")) {
    		getActivity().finish();
		}else if (v.getId() == MResource.getIdByName(MapApplication.mContext, "id", "imageButton_title_add")) {
			if (mRefresh) {
                BaseToast("还在获取好友列表，请稍后再添加~", 5);
                return;
            }
            mPopmenu_more.showAsDropDown(v);
		}
    }

    

    private void showDatas() {
        adapter = new MySimpleAdapter(myFriends);
        mMainListView.setAdapter(adapter);
        //暂时注释掉，后期会有改动
//        mMainListView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//                Log.d("Friend ", position + "  条  " + id);
//                getFriendDetailPedometers(position);
//            }
//        });
        mMainListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
//                if (position == 1)
//                    return true;
                showDeleteAlertDialog(position);
                return true;
            }
        });
        resetXListView();
    }

    private int getFriendFromDB() {
        myFriends = mMHealthProvider.getMyFriends();
        if (myFriends == null)
            myFriends = new ArrayList<OrgnizeMemberInfo>();
        return myFriends.size();
    }

    // 得到朋友的简报
    private void getFriendDetailPedometers(final int position) {
        new Thread() {
            public void run() {
                if (myFriends == null)
                    return;
                if (position < 1)
                    return;
                if (position > myFriends.size() + 1)
                    return;

                fp = myFriends.get(position - 1).friendphone;
                membername = myFriends.get(position - 1).membername;

                Log.d("Friend", membername + " " + fp);

                fpsReqData = new FriendPedometorSummary();
                int suc = DataSyn.getInstance().getFriendInfo(fp, fpsReqData);
                if (suc == 0) {
                    handle.sendEmptyMessage(GET_DETAIL_PEDOMETERS_SUCCESS);
                } else {
                    handle.sendEmptyMessage(NET_PROBLEM);
                }
            };
        }.start();
    }

    private void getMySharedInfo() {
        if (mRankingDate == null) {
            mRankingDate = new RankingDate();
        }
        mSharedInfo = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
        mAvaterName = mSharedInfo.getString(SharedPreferredKey.AVATAR, "");
        mMembername = mSharedInfo.getString(SharedPreferredKey.NAME, "");
        mRankingDate.member1seq = mSharedInfo.getString("member1seq", "");
        mRankingDate.member1info.step = mSharedInfo.getString(
                "memberi1Info_step", "");
        mSex = mSharedInfo.getString(SharedPreferredKey.GENDER, "0");

        mPhoneNum = mSharedInfo.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
        // mPassword = mSharedInfo.getString(SharedPreferredKey.PASSWORD, null);
        // // 拿到密码
        /*
         * 2014-11-26 liuxiao begin 一拖三版本不需要 if (mPassword == null || mPhoneNum
         * == null) { BaseToast("账号问题，请重新登录!"); return; }
         */
        // 2014-11-26 liuxiao end
    }

    // TODO 设定clubid
    private void getMySeq() {
        new Thread() {
            public void run() {
                int club = sp.getInt(SharedPreferredKey.CLUB_ID, -1);
                int res = DataSyn.getInstance().getPedGroupSeq(mRankingDate);
                if (res == 0) {
                    if (mMembername == null || "".equals(mMembername)) {
                        OrgnizeMemberPKInfo orgnizeMemberPKInfoYesterDay = new OrgnizeMemberPKInfo();
                        int suc = DataSyn
                                .getInstance()
                                .getOrgnizeMembersPkInfoYestoday(
                                        Integer.parseInt(mRankingDate.member1seq),
                                        Integer.parseInt(mRankingDate.member1seq),
                                        orgnizeMemberPKInfoYesterDay);
                        if (suc == 0
                                && orgnizeMemberPKInfoYesterDay.orgnizemember
                                        .size() > 0) {
                            mMembername = orgnizeMemberPKInfoYesterDay.orgnizemember
                                    .get(0).membername;
                        }
                    }
                    mAvaterName = mRankingDate.avatar;
                    Editor sharedata = getSharedPreferences(
                            SharedPreferredKey.SHARED_NAME,
                            Context.MODE_PRIVATE).edit();
                    sharedata.putString("member1seq", mRankingDate.member1seq);
                    sharedata.putString("memberi1Info_step",
                            mRankingDate.member1info.step);
                    sharedata.commit();
                    handle.sendEmptyMessage(GET_MY_SEQ_SUCCESS);
                } else {
                    handle.sendEmptyMessage(NET_PROBLEM);
                }
            }
        }.start();
    }

    class MySimpleAdapter extends BaseAdapter {
        private List<OrgnizeMemberInfo> friends;
        private int maxStep = 10000;
        private String by;

        public void setFriends(List<OrgnizeMemberInfo> friends) {
            this.friends = friends;
        }

        public MySimpleAdapter(List<OrgnizeMemberInfo> myFriends) {
            friends = myFriends;
            if (friends.size() > 0) {
                String f = friends.get(0).member7avgstep;
                if (fomat(f).length() == f.length()) {
                    maxStep = Integer.parseInt(f);
                } else {
                    maxStep = 0;
                }
            }
        }

        public String fomat(String s) {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(s);
            return m.replaceAll("").toString();
        }

        @Override
        public int getCount() {
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(),
                        MResource.getIdByName(MapApplication.mContext, "layout", "zyqt_list_item_friend"), null);
                holder.mScorebar = (ScoreBarView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "regularprogressbar"));
                holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic,
                        ConstantsBitmaps.mRunPicYellow);
                holder.mTextViewRankId = (TextView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "textview_rank_seq"));
                holder.mImageViewRankFirst = (ImageView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "imageview_rankidfirst"));
                holder.mTextViewMemberName = (TextView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "textview_member_name"));
                holder.mTextViewGroupName = (TextView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "textview_group_name"));
                holder.tv_stepNum = (TextView) convertView
                        .findViewById(MResource.getIdByName(MapApplication.mContext, "id", "tv_stepNum"));
                // holder.mTextViewGroupName.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic,
                        ConstantsBitmaps.mRunPicYellow);
            }

            OrgnizeMemberInfo friend = friends.get(position);

            // 前三名黄色 + 交替颜色的背景↓↓↓↓↓↓======================
            holder.mImageViewRankFirst.setVisibility(View.GONE);

            // if ((position & 1) == 1)
            // holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg);//
            // 186,216,255
            // else
            holder.mTextViewRankId.setText((position + 1) + "");

            // 第一名王冠 前三名黄色 + 交替颜色的背景↑↑↑↑↑↑ =================

            // 名字，组名部分↓↓↓↓↓↓======================
            holder.mTextViewMemberName.setText(friend.membername);
            // 名字，组名部分↑↑↑↑↑↑=================

            // 分数条↓↓↓↓↓↓======================
            holder.mScorebar.setMaxValue(maxStep);
            by = friend.member7avgstep;
            if (fomat(by).length() == by.length()) {
                holder.mScorebar.setScore(Integer.parseInt(by));
            } else {
                holder.mScorebar.setScore(Integer.parseInt("0"));
            }
            holder.mScorebar.setTypeface(Typeface.DEFAULT_BOLD);// 字体类型加粗
            holder.mScorebar.reDraw();
            // 分数条↑↑↑↑↑↑=================
            holder.tv_stepNum.setText(friend.member7avgstep + "步");
            return convertView;
        }
    }

    private class ViewHolder {
        TextView mTextViewRankId, mTextViewMemberName, mTextViewGroupName,
                tv_stepNum;
        ImageView mImageViewRankFirst;
        ScoreBarView mScorebar;
        // String fp;
    }

    /**
     * bigface相关
     */
    private static RelativeLayout mFaceRL;
    public static boolean isFaceShowing;
    private LinearLayout mFaceLL;
    private RoundAngleImageView mFaceIV;

    public static void closebigFace() {
        if (mFaceRL.getVisibility() == View.VISIBLE) {
            mFaceRL.setVisibility(View.GONE);
            isFaceShowing = false;
        }
    }

    // 静态int
    private static final int ADD_FRIEND_SUCCESS = 0; // 添加好友成功
    private static final int ADD_FRIEND_FAIL = 1; // 添加好友失败
    private static final int NET_PROBLEM = 2; // 网络问题
    private static final int GET_DETAIL_PEDOMETERS_SUCCESS = 3; // 简报获取成功
    private static final int DELETE_RIEND_SUCCESS = 4; // 删除好友成功
    private static final int DELETE_RIEND_FAIL = 5; // 删除好友失败
    private static final int GET_MY_SEQ_SUCCESS = 6;
    private static final int ADD_FRIEND_SOMETHING_WRONG = 7;


    Handler handle = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
            case GET_MY_SEQ_SUCCESS:

                String sdf = new SimpleDateFormat("yyyy年MM月dd日(单日步数)")
                        .format(new Date().getTime() - 86400000);
                Editor edit = sp.edit();
                edit.putString("friend_reflesh_time", sdf);
                edit.commit();
                mTextViewTimeShowing.setText(sdf);

                break;
            case ADD_FRIEND_SUCCESS:
                BaseToast("请求已经发出，请等待对方同意！", 7);
                break;
            case ADD_FRIEND_FAIL:
                BaseToast(msg.obj+"");
                break;
            case ADD_FRIEND_SOMETHING_WRONG:
                BaseToast("请求发送失败，请重试", 5);
                break;
            case GET_DETAIL_PEDOMETERS_SUCCESS:
//                if (fp != null) {
//                    Intent intent = new Intent(mActivity,
//                            FriendPedometerInfoActivity.class);
//                    intent.putExtra("friendphone", fp);
//                    intent.putExtra("membername", membername);
//                    intent.putExtra(SharedPreferredKey.PHONENUM, mPhoneNum);
//                    // intent.putExtra(SharedPreferredKey.PASSWORD, mPassword);
//                    int size = fpsReqData.friendsinfo.size();
//                    for (int i = 0; i < size - 32; i++) {
//                        fpsReqData.friendsinfo.remove(0);
//                    }
//                    intent.putExtra("fpsReqData", fpsReqData);
//                    mActivity.startActivity(intent);
//                    mActivity.overridePendingTransition(R.anim.slide_in_right,
//                            R.anim.silde_out_left);
//                }
                break;
            case NET_PROBLEM:
                resetXListView();
                ToastUtils.showToast(MapApplication.mContext, MResource.getIdByName(MapApplication.mContext, "string", "zyqt_MESSAGE_INTERNET_ERROR"));
            case DELETE_RIEND_FAIL:
                BaseToast("移除好友失败，请重试..", 5);
                break;
            case DELETE_RIEND_SUCCESS:
                BaseToast("已将该好友移除", 8);
                if (adapter == null) {
                    autoUpdate();
                } else {
                    adapter.setFriends(myFriends);
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        };
    };

    // 添加好友
    private void addFriend(final String targetphone) {
        new Thread() {
            public void run() {
                BackInfo AfireqData = new BackInfo();
                int suc = DataSyn.getInstance().addFriendById(targetphone,
                        AfireqData);
                if (suc == 0 && "等待对方同意".equals(AfireqData.reason)) {
                    handle.sendEmptyMessage(ADD_FRIEND_SUCCESS);
                } else if ("FAILURE".equals(AfireqData.status)) {
                    Message msg = handle.obtainMessage(ADD_FRIEND_FAIL);
                    msg.obj =AfireqData.reason;
                    handle.sendMessage(msg);
                }else {
                    handle.sendEmptyMessage(ADD_FRIEND_SOMETHING_WRONG);
                }
            };
        }.start();
    }

    private void showDeleteAlertDialog(int position) {
    	//改动为自定义的Dialog；
    	mPosition = position;
		String[] buttons = { "是的", "", "取消" };
		mAskDialog = CommonAskDialog.create("您要移除这位好友吗？", buttons, false, true);
		mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
			@Override
			public void onClick(int which) {
				if (which == CommonAskDialog.BUTTON_OK) {
					deleteFirend();
				}else if(which == CommonAskDialog.BUTTON_CANCEL){
					mAskDialog.dismiss();
				}
			}
		});
		mAskDialog.show(getChildFragmentManager(), "CommonAskDialog");
    }

    // 删除好友
    private void deleteFirend() {
        new Thread() {
            public void run() {
                if (myFriends == null)
                    myFriends = mMHealthProvider.getMyFriends();
                if (mPosition < 1 || mPosition > myFriends.size() + 1) {
                    handle.sendEmptyMessage(DELETE_RIEND_FAIL);
                    return;
                }
                AcceptFriendRequestInfo afri = new AcceptFriendRequestInfo();
                int suc = DataSyn.getInstance().deleteFriend(
                        myFriends.get(mPosition - 1).friendphone, afri);
                if (suc == 0) {
                    mMHealthProvider.deleteMyFriend(myFriends
                            .get(mPosition - 1).friendphone);
                    myFriends.remove(mPosition - 1);
                    handle.sendEmptyMessage(DELETE_RIEND_SUCCESS);
                } else {
                    handle.sendEmptyMessage(NET_PROBLEM);
                }
            };
        }.start();
    }

    private Drawable getImageAsync(ImageView holder, String url) {
        return getImageAsync(holder, url, null);
    }

    private Drawable getImageAsync(ImageView holder, String url, String tag) {
        return getImageAsync(holder, url, null, 0);
    }

    private Drawable getImageAsync(ImageView holder, String url, String tag,
            int mode) {
        return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
    }

    @Override
    public void onRefresh() {
        autoUpdate();
    }

    private void resetXListView() {
        mMainListView.stopRefresh();
        mMainListView.stopLoadMore();
        mMainListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
    }

    @Override
    public void onLoadMore() {
    }
    
	/**
	 * 添加好友菜单点击事件
	 */
	private OnItemClickListener menuItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:// 匹配通讯录
			    Intent intent1 = new Intent(mActivity, MatchContactActivity.class);
			    mActivity.startActivity(intent1);
				break;
			case 1:// 手动添加好友
				Intent intent2 = new Intent(mActivity, HandAddContactActivity.class);
			    mActivity.startActivity(intent2);
				break;
			default:
				break;
			}
			mPopmenu_more.dismiss();
		}
	};
}
