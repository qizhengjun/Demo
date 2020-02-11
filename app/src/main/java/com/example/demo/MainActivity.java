package com.example.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.Utils;
import com.lcw.library.imagepicker.ImagePicker;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGES_CODE = 0x11;
    private WebView mWebView;
    private ImageView mBtn;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(getApplication());
        mBtn = findViewById(R.id.btn);
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(permission -> {

        });

        mBtn.setOnClickListener(v -> {
            ImagePicker.getInstance()
                    .setTitle("标题")//设置标题
                    .showCamera(true)//设置是否显示拍照按钮
                    .showImage(true)//设置是否展示图片
                    .showVideo(true)//设置是否展示视频
                    .showVideo(true)//设置是否展示视频
                    .setSingleType(true)//设置图片视频不能同时选择
                    .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
//                    .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
//                    .setImageLoader(new GlideLoader())//设置自定义图片加载器
                    .start(MainActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
        });
    }


}
