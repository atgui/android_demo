package com.xcore;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.common.BaseCommon;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DownModel;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import cn.dolit.p2ptrans.P2PTrans;
import cn.dolit.utils.common.Utils;

public class P2PTransActivity extends Activity {
    private static final int MSG_START_SUCCEED = 2300;
    private static final int MSG_START_FAILED = 2301;

    private static final String TAG = "p2ptrans";
    private int serverPort = BaseCommon.P2P_SERVER_PORT;
    private int retryCount = 0;

    private TextView tvProgressInfo;
    private EditText editTextTorrentUrl;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2ptrans);
        p2pServerInit();
        //enabledStreamAndSetDownloadDir();
    }
    private void p2pServerInit(){
        editTextTorrentUrl = (EditText)findViewById(R.id.editTextTorrentUrl);
        tvProgressInfo=findViewById(R.id.tv_progress_info);

        final TextView editTextStreamUrl = (TextView)findViewById(R.id.textViewStreamUrl);
        final Button buttonCreateServer = (Button)findViewById(R.id.buttonCreateServer);
//        final Button buttonGetStreamUrl = (Button)findViewById(R.id.buttonGetStreamUrl);
//        final Button buttonHelp = (Button)findViewById(R.id.buttonHelp);
        final Button buttonPlayVideo = (Button)findViewById(R.id.buttonPlayVideo);
        final RadioButton radioUrl = (RadioButton)findViewById(R.id.radioUrl);
        final RadioButton radioPath = (RadioButton)findViewById(R.id.radioPath);
        final RadioButton radioCustom = (RadioButton)findViewById(R.id.radioCustom);
        final TextView textViewInfo = (TextView)findViewById(R.id.textViewInfo);
        final RadioGroup radioGroupType = (RadioGroup)findViewById(R.id.radioGroupType);

//        buttonGetStreamUrl.setEnabled( false);
//        buttonHelp.setEnabled( false);
//        buttonPlayVideo.setEnabled(false);

//        MainApplication app = (MainApplication)this.getApplication();
        if (MainApplicationContext.mDolitBT == null) {
            MainApplicationContext.initDolitBT();
        }

        radioUrl.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                editTextTorrentUrl.setText("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent");
                editTextTorrentUrl.setText("http://23.234.12.131:81/001.torrent");
            }
        });

        radioPath.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTorrentUrl.setText(P2PTransActivity.this.getCacheDir()+"/test.torrent");
            }
        });

        radioCustom.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTorrentUrl.setText("dy://2924035d6afe295b11ef90a5c0ae61dd");
            }
        });
        //播放
        buttonPlayVideo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String torrentUrl = editTextTorrentUrl.getText().toString();
//                String url = P2PTrans.getTorrentPlayUrl( 8777, torrentUrl);
//                Intent intent = new Intent(P2PTransActivity.this, VideoActivity.class);
//                intent.putExtra("url", url);
//                intent.putExtra("torrentUrl", torrentUrl);
//                startActivity(intent);

//                Intent intent=new Intent(P2PTransActivity.this,CacheViewActivity.class);
//                startActivity(intent);

//                final Handler runHandle = new Handler(){
//                    @Override
//                    public void handleMessage( Message msg) {
//                        super.handleMessage(msg);
//                        Bundle data = msg.getData();
//                        String url = P2PTrans.getTorrentPlayUrl( 8777, editTextTorrentUrl.getText().toString());
//                        if ( radioGroupType.getCheckedRadioButtonId() == R.id.radioUrl ) {
//                            url = P2PTrans.getTorrentPlayUrl( 8777, editTextTorrentUrl.getText().toString());
//                        }
//                        else if ( radioGroupType.getCheckedRadioButtonId() == R.id.radioPath ){
//                            url = P2PTrans.getTorrentPlayPath( 8777, editTextTorrentUrl.getText().toString());
//                        }
//                        else if ( radioGroupType.getCheckedRadioButtonId() == R.id.radioCustom ){
//                            String content = httpGet("http://demo.flvurl.cn/DYPlayer/getvideo.js?" + editTextTorrentUrl.getText().toString());
//                            if( TextUtils.isEmpty(content)) {
//                                return;
//                            }
//                            Pattern urlPat = Pattern.compile("\"url\":\"(.+?)\"");
//                            Matcher matchResult = urlPat.matcher( content);
//                            if(!matchResult.find())
//                                return;
//                            url = P2PTrans.getTorrentPlayUrl( 8777, matchResult.group(1));
//                        }
//                        String torrentUrl = editTextTorrentUrl.getText().toString();
//
//                        Intent intent = new Intent(P2PTransActivity.this, VideoActivity.class);
//                        intent.putExtra("url", url);
//                        intent.putExtra("torrentUrl", torrentUrl);
//                        startActivity(intent);
//                    }
//                };

//                new Thread( new Runnable(){
//                    public void run()
//                    {
//                        P2PTrans.stopAllStream(8777);
//
//                        Message msg = new Message();
//                        Bundle data = new Bundle();
//                        data.putString("stoped","");
//                        msg.setData(data);
//                        runHandle.sendMessage( msg);
//                    }
//                }).start();
            }
        });

//        buttonHelp.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String dStr = "http://127.0.0.1:" + BaseCommon.P2P_SERVER_PORT + "/bt/stream?type=turl&uri=http://23.234.12.131:81/002.torrent";
//                Uri uri =Uri.parse(P2PTrans.getHelpUrl(8777));
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });

//        buttonGetStreamUrl.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = P2PTrans.getTorrentPlayUrl( 8777, editTextTorrentUrl.getText().toString());
//                if ( radioGroupType.getCheckedRadioButtonId() == R.id.radioUrl ) {
//                    url = P2PTrans.getTorrentPlayUrl( 8777, editTextTorrentUrl.getText().toString());
//                }
//                else if ( radioGroupType.getCheckedRadioButtonId() == R.id.radioPath ){
//                    url = P2PTrans.getTorrentPlayPath( 8777, editTextTorrentUrl.getText().toString());
//                }
//                editTextStreamUrl.setText( url );
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("", editTextStreamUrl.getText().toString());
//                clipboard.setPrimaryClip(clip);
//            }
//        });

        buttonCreateServer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnDownloadClicked();

//                serverPort = 8777;
//                textViewInfo.setText("正在启动服务器");
//                buttonCreateServer.setEnabled(false);
//
//                final Handler runHandle = new Handler(){
//                    @Override
//                    public void handleMessage( Message msg) {
//                        super.handleMessage(msg);
//                        Bundle data = msg.getData();
//                        if (TextUtils.isEmpty(data.getString("error")))
//                        {
//                            textViewInfo.setText("服务器启动成功");
////                            buttonGetStreamUrl.setEnabled( true);
////                            buttonHelp.setEnabled( true);
//                            buttonPlayVideo.setEnabled( true);
//                        }
//                        else
//                        {
//                            textViewInfo.setText(data.getString("error"));
//                            buttonCreateServer.setEnabled(true);
//                        }
//                    }
//                };

//                new Thread( new Runnable(){
//                    public void run()
//                    {
//                        int i = 0;
//                        for( ; i < 10; i++)
//                        {
//                            int ret = P2PTrans.run( "", serverPort );
//                            Log.v(TAG, "RunServer ret:" + ret);
//                            if ( ret != 0){
//                                serverPort++;
//                            }
//                            else
//                            {
//                                break;
//                            }
//                        }
//
//                        if ( i == 10){
//                            Message msg = new Message();
//                            Bundle data = new Bundle();
//                            data.putString("error","启动服务器失败");
//                            msg.setData(data);
//                            runHandle.sendMessage( msg);
//                        }
//                    }
//                }).start();
//
//                new Thread( new Runnable(){
//                    public void run()
//                    {
//                    File file = new File(P2PTransActivity.this.getCacheDir()+"/testbt");
//                    if (!file.exists()) {
//                        try {
//                            //按照指定的路径创建文件夹
//                            file.mkdirs();
//                        } catch (Exception e) {
//                            Log.e(TAG,e.getMessage());
//                        }
//                    }
//
//                    if ( file.exists()) {
//                        for( int i = 0; i < 10; i++)
//                        {
//                            try {
//                                Thread.sleep(1000);
//                            } catch (Exception e) {
//                            }
//                            if ( !P2PTrans.testStream( serverPort)) {
//                                continue;
//                            }
//                            if ( !P2PTrans.enableStream( serverPort, file.getPath(), file.getPath(), "8aaf88937afbd0826b2c6c3c5d4e01b3")) {
//                                continue;
//                            }
//                            Message msg = new Message();
//                            runHandle.sendMessage( msg);
//                            return;
//                        }
//                    }
//                    Message msg = new Message();
//                    Bundle data = new Bundle();
//                    data.putString("error","启动服务器失败");
//                    msg.setData(data);
//                    runHandle.sendMessage( msg);
//                    }
//                }).start();
            }
        });
    }
    private static String httpGet( String url){
        String result = "";
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try {
            URL __url = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) __url.openConnection();
            urlConnection.setRequestMethod("GET");
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.i(TAG, e.getMessage());
            result = "";
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }
        return result;
    }

//    private final Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == MSG_START_FAILED) {
//                tvTip.setText("启动服务器失败");
//                btnCreateServer.setEnabled(true);
//            } else if (msg.what == MSG_START_SUCCEED) {
//                tvTip.setText("服务器启动成功");
////                btnGetStreamUrl.setEnabled(true);
////                btnHelp.setEnabled(true);
//                btnPlayVideo.setEnabled(true);
//                btnDownload.setEnabled(true);
//                btnStopAll.setEnabled(true);
//            }
//        }
//    };
private void doOnDownloadClicked() {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            //线程执行内容
            DownModel downModel=new DownModel();
            downModel.setName("这里是测试下载的标题222222");
            downModel.setUrl("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent");
//            downModel.setUrl("http://23.234.12.131:81/011618_633-1.torrent");
//            downModel.setUrl("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent");
            CacheManager.getInstance().downByUrl(downModel);
        }
    });
    //开启线程
    thread.start();

//    Thread thread1 = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            //线程执行内容
//            DownModel downModel=new DownModel();
//            downModel.setName("这里是测试下载的标题");
//            downModel.setUrl("http://23.234.12.131:81/011618_633-2.torrent");
//            CacheManager.getInstance().downByUrl(downModel);
//        }
//    });
//    //开启线程
//    thread1.start();

//    Thread thread2 = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            //线程执行内容
//            DownModel downModel=new DownModel();
//            downModel.setName("这里是测试下载的标题");
//            downModel.setUrl("http://23.234.12.131:81/011618_633-3.torrent");
//            CacheManager.getInstance().downByUrl(downModel);
//        }
//    });
//    //开启线程
//    thread2.start();

//    DownModel downModel1=new DownModel();
//    downModel1.setName("这里是测试下载的标题1111");
//    downModel1.setUrl("http://23.234.12.131:81/001.torrent");
//    CacheManager.getInstance().downByUrl(downModel1);

//    final String url = editTextTorrentUrl.getText().toString().trim();
//    if (url == null) {
//        return;
//    }
//    List<String> urls= Arrays.asList("http://23.234.12.131:81/001.torrent","http://23.234.12.131:81/002.torrent");
//    for(int i=0;i<2;i++){
//        final String url=urls.get(i);
//        new AsyncTask<Void, Integer, P2PTrans.StartStreamResult>() {
//            @Override
//            protected P2PTrans.StartStreamResult doInBackground(Void... voids) {
//                return P2PTrans.startStream(url, BaseCommon.P2P_SERVER_PORT);
//            }
//
//            @Override
//            protected void onPostExecute(P2PTrans.StartStreamResult result) {
//                if (null == result && result.getCode() != 0) {
//                    Toast.makeText(P2PTransActivity.this, "下载失败" + (result == null ? "" : result.getCode()),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Log.e("TAG","显示下载信息");
//                startRefreshDownloadProgressTimer();
//            }
//        }.execute();
//    }


}
private synchronized void startRefreshDownloadProgressTimer() {
        if (timer != null) {
            return;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                P2PTrans.StreamsResult result = P2PTrans.getStreams(serverPort);
                if (result == null) {
                    return;
                }

                List<P2PTrans.StreamInfo> streamInfoList = result.getStreams();
                if (streamInfoList == null) {
                    return;
                }

                final String content = buildLogInfo(streamInfoList);
                Log.e("TAG",content);
                tvProgressInfo.post(new Runnable() {
                    @Override
                    public void run() {
                        tvProgressInfo.setText(content);
                    }
                });
            }
        }, 0, 1500);
    }
    private String buildLogInfo(List<P2PTrans.StreamInfo> streamInfoList) {
        if (streamInfoList == null || streamInfoList.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (P2PTrans.StreamInfo info : streamInfoList) {
            builder.append(String.format(Locale.CHINA, "#streamId: %s，下载速度：%s，进度：%s，已下载：%s",
                    info.getId(), Utils.getDisplayFileSize(info.getDownloadSpeed()), info.getPercent() + "%",
                    Utils.getDisplayFileSize(info.getDownloadedBytes())
            ));
            builder.append("\r\n");
        } // end of for
        return builder.toString();
    }
    //设置缓存目录
//    private final void enabledStreamAndSetDownloadDir() {
//        new AsyncTask<Void, Integer, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                // 设置下载目录
//                File file = new File(MainApplicationContext.SD_PATH);
//                if (!file.exists()) {
//                    try {
//                        //按照指定的路径创建文件夹
//                        file.mkdirs();
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                        return false;
//                    }
//                }
//
//                if (!file.exists()) {
//                    return false;
//                }
//
//                for (int i = 0; i < 10; i++) {
//                    SystemClock.sleep(1000);
//                    if (!P2PTrans.testStream(serverPort)) {
//                        continue;
//                    }
//                    if (!P2PTrans.enableStream(serverPort, file.getPath(), file.getPath(), "8aaf88937afbd0826b2c6c3c5d4e01b3")) {
//                        continue;
//                    }
//                    return true;
//                } // end of for
//                return false;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean succeed) {
//                if (succeed == true) {
////                    handler.sendEmptyMessage(MSG_START_SUCCEED);
//                } else {
////                    handler.sendEmptyMessage(MSG_START_FAILED);
//                }
//            }
//        }.execute();
//    }
}
