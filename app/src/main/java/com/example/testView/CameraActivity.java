package com.example.testView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hmail_Beta01.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by GOD on 2018/8/30.
 */

public class CameraActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private Button button, button2;
    private ImageView view1, view2, view3, view4;

    // 记录文件保存位置
    private String mFilePath;
    private FileInputStream is = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))+".jpg";
        // 获取SD卡路径
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        // 文件名
        mFilePath = mFilePath + "/Hmail/" + name;
        Log.w("拍照文件路径",mFilePath+"");
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        view1 = (ImageView) findViewById(R.id.imageView1);
        view2 = (ImageView) findViewById(R.id.imageView2);
        view3 = (ImageView) findViewById(R.id.imageView3);
        view4 = (ImageView) findViewById(R.id.imageView4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                // 指定拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 加载路径
                Uri uri = Uri.fromFile(new File(mFilePath));
                // 指定存储路径，这样就可以保存原图了
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 拍照返回图片
                startActivityForResult(intent, 2);

            }
        });
    }

    @SuppressLint("SdCardPath")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("TestFile",
                            "SD card is not avaiable/writeable right now.");
                    return;
                }
                new DateFormat();
                String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                FileOutputStream b = null;
                File file = new File("/sdcard/Hmail/");
                file.mkdirs();// 创建文件夹
                String fileName = "/sdcard/Hmail/" + name;
                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    view1.setImageBitmap(bitmap);// 将图片显示在ImageView里
                    view2.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }else if(requestCode == 2){
                try {
                    // 获取输入流
                    is = new FileInputStream(mFilePath);
                    // 把流解析成bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    // 设置图片
                    view3.setImageBitmap(bitmap);
                    view4.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // 关闭流
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}