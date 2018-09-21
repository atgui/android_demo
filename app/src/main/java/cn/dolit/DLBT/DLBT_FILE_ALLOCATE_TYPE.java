package cn.dolit.DLBT;

public enum DLBT_FILE_ALLOCATE_TYPE {
	FILE_ALLOCATE_REVERSED,   		// 预分配模式,预先创建文件,下载每一块后放到正确的位置
    FILE_ALLOCATE_SPARSE,          // Default mode, more effient and less disk space.NTFS下有效 http://msdn.microsoft.com/en-us/library/aa365564(VS.85).aspx
    FILE_ALLOCATE_COMPACT ;         // 文件大小随着下载不断增长,每下载一块数据按次序紧密排在一起,但不是他们的最终位置,下载中不断调整位置,最后文件位置方能确定    
	 
	 public static DLBT_FILE_ALLOCATE_TYPE fromInteger(int x)
	 {
        switch(x) 
        {
	        case 0:
	            return FILE_ALLOCATE_REVERSED;
	        case 1:
	            return FILE_ALLOCATE_SPARSE;
	        case 2:
	            return FILE_ALLOCATE_COMPACT;
        }
        return FILE_ALLOCATE_SPARSE;
	 }
	 
	 public static int toInt(DLBT_FILE_ALLOCATE_TYPE x)
	 {
        switch(x) 
        {
	        case FILE_ALLOCATE_REVERSED:
	            return 0;
	        case FILE_ALLOCATE_SPARSE:
	            return 1;
	        case FILE_ALLOCATE_COMPACT:
	            return 2;
        }
        return 1;
	 }
}
