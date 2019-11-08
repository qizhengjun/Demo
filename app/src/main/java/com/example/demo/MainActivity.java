package com.example.demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private ImageView mBtn;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(getApplication());
        mBtn = findViewById(R.id.btn);
        new RxPermissions(this).request(Manifest.permission.READ_PHONE_STATE).subscribe(permission -> {
        });
        mBtn.setOnClickListener(v -> {
            //删除
            deal();
        });
    }

    private void deal() {
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null);
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(file, "b.jpeg");
        //保存
        try {
            if (image.exists()) {
                Uri uri = insertFileIntoMediaStore(MainActivity.this, image);
                save(MainActivity.this, image, uri);
                if (uri != null) {
                    Log.e("TAG", uri.toString() + "");
                    Log.e("TAG", MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //读取
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_display_name=?", new String[]{"b.jpeg"}, null);
        while (cursor.moveToFirst()) {
            // 以下方式获取媒体文件URI
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            Log.e("TAG", "dipplay=" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.e("TAG", "data=" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            try {
                Bitmap bitmap = getBitmapFromUri(photoUri);
                if (bitmap != null) {
                    mBtn.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (Exception e){}
        return bitmap;
    }
    public static Uri insertFileIntoMediaStore(Context context, File file) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Images.Media.TITLE, file.getName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator+"test");
        Uri uri = null;
        try {
            //保存bitmap
//            OutputStream out = resolver.openOutputStream(uri);
//            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }


    public void save(Context context, File file, Uri uri) {
        FileOutputStream os = null;
        FileInputStream is = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w");
            os = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            os.write(b);
            os.flush();
        } catch (Exception e) {
        } finally {
            try {
                if (os != null) os.close();
                if (is != null) is.close();
            } catch (Exception e){}
        }
    }
}
