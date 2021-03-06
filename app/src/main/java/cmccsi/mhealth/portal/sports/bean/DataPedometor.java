package cmccsi.mhealth.portal.sports.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import cmccsi.mhealth.portal.sports.common.Common;

public class DataPedometor {
	public String createtime = "2012-11-21 20:10:24"; // 格式为yyyy-MM-dd HH:mm:ss
	public PedometorSummary data;

	public DataPedometor() {
		data = new PedometorSummary();
	}

	public DataPedometor(long createTime) {
		createtime = Common.getDateAsYYYYMMDDHHMMSSCreateTime(createTime);
		data = new PedometorSummary();
	}

	public DataPedometor(String createTime) {
		createtime = createTime;
		data = new PedometorSummary();
	}
	
	/**
	 * 比较两个数据创建时间
	 * 
	 * @param data
	 * @return biger 1 small 0
	 */
	@SuppressLint("SimpleDateFormat")
	public int compareCreatetime(DataPedometor data) {
		if (data == null)
			return -1;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date self_createtime = null;
		Date compare_createtime = null;
		try {
			self_createtime = df.parse(this.createtime);
			compare_createtime = df.parse(data.createtime);
			
			long self_one = self_createtime.getTime();
			long compare_one = compare_createtime.getTime();

			if (self_one > compare_one)
				return 1;
			else if (self_one < compare_one)
				return 0;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}


		return -1;
	}
}
