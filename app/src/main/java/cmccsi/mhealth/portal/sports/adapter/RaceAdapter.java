package cmccsi.mhealth.portal.sports.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.bean.RaceData;
import cmccsi.mhealth.portal.sports.bean.RaceInfo;
import cmccsi.mhealth.portal.sports.common.Common;
import cmccsi.mhealth.portal.sports.common.Config;
import cmccsi.mhealth.portal.sports.common.ImageUtil;

/**
 * 比赛adapter 负责绘制listview
 * 
 * @author zy
 * 
 */
public class RaceAdapter extends BaseAdapter {
	private Context context;
    private String[] typename = new String[] { "", "单人", "多人", "团队", "", "" };
	private int[] typeicon = new int[] { 0, MResource.getIdByName(context, "drawable", "zyqt_race_race_item_state0"), MResource.getIdByName(context, "drawable", "zyqt_race_race_item_state1"), MResource.getIdByName(context, "drawable", "zyqt_race_race_item_state2"), 0, 0 };
	private RaceInfo ri;
	private int size;

	public RaceAdapter(Context context, RaceInfo ri) {
		this.context = context;
		this.ri = ri;
		size = ri.racelistinfo.size();
	}

	public RaceAdapter(Context context) {
		this.context = context;
	}

	public void setRi(RaceInfo ri) {
		this.ri = ri;
		size = ri.racelistinfo.size();
	}

	@Override
	public int getCount() {
		return size;
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
		View view = null;
		Holder holder = null;
		Map<String, Object> tags = new HashMap<String, Object>();
		if (convertView == null || ((Map<String, Object>) convertView.getTag()).get("holder") == null) {
			holder = new Holder();
			view = View.inflate(context, MResource.getIdByName(context, "layout", "zyqt_list_item_race"), null);
			holder.title = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_title"));
			holder.content = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_content"));
			holder.type = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_type"));
			holder.time = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_time"));
			holder.joinedMemberNum = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_joinedmembernum"));
			holder.mainImage = (ImageView) view.findViewById(MResource.getIdByName(context, "id", "lir_race_imageview"));
			tags.put("holder", holder);
		} else {
			view = convertView;
			holder = (Holder) ((Map<String, Object>) view.getTag()).get("holder");
		}
		RaceData rd = ri.racelistinfo.get(position);
		holder.mainImage.setBackgroundResource(MResource.getIdByName(context, "drawable", "zyqt_umeng_socialize_share_pic"));
		holder.title.setText(rd.getRacename());
        holder.content.setText("创建人:" + rd.getFoundername());
		holder.time.setText(rd.getRacename());
        holder.joinedMemberNum.setText(rd.getMembernum() + "人参加");

		holder.type.setText(typename[Integer.parseInt(rd.getType())]);
		holder.type.setBackgroundResource(typeicon[Integer.parseInt(rd.getType())]);

		holder.time.setText(Common.getCurrentDayLongTimeDot(Long.parseLong(rd.getStarttime())) + "-" + Common.getCurrentDayLongTimeDot(Long.parseLong(rd.getEndtime())));
		holder.mainImage.setTag(position + "ra");
        if (!rd.getTitlepicurl().contains("_big.")) {
            rd.setTitlepicurl(rd.getTitlepicurl().replace(".", "_big."));
        }

		ImageUtil.getInstance().loadBitmap(holder.mainImage, Config.RACE_PIC_SERVER_ROOT + rd.getTitlepicurl(), position + "ra", 0);
		
		tags.put("racedata", rd);
		tags.put("racesize", size);
		view.setTag(tags);
		return view;
	}

	class Holder {
		public TextView title, content, type, time, joinedMemberNum;
		public ImageView mainImage;
	}
}