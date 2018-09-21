package cn.dolit.DLBT;

//任务的状态信息
public enum DLBT_DOWNLOAD_STATE
{	
	BTDS_QUEUED,	                // 已添加	
	BTDS_CHECKING_FILES,	        // 正在检查校验文件
	BTDS_DOWNLOADING_TORRENT,	    // 无种子模式下，正在获取种子的信息	
	BTDS_DOWNLOADING,	            // 正在下载中
	BTDS_PAUSED,                    // 暂停
	BTDS_FINISHED,	                // 指定的文件下载完成
	BTDS_SEEDING,	                // 供种中（种子中的所有文件下载完成） 	
	BTDS_ALLOCATING,                // 正在预分配磁盘空间 -- 预分配空间，减少磁盘碎片，和 
	                  				// 启动选项有关，启动时如果选择预分配磁盘方式，可能进入该状态
	BTDS_ERROR;                     // 出错，可能是写磁盘出错等原因，详细原因可以通过调用DLBT_Downloader_GetLastError获知
	
	
	 public static DLBT_DOWNLOAD_STATE fromInteger(int x)
	 {
      switch(x) 
      {
	        case 0:
	            return BTDS_QUEUED;
	        case 1:
	            return BTDS_CHECKING_FILES;
	        case 2:
	            return BTDS_DOWNLOADING_TORRENT;
	        case 3:
	            return BTDS_DOWNLOADING;
	        case 4:
	        	return BTDS_PAUSED;
	        case 5:
	        	return BTDS_FINISHED;
	        case 6:
	        	return BTDS_SEEDING;
	        case 7:
	        	return BTDS_ALLOCATING;
	        case 8:
	        	return BTDS_ERROR;
      }
      return BTDS_ERROR;
	 }
	 
	 public static int toInt(DLBT_DOWNLOAD_STATE x)
	 {
      switch(x) 
      {
	        case BTDS_QUEUED:
	            return 0;
	        case BTDS_CHECKING_FILES:
	            return 1;
	        case BTDS_DOWNLOADING_TORRENT:
	            return 2;
	        case BTDS_DOWNLOADING:
	            return 3;
	        case BTDS_PAUSED:
	        	return 4;
	        case BTDS_FINISHED:
	        	return 5;
	        case BTDS_SEEDING:
	        	return 6;
	        case BTDS_ALLOCATING:
	        	return 7;
	        case BTDS_ERROR:
	        	return 8;
      }
      return 8;
	 }
}