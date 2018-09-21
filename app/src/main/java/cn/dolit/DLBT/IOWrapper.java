package cn.dolit.DLBT;

import android.util.Log;


public class IOWrapper {
	
	protected static final String TAG = "DolitBT/Java_IOWrapper";	

	// 默认的函数不做任何处理，只是一个接口，默认由C++层自己的IO实现文件的读写
	public static IOResult MKDir(String dir, int mode)
	{
		Log.d(TAG, "Java MkDir : " + dir);
		//如果Java需要处理这个函数，该函数要赋值intRet(因为改函数的返回值是一个32位整数）
		return new IOResult();
	}
	
	// 默认的函数不做任何处理，只是一个接口，默认由C++层自己的IO实现文件的读写
	public static IOResult Open(String path, int mode, int permission)
	{
		Log.d(TAG, "Java Open : " + path);
		//如果Java需要处理这个函数，该函数要赋值intRet(因为改函数的返回值是一个32位整数）
		return new IOResult();
	}

	public static IOResult Rename(String oldName, String newName)
	{
		Log.d(TAG, "Java Rename, oldName: " + oldName + ", newName: " + newName);
		return new IOResult();
	}

	public static IOResult Remove(String fileName)
	{
		Log.d(TAG, "Java Remove, fileName: " + fileName);
		return new IOResult();
	}

	public static IOResult ListDir(String dir)
	{
		Log.d(TAG, "Java ListDir, dir: " + dir);
		return new IOResult();
	}
}
