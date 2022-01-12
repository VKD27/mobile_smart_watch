package ai.docty.mobile_smart_watch.handler;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ai.docty.mobile_smart_watch.receiver.MobileBroadcastReceiver;
import ai.docty.mobile_smart_watch.util.WatchConstants;
import io.flutter.plugin.common.EventChannel;

public class SmartStreamHandler implements EventChannel.StreamHandler {

    private final Context mContext;
    private MobileBroadcastReceiver broadcastReceiver;


    public SmartStreamHandler(Context context){
        this.mContext = context;
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        broadcastReceiver = new MobileBroadcastReceiver(eventSink);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter(WatchConstants.BROADCAST_ACTION_NAME));
    }

    @Override
    public void onCancel(Object o) {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }
}