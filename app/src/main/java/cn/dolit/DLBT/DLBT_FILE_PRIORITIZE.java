package cn.dolit.DLBT;

public enum DLBT_FILE_PRIORITIZE {
    DLBT_FILE_PRIORITY_CANCEL,     // 取消该文件的下载
    DLBT_FILE_PRIORITY_NORMAL,                  // 正常优先级
    DLBT_FILE_PRIORITY_ABOVE_NORMAL,            // 高优先级 
    DLBT_FILE_PRIORITY_MAX;                      // 最高优先级（如果有该优先级的文件还未下完，不会下载低优先级的文件）
}
