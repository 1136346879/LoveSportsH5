package cmccsi.mhealth.portal.sports.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cmccsi.mhealth.portal.sports.appversion.MResource;

public class ConstantsBitmaps {
	public static Bitmap mLeftPic;
	public static Bitmap mRunPicYellow;
	public static Bitmap mRunPicGreen;
	public static Bitmap mRunPicBlue;
	public static Bitmap mRunPicDouble;
	
	public static Bitmap mBitmapBgCenterRound;
	public static Bitmap mBitmapBgCenterRoundEcg;
	public static Bitmap mBitmapBgCenterRoundEcgNew;
	public static Bitmap mBitmapPointRound;
	public static Bitmap mBitmapPointRoundECG;
	public static void initRunPics(Context context){
		if (mLeftPic == null || mLeftPic.isRecycled()) {
			mLeftPic = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_rank_avg_bg"));
		}
		if (mRunPicYellow == null || mRunPicYellow.isRecycled()) {
			mRunPicYellow = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_rank_avg_icon"));
		}
		if (mRunPicGreen == null || mRunPicGreen.isRecycled()) {
			mRunPicGreen = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_rank_avg_icon_green"));
		}
		if (mRunPicBlue == null || mRunPicBlue.isRecycled()) {
			mRunPicBlue = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_rank_avg_icon_blue"));
		}
		if (mRunPicDouble == null || mRunPicDouble.isRecycled()) {
			mRunPicDouble = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_rank_avg_icon_group"));
		}
		
		if (mBitmapBgCenterRound == null || mBitmapBgCenterRound.isRecycled()) {
			mBitmapBgCenterRound = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_center_round"));
		}
		if (mBitmapBgCenterRoundEcg == null || mBitmapBgCenterRoundEcg.isRecycled()) {
			mBitmapBgCenterRoundEcg = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_ecg_ringbackground3"));
		}
		if (mBitmapPointRound == null || mBitmapPointRound.isRecycled()) {
			mBitmapPointRound = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_yellow_point"));
		}
		if (mBitmapBgCenterRoundEcgNew == null || mBitmapBgCenterRoundEcgNew.isRecycled()) {
			mBitmapBgCenterRoundEcgNew = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_countdown_ring_22"));
		}
		if (mBitmapPointRoundECG == null || mBitmapPointRoundECG.isRecycled()) {
			mBitmapPointRoundECG = BitmapFactory.decodeResource(context.getResources(), MResource.getIdByName(context, "drawable", "zyqt_countdown_ring_1"));
		}	
	}
}
