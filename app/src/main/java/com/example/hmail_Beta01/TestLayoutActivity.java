package com.example.hmail_Beta01;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SQLite.SQLiteActivity;
import com.example.testView.CameraActivity;
import com.example.testView.MyAsyncTask;
import com.example.testView.myImageTestActivity;
import com.example.testView.myViewActivity;
import com.example.widegt.GoTopScrollView;
import com.example.widegt.RoundImageView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class TestLayoutActivity extends Activity implements OnClickListener,
        SensorEventListener {

    SeekBar sb_normal;
    TextView txt_cur, txttitle, tv_camera, tv_ip, tv_sensor_show, tv_value1, tv_value2,
            tv_value3;
    Button btn_toast;
    private ProgressBar pgbar;

    private NotificationManager mNManager;
    private Notification notify1;
    Bitmap LargeBitmap = null;
    private static final int NOTIFYID_1 = 1;
    private Button btn_show_normal;
    private Button btn_close_normal;
    private Button btn_show, btnupdate, btn_sqlite;
    private GoTopScrollView sv_home;
    private ImageView iv_zd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.test_layout);

        initView();
        initTextView();
        initImageView();
        initSeekBar();
        initRatingBar();
        initToast();
        initNotification();
        initPopupWindow();
        initAsyncTask();
        initSQLite();
        initIp();
        initSensor();
        initEdit();

        // 比如这里做一个简单的判断，当页面发生滚动，显示那个Button
        sv_home.setImgeViewOnClickGoToFirst(iv_zd);
    }

    private void initView() {
        tv_ip = (TextView) findViewById(R.id.tv_ip);
        Button tomyview = (Button) findViewById(R.id.btn_tomyview);
        sv_home = (GoTopScrollView) findViewById(R.id.sv_home);
        Button btn_tomyimagetest = (Button) findViewById(R.id.btn_tomyimagetest);
        iv_zd = (ImageView) findViewById(R.id.iv_zd);

        iv_zd.setOnClickListener(this);

        tomyview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent();
                i.setClass(TestLayoutActivity.this, myViewActivity.class);
                startActivity(i);
            }
        });
        btn_tomyimagetest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent();
                i.setClass(TestLayoutActivity.this, myImageTestActivity.class);
                startActivity(i);
            }
        });

        TextView tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(TestLayoutActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });

    }

    // 定义一个点击每个部分文字的处理方法
    private SpannableStringBuilder addClickPart(String str) {
        // 赞的图标，这里没有素材，就找个笑脸代替下~
        ImageSpan imgspan = new ImageSpan(TestLayoutActivity.this,
                R.drawable.yhm);
        SpannableString spanStr = new SpannableString("p.");
        spanStr.setSpan(imgspan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        // 创建一个SpannableStringBuilder对象，连接多个字符串
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
        ssb.append(str);
        String[] likeUsers = str.split("，");
        if (likeUsers.length > 0) {
            for (int i = 0; i < likeUsers.length; i++) {
                final String name = likeUsers[i];
                final int start = str.indexOf(name) + spanStr.length();
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(TestLayoutActivity.this, name,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        // 删除下划线，设置字体颜色为蓝色
                        ds.setColor(Color.BLUE);
                        ds.setUnderlineText(false);
                    }
                }, start, start + name.length(), 0);
            }
        }
        return ssb.append("等" + likeUsers.length + "个人觉得很赞");
    }

    private void initTextView() {
        TextView txt_1 = (TextView) findViewById(R.id.txt_1);
        String s1 = "<font color='red'><b>百度一下，你就知道~：</b></font><br>";
        s1 += "<a href = 'http://www.baidu.com'>百度</a>";
        txt_1.setText(Html.fromHtml(s1));
        txt_1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txt_2 = (TextView) findViewById(R.id.txt_2);
        SpannableString span = new SpannableString("红色打电话斜体删除线绿色下划线图片:.");
        // 1.设置背景色,setSpan时需要指定的flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        span.setSpan(new ForegroundColorSpan(Color.RED), 0, 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 2.用超链接标记文本
        span.setSpan(new URLSpan("tel:133****2537"), 2, 5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 3.用样式标记文本（斜体）
        span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 5, 7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 4.用删除线标记文本
        span.setSpan(new StrikethroughSpan(), 7, 10,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 5.用下划线标记文本
        span.setSpan(new UnderlineSpan(), 10, 16,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 6.用颜色标记
        span.setSpan(new ForegroundColorSpan(Color.GREEN), 10, 13,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 7.//获取Drawable资源
        Drawable d = getResources().getDrawable(R.drawable.wd);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        // 8.创建ImageSpan,然后用ImageSpan来替换文本
        ImageSpan imgspan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        span.setSpan(imgspan, 18, 19, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_2.setText(span);

        TextView txt_3 = (TextView) findViewById(R.id.txt_3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append("好友" + i + "，");
        }

        String likeUsers = sb.substring(0, sb.lastIndexOf("，")).toString();
        txt_3.setMovementMethod(LinkMovementMethod.getInstance());
        txt_3.setText(addClickPart(likeUsers), TextView.BufferType.SPANNABLE);

        // Configuration
        TextView tv_configuration = (TextView) findViewById(R.id.tv_configuration);
        StringBuffer status = new StringBuffer();
        //①获取系统的Configuration对象
        Configuration cfg = getResources().getConfiguration();
        //②想查什么查什么
        status.append("densityDpi:" + cfg.densityDpi + "\n");
        status.append("fontScale:" + cfg.fontScale + "\n");
        status.append("hardKeyboardHidden:" + cfg.hardKeyboardHidden + "\n");
        status.append("keyboard:" + cfg.keyboard + "\n");
        status.append("keyboardHidden:" + cfg.keyboardHidden + "\n");
        status.append("locale:" + cfg.locale + "\n");
        status.append("mcc:" + cfg.mcc + "\n");
        status.append("mnc:" + cfg.mnc + "\n");
        status.append("navigation:" + cfg.navigation + "\n");
        status.append("navigationHidden:" + cfg.navigationHidden + "\n");
        status.append("orientation:" + cfg.orientation + "\n");
        status.append("screenHeightDp:" + cfg.screenHeightDp + "\n");
        status.append("screenWidthDp:" + cfg.screenWidthDp + "\n");
        status.append("screenLayout:" + cfg.screenLayout + "\n");
        status.append("smallestScreenWidthDp:" + cfg.densityDpi + "\n");
        status.append("touchscreen:" + cfg.densityDpi + "\n");
        status.append("uiMode:" + cfg.densityDpi + "\n");
        tv_configuration.setText(status.toString());
    }

    private void initImageView() {

        RoundImageView img_round; // 圆形imageview
        img_round = (RoundImageView) findViewById(R.id.img_round);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.lj);
        img_round.setBitmap(bitmap);
    }

    /**
     * 滑动条
     */
    private void initSeekBar() {
        bindViews();
    }

    private void bindViews() {
        sb_normal = (SeekBar) findViewById(R.id.seekbar_1);
        txt_cur = (TextView) findViewById(R.id.tv_seekbar);
        sb_normal
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        txt_cur.setText("当前进度值:" + progress + "  / 100 ");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(TestLayoutActivity.this, "触碰SeekBar",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(TestLayoutActivity.this, "放开SeekBar",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 笑脸评分
     */
    private void initRatingBar() {
        RatingBar rb_normal;
        rb_normal = (RatingBar) findViewById(R.id.rb_normal);
        rb_normal
                .setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float rating, boolean fromUser) {
                        Toast.makeText(TestLayoutActivity.this,
                                "rating:" + String.valueOf(rating),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 滑动条
     */
    private void initToast() {
        btn_toast = (Button) findViewById(R.id.btn_toast);
        btn_toast.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("toast!!!");
                midToast("数据异常，请稍后重试!", 1);
            }
        });
    }

    /**
     * 自定义toast
     *
     * @param str
     * @param showTime
     */
    private void midToast(String str, int showTime) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_toast_custom,
                (ViewGroup) findViewById(R.id.lly_toast));
        ImageView img_logo = (ImageView) view.findViewById(R.id.img_logo);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);

        tv_msg.setText(str);
        Toast toast = new Toast(TestLayoutActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0); // 位置
        toast.setDuration(Toast.LENGTH_SHORT);
        // toast.setDuration(showTime); // 持续时间
        toast.setView(view);
        toast.show();
    }

    /**
     * 消息通知
     */
    private void initNotification() {
        LargeBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.huaji);
        mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        btn_show_normal = (Button) findViewById(R.id.btn_show_normal);
        btn_close_normal = (Button) findViewById(R.id.btn_close_normal);
        btn_show_normal.setOnClickListener(this);
        btn_close_normal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_show_normal:
                // 定义一个PendingIntent点击Notification后启动一个Activity
                Intent it = new Intent(TestLayoutActivity.this,
                        LoadingActivity.class);
                PendingIntent pit = PendingIntent.getActivity(
                        TestLayoutActivity.this, 0, it, 0);

                // 设置图片,通知标题,发送时间,提示方式等属性
                Notification.Builder mBuilder = new Notification.Builder(this);
                mBuilder.setContentTitle("叶良辰") // 标题
                        .setContentText("我有一百种方法让你呆不下去~") // 内容
                        .setSubText("——跳转到加载页面") // 内容下面的一小段文字
                        .setTicker("收到叶良辰发送过来的信息~") // 收到信息后状态栏显示的文字信息
                        .setWhen(System.currentTimeMillis()) // 设置通知时间
                        .setSmallIcon(R.drawable.ic_rating_on1) // 设置小图标
                        .setLargeIcon(LargeBitmap) // 设置大图标
                        .setDefaults(Notification.DEFAULT_LIGHTS) // 设置默认的三色灯与振动器
                        // 默认震动|
                        // Notification.DEFAULT_VIBRATE
                        .setVibrate(new long[]{0, 100, 100, 100, 300, 100}) // 延时0ms响100ms，再延时100ms响100ms，延时300ms响100ms
                        // .setSound(Uri.parse("android.resource://" +
                        // getPackageName() + "/" + R.raw.biaobiao)) //设置自定义的提示音
                        .setAutoCancel(true) // 设置点击后取消Notification
                        .setContentIntent(pit); // 设置PendingIntent
                notify1 = mBuilder.build();
                mNManager.notify(NOTIFYID_1, notify1);
                break;

            case R.id.btn_close_normal:
                // 除了可以根据ID来取消Notification外,还可以调用cancelAll();关闭该应用产生的所有通知
                mNManager.cancel(NOTIFYID_1); // 取消Notification
                break;

            case R.id.iv_zd:
                sv_home.setScrollY(0);
                iv_zd.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 浮动框
     */
    private void initPopupWindow() {
        btn_show = (Button) findViewById(R.id.btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow(v);
            }
        });
    }

    private void initPopWindow(View v) {
        System.out.println("进入悬浮框");
        View view = LayoutInflater.from(TestLayoutActivity.this).inflate(
                R.layout.item_popip, null, false);
        Button btn_xixi = (Button) view.findViewById(R.id.btn_xixi);
        Button btn_hehe = (Button) view.findViewById(R.id.btn_hehe);
        // 1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.animator.anim_pop); // 设置加载动画

        // 这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        // 代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        // PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 要为popWindow设置一个背景才有效

        // 设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 50, -50);

        // 设置popupWindow里的按钮的事件
        btn_xixi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestLayoutActivity.this, "你点击了添加~",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btn_hehe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestLayoutActivity.this, "你点击了删除~",
                        Toast.LENGTH_SHORT).show();
                popWindow.dismiss();
            }
        });
    }

    /**
     * AsyncTask
     */
    private void initAsyncTask() {
        txttitle = (TextView) findViewById(R.id.txttitle);
        pgbar = (ProgressBar) findViewById(R.id.pgbar);
        btnupdate = (Button) findViewById(R.id.btnupdate);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask myTask = new MyAsyncTask(txttitle, pgbar);
                myTask.execute(1000);
            }
        });
    }

    private void initSQLite() {
        btn_sqlite = (Button) findViewById(R.id.btn_sqlite);
        btn_sqlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(TestLayoutActivity.this, SQLiteActivity.class);
                startActivity(i);
            }
        });
    }

    private void initIp() {
        new Thread() {
            @Override
            public void run() {
                // 把网络访问的代码放在这里
                InetAddress address = null;

                try {
                    address = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                System.out.println("本机名：" + address.getHostName());
                System.out.println("IP地址：" + address.getHostAddress());
                byte[] bytes = address.getAddress();
                System.out.println("字节数组形式的IP地址：" + Arrays.toString(bytes));
                System.out.println("直接输出InetAddress对象：" + address);
				tv_ip.setText("本机名：" + address.getHostName() + "\r\n" + "IP地址："
						+ address.getHostAddress() + "\r\n" + "字节数组形式的IP地址："
						+ Arrays.toString(bytes) + "\r\n"
						+ "直接输出InetAddress对象：" + address);
            }
        }.start();

    }

    private SensorManager sm;
    private SensorManager sManager;
    private Sensor mSensorOrientation;

    /**
     * 传感器
     */
    private void initSensor() {
        TextView tv_sensor_show = (TextView) findViewById(R.id.tv_sensor_show);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sb = new StringBuilder();

        sb.append("此手机有" + allSensors.size() + "个传感器，分别有：\n\n");
        for (Sensor s : allSensors) {
            switch (s.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sb.append(s.getType() + " 加速度传感器(Accelerometer sensor)" + "\n");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sb.append(s.getType() + " 陀螺仪传感器(Gyroscope sensor)" + "\n");
                    break;
                case Sensor.TYPE_LIGHT:
                    sb.append(s.getType() + " 光线传感器(Light sensor)" + "\n");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sb.append(s.getType() + " 磁场传感器(Magnetic field sensor)" + "\n");
                    break;
                case Sensor.TYPE_ORIENTATION:
                    sb.append(s.getType() + " 方向传感器(Orientation sensor)" + "\n");
                    break;
                case Sensor.TYPE_PRESSURE:
                    sb.append(s.getType() + " 气压传感器(Pressure sensor)" + "\n");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sb.append(s.getType() + " 距离传感器(Proximity sensor)" + "\n");
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    sb.append(s.getType() + " 温度传感器(Temperature sensor)" + "\n");
                    break;
                default:
                    sb.append(s.getType() + " 其他传感器" + "\n");
                    break;
            }
            sb.append("设备名称：" + s.getName() + "\n 设备版本：" + s.getVersion()
                    + "\n 供应商：" + s.getVendor() + "\n\n");
        }
        tv_sensor_show.setText(sb.toString());

        // 方向传感器
        tv_value1 = (TextView) findViewById(R.id.tv_value1);
        tv_value2 = (TextView) findViewById(R.id.tv_value2);
        tv_value3 = (TextView) findViewById(R.id.tv_value3);

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sManager.registerListener(this, mSensorOrientation,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_value1.setText("方位角：" + (float) (Math.round(event.values[0] * 100))
                / 100);
        tv_value2.setText("倾斜角：" + (float) (Math.round(event.values[1] * 100))
                / 100);
        tv_value3.setText("滚动角：" + (float) (Math.round(event.values[2] * 100))
                / 100);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private EditText et_cardNumber;
    private EditText et_phoneNumber;

    /**
     * 银行卡输入
     */
    private void initEdit() {

        et_cardNumber = (EditText) findViewById(R.id.et_cardNumber);
        et_cardNumber.addTextChangedListener(watcher);
        et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        et_phoneNumber.addTextChangedListener(watcher_et_phoneNumber);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString().trim().replace(" ", "");
            String result = "";
            if (str.length() >= 4) {
                et_cardNumber.removeTextChangedListener(watcher);
                for (int i = 0; i < str.length(); i++) {
                    result += str.charAt(i);
                    if ((i + 1) % 4 == 0) {
                        result += " ";
                    }
                }
                if (result.endsWith(" ")) {
                    result = result.substring(0, result.length() - 1);
                }
                et_cardNumber.setText(result);
                et_cardNumber.addTextChangedListener(watcher);
                et_cardNumber.setSelection(et_cardNumber.getText().toString()
                        .length());// 焦点到输入框最后位置
            }
        }
    };

    private TextWatcher watcher_et_phoneNumber = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString().trim().replace("-", "");
            String result = "";
            if (str.length() >= 3 && str.length() < 11) {
                et_phoneNumber
                        .removeTextChangedListener(watcher_et_phoneNumber);
                for (int i = 0; i < str.length(); i++) {
                    result += str.charAt(i);
                    if (i == 2) {
                        result += "-";
                    }
                    if (i == 6) {
                        result += "-";
                    }
                }
                if (result.endsWith("-")) {
                    result = result.substring(0, result.length() - 1);
                }
                et_phoneNumber.setText(result);
                et_phoneNumber.addTextChangedListener(watcher_et_phoneNumber);
                et_phoneNumber.setSelection(et_phoneNumber.getText().toString()
                        .length());// 焦点到输入框最后位置
            } else if (str.length() == 11) {
                et_cardNumber.requestFocus();//让et_cardNumber获取焦点
                et_cardNumber.setSelection(et_cardNumber.getText().toString()
                        .length());
            }
        }
    };
}
