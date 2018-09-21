package com.xcore;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SAFUtils {
    private static String TAG = SAFUtils.class.getSimpleName();
    public static final int REQUEST_CODE = 0;

    /**
     * 弹出获取权限对话框
     */
    public static void showGetRootDialog(final Activity activity) {
//        ImageView imageView = new ImageView(activity);
//        imageView.setImageResource(R.drawable.pic_saf_permission);
//        new android.app.AlertDialog.Builder(activity).setCancelable(false)
//                .setTitle("获取U盘权限")
//                .setView(imageView)
//                .setPositiveButton("前往获取", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        activity.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_CODE);
//                    }
//                })
//                .setNegativeButton("取消", null)
//                .create().show();
    }

    /**
     * 保存获取到的根目录，此方法写在Activity的onActivityResult中
     */
    public static void setRoot(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == activity.RESULT_OK) {
            Uri treeUri = data.getData();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("usbroot", treeUri.toString()).commit();
        }
    }

    /**
     * 取出保存的根目录
     */
    public static String getRoot(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("usbroot", "");
        //return "content://com.android.externalstorage.documents/tree/2839-761C%3A";
    }

    /**
     * 创建单个或多级文件夹
     *
     * @param ctx  上下文
     * @param path 在U盘根目录下的相对路径
     * @return 是否创建成功
     */
    public static DocumentFile mkdirs(Context ctx, String path) {
        DocumentFile document = DocumentFile.fromTreeUri(ctx, Uri.parse(getRoot(ctx)));
        String[] paths = path.split("/");
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].isEmpty())
                continue;
            DocumentFile nextDocument = document.findFile(paths[i]);
            if (nextDocument == null) {
                nextDocument = document.createDirectory(paths[i]);
            }
            document = nextDocument;
        }
        return document;
    }

    /**
     * 查看某个文件是否存在
     *
     * @param ctx  上下文
     * @param path 在U盘根目录下的相对路径
     * @return 是否存在
     */
    public static boolean exists(Context ctx, String path) {
        DocumentFile document = DocumentFile.fromTreeUri(ctx, Uri.parse(getRoot(ctx)));
        String[] paths = path.split("/");
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].isEmpty())
                continue;
            DocumentFile nextDocument = document.findFile(paths[i]);
            if (nextDocument == null) {
                return false;
            }
            document = nextDocument;
        }
        return true;
    }

    /**
     * 创建一个文件
     *
     * @param ctx  上下文
     * @param path 在U盘根目录下的相对路径
     * @return 是否创建成功
     */
    public static DocumentFile createFile(Context ctx, String path, String fileName) {
        //1.创建文件目录
        DocumentFile document = DocumentFile.fromTreeUri(ctx, Uri.parse(getRoot(ctx)));
        Log.d(TAG, document.getName() + "getName");

        String[] paths = path.split("/");
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].isEmpty())
                continue;
            DocumentFile nextDocument = document.findFile(paths[i]);
            if (nextDocument == null) {
                nextDocument = document.createDirectory(paths[i]);
            }
            document = nextDocument;
        }

        //2.创建文件
        if (!TextUtils.isEmpty(fileName)) {
            if (document.findFile(fileName) == null) {
                document = document.createFile("*/*", fileName);
            } else {
                document = document.findFile(fileName);
            }
        }
        return document;
    }

    /**
     * 获取文件列表
     *
     * @param ctx  上下文
     * @param path 在U盘根目录下的相对路径，可以是单个文件也可以是文件夹
     * @return 用竖线间隔的字符串，比如"folder1|folder2"
     */
    public static String ListDir(Context ctx, String path) {
        DocumentFile document = DocumentFile.fromTreeUri(ctx, Uri.parse(getRoot(ctx)));
        DocumentFile targetDocument = document.findFile(path);
        if (targetDocument == null) {
            return "";
        }
        String str = "";
        // TODO: 如果有子目录等，应该要递归获取！！！
        for (DocumentFile doc : targetDocument.listFiles()) {
            if (!TextUtils.isEmpty(str))
                str += "|";
            str += doc.getName();
        }
        return str;
    }

//    /**
//     * 重命名（移动文件到一个新目录）
//     *
//     * @param ctx  上下文
//     * @param path 在U盘根目录下的相对路径，可以是单个文件也可以是文件夹
//     * @return 用竖线间隔的字符串，比如"folder1|folder2"
//     */
    //path 在U盘根目录下的相对路径，可以是单个文件也可以是文件夹
    //用竖线间隔的字符串，比如"folder1|folder2"
    public static boolean Renmae(Context ctx, String oldFile, String newFile) {
        // TODO: todo
        return false;
    }

    /**
     * 删除文件
     *
     * @param ctx      上下文
     * @param fileName 文件名
     * @return
     */
    public static boolean deleteFile(Context ctx, String fileName) {

        /*if (!exists(ctx, fileName)) {
            return false;
        }*/
        DocumentFile document = DocumentFile.fromTreeUri(ctx, Uri.parse(getRoot(ctx)));
        DocumentFile targetDocument = document.findFile(fileName);
        if (targetDocument == null) {
            return true;
        }
        return targetDocument.delete();
    }


    /**
     * 复制文件到SAF
     * @param ctx
     * @param sourceUrl
     * @param targetDir
     * @param targetFile
     * @return
     */
    public static boolean copyFileToSAF(Context ctx, String sourceUrl, String targetDir, String targetFile) {

        OutputStream os = null;
        InputStream is = null;

        try {
            //输入流
            is = new FileInputStream(new File(sourceUrl));

            //输出流
            DocumentFile targetDocument = SAFUtils.createFile(ctx, targetDir, targetFile);
            os = ctx.getContentResolver().openOutputStream(targetDocument.getUri());

            //拷贝数据
            int len;
            byte[] buffer = new byte[2 * 1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
