package com.xcore.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHelpter {
    public static final int REQUEST_OPEN_CAMERA = 0x011;
    public static final int REQUEST_OPEN_GALLERY = 0x022;
    public static final int REQUEST_CROP_PHOTO = 0x033;
    public static final int REQUEST_PERMISSIONS = 0x044;

    //原图像 路径
    public static String imgPathOri;
    //裁剪图像 路径
    public static String imgPathCrop;
    //原图像 URI
    public static Uri imgUriOri;
    //裁剪图像 URI
    public static Uri imgUriCrop;

    /**
     * 打开相机
     */
    public static void openCamera(Activity context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
        File oriPhotoFile = null;
        oriPhotoFile = createOriImageFile(context);
        if (oriPhotoFile != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imgUriOri = Uri.fromFile(oriPhotoFile);
            } else {
                imgUriOri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", oriPhotoFile);
            }
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUriOri);
            context.startActivityForResult(intent, REQUEST_OPEN_CAMERA);
        }
    }

    /**
     * 创建原图像保存的文件
     * @return
     * @throws IOException
     */
    public static File createOriImageFile(Activity context){
        String imgNameOri = "HomePic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image =null;
        try {
            File pictureDirOri = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/OriPicture");
            if (!pictureDirOri.exists()) {
                pictureDirOri.mkdirs();
            }
            image = File.createTempFile(
                    imgNameOri,         /* prefix */
                    ".jpg",             /* suffix */
                    pictureDirOri       /* directory */
            );
            imgPathOri = image.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 将bitmap转为Base64字符串
     *
     * @param bitmap
     * @return base64字符串
     */
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
        byte[] bytes = outputStream.toByteArray();
        //Base64算法加密，当字符串过长（一般超过76）时会自动在中间加一个换行符，字符串最后也会加一个换行符。
        // 导致和其他模块对接时结果不一致。所以不能用默认Base64.DEFAULT，而是Base64.NO_WRAP不换行
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * 根据路径得到文件的base64 带 data:image/jpeg;base64,
     * @param imgFile 图片路径
     * @return base64
     */
//    public static String getFileToBase64(String imgFile)
//    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//        InputStream in = null;
//        byte[] data = null;
//        //读取图片字节数组
//        try
//        {
//            File file = new File (imgFile); //根据文件路径创建一个文件对象
//            in = new FileInputStream(file);
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        //对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        String v= encoder.encode(data).trim();//返回Base64编码过的字节数组字符串
//        v="data:image/jpeg;base64,"+v;
//        return v;
//    }

    /**
     * 把图像添加进系统相册
     *
     * @param imgPath 图像路径
     */
    public static void addPicToGallery(String imgPath,Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imgPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


    /**
     * 打开相册
     */
    public static void openGallery(Activity context) {
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setAction(Intent.ACTION_PICK);
        }
        intent.setType("image/*");
        context.startActivityForResult(intent, REQUEST_OPEN_GALLERY);
    }

    /**
     * 裁剪图片
     * @param uri 需要 裁剪图像的Uri
     */
    public static void cropPhoto(Uri uri,Activity context,int x,int y) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        File cropPhotoFile = null;
        try {
            cropPhotoFile = createCropImageFile(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cropPhotoFile != null) {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                imgUriCrop = Uri.fromFile(cropPhotoFile);
//            }else{
//                imgUriCrop = FileProvider.getUriForFile(this, getPackageName() + ".provider", cropPhotoFile);
//            }

            //7.0 安全机制下不允许保存裁剪后的图片
            //所以仅仅将File Uri传入MediaStore.EXTRA_OUTPUT来保存裁剪后的图像
            imgUriCrop = Uri.fromFile(cropPhotoFile);

            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.5);
//            intent.putExtra("outputX", 300);
//            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUriCrop);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.startActivityForResult(intent, REQUEST_CROP_PHOTO);
        }
    }

    /**
     * 创建裁剪图像保存的文件
     * @return
     * @throws IOException
     */
    public static File createCropImageFile(Activity context) throws IOException {
        String imgNameCrop = "HomePic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pictureDirCrop = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/CropPicture");
        if (!pictureDirCrop.exists()) {
            pictureDirCrop.mkdirs();
        }
        File image = File.createTempFile(
                imgNameCrop,         /* prefix */
                ".jpg",             /* suffix */
                pictureDirCrop      /* directory */
        );
        imgPathCrop = image.getAbsolutePath();
        return image;
    }

}
