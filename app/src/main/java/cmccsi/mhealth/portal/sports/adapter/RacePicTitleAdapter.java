package cmccsi.mhealth.portal.sports.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.common.Config;
import cmccsi.mhealth.portal.sports.common.ImageUtil;

public class RacePicTitleAdapter extends PagerAdapter {
	private Context context;

	public RacePicTitleAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 14;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View view = View.inflate(context, MResource.getIdByName(context, "layout", "zyqt_imageview"), null);
		ImageView imageView = (ImageView) view.findViewById(MResource.getIdByName(context, "id", "the_imageview"));
		imageView.setImageResource(MResource.getIdByName(context, "drawable", "zyqt_umeng_socialize_share_pic"));
		imageView.setTag(position + "rpta");
		ImageUtil.getInstance().loadBitmap(imageView, Config.RACE_PIC_SERVER_ROOT + Config.RACE_TITLE_PIC + (position + 1) + "_big.jpg", position + "rpta", 0);
		((ViewPager) collection).addView(view);
		return view;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}
}
