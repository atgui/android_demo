package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.NvStarBean;
import com.xcore.data.bean.RecommendBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.RecommendView;
import com.xcore.services.ApiFactory;

public class RecommendPresenter extends BasePresent<RecommendView> {

    /**
     * 女星推荐
     * @param pageIndex
     */
   public void getNvStarRecommend(final int pageIndex){

       HttpParams params=new HttpParams();
       params.put("PageIndex",pageIndex);
       params.put("PageSize",8);

       ApiFactory.getInstance().<NvStarBean>getNvStarRecommend(params, new AGCallback<NvStarBean>() {
           @Override
           public void onNext(NvStarBean nvStarBean) {
               view.onRecommendNvStar(nvStarBean);
           }

           @Override
           public void onError(Response<NvStarBean> response) {
               super.onError(response);
           }
       });
   }
    //主题推荐
   public void getThemeRecommend(){
       HttpParams params=new HttpParams();
       params.put("PageIndex",1);

       ApiFactory.getInstance().<ThemeRecommendBean>getThems(new AGCallback<ThemeRecommendBean>() {
           @Override
           public void onNext(ThemeRecommendBean recommendBean) {
               view.onRecommendTheme(recommendBean);
           }

           @Override
           public void onError(Response<ThemeRecommendBean> response) {
               super.onError(response);
           }
       },params);
   }

   //得到推荐数据
   public void getRecommendData(){
       if(!checkNetwork()){
           return;
       }
       if(dialog!=null){
           dialog.show();
       }
       try {
           ApiFactory.getInstance().<RecommendBean>getRecommends(new AGCallback<RecommendBean>() {
               @Override
               public void onNext(RecommendBean recommendBean) {
                   view.onRecommendResult(recommendBean);
                   if (dialog != null) {
                       dialog.cancel();
                   }
               }

               @Override
               public void onError(Response<RecommendBean> response) {
                   super.onError(response);
                   if (dialog != null) {
                       dialog.cancel();
                   }
               }
           });
       }catch (Exception e){}

   }


}
