package com.xcore.data.p2pbean;

import android.text.TextUtils;

import com.xcore.data.CmModelBean;

import java.util.HashMap;
import java.util.List;

public class StreamsResultBean extends CmModelBean {
    private List<StartStreamResultBean.StreamInfo> streams;

    public List<StartStreamResultBean.StreamInfo> getStreams() {
        return streams;
    }

    public void setStreams(List<StartStreamResultBean.StreamInfo> streams) {
        this.streams = streams;
    }

    public HashMap<String, StartStreamResultBean.StreamInfo> getStreamsMap() {
        HashMap<String, StartStreamResultBean.StreamInfo> result = new HashMap<>();
        if (streams == null) {
            return result;
        }

        for (StartStreamResultBean.StreamInfo info : streams) {
            if (TextUtils.isEmpty(info.getId()) || info == null) {
                continue;
            }

            result.put(info.getId(), info);
        } // end of for

        return result;
    }
}
