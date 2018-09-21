package cn.dolit.DLBT;

import android.content.Context;
import android.util.Log;

public class DLL {
	protected static final String TAG = "Dolit/";	
	protected Context m_applicationContext;
	
    /* Load library before object instantiation */
    static {       
        try {
            System.loadLibrary("DLBT");
            System.loadLibrary("DLBT_API");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Can't load DLBT library: " + ule);
        } catch (SecurityException se) {
            Log.e(TAG, "Encountered a security issue when loading DLBT library: " + se);
        }
    }       

    
    // 解析组件的so层接口
    
    // 初始化，主要是传入序列号
    protected native int Init(String key1, String key2, Object context);
    
    // 由外部接管IO操作，一般不特殊需要，不建议设置
    protected native int SetIOWrapper(String wrapperClsName);
    
    /**
	  * 启动BT内核，初始化运行环境,返回值表示是否成功, 
	  * @return	 整型4个字节的返回值  ；1(TRUE)代表成功， 0代表失败
	  */
    protected native int Startup(
			    String param, // 内核启动设置，参考KERNEL_START_PARAM，如果为NULL，则使用内部默认设置
			    String protectedProtocolIDs,  // 可以自定义私有协议，突破运营商限制。如果为NULL，则作为标准的BT客户端启动
			                                        // 点量BT的私有协议在3.4版本中进行了全新改进，可以穿透大部分运营商对协议的封锁
			    int    seedServerMode,     // 是否上传模式，上传模式内部有些参数进行了优化，适合大并发上传，但不建议普通客户端启用，只建议上传服务器使用
			                                        // 专业上传服务器模式仅在商业版中有效，演示版暂不支持该功能。详见使用说明文档
			    String  productNum           // 商业版用户的数字ID，在购买后作者会提供一个产品密钥，激活商业版功能，试用版用户请使用NULL。 
			    );
    
    // 获得内核监听的端口
    protected native short GetListenPort ();

	/**
	 * 关闭BT内核，释放所有内核资源
	 */
    protected native void Shutdown ();
    
 // 由于关闭的速度可能会比较慢(需要通知Tracker Stop), 所以可以调用该函数提前通知,提高下线速度
 // 然后最后在程序最后退出时调用Shutdown等待真正的结束
    protected native void PreShutdown ();
    
    
    
    
  //=======================================================================================
//  内核的上传下载速度、最大连接用户数的设置
//=======================================================================================
// 速度限制，单位是字节(BYTE)，如果需要限速1M，请输入 1024*1024
    protected native void SetUploadSpeedLimit (int limit);
    protected native void SetDownloadSpeedLimit (int limit);

// 最大连接数（真正完成连接的连接数）
    protected native void SetMaxUploadConnection (int limit);
    protected native void SetMaxTotalConnection (int limit);

// 最多发起的连接数（很多连接可能是发起了，但还没连上）
    protected native void SetMaxHalfOpenConnection (int limit);

// 用于设置是否对跟自己在同一个局域网的用户限速，limit如果为true，则使用后面参数中的限速数值进行限速，否则不限。默认不对同一个局域网下的用户应用限速。
    protected native void SetLocalNetworkLimit (
    	int     limit,              // 是否启用局域网限速
	    int     downSpeedLimit,     // 如果启用局域网限速，下载限速的大小，单位字节/秒
	    int     uploadSpeedLimit    // 如果启用局域网限速，限制上传速度大小，单位字节/秒
	    );  

// 设置文件扫描校验时的休息参数，circleCount代表循环多少次做一次休息。默认是0（也就是不休息）
// sleepMs代表休息多久，默认是1ms
    protected native void SetFileScanDelay (int circleCount, int sleepMs);  

// 设置文件下载完成后，是否修改为原始修改时间（制作种子时每个文件的修改时间状态）。调用该函数后，制作的torrent中会包含有每个文件此时的修改时间信息
// 用户在下载时，发现有这个信息，并且调用了该函数后，则会在每个文件完成时，自动将文件的修改时间设置为torrent种子中记录的时间
// 如果只是下载的机器上启用了该函数，但制作种子的机器上没有使用该函数（种子中没有每个文件的时间信息），则也无法进行时间修改
    // protected native void UseServerModifyTime(Boolean bUseServerTime);  android版本暂时不支持该接口

// 是否启用UDP穿透传输功能，默认是自动适应，如果对方支持，在tcp无法到达时，自动切换为udp通讯
    protected native void EnableUDPTransfer(int bEnabled);

// 是否启用伪装Http传输，某些地区（比如马来西亚、巴西的一些网络）对Http不限速，但对P2P限速在20K左右，这种网络环境下，可以启用Http传输
//  默认是允许伪装Http的传输进入（可以接受他们的通讯），但自己发起的连接不主动伪装。 如果客户群中有这类用户，可以考虑都设置：主动伪装。
// 但这种伪装也有副作用，国内有些地区机房（一般是网通）设置了Http必须使用域名，而不能使用IP，而BT传输中，对方没有合法域名，反而会被这种限制截杀
// 如果有这种限制，反而主动伪装后会没有速度。所以请根据实际使用选择。
    protected native void SetP2PTransferAsHttp (int bHttpOut, int bAllowedIn);

// 是否使用单独的穿透服务器，如果不使用单独服务器，穿透的协助将由某个双方都能连上的第三方p2p节点辅助完成
    protected native int AddHoleServer(String ip, short port);

// 设置服务器的IP，可以多次调用设置多个，用于标记哪些IP是服务器，以便统计从服务器下载到的数据等信息，甚至速度到了一定程度可以断开服务器连接，节省服务器带宽
    protected native void AddServerIP (String ip);
// 不去连接这个p2sp的url，可以重复调用. 目的是，如果是服务器上，这个p2sp的url就在本机，就没必要去连接这个url了
    protected native void AddBanServerUrl (String url);

// 保存一次状态文件的条件，内部默认全部下载完成后保存一次。可以调整为自己需要的时间或者上限数目，比如每5分钟保存一次，或者下载100块数据后保存一次
    protected native int SetStatusFileSavePeriod (
    int             iPeriod,               //保存间隔，单位是秒。默认是0，代表除非下载完成，否则永不保存
    int             iPieceCount            //分块数目，默认0，代表除非下载完成，否则永不保存
    );

//=======================================================================================
//  设置报告Tracker的本机IP，内网下载和供种时设置自己NAT的公网IP会比较有效，详细参考
//  点量BT的使用说明文档
//=======================================================================================
    protected native void SetReportIP (String ip);
    protected native String GetReportIP ();

    protected native void SetUserAgent (String agent);

//=======================================================================================
//  设置磁盘缓存，3.3版本后已对外开放，3.3版本后系统内部自动设置8M缓存，如需调整可使用该
//  函数进行调整，单位是K，比如要设置1M的缓存，需要传入1024
//=======================================================================================
    protected native void SetMaxCacheSize (int size);

// 一些性能参数设置，默认情况下，点量BT是为了普通网络环境下的上传和下载所用，如果是在千M局域网下
// 并且磁盘性能良好，想获得50M/s甚至100M/s的单个文件传输速度，则需要设置这些参数。或者想节约内存，也可以设置这些参数
// 具体参数的设置建议，请咨询点量软件获取

    protected native void SetPerformanceFactor(
    int             socketRecvBufferSize,      // 网络的接收缓冲区，默认是用操作系统默认的缓冲大小
    int             socketSendBufferSize,      // 网络的发送缓冲区，默认用操作系统的默认大小
    int             maxRecvDiskQueueSize,      // 磁盘如果还未写完，超过这个参数后，将暂停接收，等磁盘数据队列小于该参数
    int             maxSendDiskQueueSize       // 如果小于该参数，磁盘线程将为发送的连接塞入数据，超过后，将暂停磁盘读取
    );

//=======================================================================================
//  DHT相关函数,port是DHT监听的端口（udp端口），如果为0则使用内核监听的TCP端口号监听
//=======================================================================================
    protected native void DHTStart (short port);
    protected native void DHTStop ();
    protected native int DHTIsStarted ();

    
	// ***************************  以下几个都是启动一个BT下载的函数，但功能上略有区别 ********************************
	//=======================================================================================
	//  启动一个文件的下载，返回这个下载的句柄，以后对该下载任务的所有操作，需要根据句柄来进行
	//=======================================================================================
    protected native int DownloaderInitialize (
	        String             torrentFile,                    // 种子文件的路径（具体到文件名）
	        String             outPath,                        // 下载后的保存路径（只是目录）
	        String             statusFile,           // 状态文件的路径
	        int   fileAllocateType,         // 文件分配模式
	        int                bPaused,                // 是否不立即运行下载任务，打开句柄后暂停任务的启动
	        int                bQuickSeed,             // 是否快速供种（仅商业版提供）
	        String             password,                // 是否加密种子，如果为Null，则是普通种子，否则是种子的密码，试用版不支持，该参数会被忽略
	        String             rootPathName,             // 种子内部目录的名字，如果为NULL，则用种子中的名字，否则改为指定的这个名字。
	                                                            // 对单个文件则直接进行改名为指定的这个名字
	        int                bprotectedProtocol,        // 该种子是否私有协议（可以对不同种子采用不同的下载方式）
            int				bZipTransfer			// 是否压缩传输，一般用于文本文件的下载，如果对方是支持这个功能的dlbt用户，则可以互相压缩传输，减少流量
	        );

    // 无种子模式的另一个接口，可以直接通过地址下载，地址格式为： DLBT://xt=urn:btih: Base32 编码过的info-hash [ &dn= Base32后的名字 ] [ &tr= Base32后的tracker的地址 ]  ([]为可选参数)
// 完全遵循uTorrent的官方BT扩展协议
    protected native int DownloaderInitializeFromUrl (
            String              url,                            // 网址
            String             outPath,                        // 保存目录
            String             statusFile,              // 状态文件的路径
            int                 fileAllocateType,
            int                bPaused,
            String             rootPathName,            // 种子内部目录的名字，如果为NULL，则用种子中的名字，否则改为指定的这个名字。
            // 对单个文件则直接进行改名为指定的这个名字
            int                bprotectedProtocol,        // 该种子是否私有协议（可以对不同种子采用不同的下载方式）
            int				bZipTransfer			// 是否压缩传输，一般用于文本文件的下载，如果对方是支持这个功能的dlbt用户，则可以互相压缩传输，减少流量
    );
	
	    
  //-------------------------------------------------------------------------------------------------------------------------------
 // 关闭任务之前，可以调用该函数停掉IO线程对该任务的操作（异步的，需要调用DownloaderIsReleasingFiles函数来获取是否还在释放中）。
 // 该函数调用后，请直接调用_Release，不可对该句柄再调用其它Dwonloader函数。本函数内部会先暂停所有数据下载，然后释放掉文件句柄
 protected native void DownloaderReleaseAllFiles(int hDownloader);
 // 是否还在释放句柄的过程中
 protected native int DownloaderIsReleasingFiles(int hDownloader);

 // 关闭hDownloader所标记的下载任务,如果需要删除文件,可以将第2个参数置为True
 protected native int DownloaderRelease (int hDownloader, int bDeleteAllFiles);

 // 增加一个http的地址，当该下载文件在某个Web服务器上有http下载时可以使用，web服务器的编码方式最好为UTF-8，如果是其它格式可以联系点量软件进行修改
 protected native void DownloaderAddHttpDownload (int hDownloader, String url);
 // 移除一个P2SP的地址，如果正在下载中，会进行断开并且从候选者列表中移除，不再进行重试
 protected native void DownloaderRemoveHttpDownload (int hDownloader, String url);
 // 设置一个Http地址，最多可以建立多少个连接，默认是1个连接. 如果服务器性能好，可以酌情设置，比如设置10个，则是对一个Http地址，可以建立10个连接。
 // 设置之前如果已经一个Http地址建立好了多个连接，则不再断开，仅对设置后再新建连接时生效
 protected native void DownloaderSetMaxSessionPerHttp (int hDownloader, int limit);

 // 获取本任务所有的Http连接，内存必须调用DownloaderFreeConnections释放
 //protected native void DownloaderGetHttpConnections(int hDownloader, LPSTR ** urls, int * urlCount);
 // 释放DownloaderGetHttpConnections传出的内存
 //protected native void DownloaderFreeConnections(LPSTR * urls, int urlCount);

 protected native void DownloaderAddTracker (int hDownloader, String url, int tier);
 protected native void DownloaderRemoveAllTracker (int hDownloader);
 protected native void DownloaderAddHttpTrackerExtraParams (int hDownloader, String extraParams);

 // 设置任务下载是否按顺序下载,默认是非顺序下载(随机的下载,一般遵循稀有者优先,这种方式速度快),但顺序下载适用于边下边播放
 protected native void DownloaderSetDownloadSequence (int hDownloader, int ifSeq);

 // 下载的状态 以及 暂停和继续的接口
 protected native int DownloaderGetState (int hDownloader);
 protected native int DownloaderIsPaused (int hDownloader);
 protected native void DownloaderPause (int hDownloader);        //暂停
 protected native void DownloaderResume (int hDownloader);       //继续
 //出错状态下的两个接口 （一般只有在极其特殊情况下文件无法写入时才会出错，比如磁盘满了）
 //如果任务的状态为BTDS_ERROR，通过该接口获取详细错误信息
 protected native String DownloaderGetLastError (int hDownloader); 
 protected native void DownloaderResumeInError (int hDownloader); //清除这个错误，尝试重新开始任务

 // 无种子下载的相关接口（无种子模式在试用版中无效，可以调用这些接口，但不会有效果）
 protected native int DownloaderIsHaveTorrentInfo (int hDownloader); // 无种子下载时，用于判断是否成功获取到了种子信息
 protected native String DownloaderMakeURL (  // 通过种子，制作一个可以不需要种子即可下载的网址，参考DownloaderInitialize_FromUrl
     int      hDownloader
     ); 
 // 无种子下载，如果已经下载到了种子，可以利用这个函数将种子保存起来，以后就能使用了
 protected native int DownloaderSaveTorrentFile (int hDownloader, String filePath, String password);

 // 下载的限速和限制连接的接口
 protected native void DownloaderSetDownloadLimit (int hDownloader, int limit);
 protected native void DownloaderSetUploadLimit (int hDownloader, int limit);
 protected native void DownloaderSetMaxUploadConnections (int hDownloader, int limit);
 protected native void DownloaderSetMaxTotalConnections (int hDownloader, int limit);

 // 设置对服务器IP进行下载限速，单位是BYTE(字节），如果需要限速1M，请输入1024*1024
 protected native void DownloaderSetServerDownloadLimit(int hDownloader, int limit);
 // 设置本任务不再去跟所有的服务器IP建立连接（对于对方连过来的连接，需要BT协议握手通过后，知道是对应于这个下载任务hDownloader的后才再断开）。
 protected native void DownloaderBanServerDownload(int hDownloader, int bBan);

 // 下载分享率 (上传/下载的比例）的接口
 protected native void DownloaderSetShareRateLimit (int hDownloader, float fRate);
 protected native double DownloaderGetShareRate (int hDownloader);

 // 正在下载的文件的属性（文件大小、完成数、进度等）
 protected native String DownloaderGetTorrentName (int hDownloader);
 protected native double DownloaderGetTotalFileSize (int hDownloader);
 protected native double DownloaderGetTotalWanted (int hDownloader);     // 共有选择了多少下载量，不包含不想下载的文件
 protected native double DownloaderGetTotalWantedDone (int hDownloader); // 在选定的文件中，下载了多少
 protected native float DownloaderGetProgress (int hDownloader);

 protected native double DownloaderGetDownloadedBytes (int hDownloader);
 protected native double DownloaderGetUploadedBytes (int hDownloader);
 protected native int DownloaderGetDownloadSpeed (int hDownloader);
 protected native int DownloaderGetUploadSpeed (int hDownloader);

 // 获得该任务的节点的数目，数目的参数为int的指针，如果不想要某个值，则传NULL
 /*protected native void DownloaderGetPeerNums (
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
 protected native int DownloaderGetFileCount (int hDownloader);
 protected native double DownloaderGetFileSize (int hDownloader, int index);
 protected native int DownloaderIsPadFile (int hDownloader, int index);
 protected native String DownloaderGetFilePathName (
     int      hDownloader,        // 下载任务的句柄
     int         index,              // 文件的序号
     int        needFullPath// 是否需要全部的路径还是只需要文件在种子中的相对路径    
     );

 // 该函数会将下载目录下存在，但torrent记录中不存在的文件全部删除，对单个文件的种子无效。请慎重使用。
 protected native int DownloaderDeleteUnRelatedFiles (int hDownloader);

 // 获取每个文件的Hash值，只有制作种子时使用bUpdateExt才能获取到
 protected native String DownloaderGetFileHash (
 	int      hDownloader,        // 下载任务的句柄
 	int         index              // 要获取的文件的序号，piece的数目可以通过DownloaderGetFileCount获得
 	);

 // 取文件的下载进度，该操作需要进行较多操作，建议仅在必要时使用
 protected native float DownloaderGetFileProgress (int hDownloader, int index);


 // 设置文件的下载优先级，比如可以用于取消某个指定文件的下载,index表示文件的序号
 protected native int DownloaderSetFilePrioritize (
     int                  hDownloader, 
     int                     index,              // 文件序号
     int                    prioritize,         // 优先级
     int                    bDoPriority  // 是否立即应用这个设置（如果有多个文件需要设置，建议暂时不立即应用，让最后一个文件应用设置
                                                 // 或者可以主动调用DownloaderApplyPrioritize函数来应用，因为每应用一次设置都要对所有Piece
                                                 // 操作一遍，比较麻烦，所以应该一起应用
     );

 // 立即应用优先级的设置
 protected native void DownloaderApplyPrioritize (int hDownloader);

 // 获取当前每个分块的状态，比如可以用于判断是否需要去更新（是否已经拥有了该块）。
 protected native String DownloaderGetPiecesStatus (
     int                  hDownloader
     );

 // 设置Piece（分块）的下载优先级，比如可以用于取消某些分块的下载，从指定位置开始下载等。index表示分块的序号
 protected native int DownloaderSetPiecePrioritize (
     int                  hDownloader, 
     int                     index,              // 块的序号
     int                    prioritize,         // 优先级
     int                    bDoPriority  // 是否立即应用这个设置（如果有多个分块需要设置，建议暂时不立即应用，让最后一个块应用设置
                                                 // 或者可以主动调用DownloaderApplyPrioritize函数来应用，因为每应用一次设置都要对所有Piece
                                                 // 操作一遍，比较麻烦，所以应该一起应用
     );

 // 设置手工指定的Peer信息
 protected native void DownloaderAddPeerSource (int hDownloader, String ip, short port);


 // 获得可显示的文件Hash值
 protected native String DownloaderGetInfoHash (int hDownloader);

 protected native int DownloaderGetPieceCount (int hDownloader);
 protected native int DownloaderGetPieceSize (int hDownloader);

 // 主动保存一次状态文件，通知内部的下载线程后立即返回，是异步操作，可能会有一点延迟才会写
 protected native void DownloaderSaveStatusFile (int hDownloader);

 // 向BT系统中写入通过其它方式接收来的数据块。offset为该数据块在整个文件（文件夹）中的偏移量，size为数据块大小，data为数据缓冲区
 // 成功返回S_OK，失败为其它，失败原因可能是该块不需要再次传输了。 本函数仅VOD增强版中有效
 //protected native int DownloaderAddPartPieceData(int hDownloader, double offset, double size, char *data);

 // 手工添加一块完整的数据进来 本函数仅VOD增强版中有效
 /*protected native int DownloaderAddPieceData(
     int                  hDownloader,
     int                     piece,          //分块序号
     char            *       data,           //数据本身
     Boolean                    bOverWrite      //如果已经有了，是否覆盖
     );*/


 /*
 // 每次要替换一个数据块时调用一下这个回调，外部可以根据这个回调显示替换的进度；以及是否终止整个替换工作（功能相当于：DownloaderCancelReplace)
 // 返回FALSE代表希望立即终止，TRUE代表继续。
 // 一个分块可能会包括多个小文件（或者大文件的尾部，多文件粘连的地方），因此一个分块pieceIndex可能会对应了多个文件片段。本回调是替换一个文件片段时触发一次
 typedef Boolean (* REPLACE_PROGRESS_CALLBACK) (
       IN void * pContext,                   // 回调的上下文（通过DownloaderReplacePieceData的pContext参数传入），这里传回去，便于外面处理
                                             // 比如，外部传入一个this指针，回调的时候再通过这个指针知道是对应哪个对象
       IN int pieceIndex,                    //本次要替换的数据位于哪个分块（分块在torrent中的索引）
       IN int replacedPieceCount,            //已经完成了多少个piece的替换
       IN int totalNeedReplacePieceCount,    //总共有多少个要替换的piece分块
       IN int fileIndex,                     //本次要替换的数据位于哪个文件
       IN UINT64 offset,                     //在这个文件中，这块数据的偏移量
       IN UINT64 size,                       //这块数据的总大小（距离偏移量）
       IN int replacedFileSliceCount,        //已经完成了多少个文件片段的替换                                  
       IN int totalFileSliceCount            //总共有多少个需要替换的文件片段
       );

 // 替换数据块的接口：将某块数据直接替换到目标文件的相同位置，一般用于：下载时将需要下载的分块自动下载到一个临时目录，完成后再替换回原始文件
 // 这样下载过程中原始文件可以正常使用，并保留了只下载部分数据的优点。
 // 该函数是立即返回，如果HRESULT返回的不是S_OK，说明出错，需要查看返回值。
 // 如果返回S_OK，则内部会启动线程来进行替换，中间的结果随时通过DownloaderGetReplaceResult来进行查看结果。随时可以调用：DownloaderCancelReplace进行取消线程操作
 protected native HRESULT DownloaderReplacePieceData(
 	HANDLE			hDownloader,		//下载任务句柄
 	int   *			pieceArray,			// 需要将哪些分块替换，是一个int数组
 	int				arrayLength,		// 分块数组的长度
 	LPCWSTR			destFilePath,		// 需要替换的文件（文件夹）的目录。比如：E:\Test\1.rar或者E:\Test\Game\天龙八部 等。
 	LPCWSTR			tempRootPathName = NULL,	// 临时目录下载时，如果使用了rootPathName，则这里也要设置上，以便从这个文件（文件夹）下读取数据块
 	LPCWSTR			destRootPathName = NULL,	// 需要替换的那个下载任务，如果使用了rootPathName，则这里也要设置上，以便对这个文件（文件夹）进行替换
     LPVOID          pContext = NULL,
     REPLACE_PROGRESS_CALLBACK  callback = NULL  //接收进度，并可以随时取消的回调
 	);

 // ReplacePieceData的一些状态，可以通过DownloaderGetReplaceResult来进行查看
 enum REPLACE_RESULT
 {
     RPL_IDLE  = 0,     //尚未开始替换
     RPL_RUNNING,       //正在运行中
     RPL_SUCCESS,       //替换成功
     RPL_USER_CANCELED, //替换了一半，用户取消掉了
     RPL_ERROR,         //出错，可以通过hrDetail来获取详细信息，参考：DownloaderGetReplaceResult
 };

 // 获取替换数据的结果
 protected native REPLACE_RESULT DownloaderGetReplaceResult(
     int          hDownloader,        //下载任务句柄
     HRESULT  *      hrDetail,           //如果是有出错，返回详细的出错原因
     Boolean     *      bThreadRunning      //Replace的整个操作是否结束了（出错也会立即结束的）
     );

 // 中间随时取消替换数据的操作（不建议取消，因为有可能会替换到一半，这样有些文件是不完整的）
 protected native void DownloaderCancelReplace(int hDownloader);

 //////////////////////   Move的相关接口   /////////////
 // Move的结果
 enum DownloaderMOVE_RESULT
 {
 	MOVED	= 0,	//移动成功
 	MOVE_FAILED,	//移动失败
 	MOVING         //正在移动
 };

 //移动到哪个目录，如果是同一磁盘分区，是剪切；如果是不同分区，是复制后删除原始文件。由于操作是异步操作，所以立即返回
 //结果使用DownloaderGetMoveResult去获取
 protected native void DownloaderMove (int hDownloader, LPCWSTR savePath);
 //查看移动操作的结果
 protected native DownloaderMOVE_RESULT DownloaderGetMoveResult (
 	HANDLE			hDownloader,   // 下载任务的句柄
 	LPSTR			errorMsg,      // 用于返回出错信息的内存，在MOVE_FAILED状态下这里返回出错的详情。如果传入NULL，则不返回错误信息
 	int				msgSize		   // 出错信息内存的大小
 	); 
*/

	
	
	// 获取批量信息的接口
    protected native String GetKernelInfo ();	
    protected native String GetDownloaderInfo (int hDownloader);
		
}
