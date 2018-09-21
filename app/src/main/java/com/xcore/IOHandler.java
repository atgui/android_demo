package com.xcore;

import android.graphics.Path;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.dolit.DLBT.IOConst;
import cn.dolit.DLBT.IOResult;
import cn.dolit.DLBT.IOWrapper;

public class IOHandler extends IOWrapper{

    protected static String TAG = "DolitBT/Java_IOWrapperSample";

    protected static ArrayList m_filePool;

    protected static synchronized IOResult OpenFile(String path, int mode, int permission)
    {
        // 如果没有设置O_CREAT mode，那么判断文件是否存在
        String folder = "";
        String fileName = path;
        int pos = path.lastIndexOf("/");
        if (pos > 0) {
            folder = path.substring(0, pos);
            fileName = path.substring(pos + 1);
        }

        // 只是打开文件夹句柄，内部获取fd去stat
        DocumentFile targetDocument = SAFUtils.createFile(MainApplicationContext.context, folder, fileName);

        /** OS写法 **/
        String openMode = "r";
        if ((mode & IOConst.O_WRONLY ) != 0)
            openMode = "w";
        if ((mode & IOConst.O_RDWR) != 0)
            openMode = "rw";
        if ((mode & IOConst.O_TRUNC) != 0)
            openMode += "t";
        if ((mode & IOConst.O_LARGEFILE) != 0)
        {
            // how can I do, mabe java can handle the file bigger than 4G auto.
        }

        IOResult ir = new IOResult();
        ir.iHandled = 1;

        ParcelFileDescriptor pfd = null;
        try {
            pfd = MainApplicationContext.context.getContentResolver().openFileDescriptor(targetDocument.getUri(), openMode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ir.intRet = IOConst.ERROR_NOTFOUND;
            return ir;
        } catch (Exception e) {
            e.printStackTrace();
            ir.intRet = IOConst.ERROR;
            return ir;
        }
        if (pfd == null)
            ir.intRet = IOConst.ERROR;
        else
            ir.intRet = pfd.detachFd();
        return ir;
    }

    // 预处理一些内部路径
    public static String PreProcessPath(String path)
    {
        // 如果是./开头，代表是用的当前目录，这应该怎么处理所谓的当前目录呢？直接去掉就是了
        if (path.startsWith("."))
            path = path.substring(1);

        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        if (path.startsWith("/"))
            path = path.substring(1);
        return path;
    }

    // 默认的函数不做任何处理，只是一个接口，默认由C++层自己的IO实现文件的读写
    public static IOResult MKDir(String dir, int mode)
    {
        Log.d(TAG, "Java MkDir" + dir);
        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        dir = PreProcessPath(dir);
        DocumentFile targetDocument = SAFUtils.mkdirs(MainApplicationContext.context, dir);
        IOResult ir = new IOResult();
        if (targetDocument.isDirectory()) {     //如果已经成功
            ir.iHandled = 1;        //已经处理过
            ir.intRet = 0;
        }
        return ir;
    }

    // 默认的函数不做任何处理，只是一个接口，默认由C++层自己的IO实现文件的读写
    public static IOResult Open(String path, int mode, int permission)
    {
        Log.d(TAG, "Java Open" + path + ", mode: " + mode);
        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        path = PreProcessPath(path);

        IOResult ir = new IOResult();
        if (((mode & IOConst.O_CREAT) == 0) && !SAFUtils.exists(MainApplicationContext.context, path)) {
            ir.intRet = IOConst.ERROR_NOTFOUND;
            ir.iHandled = 1;
            return ir;
        }

        //如果Java需要处理这个函数，该函数要赋值intRet(因为改函数的返回值是一个32位整数）
        ir = OpenFile(path, mode, permission);
        return ir;
    }

    /**
     * TODO:重命名文件（剪切文件），这个功能一般不会用到，除非您调用了rootPathName，或者Move等高级函数
     * 因为对SAF里面的操作不熟悉，这个没有去翻译，建议贵司自行翻译。函数的要求和效果，内部是按linux标准函数来做的：
     * http://man7.org/linux/man-pages/man2/rename.2.html
     * 需要考虑：newName的目录如果不存在，需要先创建目录，如果同名文件存在要覆盖替换等一些特殊情况。如果oldName和newName一样，则不处理
     *
     * @param oldName 老的文件名
     * @param newName 新的文件名
     * @return 是否存在
     */

    public static IOResult Rename(String oldName, String newName)
    {
        Log.d(TAG, "Java Rename, oldName: " + oldName + ", newName: " + newName);
        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        oldName = PreProcessPath(oldName);
        newName = PreProcessPath(newName);

        IOResult ir = new IOResult();
        //TODO:SAFUtils里面的函数需要重写
        boolean b = SAFUtils.Renmae(MainApplicationContext.context, oldName, newName);
        ir.iHandled = 1;        //已经处理过
        ir.intRet = (b ? 1 : 0);  //是否成功
        return new IOResult();
    }

    public static IOResult Remove(String fileName)
    {
        Log.d(TAG, "Java Remove, fileName: " + fileName);
        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        fileName = PreProcessPath(fileName);
        boolean b = SAFUtils.deleteFile(MainApplicationContext.context, fileName);
        IOResult ir = new IOResult();
        ir.iHandled = 1;        //已经处理过
        ir.intRet = (b ? 1 : 0);  //是否成功
        return new IOResult();
    }

    public static IOResult ListDir(String dir)
    {
        Log.d(TAG, "Java ListDir, dir: " + dir);
        // 内部的文件一般可能会格式化为/开头的，建议去掉，然后这个代表相对root的相对目录
        dir = PreProcessPath(dir);
        String strList = SAFUtils.ListDir(MainApplicationContext.context,  dir);
        IOResult ir = new IOResult();
        ir.iHandled = 1;        //已经处理过
        ir.stringRet = strList;  // 赋值结果
        return ir;
    }

}
