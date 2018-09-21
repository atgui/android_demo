package cn.dolit.utils.common;

import java.text.DecimalFormat;

public class Utils {

	public Utils() {

	}
	public static String getDisplayFileSize(long fileSize) {
		long kbSize = fileSize / 1024;
		if (kbSize > 0)
			fileSize = fileSize % 1024;

		long mbSize = kbSize / 1024;
		if (mbSize > 0)
			kbSize = kbSize % 1024;

		String str = "";
		if (kbSize == 0) {
			str += String.valueOf(fileSize) + "B";
			return str;
		}
		if (mbSize == 0) {
			str += String.valueOf(kbSize) + "KB";
			return str;
		}

		str += String.valueOf(mbSize) + "M" + String.valueOf(kbSize) + "KB";
		return str;
	}
	public static String GetDisplayFileSize(long fileSize)
	{
		long kbSize = fileSize / 1024;
		if (kbSize > 0)
			fileSize = fileSize % 1024;
	
		long mbSize = kbSize / 1024;
		if (mbSize > 0)
			kbSize = kbSize % 1024;
		
		String str = "";
		if(kbSize == 0)
		{
			str += String.valueOf(fileSize) + "B";
			return str;
		}
		if(mbSize == 0)
		{
			str += String.valueOf(kbSize) + "KB";
			return str;
		}
		
		str += String.valueOf(mbSize) + "M" + String.valueOf(kbSize) + "KB";
		return str;
	}
	
	public static String SecondToStr(long secondTimes)
	{
		long minutes = secondTimes / 60;
		if (minutes > 0)
			secondTimes = secondTimes % 60;
	
		long hours = minutes / 60;
		if (hours > 0)
			minutes = minutes % 60;
		
		String str = "";
		if(minutes == 0)
		{
			str += String.valueOf(secondTimes) + "秒";
			return str;
		}
		if(hours == 0)
		{
			str += String.valueOf(minutes) + "分" + String.valueOf(secondTimes) + "秒";
			return str;
		}
		
		str += String.valueOf(hours) + "小时" + String.valueOf(minutes) + "分";
		return str;
	}
	
	public static String FloatToStr (float f)
    {
		 DecimalFormat format = new DecimalFormat("#.00");
		 String str = format.format(f);
		return str;
    }
}
