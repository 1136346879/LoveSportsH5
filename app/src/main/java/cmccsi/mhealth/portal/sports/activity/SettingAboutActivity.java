package cmccsi.mhealth.portal.sports.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cmccsi.mhealth.portal.sports.appversion.MResource;
import cmccsi.mhealth.portal.sports.basic.BaseActivity;
import cmccsi.mhealth.portal.sports.basic.MapApplication;

public class SettingAboutActivity extends BaseActivity {
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MResource.getIdByName(MapApplication.mContext , "layout", "zyqt_activity_about"));
		BaseBackKey("关于", this);
		mImageView = findView(MResource.getIdByName(MapApplication.mContext , "id", "setting_about"));
		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingAboutActivity.this.finish();
				overridePendingTransition(MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_slide_in_left"),
		        		MResource.getIdByName(MapApplication.mContext, "anim", "zyqt_silde_out_right"));
			}
		});
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		mImageView.measure(w, h);
        double iv_ratio = 1.0 * mImageView.getMeasuredWidth()
                * mImageView.getMeasuredHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                MResource.getIdByName(MapApplication.mContext , "drawable", "zyqt_bg_about"));
        double img_ratio = 1.0 * bitmap.getWidth() / bitmap.getHeight();
        if (iv_ratio > img_ratio) {
            mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        }else if (iv_ratio<img_ratio) {
            mImageView.setScaleType(ScaleType.CENTER_CROP);
        }else {
            mImageView.setScaleType(ScaleType.FIT_XY);
        }
        mImageView.setImageBitmap(bitmap);
	}
}
