package ai.docty.mobile_smart_watch.receiver;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.flutter.plugin.common.EventChannel;

public class RebootMobileBroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String paramsStr = intent.getStringExtra("params");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("GEOFENCING REBOOT", "Reregistering geofences!");
           // GeofencingPlugin.reRegisterAfterReboot(context)
        }
        //mEventSink.success(paramsStr);
    }
}
