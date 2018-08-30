package com.example.hmail_Beta01;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testView.CameraActivity;
import com.example.testView.MyAsyncTask;
import com.example.testView.myImageTestActivity;
import com.example.testView.myViewActivity;
import com.example.widegt.RoundImageView;

public class TestLayoutActivity extends Activity implements OnClickListener {

	SeekBar sb_normal;
	TextView txt_cur, txttitle,tv_camera;
	Button btn_toast;
    private ProgressBar pgbar;  
	
	private NotificationManager mNManager;
	private Notification notify1;
	Bitmap LargeBitmap = null;
	private static final int NOTIFYID_1 = 1;

	private Button btn_show_normal;
	private Button btn_close_normal;

	private Button btn_show, btnupdate;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
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
	}

	private void initView(){
		Button tomyview = (Button)findViewById(R.id.btn_tomyview);

		Button btn_tomyimagetest = (Button)findViewById(R.id.btn_tomyimagetest);
		
		tomyview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i =new Intent();
				i.setClass(TestLayoutActivity.this, myViewActivity.class);
				startActivity(i);
			}
		});
		btn_tomyimagetest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i =new Intent();
				i.setClass(TestLayoutActivity.this, myImageTestActivity.class);
				startActivity(i);
			}
		});

		TextView tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(TestLayoutActivity.this,CameraActivity.class);
				startActivity(i);
			}
		});

	}
	
	// ����һ�����ÿ���������ֵĴ�����
	private SpannableStringBuilder addClickPart(String str) {
		// �޵�ͼ�꣬����û���زģ����Ҹ�Ц��������~
		ImageSpan imgspan = new ImageSpan(TestLayoutActivity.this,
				R.drawable.yhm);
		SpannableString spanStr = new SpannableString("p.");
		spanStr.setSpan(imgspan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		// ����һ��SpannableStringBuilder�������Ӷ���ַ���
		SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
		ssb.append(str);
		String[] likeUsers = str.split("��");
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
						// ɾ���»��ߣ�����������ɫΪ��ɫ
						ds.setColor(Color.BLUE);
						ds.setUnderlineText(false);
					}
				}, start, start + name.length(), 0);
			}
		}
		return ssb.append("��" + likeUsers.length + "���˾��ú���");
	}

	private void initTextView() {
		TextView txt_1 = (TextView) findViewById(R.id.txt_1);
		String s1 = "<font color='red'><b>�ٶ�һ�£����֪��~��</b></font><br>";
		s1 += "<a href = 'http://www.baidu.com'>�ٶ�</a>";
		txt_1.setText(Html.fromHtml(s1));
		txt_1.setMovementMethod(LinkMovementMethod.getInstance());

		TextView txt_2 = (TextView) findViewById(R.id.txt_2);
		SpannableString span = new SpannableString("��ɫ��绰б��ɾ������ɫ�»���ͼƬ:.");
		// 1.���ñ���ɫ,setSpanʱ��Ҫָ����flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(ǰ�󶼲�����)
		span.setSpan(new ForegroundColorSpan(Color.RED), 0, 2,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 2.�ó����ӱ���ı�
		span.setSpan(new URLSpan("tel:133****2537"), 2, 5,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 3.����ʽ����ı���б�壩
		span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 5, 7,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 4.��ɾ���߱���ı�
		span.setSpan(new StrikethroughSpan(), 7, 10,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 5.���»��߱���ı�
		span.setSpan(new UnderlineSpan(), 10, 16,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 6.����ɫ���
		span.setSpan(new ForegroundColorSpan(Color.GREEN), 10, 13,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 7.//��ȡDrawable��Դ
		Drawable d = getResources().getDrawable(R.drawable.wd);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		// 8.����ImageSpan,Ȼ����ImageSpan���滻�ı�
		ImageSpan imgspan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		span.setSpan(imgspan, 18, 19, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		txt_2.setText(span);

		TextView txt_3 = (TextView) findViewById(R.id.txt_3);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			sb.append("����" + i + "��");
		}

		String likeUsers = sb.substring(0, sb.lastIndexOf("��")).toString();
		txt_3.setMovementMethod(LinkMovementMethod.getInstance());
		txt_3.setText(addClickPart(likeUsers), TextView.BufferType.SPANNABLE);
		
		// Configuration
		TextView tv_configuration = (TextView) findViewById(R.id.tv_configuration);
		 StringBuffer status = new StringBuffer();
	        //�ٻ�ȡϵͳ��Configuration����
	        Configuration cfg = getResources().getConfiguration();
	        //�����ʲô��ʲô
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

		RoundImageView img_round; // Բ��imageview
		img_round = (RoundImageView) findViewById(R.id.img_round);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.lj);
		img_round.setBitmap(bitmap);
	}

	/**
	 * ������
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
						txt_cur.setText("��ǰ����ֵ:" + progress + "  / 100 ");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						Toast.makeText(TestLayoutActivity.this, "����SeekBar",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						Toast.makeText(TestLayoutActivity.this, "�ſ�SeekBar",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * Ц������
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
	 * ������
	 */
	private void initToast() {
		btn_toast = (Button) findViewById(R.id.btn_toast);
		btn_toast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("toast!!!");
				midToast("�����쳣�����Ժ�����!", 1);
			}
		});
	}

	/**
	 * �Զ���toast
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
		toast.setGravity(Gravity.CENTER, 0, 0); // λ��
		toast.setDuration(Toast.LENGTH_SHORT);
		// toast.setDuration(showTime); // ����ʱ��
		toast.setView(view);
		toast.show();
	}

	/**
	 * ��Ϣ֪ͨ
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
			// ����һ��PendingIntent���Notification������һ��Activity
			Intent it = new Intent(TestLayoutActivity.this,
					LoadingActivity.class);
			PendingIntent pit = PendingIntent.getActivity(
					TestLayoutActivity.this, 0, it, 0);

			// ����ͼƬ,֪ͨ����,����ʱ��,��ʾ��ʽ������
			Notification.Builder mBuilder = new Notification.Builder(this);
			mBuilder.setContentTitle("Ҷ����") // ����
					.setContentText("����һ���ַ������������ȥ~") // ����
					.setSubText("������ת������ҳ��") // ���������һС������
					.setTicker("�յ�Ҷ�������͹�������Ϣ~") // �յ���Ϣ��״̬����ʾ��������Ϣ
					.setWhen(System.currentTimeMillis()) // ����֪ͨʱ��
					.setSmallIcon(R.drawable.ic_rating_on1) // ����Сͼ��
					.setLargeIcon(LargeBitmap) // ���ô�ͼ��
					.setDefaults(Notification.DEFAULT_LIGHTS) // ����Ĭ�ϵ���ɫ��������
																// Ĭ����|
																// Notification.DEFAULT_VIBRATE
					.setVibrate(new long[] { 0, 100, 100, 100, 300, 100 }) // ��ʱ0ms��100ms������ʱ100ms��100ms����ʱ300ms��100ms
					// .setSound(Uri.parse("android.resource://" +
					// getPackageName() + "/" + R.raw.biaobiao)) //�����Զ������ʾ��
					.setAutoCancel(true) // ���õ����ȡ��Notification
					.setContentIntent(pit); // ����PendingIntent
			notify1 = mBuilder.build();
			mNManager.notify(NOTIFYID_1, notify1);
			break;

		case R.id.btn_close_normal:
			// ���˿��Ը���ID��ȡ��Notification��,�����Ե���cancelAll();�رո�Ӧ�ò���������֪ͨ
			mNManager.cancel(NOTIFYID_1); // ȡ��Notification
			break;

		}
	}

	/**
	 * ������
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
		System.out.println("����������");
		View view = LayoutInflater.from(TestLayoutActivity.this).inflate(
				R.layout.item_popip, null, false);
		Button btn_xixi = (Button) view.findViewById(R.id.btn_xixi);
		Button btn_hehe = (Button) view.findViewById(R.id.btn_hehe);
		// 1.����һ��PopupWindow�����������Ǽ��ص�View�����
		final PopupWindow popWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);

		popWindow.setAnimationStyle(R.animator.anim_pop); // ���ü��ض���

		// ��ЩΪ�˵����PopupWindow����PopupWindow����ʧ�ģ����û�������
		// ����Ļ�����ᷢ�֣������PopupWindow��ʾ�����ˣ������㰴���ٴκ��˼�
		// PopupWindow������رգ������˲������򣬼�������������Խ���������
		popWindow.setTouchable(true);
		popWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// �����������true�Ļ���touch�¼���������
				// ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
			}
		});
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); 
		// ҪΪpopWindow����һ����������Ч

		// ����popupWindow��ʾ��λ�ã����������ǲ���View��x���ƫ������y���ƫ����
		popWindow.showAsDropDown(v, 50, -50);

		// ����popupWindow��İ�ť���¼�
		btn_xixi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(TestLayoutActivity.this, "���������~",
						Toast.LENGTH_SHORT).show();
			}
		});
		btn_hehe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(TestLayoutActivity.this, "������ɾ��~",
						Toast.LENGTH_SHORT).show();
				popWindow.dismiss();
			}
		});
	}
	
	/**
	 *  AsyncTask
	 */
	private void initAsyncTask() {
		txttitle = (TextView)findViewById(R.id.txttitle);  
        pgbar = (ProgressBar)findViewById(R.id.pgbar);  
        btnupdate = (Button)findViewById(R.id.btnupdate);  
        btnupdate.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                MyAsyncTask myTask = new MyAsyncTask(txttitle,pgbar);  
                myTask.execute(1000);  
            }  
        });  
	}
}
