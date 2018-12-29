package cmccsi.mhealth.portal.sports.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.bean.RankInfo;
import cmccsi.mhealth.portal.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.portal.sports.view.ScoreBarView;

public class CampaignRankListAdapter extends ArrayListAdapter<RankInfo> {

	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private ViewHolder mViewHolder = null;
	private int mMaxValue;

	public CampaignRankListAdapter(Context context) {
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(MResource.getIdByName(mContext, "layout", "zyqt_list_item_rank"), null);
			mViewHolder.mTvRankSeq = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "textview_rank_seq"));
			mViewHolder.mTvName = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "textview_member_name"));
			mViewHolder.mTvOrg = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "textview_group_name"));
			mViewHolder.mTvStepNum = (TextView) convertView.findViewById(MResource.getIdByName(mContext, "id", "tv_stepNum"));
			mViewHolder.mIvRankFirst = (ImageView) convertView.findViewById(MResource.getIdByName(mContext, "id", "imageview_rankidfirst"));
			mViewHolder.mSbvLine = (ScoreBarView) convertView.findViewById(MResource.getIdByName(mContext, "id", "regularprogressbar"));
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		RankInfo mRankInfo_zfj;
		mRankInfo_zfj = mList.get(position);
		if (mRankInfo_zfj.getName().equals("无数据")) {
			mViewHolder.mTvRankSeq.setVisibility(View.INVISIBLE);
			mViewHolder.mTvName.setVisibility(View.INVISIBLE);
			mViewHolder.mTvOrg.setVisibility(View.INVISIBLE);
			mViewHolder.mTvStepNum.setVisibility(View.INVISIBLE);
			mViewHolder.mSbvLine.setVisibility(View.INVISIBLE);

		} else {

			mViewHolder.mTvRankSeq.setText(position + 1 + "");
			mViewHolder.mTvName.setText(mRankInfo_zfj.getName() + "");
			mViewHolder.mTvOrg.setText(mRankInfo_zfj.getGroup() + "");
			mViewHolder.mTvStepNum.setText(mRankInfo_zfj.getStep() + "步");
			if (position < 3) {
//				mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
				if (position == 0) {
					// mViewHolder.mIvRankFirst.setVisibility(View.VISIBLE);
					mMaxValue = mList.get(0).getStep();
				}
			}
			mViewHolder.mSbvLine.setMaxValue(mMaxValue);
			mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mRunPicYellow);
			mViewHolder.mSbvLine.setScore(mRankInfo_zfj.getStep());
		}
		return convertView;
	}

	class ViewHolder {
		ImageView mIvRankFirst;
		TextView mTvRankSeq;
		TextView mTvName;
		TextView mTvOrg;
		TextView mTvStepNum;
		ScoreBarView mSbvLine;
	}

}
