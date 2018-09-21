package cn.dolit.DLBT;

import android.content.Context;

public class DolitBT extends DLL {
	     
    public DolitBT(Context applicationContext) {
    	m_applicationContext = applicationContext;
	}

    public int InitAPIKey(String key1, String key2)
    {	    
    	return Init(key1, key2, m_applicationContext);
    }
    
    public int DLBT_SetIOWrapper(String wrapperClsName)
    {
    	return SetIOWrapper(wrapperClsName);
    }
    
    // 以下为对外的接口
    /**
	  * 启动BT内核，初始化运行环境,返回值表示是否成功, 
	  * @return	 整型4个字节的返回值  ；1(TRUE)代表成功， 0代表失败
	  */
   public int DLBT_Startup(
           DLBT_KERNEL_START_PARAM param, // 内核启动设置，参考KERNEL_START_PARAM，如果为NULL，则使用内部默认设置
           String protectedProtocolIDs,  // 可以自定义私有协议，突破运营商限制。如果为NULL，则作为标准的BT客户端启动
           // 点量BT的私有协议在3.4版本中进行了全新改进，可以穿透大部分运营商对协议的封锁
           boolean seedServerMode,     // 是否上传模式，上传模式内部有些参数进行了优化，适合大并发上传，但不建议普通客户端启用，只建议上传服务器使用
           // 专业上传服务器模式仅在商业版中有效，演示版暂不支持该功能。详见使用说明文档
           String  productNum           // 商业版用户的数字ID，在购买后作者会提供一个产品密钥，激活商业版功能，试用版用户请使用NULL。
			    )
   {	  
	   return Startup(param.ToParamString(), protectedProtocolIDs, seedServerMode ? 1 : 0, productNum);
   }
   
   
   // 获得内核监听的端口
   public short DLBT_GetListenPort ()
   {
	   return GetListenPort();
   }

	/**
	 * 关闭BT内核，释放所有内核资源
	 */
   public void DLBT_Shutdown ()
   {
	   Shutdown();
   }
   
// 由于关闭的速度可能会比较慢(需要通知Tracker Stop), 所以可以调用该函数提前通知,提高下线速度
// 然后最后在程序最后退出时调用Shutdown等待真正的结束
   public void DLBT_PreShutdown ()
   {
	   PreShutdown();
   }
   
   
 //=======================================================================================
// 内核的上传下载速度、最大连接用户数的设置
//=======================================================================================
//速度限制，单位是字节(BYTE)，如果需要限速1M，请输入 1024*1024
   public void DLBT_SetUploadSpeedLimit (int limit)
   {
	   SetUploadSpeedLimit(limit);
   }
   public void DLBT_SetDownloadSpeedLimit (int limit)
   {
	   SetDownloadSpeedLimit (limit);
   }

//最大连接数（真正完成连接的连接数）
   public void DLBT_SetMaxUploadConnection (int limit)
   {
	   SetMaxUploadConnection(limit);
   }
   public void DLBT_SetMaxTotalConnection (int limit)
   {
	   SetMaxTotalConnection(limit);
   }

//最多发起的连接数（很多连接可能是发起了，但还没连上）
   public void DLBT_SetMaxHalfOpenConnection (int limit)
   {
	   SetMaxHalfOpenConnection(limit);
   }

//用于设置是否对跟自己在同一个局域网的用户限速，limit如果为true，则使用后面参数中的限速数值进行限速，否则不限。默认不对同一个局域网下的用户应用限速。
   public void DLBT_SetLocalNetworkLimit (
   	int     limit,              // 是否启用局域网限速
	    int     downSpeedLimit,     // 如果启用局域网限速，下载限速的大小，单位字节/秒
	    int     uploadSpeedLimit    // 如果启用局域网限速，限制上传速度大小，单位字节/秒
	    )
   {
	   SetLocalNetworkLimit(limit, downSpeedLimit, uploadSpeedLimit);
   }

//设置文件扫描校验时的休息参数，circleCount代表循环多少次做一次休息。默认是0（也就是不休息）
//sleepMs代表休息多久，默认是1ms
   public void DLBT_SetFileScanDelay (int circleCount, int sleepMs)
   {
	   SetFileScanDelay(circleCount, sleepMs);
   }

//设置文件下载完成后，是否修改为原始修改时间（制作种子时每个文件的修改时间状态）。调用该函数后，制作的torrent中会包含有每个文件此时的修改时间信息
//用户在下载时，发现有这个信息，并且调用了该函数后，则会在每个文件完成时，自动将文件的修改时间设置为torrent种子中记录的时间
//如果只是下载的机器上启用了该函数，但制作种子的机器上没有使用该函数（种子中没有每个文件的时间信息），则也无法进行时间修改
   // protected native void UseServerModifyTime(Boolean bUseServerTime);  android版本暂时不支持该接口

//是否启用UDP穿透传输功能，默认是自动适应，如果对方支持，在tcp无法到达时，自动切换为udp通讯
   public void DLBT_EnableUDPTransfer(int bEnabled)
   {
	   EnableUDPTransfer(bEnabled);
   }

//是否启用伪装Http传输，某些地区（比如马来西亚、巴西的一些网络）对Http不限速，但对P2P限速在20K左右，这种网络环境下，可以启用Http传输
// 默认是允许伪装Http的传输进入（可以接受他们的通讯），但自己发起的连接不主动伪装。 如果客户群中有这类用户，可以考虑都设置：主动伪装。
//但这种伪装也有副作用，国内有些地区机房（一般是网通）设置了Http必须使用域名，而不能使用IP，而BT传输中，对方没有合法域名，反而会被这种限制截杀
//如果有这种限制，反而主动伪装后会没有速度。所以请根据实际使用选择。
   public void DLBT_SetP2PTransferAsHttp (int bHttpOut, int bAllowedIn)
   {
	   SetP2PTransferAsHttp (bHttpOut, bAllowedIn);
   }

	// 是否使用单独的穿透服务器，如果不使用单独服务器，穿透的协助将由某个双方都能连上的第三方p2p节点辅助完成
	public int DLBT_AddHoleServer(String ip, short port)
	{
		return AddHoleServer(ip, port);
	}

	// 设置服务器的IP，可以多次调用设置多个，用于标记哪些IP是服务器，以便统计从服务器下载到的数据等信息，甚至速度到了一定程度可以断开服务器连接，节省服务器带宽
	public void DLBT_AddServerIP (String ip)
	{
		AddServerIP(ip);
	}
	// 不去连接这个p2sp的url，可以重复调用. 目的是，如果是服务器上，这个p2sp的url就在本机，就没必要去连接这个url了
	public void DLBT_AddBanServerUrl (String url)
	{
		AddBanServerUrl(url);
	}

	// 保存一次状态文件的条件，内部默认全部下载完成后保存一次。可以调整为自己需要的时间或者上限数目，比如每5分钟保存一次，或者下载100块数据后保存一次
	public int DLBT_SetStatusFileSavePeriod (
			int             iPeriod,               //保存间隔，单位是秒。默认是0，代表除非下载完成，否则永不保存
			int             iPieceCount            //分块数目，默认0，代表除非下载完成，否则永不保存
	)
	{
		return SetStatusFileSavePeriod(iPeriod, iPieceCount);
	}

	//=======================================================================================
//  设置报告Tracker的本机IP，内网下载和供种时设置自己NAT的公网IP会比较有效，详细参考
//  点量BT的使用说明文档
//=======================================================================================
	public void DLBT_SetReportIP (String ip)
	{
		SetReportIP(ip);
	}
	public String DLBT_GetReportIP ()
	{
		return GetReportIP ();
	}

	public void DLBT_SetUserAgent (String agent)
	{
		SetUserAgent (agent);
	}

	//=======================================================================================
//  设置磁盘缓存，3.3版本后已对外开放，3.3版本后系统内部自动设置8M缓存，如需调整可使用该
//  函数进行调整，单位是K，比如要设置1M的缓存，需要传入1024
//=======================================================================================
	public void DLBT_SetMaxCacheSize (int size)
	{
		SetMaxCacheSize (size);
	}

// 一些性能参数设置，默认情况下，点量BT是为了普通网络环境下的上传和下载所用，如果是在千M局域网下
// 并且磁盘性能良好，想获得50M/s甚至100M/s的单个文件传输速度，则需要设置这些参数。或者想节约内存，也可以设置这些参数
// 具体参数的设置建议，请咨询点量软件获取

	public void DLBT_SetPerformanceFactor(
			int             socketRecvBufferSize,      // 网络的接收缓冲区，默认是用操作系统默认的缓冲大小
			int             socketSendBufferSize,      // 网络的发送缓冲区，默认用操作系统的默认大小
			int             maxRecvDiskQueueSize,      // 磁盘如果还未写完，超过这个参数后，将暂停接收，等磁盘数据队列小于该参数
			int             maxSendDiskQueueSize       // 如果小于该参数，磁盘线程将为发送的连接塞入数据，超过后，将暂停磁盘读取
	)
	{
		SetPerformanceFactor(socketRecvBufferSize, socketSendBufferSize, maxRecvDiskQueueSize, maxSendDiskQueueSize);
	}

	//=======================================================================================
//  DHT相关函数,port是DHT监听的端口（udp端口），如果为0则使用内核监听的TCP端口号监听
//=======================================================================================
	public void DLBT_DHT_Start (short port) {DHTStart (port);}
	public void DLBT_DHT_Stop () {DHTStop ();}
	public int DLBT_DHT_IsStarted ()
	{
		return DHTIsStarted();
	}

	public KERNEL_INFO DLBT_GetKernelInfo ()
   {
	   String str = GetKernelInfo();
	   if (str == null)
		   return null;
	   String arry[] = str.split("|");  
	   if (arry.length != 12)
		   return null;
	   
	   KERNEL_INFO kInfo = new KERNEL_INFO();
	   kInfo.Init(arry);
	   return kInfo;
   }
   
   public int DLBT_Downloader_Initialize(
			String				torrentFile,
	        String              outPath,
	        String              statusFile,
	        DLBT_FILE_ALLOCATE_TYPE fileAllocateType, /* = FILE_ALLOCATE_SPARSE*/
	        boolean             bPaused,
	        boolean             bQuickSeed,             // 是否快速供种（专业服务器模式下有效，仅商业版提供，演示版暂不提供）
	        String              password,               // 是否加密种子，如果为Null，则是普通种子，否则是种子的密码
	        String              rootPathName,
	        boolean             bPrivateProtocol,
	        boolean				bZipTransfer)
   {
	   int ret = DownloaderInitialize(
				torrentFile,   
		        outPath,
		        statusFile,
		        DLBT_FILE_ALLOCATE_TYPE.toInt(fileAllocateType), /* = FILE_ALLOCATE_SPARSE*/
		        bPaused ? 1 : 0,
		        bQuickSeed ? 1 : 0,             // 是否快速供种（专业服务器模式下有效，仅商业版提供，演示版暂不提供）
		        password,               // 是否加密种子，如果为Null，则是普通种子，否则是种子的密码
		        rootPathName,
		        bPrivateProtocol ? 1 : 0,
		        bZipTransfer ? 1 : 0
			   );
	   return ret;
   }

    public int DLBT_Downloader_Initialize_FromUrl (
            String				url,
            String              outPath,
            String              statusFile,
            DLBT_FILE_ALLOCATE_TYPE fileAllocateType, /* = FILE_ALLOCATE_SPARSE*/
            boolean             bPaused,
            String              rootPathName,
            boolean             bPrivateProtocol,
            boolean				bZipTransfer)
    {
        return DownloaderInitializeFromUrl (
                url,
                outPath,
                statusFile,
                DLBT_FILE_ALLOCATE_TYPE.toInt(fileAllocateType), /* = FILE_ALLOCATE_SPARSE*/
                bPaused ? 1 : 0,
                rootPathName,
                bPrivateProtocol ? 1 : 0,
                bZipTransfer ? 1 : 0
        );
    }

	public DOWNLOADER_INFO DLBT_GetDownloaderInfo(int hDownloader)
	{
		 String str = GetDownloaderInfo(hDownloader);
		 if (str == null)
			 return null;
		 String[] arry = str.split("\\|"); 
		 if (arry.length < 31)
			 return null;
		 
		 DOWNLOADER_INFO downInfo = new DOWNLOADER_INFO();
		 downInfo.Init(arry);
		 return downInfo;
	}
	
	public String DLBT_Downloader_GetLastError(int hDownloader)
	{
		return DownloaderGetLastError(hDownloader);
	}

	 // 关闭hDownloader所标记的下载任务,如果需要删除文件,可以将第2个参数置为True
	public int DLBT_Downloader_Release (int hDownloader, int bDeleteAllFiles)
	{
		return DownloaderRelease(hDownloader, bDeleteAllFiles);
	}

	//-------------------------------------------------------------------------------------------------------------------------------
	// 关闭任务之前，可以调用该函数停掉IO线程对该任务的操作（异步的，需要调用DownloaderIsReleasingFiles函数来获取是否还在释放中）。
	// 该函数调用后，请直接调用_Release，不可对该句柄再调用其它Dwonloader函数。本函数内部会先暂停所有数据下载，然后释放掉文件句柄
	public void DLBT_DownloaderReleaseAllFiles(int hDownloader)
	{
		DownloaderReleaseAllFiles(hDownloader);
	}
	// 是否还在释放句柄的过程中
	public int DLBT_DownloaderIsReleasingFiles(int hDownloader)
	{
		return DownloaderIsReleasingFiles(hDownloader);
	}

	// 关闭hDownloader所标记的下载任务,如果需要删除文件,可以将第2个参数置为True
	public int DLBT_DownloaderRelease (int hDownloader, int bDeleteAllFiles)
	{
		return DownloaderRelease(hDownloader, bDeleteAllFiles);
	}

	// 增加一个http的地址，当该下载文件在某个Web服务器上有http下载时可以使用，web服务器的编码方式最好为UTF-8，如果是其它格式可以联系点量软件进行修改
	public void DLBT_DownloaderAddHttpDownload (int hDownloader, String url)
	{
		DownloaderAddHttpDownload(hDownloader, url);
	}
	// 移除一个P2SP的地址，如果正在下载中，会进行断开并且从候选者列表中移除，不再进行重试
	public void DLBT_DownloaderRemoveHttpDownload (int hDownloader, String url)
	{
		DownloaderRemoveHttpDownload(hDownloader, url);
	}
	// 设置一个Http地址，最多可以建立多少个连接，默认是1个连接. 如果服务器性能好，可以酌情设置，比如设置10个，则是对一个Http地址，可以建立10个连接。
	// 设置之前如果已经一个Http地址建立好了多个连接，则不再断开，仅对设置后再新建连接时生效
	public void DLBT_DownloaderSetMaxSessionPerHttp (int hDownloader, int limit)
	{
		DownloaderSetMaxSessionPerHttp (hDownloader, limit);
	}

	// 获取本任务所有的Http连接，内存必须调用DownloaderFreeConnections释放
	//public void DLBT_DownloaderGetHttpConnections(int hDownloader, LPSTR ** urls, int * urlCount);
	// 释放DownloaderGetHttpConnections传出的内存
	//public void DLBT_DownloaderFreeConnections(LPSTR * urls, int urlCount);

	public void DLBT_DownloaderAddTracker (int hDownloader, String url, int tier)
	{
		DownloaderAddTracker (hDownloader, url, tier);
	}
	public void DLBT_DownloaderRemoveAllTracker (int hDownloader)
	{
		DownloaderRemoveAllTracker (hDownloader);
	}
	public void DLBT_DownloaderAddHttpTrackerExtraParams (int hDownloader, String extraParams)
	{
		DownloaderAddHttpTrackerExtraParams (hDownloader, extraParams);
	}

	// 设置任务下载是否按顺序下载,默认是非顺序下载(随机的下载,一般遵循稀有者优先,这种方式速度快),但顺序下载适用于边下边播放
	public void DLBT_DownloaderSetDownloadSequence (int hDownloader, int ifSeq)
	{
		DownloaderSetDownloadSequence (hDownloader, ifSeq);
	}

	// 下载的状态 以及 暂停和继续的接口
	public int DLBT_DownloaderGetState (int hDownloader)
	{
		return DownloaderGetState(hDownloader);
	}
	public int DLBT_DownloaderIsPaused (int hDownloader)
	{
		return DownloaderIsPaused(hDownloader);
	}
	public void DLBT_DownloaderPause (int hDownloader)        //暂停
	{
		DownloaderPause(hDownloader);
	}
	public void DLBT_DownloaderResume (int hDownloader)
	{
		DownloaderResume(hDownloader);
	}
	//出错状态下的两个接口 （一般只有在极其特殊情况下文件无法写入时才会出错，比如磁盘满了）
	//如果任务的状态为BTDS_ERROR，通过该接口获取详细错误信息
	public String DLBT_DownloaderGetLastError (int hDownloader)
	{
		return DownloaderGetLastError(hDownloader);
	}
	public void DLBT_DownloaderResumeInError (int hDownloader) //清除这个错误，尝试重新开始任务
	{
		DownloaderResumeInError(hDownloader);
	}

	// 无种子下载的相关接口（无种子模式在试用版中无效，可以调用这些接口，但不会有效果）
	public int DLBT_DownloaderIsHaveTorrentInfo (int hDownloader) // 无种子下载时，用于判断是否成功获取到了种子信息
	{
		return DownloaderIsHaveTorrentInfo(hDownloader);
	}
	public String DLBT_DownloaderMakeURL (  // 通过种子，制作一个可以不需要种子即可下载的网址，参考DownloaderInitialize_FromUrl
												 int      hDownloader
	)
	{
		return DownloaderMakeURL(hDownloader);
	}
	// 无种子下载，如果已经下载到了种子，可以利用这个函数将种子保存起来，以后就能使用了
	public int DLBT_DownloaderSaveTorrentFile (int hDownloader, String filePath, String password)
	{
		return DownloaderSaveTorrentFile(hDownloader, filePath, password);
	}

	// 下载的限速和限制连接的接口
	public void DLBT_DownloaderSetDownloadLimit (int hDownloader, int limit)
	{
		DownloaderSetDownloadLimit(hDownloader, limit);
	}
	public void DLBT_DownloaderSetUploadLimit (int hDownloader, int limit)
	{
		DownloaderSetUploadLimit(hDownloader, limit);
	}
	public void DLBT_DownloaderSetMaxUploadConnections (int hDownloader, int limit)
	{
		DownloaderSetMaxUploadConnections(hDownloader, limit);
	}
	public void DLBT_DownloaderSetMaxTotalConnections (int hDownloader, int limit)
	{
		DownloaderSetMaxTotalConnections(hDownloader, limit);
	}

	// 设置对服务器IP进行下载限速，单位是BYTE(字节），如果需要限速1M，请输入1024*1024
	public void DLBT_DownloaderSetServerDownloadLimit(int hDownloader, int limit)
	{
		DownloaderSetServerDownloadLimit(hDownloader, limit);
	}
	// 设置本任务不再去跟所有的服务器IP建立连接（对于对方连过来的连接，需要BT协议握手通过后，知道是对应于这个下载任务hDownloader的后才再断开）。
	public void DLBT_DownloaderBanServerDownload(int hDownloader, int bBan)
	{
		DownloaderBanServerDownload(hDownloader, bBan);
	}

	// 下载分享率 (上传/下载的比例）的接口
	public void DLBT_DownloaderSetShareRateLimit (int hDownloader, float fRate)
	{
		DownloaderSetShareRateLimit(hDownloader, fRate);
	}
	public double DLBT_DownloaderGetShareRate (int hDownloader)
	{
		return DownloaderGetShareRate(hDownloader);
	}

	// 正在下载的文件的属性（文件大小、完成数、进度等）
	public String DLBT_DownloaderGetTorrentName (int hDownloader)
	{
		return DownloaderGetTorrentName(hDownloader);
	}
	public double DLBT_DownloaderGetTotalFileSize (int hDownloader)
	{
		return DownloaderGetTotalFileSize(hDownloader);
	}
	public double DLBT_DownloaderGetTotalWanted (int hDownloader)     // 共有选择了多少下载量，不包含不想下载的文件
	{
		return DownloaderGetTotalWanted(hDownloader);
	}
	public double DLBT_DownloaderGetTotalWantedDone (int hDownloader) // 在选定的文件中，下载了多少
	{
		return DownloaderGetTotalWantedDone(hDownloader);
	}
	public float DLBT_DownloaderGetProgress (int hDownloader)
	{
		return DownloaderGetProgress (hDownloader);
	}

	public double DLBT_DownloaderGetDownloadedBytes (int hDownloader)
	{
		return DownloaderGetDownloadedBytes (hDownloader);
	}
	public double DLBT_DownloaderGetUploadedBytes (int hDownloader)
	{
		return DownloaderGetUploadedBytes (hDownloader);
	}
	public int DLBT_DownloaderGetDownloadSpeed (int hDownloader)
	{
		return DownloaderGetDownloadSpeed(hDownloader);
	}
	public int DLBT_DownloaderGetUploadSpeed (int hDownloader)
	{
		return DownloaderGetUploadSpeed (hDownloader);
	}

	// 获得该任务的节点的数目，数目的参数为int的指针，如果不想要某个值，则传NULL
 /*public void DLBT_DownloaderGetPeerNums (
     int      hDownloader,        // 下载任务的句柄
     int     *   connectedCount,     // 该任务连接上的节点数（用户数）
     int     *   totalSeedCount,     // 总的种子数目，如果Tracker不支持scrap，则返回-1
     int     *   seedsConnected,     // 自己连上的种子数
     int     *   inCompleteCount,    // 未下完的人数，如果Tracker不支持scrap，则返回-1
     int     *   totalCurrentSeedCount, // 当前在线的总的下载完成的人数（包括连上的和未连上的）
     int     *   totalCurrentPeerCount  // 当前在线的总的下载的人数（包括连上的和未连上的）
     );
 */

	// 单个种子中包含多个文件时的一些接口,index为文件的序列号，从0开始
	public int DLBT_DownloaderGetFileCount (int hDownloader)
	{
		return DownloaderGetFileCount (hDownloader);
	}
	public double DLBT_DownloaderGetFileSize (int hDownloader, int index)
	{
		return DownloaderGetFileSize(hDownloader, index);
	}
	public int DLBT_DownloaderIsPadFile (int hDownloader, int index)
	{
		return DownloaderIsPadFile (hDownloader, index);
	}
	public String DLBT_DownloaderGetFilePathName (
			int      hDownloader,        // 下载任务的句柄
			int         index,              // 文件的序号
			int        needFullPath// 是否需要全部的路径还是只需要文件在种子中的相对路径
	)
	{
		return DownloaderGetFilePathName(hDownloader, index, needFullPath);
	}

	// 该函数会将下载目录下存在，但torrent记录中不存在的文件全部删除，对单个文件的种子无效。请慎重使用。
	public int DLBT_DownloaderDeleteUnRelatedFiles (int hDownloader)
	{
		return DownloaderDeleteUnRelatedFiles (hDownloader);
	}

	// 获取每个文件的Hash值，只有制作种子时使用bUpdateExt才能获取到
	public String DLBT_DownloaderGetFileHash (
			int      hDownloader,        // 下载任务的句柄
			int         index              // 要获取的文件的序号，piece的数目可以通过DownloaderGetFileCount获得
	)
	{
		return DownloaderGetFileHash(hDownloader, index);
	}

	// 取文件的下载进度，该操作需要进行较多操作，建议仅在必要时使用
	public float DLBT_DownloaderGetFileProgress (int hDownloader, int index)
	{
		return DownloaderGetFileProgress(hDownloader, index);
	}

	// 设置文件的下载优先级，比如可以用于取消某个指定文件的下载,index表示文件的序号
	public int DLBT_DownloaderSetFilePrioritize (
			int                  hDownloader,
			int                     index,              // 文件序号
			int                    prioritize,         // 优先级
			int                    bDoPriority  // 是否立即应用这个设置（如果有多个文件需要设置，建议暂时不立即应用，让最后一个文件应用设置
			// 或者可以主动调用DownloaderApplyPrioritize函数来应用，因为每应用一次设置都要对所有Piece
			// 操作一遍，比较麻烦，所以应该一起应用
	)
	{
		return DownloaderSetFilePrioritize(hDownloader, index, prioritize, bDoPriority);
	}

	// 立即应用优先级的设置
	public void DLBT_DownloaderApplyPrioritize (int hDownloader)
	{
		DownloaderApplyPrioritize(hDownloader);
	}

	// 获取当前每个分块的状态，比如可以用于判断是否需要去更新（是否已经拥有了该块）。
	public String DLBT_DownloaderGetPiecesStatus (
			int                  hDownloader
	)
	{
		return DownloaderGetPiecesStatus(hDownloader);
	}

	// 设置Piece（分块）的下载优先级，比如可以用于取消某些分块的下载，从指定位置开始下载等。index表示分块的序号
	public int DLBT_DownloaderSetPiecePrioritize (
			int                  hDownloader,
			int                     index,              // 块的序号
			int                    prioritize,         // 优先级
			int                    bDoPriority  // 是否立即应用这个设置（如果有多个分块需要设置，建议暂时不立即应用，让最后一个块应用设置
			// 或者可以主动调用DownloaderApplyPrioritize函数来应用，因为每应用一次设置都要对所有Piece
			// 操作一遍，比较麻烦，所以应该一起应用
	)
	{
		return DownloaderSetPiecePrioritize(hDownloader, index, prioritize, bDoPriority);
	}

	// 设置手工指定的Peer信息
	public void DLBT_DownloaderAddPeerSource (int hDownloader, String ip, short port)
	{
		DownloaderAddPeerSource(hDownloader, ip, port);
	}


	// 获得可显示的文件Hash值
	public String DLBT_DownloaderGetInfoHash (int hDownloader)
	{
		return DownloaderGetInfoHash(hDownloader);
	}

	public int DLBT_DownloaderGetPieceCount (int hDownloader)
	{
		return DownloaderGetPieceCount(hDownloader);
	}
	public int DLBT_DownloaderGetPieceSize (int hDownloader)
	{
		return DownloaderGetPieceSize(hDownloader);
	}

	// 主动保存一次状态文件，通知内部的下载线程后立即返回，是异步操作，可能会有一点延迟才会写
	public void DLBT_DownloaderSaveStatusFile (int hDownloader)
	{
		DownloaderSaveStatusFile(hDownloader);
	}
}
