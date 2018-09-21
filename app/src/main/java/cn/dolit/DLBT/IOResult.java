package cn.dolit.DLBT;

public class IOResult {
	
	// 如果是0则代表应用层希望由C++底层继续按自己内部的IO函数执行。否则，代表IOWrapper已经处理过了，点量BT内部不要再执行系统层的IO函数了
	public int iHandled;
	
	// 以下是一些IO函数本身的返回值，在iHandled不为0的情况下有效，直接作为执行结果，内部不再调用自己的函数
	public int intRet;
	//public long longRet;  如果需要seek就可以用这个返回，目前不需要
	//public byte[] byteRet;		如果需要readv就可以用这个返回

	// listDir的返回
	public String stringRet;

	public IOResult()
	{
		iHandled = 0;
		intRet = 0;
		//longRet = 0;
		//byteRet = null;
		stringRet = "";
	}
}
