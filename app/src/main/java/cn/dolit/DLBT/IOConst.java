package cn.dolit.DLBT;

/**
 * Created by peng on 2016/11/12.
 */

public final class IOConst {
    // return code
    public  static final int ERROR   = -1 ;
    public  static final int ERROR_NOTFOUND   = -2;

    // open mode
    public  static final int O_RDONLY = 00000000;
    public  static final int O_WRONLY = 00000001;
    public  static final int O_RDWR = 00000002;

    public static final  int O_LARGEFILE = 0400000;

    public  static final int O_CREAT = 00000100;     /* second byte, away from DOS bits */
    public  static final int O_TRUNC = 00000200;
    public  static final int O_APPEND = 00002000;
}
