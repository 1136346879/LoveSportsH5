package cmccsi.mhealth.portal.sports.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.bean.RaceMemberData;
import cmccsi.mhealth.portal.sports.bean.RaceMemberInfo;
import cmccsi.mhealth.portal.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.portal.sports.view.ScoreBarView;

/**
 * 比赛详情的adapter
 * @author zy
 *
 */
public class RaceDetailAdapter extends BaseAdapter {
	private Context context;
	private RaceMemberInfo rmi;
	private int maxValue;
//	private int[] sex = new int[] { R.drawable.avatar_male_middle, R.drawable.avatar_female_middle };

	public RaceDetailAdapter(Context context, RaceMemberInfo rmi) {
		this.context = context;
		this.rmi = rmi;
	}

	public void setRmi(RaceMemberInfo rmi) {
		this.rmi = rmi;
	}

	@Override
	public int getCount() {
		return rmi.racemember.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		Holder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new Holder();
			convertView = View.inflate(context, MResource.getIdByName(context, "layout", "zyqt_list_item_detail_race"), null);
			holder.rank = (ImageView) convertView.findViewById(MResource.getIdByName(context, "id", "tab_rank_head"));
			holder.rankcount = (TextView) convertView.findViewById(MResource.getIdByName(context, "id", "textview_race_seq"));
			holder.member_name = (TextView) convertView.findViewById(MResource.getIdByName(context, "id", "race_textview_member_name"));
			holder.group_name = (TextView) convertView.findViewById(MResource.getIdByName(context, "id", "race_textview_group_name"));
			holder.sbv = (ScoreBarView) convertView.findViewById(MResource.getIdByName(context, "id", "race_regularprogressbar"));
            holder.tv_stepNum = (TextView) convertView
                    .findViewById(MResource.getIdByName(context, "id", "tv_stepNum"));
            holder.sbv.setPics(ConstantsBitmaps.mLeftPic,
                    ConstantsBitmaps.mRunPicYellow);
			convertView.setTag(holder);
		} else {
			holder = (Holder)convertView.getTag();
		}
		RaceMemberData rmd = rmi.racemember.get(position);
		if(position > 2){
			holder.rank.setVisibility(View.GONE);
		}else{
			holder.rank.setVisibility(View.VISIBLE);
		}
		if (position == 0) {
			maxValue = Integer.parseInt(rmd.getMemberstepvalue());
		}
		
		holder.sbv.setMaxValue(maxValue);
		holder.sbv.setScore(Integer.parseInt(rmd.getMemberstepvalue()));
		holder.sbv.reDraw();
		holder.tv_stepNum.setText(rmd.getMemberstepvalue() + "步");
		holder.member_name.setText(rmd.getNickname());
		holder.group_name.setText(rmd.getGroupname());
		holder.rankcount.setText((position + 1) + "");

		return convertView;
	}

	class Holder {
		public TextView member_name, group_name,tv_stepNum;
		public ScoreBarView sbv;
		public ImageView rank;
		public TextView rankcount;
	}
}
