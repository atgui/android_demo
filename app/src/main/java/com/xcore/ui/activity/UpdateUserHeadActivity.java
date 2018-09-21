package com.xcore.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lzy.okgo.model.Response;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.UpdateUserPresenter;
import com.xcore.presenter.view.UpdateUserView;
import com.xcore.services.ApiFactory;
import com.xcore.utils.Base64Util;
import com.xcore.utils.CameraHelpter;
import com.xcore.utils.DateUtils;
import com.xcore.utils.ImageUtils;
import com.xcore.utils.UriUtils;

import java.io.File;

public class UpdateUserHeadActivity extends MvpActivity<UpdateUserView,UpdateUserPresenter> implements UpdateUserView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_user_head;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEdit("");
        setTitle("设置个人头像");

        ImageViewExt img= findViewById(R.id.headImg);
        PlayerBean playerBean=DataUtils.playerBean;
        if(playerBean!=null){
            UserInfo userInfo=playerBean.getData();
            img.loadUrl(userInfo.getHeadUrl());
        }

        findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraHelpter.openGallery(UpdateUserHeadActivity.this);
            }
        });
        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraHelpter.openCamera(UpdateUserHeadActivity.this);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    public UpdateUserPresenter initPresenter() {
        return new UpdateUserPresenter();
    }

    //上传完成后把文件删除了
    @Override
    public void onUploadResult(SpeedDataBean speedDataBean) {
        try {
            //把临时文件删除了
            File file = new File(CameraHelpter.imgPathOri);
            if (file.exists()) {
                file.delete();
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
        try {
            //把原文件删除了
            File file1 = new File(CameraHelpter.imgPathCrop);
            if (file1.exists()) {
                file1.delete();
            }
        }catch (Exception e){
//            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateResult() {
        //修改成功
        toast("修改头像成功");
        finish();
    }

    @Override
    public void onError(String msg) {
        toast(msg);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        //data的返回值根据
        switch (requestCode) {
            case CameraHelpter.REQUEST_OPEN_CAMERA:
                CameraHelpter.addPicToGallery(CameraHelpter.imgPathOri,this);
                CameraHelpter.cropPhoto(CameraHelpter.imgUriOri,UpdateUserHeadActivity.this,300,300);
                break;
            case CameraHelpter.REQUEST_OPEN_GALLERY:
                if (data != null) {
                    Uri imgUriSel = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //打开相册会返回一个经过图像选择器安全化的Uri，直接放入裁剪程序会不识别，抛出[暂不支持此类型：华为7.0]
                        //formatUri会返回根据Uri解析出的真实路径
                        String imgPathSel = UriUtils.formatUri(UpdateUserHeadActivity.this, imgUriSel);
                        //根据真实路径转成File,然后通过应用程序重新安全化，再放入裁剪程序中才可以识别
                        Uri xUri= FileProvider.getUriForFile(UpdateUserHeadActivity.this,getPackageName() + ".provider", new File(imgPathSel));
                        CameraHelpter.cropPhoto(xUri,UpdateUserHeadActivity.this,400,400);
                    } else {
                        CameraHelpter.cropPhoto(imgUriSel,UpdateUserHeadActivity.this,400,400);
                    }
                }
                break;
            case CameraHelpter.REQUEST_CROP_PHOTO:
                CameraHelpter.addPicToGallery(CameraHelpter.imgPathCrop,UpdateUserHeadActivity.this);
//                ImageUtils.imageViewSetPic(ivImage, imgPathCrop);
                Bitmap bitmap= Base64Util.getBase64ByPath(CameraHelpter.imgPathCrop);

//                ImageView img=findViewById(R.id.image_iv);
//                img.setImageBitmap(bitmap);/storage/emulated/0/Android/data/com.xcore/files/Pictures/CropPicture/HomePic_20180822_1402345768340686477147908.jpg

//                String value=CameraHelpter.getFileToBase64(CameraHelpter.imgPathCrop);
                revokeUriPermission(CameraHelpter.imgUriCrop, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String value=CameraHelpter.bitmapToString(bitmap);
                value="data:image/jpeg;base64,"+value;
                toServer(value);
                break;
        }
    }

    //上传到服务器
    private void toServer(final String value){
        Log.e("TAG",value);
        presenter.upload(value);
        //开一个线程处理
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }

}
