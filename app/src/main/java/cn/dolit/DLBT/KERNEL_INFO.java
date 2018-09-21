package cn.dolit.DLBT;

public class KERNEL_INFO {

    public short                      port;                           // 监听端口
    public Boolean                        dhtStarted;                     // DHT是否启动
    public int                          totalDownloadConnectionCount;   // 总的下载连接数
    public int                          downloadCount;                  // 下载任务的个数
    public int                          totalDownloadSpeed;             // 总下载速度
    public int                          totalUploadSpeed;               // 总上传速度
    public double                      totalDownloadedByteCount;       // 总下载的字节数
    public double                      totalUploadedByteCount;         // 总上传的字节数

    public int                         peersNum;                       // 当前连接上的节点总数
    public int                         dhtConnectedNodeNum;            // dht连接上的活跃节点数
    public int                         dhtCachedNodeNum;               // dht已知的节点数
    public int                         dhtTorrentNum;                  // dht中已知的torrent文件数
    
	  	  
	public KERNEL_INFO ()
	{
		
	}
	
	public void Init(String[] arry)
	{		 
	   if (arry == null || arry.length != 12)
		   return;
	   
	   port = Short.parseShort(arry[0]);
	   dhtStarted = (Integer.parseInt(arry[1]) == 0 ? false : true);
	   totalDownloadConnectionCount = Integer.parseInt(arry[2]);
	   downloadCount = Integer.parseInt(arry[3]);
	   totalDownloadSpeed = Integer.parseInt(arry[4]);
	   totalUploadSpeed = Integer.parseInt(arry[5]);
	   
	   totalDownloadedByteCount = Double.parseDouble(arry[6]);
	   totalUploadedByteCount = Double.parseDouble(arry[7]);
	   
	   peersNum = Integer.parseInt(arry[8]);
	   dhtConnectedNodeNum = Integer.parseInt(arry[9]);
	   dhtCachedNodeNum = Integer.parseInt(arry[10]);
	   dhtTorrentNum = Integer.parseInt(arry[11]);
	}
}
