package cn.dolit.DLBT;

public class DLBT_KERNEL_START_PARAM {
	
	public int bStartLocalDiscovery;		// 是否启动局域网内的自动发现（不通过DHT、Tracker，只要在一个局域网也能立即发现，局域网速度快，可以加速优先发现同一个局域网的人）0代表不启动
	public int bStartUPnP;				// 是否自动UPnP映射点量BT内核所需的端口，0代表不启动
	public int bStartDHT;					// 默认是否启动DHT，如果默认不启动，可以后面调用接口来启动0代表不启动
	public int bLanUser;                  // 是否纯局域网用户（不希望用户进行外网连接和外网通讯，纯局域网下载模式---不占用外网带宽，只通过内网的用户间下载）
    public int bVODMode;                  // 设置内核的下载模式是否严格的VOD模式，严格的VOD模式下载时，一个文件的分块是严格按比较顺序的方式下载，从前向后下载；或者从中间某处拖动的位置向后下载
                                        // 该模式比较适合边下载边播放,针对这个模式做了很多优化。但由于不是随机下载，所以不大适合纯下载的方案，只建议在边下载边播放时使用。默认是普通模式下载
                                        // 仅VOD以上版本中有效

	public short  startPort;	            // 内核监听的端口，如果startPort和endPort均为0 或者startPort > endPort || endPort > 32765 这种参数非法，则内核随机监听一个端口。 如果startPort和endPort合法
	public short  endPort;				// 内核则自动从startPort ---- endPort之间监听一个可用的端口。这个端口可以从DLBT_GetListenPort获得
	  	  
	public DLBT_KERNEL_START_PARAM ()
	{
		 bStartLocalDiscovery = 1;
	     bStartUPnP = 1;
	     bStartDHT = 1;
	     bLanUser = 0;
	     bVODMode = 0;
	
	     startPort = 0;
	     endPort = 0;
	}

	public String ToParamString()
	{
		 String strParam = "";
	     strParam += String.valueOf(bStartLocalDiscovery) + "|" + String.valueOf(bStartUPnP) + "|";
	     strParam += String.valueOf(bStartDHT) + "|" + String.valueOf(bLanUser) + "|";
	     strParam += String.valueOf(startPort) + "|" + String.valueOf(endPort) + "|";
	     strParam += String.valueOf(bVODMode);
	     return strParam;
	}
}
