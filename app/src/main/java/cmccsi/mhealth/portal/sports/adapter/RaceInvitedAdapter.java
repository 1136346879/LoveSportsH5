package cmccsi.mhealth.portal.sports.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.bean.OrgnizeMemberInfo;
import cmccsi.mhealth.portal.sports.common.ImageUtil;
import cmccsi.mhealth.portal.sports.net.DataSyn;
import cmccsi.mhealth.portal.sports.view.RoundAngleImageView;


public class RaceInvitedAdapter extends BaseAdapter {
	protected static final String TAG = "RaceInvitedAdapter";
	private Context context;
	private List<OrgnizeMemberInfo> friends;
	private List<Boolean> mCheckRecorder;
	public void setFriends(List<OrgnizeMemberInfo> friends) {
		this.friends = friends;
	}

	public RaceInvitedAdapter(Context context, List<OrgnizeMemberInfo> myFriends, List<Boolean> checkRecorder) {
		friends = myFriends;
		mCheckRecorder = checkRecorder;
		this.context = context;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, MResource.getIdByName(context, "layout", "zyqt_list_item_inviting"), null);
			holder.itemll =  (LinearLayout) convertView.findViewById(MResource.getIdByName(context, "id", "lli_mainLLlayout"));
			holder.mImageViewRankIcon = (RoundAngleImageView) convertView.findViewById(MResource.getIdByName(context, "id", "lli_icon"));
			holder.mTextViewMemberName = (TextView) convertView.findViewById(MResource.getIdByName(context, "id", "lli_name"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (mCheckRecorder.get(position)) {
			holder.itemll.setBackgroundResource(MResource.getIdByName(context, "drawable", "zyqt_race_race_item_red"));
		} else {
			holder.itemll.setBackgroundResource(MResource.getIdByName(context, "drawable", "zyqt_race_race_item"));
		}

		OrgnizeMemberInfo friend = friends.get(position);

		// 头像部分↓↓↓↓↓↓======================
		String avater = friend.memberinforev1;
		if (!TextUtils.isEmpty(avater) && avater.equals("1")) {
			holder.mImageViewRankIcon.setImageDrawable(context.getResources().getDrawable(MResource.getIdByName(context, "drawable", "zyqt_avatar_male_middle")));
		} else {
			holder.mImageViewRankIcon.setImageDrawable(context.getResources().getDrawable(MResource.getIdByName(context, "drawable", "zyqt_avatar_female_middle")));
		}
		holder.mImageViewRankIcon.setTag(position + "0");

		String imageName = friend.avatar;
		if (!TextUtils.isEmpty(imageName)) {
			ImageUtil.getInstance().loadBitmap(holder.mImageViewRankIcon,imageName, position + "0", 0);
		}
		// 头像部分↑↑↑↑↑↑=================

		holder.mTextViewMemberName.setText(friend.membername);
		return convertView;

	}

	private class ViewHolder {
		LinearLayout itemll;
		TextView mTextViewMemberName;
		RoundAngleImageView mImageViewRankIcon;
	}

}