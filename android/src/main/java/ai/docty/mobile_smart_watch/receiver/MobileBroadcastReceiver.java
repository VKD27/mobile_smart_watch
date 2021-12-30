package ai.docty.mobile_smart_watch.receiver;


import android.content.Context;
import android.content.Intent;

import io.flutter.plugin.common.EventChannel;

public class MobileBroadcastReceiver extends android.content.BroadcastReceiver {

    private final EventChannel.EventSink mEventSink;

    public MobileBroadcastReceiver(EventChannel.EventSink eventSink){
        this.mEventSink = eventSink;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String paramsStr = intent.getStringExtra("params");
        mEventSink.success(paramsStr);
    }
}
