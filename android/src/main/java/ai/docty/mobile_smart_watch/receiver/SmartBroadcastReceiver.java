package ai.docty.mobile_smart_watch.receiver;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import io.flutter.plugin.common.EventChannel;

public class SmartBroadcastReceiver extends android.content.BroadcastReceiver {

    private final EventChannel.EventSink mEventSink;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SmartBroadcastReceiver(EventChannel.EventSink eventSink){
        this.mEventSink = eventSink;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String paramsStr = intent.getStringExtra("params");
        mEventSink.success(paramsStr);
    }

    private void sendEvent() {
        Runnable runnable = new Runnable() {
           @Override
           public void run() {
               //mEventSink.success();
           }
        };
        mainHandler.post(runnable);
    }
}
