package com.xcore.services;

import android.util.Log;
import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;

public class ApiFactory {
    private ApiService apiService=new ApiService();

    public ApiFactory(){}
    private static ApiFactory instance;
    public static ApiFactory getInstance(){
        if(instance==null){
            instance=new ApiFactory();
        }
        //判断token 是否失效
        final TokenBean tokenBean= DataUtils.tokenBean;
        if(tokenBean!=null&&!tokenBean.token_verify()){//没有token
            Log.e("TAG","重新刷新TOKEN");
            ApiSystemFactory.getInstance().refreshToken(tokenBean);
        }
        return instance;
    }

    //得到我的推广信息
    public <T> void getMyShareList(int pageIndex,AGCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/appuser/getShareRecord";
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        apiService.<T>post(url,callback,params,null);
//        OkGo.<T>post(url)
//                .params("PageIndex",pageIndex)
//                .execute(callback);
    }
    //得到个人中心信息
    public <T> void getUserInfo(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/appuser/getUserInfo";
//        OkGo.<T>post(url).execute(callback);
        apiService.<T>post(url,callback,null,null);
    }
    //得到标签
    public <T> void getTags(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/categories/getMovueCategories";
        apiService.<T>get(url,callback);
    }
    //得到首页数据
    public <T> void getHomeData(AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getHomeData";
        apiService.<T>post(url,callback,null,null);
    }
    //根据类型查询(类型 复杂查询)
    public <T>void getTypeByData(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieSearch";
        apiService.<T>post(url,callback,params,null);
    }

    //得到女星信息
    public <T> void getActress(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/actor/getMovieByActor";
        apiService.<T>post(url,callback,params,null);
    }
    //得到标签信息
    public <T> void getTag(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getMovieBytags";
        apiService.<T>post(url,callback,params,null);
    }
    //得到标签列表
    public <T> void getMovieBytagsList(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getMovieBytagsList";
        apiService.<T>post(url,callback,params,null);
    }

    //获取开宝箱时间
    public <T> void getBoxTime(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/openbox/boxNextTime";
        apiService.<T>get(url,callback);
    }
    //打开宝箱
    public <T> void getOpenBox(AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/openbox/openbox";
        apiService.<T>get(url,callback);
    }
    //得到影片详情信息
    public <T>void getMovieDetail(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/Detail";
        apiService.<T>post(url,callback,params,null);
    }
    //得到播放路径
    public <T>void getPlayPath(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/Play";
        apiService.<T>post(url,callback,params,null);
    }
    //添加收藏
    public <T>void addCollect(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/addCollection";
        apiService.<T>post(url,callback,params,null);
    }
    //取消收藏
    public <T>void removeCollect(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/deleteCollection";
        apiService.<T>post(url,callback,params,null);
    }
    //得到下载路径
    public <T>void getDownPath(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/addCache";
        apiService.<T>post(url,callback,params,null);
    }
    //喜欢或踩
    public <T>void getLikeOrNo(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/praise";
        apiService.<T>post(url,callback,params,null);
    }
    //得到反馈类型
    public <T> void getFeedbackTypes(AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/getGuestBookType";
        apiService.<T>get(url,callback);
    }
    //添加反馈
    public <T>void addFeedback(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/addBuestBook";
        apiService.<T>post(url,callback,params,null);
    }
    //获取反馈列表
    public <T>void getFeedbackList(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/buestBook/getBuestBook";
        apiService.<T>post(url,callback,params,null);
    }
    //获取发现列表
        public <T>void getFinds(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/findings";
        apiService.<T>post(url,callback,params,null);
    }
    //得到公告信息
    public <T>void getNotices(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getAnnouncement";
        apiService.<T>post(url,callback,params,null);
    }
    //得到女星推荐
    public <T>void getNvStarRecommend(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/actor/getRecommendActor";
        apiService.<T>post(url,callback,params,null);
    }
    //主题推荐
    public <T>void getThems(AGCallback<T> callback,HttpParams params){
        String url=BaseCommon.API_URL+"api/v1/themes/getThemes";
        apiService.<T>post(url,callback,params,null);
    }
    //根据主题ID查询
    public <T>void getThemeById(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/themes/getThemesByThemeID";
        apiService.<T>post(url,callback,params,null);
    }

    //得到主题大图片
    public <T>void getThemeByIdCover(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/themes/getThemesById";
        apiService.<T>post(url,callback,params,null);
    }


    //得到热门关键字
    public <T>void getHotDic(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getSearchKey";
        apiService.<T>get(url,callback);
    }
    //根据关键字搜索
    public <T>void getSearchByKey(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieKeyword";
        apiService.<T>post(url,callback,params,null);
    }
    //得到所有标签
    public <T>void getAllTags(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/tags/getTagsRoot";
        apiService.<T>get(url,callback);
    }
    //得到所有的标签列表
    public <T>void getAllTagsList(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/tags/getTagsByTagsRoot";
        apiService.<T>post(url,callback,params,null);
    }
    //得到vip信息?
    public <T>void getViplevel(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/viplevel/getViplevel";
        apiService.<T>get(url,callback);
    }

    /**
     * 更新用户信息
     * @param params
     * @param callback
     * @param <T>
     */
    public <T>void updateUserInfo(HttpParams params,AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/appuser/saveUserInfo";
        apiService.<T>post(url,callback,params,null);
    }

    /**
     * 更新用户密码
     * @param params
     * @param callback
     * @param <T>
     */
    public <T>void updateUserPass(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/appuser/savePassword";
        apiService.<T>post(url,callback,params,null);
    }


    //得到推荐信息
    public <T>void getRecommends(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/movie/getRecommendData";
        apiService.<T>post(url,callback,null,null);
    }

    //得到弹窗公告
    public <T>void getNoticeAlert(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/GetAlert";
        apiService.get(url,callback);
    }


    //进入activity
    public <T>void joinView(String className,String paramsStr,AGCallback callback){
        String url=BaseCommon.API_URL+"api/v1/system/joinView";
        HttpParams params=new HttpParams();
        params.put("shortId",className);
        params.put("key",paramsStr);
        apiService.<T>post(url,callback,params,null);
    }

    //退出activity
    public <T>void outView(String className,String paramsStr,AGCallback callback){
        String url=BaseCommon.API_URL+"api/v1/system/outView";
        HttpParams params=new HttpParams();
        params.put("shortId",className);
        apiService.<T>post(url,callback,params,null);
    }

    //上传崩溃日志
    public <T>void toCrashLog(String msg,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/collapse";
        HttpParams params=new HttpParams();
        params.put("key",msg);
        apiService.post(url,callback,params,null);
    }

    /**
     * 上传错误日志信息
     * @param <T>
     */
    public <T>void toError(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/ARR";
        apiService.<T>post(url,callback,params,null);
    }

    /**
     *
     * @param base64
     * @param callback
     * @param <T>
     */
    public <T>void toUploadSingle(String base64,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/fileImage";
        HttpParams params=new HttpParams();
        params.put("key",base64);
        apiService.<T>post(url,callback,params,null);
    }

    /**
     * 播放进度
     * @param params
     * @param callback
     * @param <T>
     */
    public <T>void toPlayInfo(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/playSpeedRecordAdd";
        apiService.<T>post(url,callback,params,null);
    }
    /**
     * 更新播放进度
     * @param params
     * @param callback
     * @param <T>
     */
    public <T>void toUpdatePlayInfo(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/playSpeedRecordUpdate";
        apiService.<T>post(url,callback,params,null);
    }

    /**
     *  缓存进度
     * @param params
     * @param callback
     * @param <T>
     */
    public <T>void toCacheInfo(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/updateCacheLength";
        apiService.<T>post(url,callback,params,null);
    }

    /**
     * 心跳
     * @param callback
     * @param <T>
     */
    public <T> void toHeart(AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/heartbeat";
        apiService.get(url,callback);
    }

    //获取女星信息
    public <T> void getNvStars(HttpParams httpParams,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/actor/getactor";
        apiService.post(url,callback,httpParams,null);
    }

    //获取一本道。。。标签信息
    public <T> void getCompanyTags(HttpParams httpParams,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/GetMovieCompanyData";
        apiService.post(url,callback,httpParams,null);
    }
    //获取一本道...影片信息
    public <T>void getCompanyInfos(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/movie/getMovieCompany";
        apiService.post(url,callback,params,null);
    }

    /**
     * 测试ip 端口
     * @param <T>
     */
    public <T>void toServerSocket(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/system/AddTestUrlRecord";
        apiService.post(url,callback,params,null);
    }

}
