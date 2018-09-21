package com.xcore.cache;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.data.p2pbean.StartStreamResultBean;
import com.xcore.data.p2pbean.StreamsResultBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.SystemUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.p2ptrans.P2PTrans;
import cn.dolit.utils.common.Utils;

/**
 * 下载缓存
 */
public class DownHandler {
    private Context context= MainApplicationContext.context;
    private DBHandler dbHandler;
    public DownHandler(DBHandler dbHandler){
        this.dbHandler=dbHandler;
    }

    /**
     * 初始化下载信息
     */
    public void initDown(){
        //初始化数据
        List<CacheModel> cacheModels=this.dbHandler.getDowns();
        for(CacheModel cacheModel:cacheModels){
            cacheModel.setComplete(0);
            cacheModel.setChecked(false);
            cacheModel.setStatus(0);
            cacheModel.setShowCheck(false);
            int percent= Integer.valueOf(cacheModel.getPercent());
            if(percent==100){
                cacheModel.setComplete(1);
                overCaches.put(cacheModel.getStreamId(),cacheModel);
            }else{
                runCaches.put(cacheModel.getStreamId(),cacheModel);
            }
            dataList.put(cacheModel.getStreamId(),cacheModel);
        }
    }

    private Timer timer;//计时器

    /**
     * 缓存数据
     */
    private Map<String ,CacheModel> dataList=new HashMap<>();
    public Map<String, CacheModel> getDataMap() {
        return dataList;
    }

    //得到一个任务
    public CacheModel getCacheModelByUrl(String url){
        CacheModel cacheModel=null;
        for(String key:dataList.keySet()){
            CacheModel item=dataList.get(key);
            if(item.getUrl().equals(url)){
                cacheModel=item;
                break;
            }
        }
        return cacheModel;
    }

    //未完成任务
    public Map<String,CacheModel> runCaches=new HashMap<>();
    //已完成任务
    public Map<String,CacheModel> overCaches=new HashMap<>();

    //批量删除
    public void batchDelete(List<CacheModel> cacheModels){
        List<CacheModel> list=cacheModels;
        //1.先把任务全部停止
        for(CacheModel cacheModel:list){
            //停止任务
            this.stopByUrl(cacheModel,true);
        }
        //删除本地缓存
        clearDir(list);
    }

    //清除缓存
    public void clearDir(List<CacheModel> cacheModel){
        final List<CacheModel> cModels=cacheModel;
        for(CacheModel item :cModels) {
            //先停止任务
            String str = MainApplicationContext.SD_PATH + item.getStreamId() + "/";
            File file = new File(str);
            if (SystemUtils.deleteDirWihtFile(file)) {//删除文件夹
                //删除种子文件
                String path = MainApplicationContext.SD_PATH + item.getStreamId() + ".torrent";
                File file1 = new File(path);
                boolean b=file1.delete();
                Log.e("TAG","删除:"+b);
            }
            //dataList.remove(item.getId());
        }
    }

    /**
     * 清理其他的缓存,观看缓存
     */
    public void clearOtherDir(){
        //得到所有的文件信息
        String rootPath=MainApplicationContext.SD_PATH;
        File file=new File(rootPath);
        if(!file.exists()){
            return;
        }
        File[] files=file.listFiles();
        for(File fItem:files){
            String name=fItem.getName();
            if(!dataList.containsKey(name)){
                boolean boo=false;
                if(fItem.isDirectory()){
                    boo=SystemUtils.deleteDirWihtFile(fItem);
                   Log.e("TAG","删除文件夹:"+name+"::"+boo);
                }else{
                    boo=fItem.delete();
                    Log.e("TAG","删除文件"+name+"::"+boo);
                }
            }
        }
        Toast.makeText(context,"清理完成",Toast.LENGTH_SHORT).show();
    }

    /**
     * 清除单个任务
     * @param streamId
     */
    public void clearByStreamId(String streamId){
        if(TextUtils.isEmpty(streamId)){
            return;
        }
        String rootPath=MainApplicationContext.SD_PATH;
        String path=rootPath+streamId;
        File file=new File(path);
        if(file.exists()&&file.isDirectory()){
            SystemUtils.deleteDirWihtFile(file);
        }
        path=path+".torrent";
        file=new File(path);
        if(file.exists()&&file.isFile()){
            file.delete();
        }
    }

    /**
     * 是否有任务正在下载
     * @return
     */
    public boolean isRunning(){
        if(this.timer==null)return false;
        boolean b=false;
        for (Map.Entry<String, CacheModel> entry : dataList.entrySet()) {
            CacheModel cacheModel = entry.getValue();
            if(cacheModel!=null&&cacheModel.getStreamInfo()!=null&&cacheModel.getStreamInfo().getBtStatus()!=0){
                b=true;
                break;
            }
        }
        return b;
    }

    /**
     * 下载
     * @param downModel 下载信息
     */
    public void downByUrl(final DownModel downModel){
        final DownModel mod=downModel;
        String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/start?type=%s&uri=%s", P2PTrans.LOCAL_HOST,
                BaseCommon.P2P_SERVER_PORT, P2PTrans.P2P_TYPE,downModel.getUrl());
        url=P2PTrans.getUrlAddPass(url);
        OkGo.<String>get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String v=response.body();
                Log.e("TAG","下载返回.");

                StartStreamResultBean result=new Gson().fromJson(v, StartStreamResultBean.class);
                if (null == result || result.getCode() != 0) {
                   Toast.makeText( context, "下载失败" + (result == null ? "" : result.getCode()), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("TAG","开始下载了.");
                String id=result.getStream().getId();
                CacheModel cacheModel=new CacheModel();
                cacheModel.setShortId(mod.getShortId());
                cacheModel.setTotalSize(result.getStream().getTotalBytes()+"");
                cacheModel.setStreamId(id);
                cacheModel.setName(mod.getName());
                cacheModel.setUrl(mod.getUrl());
                cacheModel.setStreamInfo(result.getStream());
                cacheModel.setConver(mod.getConver());
                cacheModel.setUpdateTime(System.currentTimeMillis()+"");
                cacheModel.settDelete("1");
                cacheModel.setPlay(false);
                add(cacheModel);
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","下载出错");
                Toast.makeText(context,"下载出错",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public CacheModel getCacheModelByShortId(String sId){
        CacheModel cacheModel=null;
        for(String key:overCaches.keySet()){
            CacheModel cacheModel1= overCaches.get(key);
            if(cacheModel1!=null&&sId.equals(cacheModel1.getShortId())){
                cacheModel=cacheModel1;
                break;
            }
        }
        return cacheModel;
    }

    /**
     * 添加下载到数据库
     * @param cacheModel 缓存信息
     */
    private void saveDown(CacheModel cacheModel){
       boolean boo= dataList.containsKey(cacheModel.getStreamId());
       if(!boo){
           dbHandler.insertDown(cacheModel);
       }
    }

    /**
     * 加入到缓存列表中
     * @param cacheModel
     */
    private void add(CacheModel cacheModel){
        if(cacheModel.getStreamId()==null||cacheModel.getStreamId().contains("null")||cacheModel.getStreamId().equals("null")){
            return;
        }
        //下载、保存信息到数据库中
        saveDown(cacheModel);

        Log.e("TAG","添加到了集合中。显示下载信息"+cacheModel.toString());
        if(!dataList.containsKey(cacheModel.getStreamId())) {
            dataList.put(cacheModel.getStreamId(), cacheModel);
        }
        if(!runCaches.containsKey(cacheModel.getStreamId())) {
            runCaches.put(cacheModel.getStreamId(), cacheModel);
        }
        //Toast.makeText(context,"添加到缓存列表",Toast.LENGTH_LONG);

        //开始获取下载信息
//        startRefreshDownloadProgressTimer();
        startTimer();
    }
    /**
     * 启动定时器,每2000ms 调用一次
     */
    private void startTimer(){
        if(timer==null){
            Log.e("TAG","启动计时器了");
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getDowninfo();
                }
            },100,2000);
        }
    }
    /**
     * 停止计时器
     */
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;

        stopDbTimer();
    }

    //保存数据库的缓存数据
    private Map<String,CacheModel> dbDatas=new HashMap<>();
    private Timer dbTimer;

    private void addDbData(CacheModel cacheModel){
        dbDatas.put(cacheModel.getStreamId(),cacheModel);
        int percent=Integer.valueOf(cacheModel.getPercent());
        if(cacheModel.getComplete()==1||percent>=100){//完成了
            //保存
            this.updateDown(cacheModel);
            this.stopByUrl(cacheModel,false);//完成了停止这个任务了
            return;
        }
        dbTimer();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            saveCacheData();
        }
    };

    /**
     * 保存到数据库中
     */
    public void saveCacheData(){
        for(String key:dbDatas.keySet()){
            CacheModel cacheModel=dbDatas.get(key);
            if(cacheModel!=null){
                updateDown(cacheModel);
            }
        }
    }
    //保存数据库时间  1分钟保存一次
    private void dbTimer(){
        if(dbTimer==null){
            dbTimer=new Timer();
            dbTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            },3000,60000);
        }
    }
    //停止时间
    private void stopDbTimer(){
        if(dbTimer!=null){
            dbTimer.cancel();
        }
        dbTimer=null;
        saveCacheData();
    }


    /**
     * 得到更新当前下载的所有信息 dataList
     */
    public void getDowninfo(){
        StreamsResultBean result = P2PTrans.getStreams1(BaseCommon.P2P_SERVER_PORT);
        if (result == null) {
            return;
        }
        List<StartStreamResultBean.StreamInfo> streamInfoList = result.getStreams();
        if(streamInfoList==null){
            return;
        }
        for(int i=0;i<streamInfoList.size();i++){
            StartStreamResultBean.StreamInfo info=streamInfoList.get(i);
            if(info!=null){
                CacheModel mod= dataList.get(info.getId());
                if(mod!=null){//没有暂停//
                    int percent=Integer.valueOf(mod.getPercent());
                    if(info.getPercent()>percent){
                        mod.setPercent(info.getPercent()+"");
                    }
                    mod.setStatus(1);
                    mod.setStreamInfo(info);
                    long v1=mod.getTotalCount1();
                    mod.setTotalCount1(v1+info.getDownloadSpeed());

                    int tV1=mod.getTimeCount();
                    tV1++;
                    mod.setTimeCount(tV1);

                    long tSize=Long.valueOf(mod.getTotalSize());
                    if(info.getTotalBytes()>tSize) {
                        mod.setTotalSize(info.getTotalBytes() + "");
                    }
                    long percentSize=Long.valueOf(mod.getDownSize());
                    if(info.getDownloadedBytes()>percentSize){
                        mod.setDownSize(info.getDownloadedBytes()+"");
                    }
                    if(info.getPercent()>=100){//完成了
                        //从未完成放到已完成
                        runCaches.remove(info.getId());
                        overCaches.put(mod.getStreamId(),mod);
                        mod.setComplete(1);
                        //更新数据库
                        //updateDown(mod);
                    }else{
                        mod.setComplete(0);
                        runCaches.put(mod.getStreamId(), mod);
                    }
                    //添加到数据库保存的集合里面去
                    addDbData(mod);
                }
                Log.e("TAG",info.getDownloadSpeed()+"  已下载:"+
                        info.getDownloadedBytes()+"  总大小:"+info.getTotalBytes());
            }
        }
        Log.e("TAG",dataList.toString());
        if(isAllComplete()==true||streamInfoList.size()<=0){
            stopTimer();
        }
    }

    /**
     * 更新数据库进度
     * @param cacheModel 缓存信息
     */
    private void updateDown(CacheModel cacheModel){
        //更新数据库
        Log.e("TAG","更新数据库进度");
        long total=Long.valueOf(cacheModel.getTotalSize());
        if(total<=0){
            return;
        }
        dbHandler.update(cacheModel);

        try {
            long downSize=Long.valueOf(cacheModel.getDownSize());

            HttpParams params=new HttpParams();
            params.put("shortId",cacheModel.getShortId());
            params.put("key",Long.valueOf(cacheModel.getTotalSize()));
            params.put("duration",downSize);
            double v=cacheModel.getTotalCount1()*1.0/cacheModel.getTimeCount()/1000;
            params.put("speed",v);
            params.put("percent",cacheModel.getPercent());//下载进度
            ApiFactory.getInstance().<String>toCacheInfo(params, new AGCallback<String>() {
                @Override
                public void onNext(String s) {
                    //Log.e("TAG", "缓存进度:" + s);
                }
            });
        }catch (Exception e){}
    }

    /**
     * 是否全部完成
     * @return
     */
    public boolean isAllComplete(){
        boolean isBoo=true;
        for (Map.Entry<String, CacheModel> entry : dataList.entrySet()) {
            CacheModel cacheModel= entry.getValue();
            if(cacheModel!=null&&Long.valueOf(cacheModel.getPercent())<100){
                isBoo=false;
                break;
            }
        }
        return  isBoo;
    }

    /**
     * 停止某个任务
     * @param cacheModel
     */
    public void stopByUrl(final CacheModel cacheModel,final boolean isDelete){
        cacheModel.setStatus(2);
        cacheModel.setPlay(true);
        try {
            CacheModel vCache= dbDatas.remove(cacheModel.getStreamId());
            if(vCache!=null){
                updateDown(vCache);
            }
        }catch (Exception e){}
        if(runCaches!=null&&runCaches.containsKey(cacheModel.getStreamId())) {
            runCaches.get(cacheModel.getStreamId()).setStatus(2);
        }
        if(dataList!=null&&dataList.containsKey(cacheModel.getStreamId())) {
            dataList.get(cacheModel.getStreamId()).setStatus(2);
        }
        if(isDelete==true){
            runCaches.remove(cacheModel.getStreamId());
            dataList.remove(cacheModel.getStreamId());
            overCaches.remove(cacheModel.getStreamId());
        }
        String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/stop?uri=%s", P2PTrans.LOCAL_HOST,
                BaseCommon.P2P_SERVER_PORT,
                cacheModel.getUrl());
        url=P2PTrans.getUrlAddPass(url);

//        String value=P2PTrans.stopByUrl(BaseCommon.P2P_SERVER_PORT,cacheModel.getUrl());
//        if(value.length()>0){
//            Log.e("TAG",value);
//            return;
//        }

        OkGo.<String>get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String  value=response.body();
                Log.e("TAG","停止：："+value);
                P2PTrans.StartStreamResult startStreamResult=new Gson().fromJson(value, P2PTrans.StartStreamResult.class);
                if(startStreamResult!=null&&startStreamResult.getCode()>=0){
                    if(isDelete==true) {
                        dataList.remove(cacheModel.getStreamId());
                        runCaches.remove(cacheModel.getStreamId());
                        overCaches.remove(cacheModel.getStreamId());
                        //停止任务
                        //删除数据库数据
                        dbHandler.delteDown(cacheModel);
                        //清除缓存
                        clearDir(Arrays.asList(cacheModel));
                    }

                    if(!isRunTask()){
                        stopTimer();
                    }
                    Log.e("TAG","停止成功");
                }else{
                    Log.e("TAG","停止失败");
                }
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","停止出错");
            }
        });
    }

    /**
     * 停止所有任务
     */
    public void stopAll(){
        if(this.timer==null){//证明暂时还没有正在下载的任务
            return;
        }
        for(String key :runCaches.keySet()){
            CacheModel cacheModel=runCaches.get(key);
            if(cacheModel!=null){
                this.stopByUrl(cacheModel,false);
            }
        }
        //停止任务,停止计时器
        stopTimer();
    }
    //开始所有任务
    public void startAll(){
        for(String key :runCaches.keySet()){
            CacheModel cacheModel=runCaches.get(key);
            if(cacheModel!=null){
                DownModel downModel=new DownModel();
                downModel.setShortId(cacheModel.getShortId());
                downModel.setName(cacheModel.getName());
                downModel.setConver(cacheModel.getConver());
                downModel.setUrl(cacheModel.getUrl());
                this.downByUrl(downModel);
            }
        }
    }


    /**
     * 是否有正在运行的任务
     * @return 有 true, 没有 false
     */
    public boolean isRunTask(){
        boolean boo=false;
        //判断是否还有正在下载的任务没有
        for(String key :runCaches.keySet()){
            CacheModel model=runCaches.get(key);
            if(model.getStatus()==1){
                boo=true;
                break;
            }
        }
        return  boo;
    }

    //查看某个任务信息
    public void startRefreshDownloadInfo(final String streamInfoUrl) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (streamInfoUrl == null || streamInfoUrl.length() == 0) {
                    return;
                }
                OkGo.<String>get(streamInfoUrl).execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            String content = response.body();
                            try {
                                if(content==null||content.length()<=0){
                                    return;
                                }
                                JSONObject resultJson = new JSONObject(content);
                                if (resultJson.getInt("code") != 0) {
                                    return;
                                }

                                JSONObject jsonObject = resultJson.getJSONObject("data");
                                if(jsonObject==null){
                                    return;
                                }
                                Integer speed=jsonObject.getInt("downloadSpeed");
                                if(speed==null){
                                    return;
                                }
                                String speedStr= Utils.getDisplayFileSize(speed);
                                final String value = String.format(Locale.CHINA, "已经下载：%s，当前进度：%d，剩余时间：%ds，" +
                                                "下载速度：%s， 上传速度：%s，下载连接数：%d，总连接数：%d，peer数量：%d，seed数量：%d",
                                        Utils.getDisplayFileSize(jsonObject.getLong("downloadedBytes")), jsonObject.getInt("percent"),
                                        jsonObject.getInt("secondsRemain"), speedStr,
                                        jsonObject.getInt("uploadSpeed"), jsonObject.getInt("downConnectionCount"),
                                        jsonObject.getInt("connectionCount"), jsonObject.getInt("totalCurrentPeerCount"),
                                        jsonObject.getInt("totalCurrentSeedCount"));
                                Log.e("TAG",value);
                                //speedTxt.setText("正在缓冲中,请稍等...\r\n"+speedStr+"/S");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }
}
