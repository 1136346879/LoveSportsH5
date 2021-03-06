package cmccsi.mhealth.portal.sports.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.MapApplication;
import cmccsi.mhealth.portal.sports.bean.RequestData;
import cmccsi.mhealth.portal.sports.bean.RequestListInfo;
import cmccsi.mhealth.portal.sports.common.Config;
import cmccsi.mhealth.portal.sports.common.Encrypt;
import cmccsi.mhealth.portal.sports.common.ImageUtil;
import cmccsi.mhealth.portal.sports.net.DataSyn;

public class MessageAdapter extends BaseAdapter {
    private Context           context;
    private RequestListInfo   frliReqData;
    private ListItemClickHelp callback;

    public void setFrliReqData(RequestListInfo frliReqData,
            ListItemClickHelp callback) {
        this.frliReqData = frliReqData;
        this.callback = callback;
    }

    public MessageAdapter(Context context, RequestListInfo frliReqData,
            ListItemClickHelp callback) {
        this.context = context;
        this.frliReqData = frliReqData;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return frliReqData.dataValue.size();
    }

    @Override
    public Object getItem(int position) {
        return frliReqData.dataValue.get(position);
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
        if (convertView == null
                || ((Map<String, Object>) convertView.getTag()).get("holder") == null) {
            holder = new Holder();
            view = View.inflate(context, MResource.getIdByName(context, "layout", "zyqt_list_item_messages"), null);
            holder.title = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lim_title"));
            holder.content = (TextView) view.findViewById(MResource.getIdByName(context, "id", "lim_content"));
            holder.acceptbt = (TextView) view
                    .findViewById(MResource.getIdByName(context, "id", "lim_accept_buttom"));
            holder.avatar = (ImageView) view.findViewById(MResource.getIdByName(context, "id", "lim_avatar"));
            tags.put("holder", holder);
        } else {
            view = convertView;
            holder = (Holder) ((Map<String, Object>) view.getTag())
                    .get("holder");
        }
        final RequestData requestData = frliReqData.dataValue.get(position);

        if (requestData.isIsoldmsgs()) {
            holder.acceptbt.setBackgroundResource(MResource.getIdByName(context, "drawable", "zyqt_btn_cancel"));
            holder.acceptbt.setText("已接受请求");
        } else {
            holder.acceptbt.setBackgroundResource(MResource.getIdByName(context, "drawable", "zyqt_btn_addfriend"));
            holder.acceptbt.setText("点击同意");
        }

        if (requestData != null) {
            holder.title.setText(requestData.getName());
            holder.avatar.setTag(position + "norl");
            if (requestData.getRaceid() != null
                    && requestData.getRaceid().length() > 0) {
                holder.content.setText("邀请您加入比赛：" + requestData.getMsg());
                if (!TextUtils.isEmpty(requestData.getAvatar())) {
                    ImageUtil.getInstance().loadBitmap(holder.avatar,
                            requestData.getAvatar(), position + "norl", 0);
                } else {
                    holder.avatar.setImageResource(MResource.getIdByName(context, "drawable", "zyqt_sport_running"));
                }
            } else {
                holder.content.setText("请求加您为好友。");
                holder.avatar.setImageResource(MResource.getIdByName(context, "drawable", "zyqt_sport_other"));
                int name2Int = Encrypt.getIntFromName(requestData.getName());
                if (TextUtils.isEmpty(requestData.getAvatar())) {
                    if (requestData.getMsg().equals("1")) {
                        holder.avatar
                                .setImageResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_a"));
                    } else if (requestData.getMsg().equals("0")) {
                        holder.avatar
                                .setImageResource(MResource.getIdByName(MapApplication.mContext, "drawable", "zyqt_p"));
                    } else {
                        holder.avatar.setImageResource(MResource.getIdByName(context, "drawable", "zyqt_unknowfriend"));
                    }
                } else {
                    ImageUtil.getInstance()
                            .loadBitmap(holder.avatar,
                                    requestData.getAvatar() + ".jpg",
                                    position + "norl");
                }
            }
            tags.put("requestdata", requestData);
        }
        view.setTag(tags);

        holder.acceptbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClick(requestData);
                }
            }
        });

        return view;
    }

    public interface ListItemClickHelp {
        abstract void onClick(RequestData requestData);
    }

    class Holder {
        TextView  title, content, acceptbt;
        ImageView avatar;
    }
}
