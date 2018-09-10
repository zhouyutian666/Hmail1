package com.example.hmail_Beta01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class LoginActivity extends Activity {
	private static MailClient client = null;
	public static final int toListActivity = 1;
	public static final int toExit = -1;
	public static final int toContentActivity = 2;
	ListActivity listactivity = new ListActivity();
	static LoginActivity self = null;
	public MyTask mtask = new MyTask();

	private ImageView iv_password;
	private CheckBox rememberPass;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	EditText UserMailInput;
	EditText UserPassWordInput;
	boolean isRemember;
	private boolean flag = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_login);

		MyApp app = (MyApp) this.getApplication();
		app.mailClient = new MailClient();
		this.client = app.mailClient;
		self = this;
		isRememberPass();

		iv_password = (ImageView) findViewById(R.id.iv_password);
		iv_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag == true) {
					UserPassWordInput
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
					flag = false;
				} else {
					UserPassWordInput
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
					flag = true;
				}
			}
		});

	}

	/**
	 * 检测是否存在记录密码数据
	 */
	public void isRememberPass() {

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		UserMailInput = (EditText) findViewById(R.id.user);
		UserPassWordInput = (EditText) findViewById(R.id.password);
		rememberPass = (CheckBox) findViewById(R.id.checkBox1);
		isRemember = pref.getBoolean("remember_password", false);

		if (isRemember) { // 将账号和密码都设置到文本框中
			String account = pref.getString("account", "");
			String password = pref.getString("password", "");
			UserMailInput.setText(account);
			UserPassWordInput.setText(password);
			rememberPass.setChecked(true);
		}
//		Drawable[] drawable_user = UserMailInput.getCompoundDrawables();
//		// 数组下表0~3,依次是:左上右下
//		drawable_user[0].setBounds(0, 0, 60, 60);
//		UserMailInput.setCompoundDrawables(drawable_user[0], null, null, null);
//
//		Drawable[] drawable_password = UserPassWordInput.getCompoundDrawables();
//		// 数组下表0~3,依次是:左上右下
//		drawable_password[0].setBounds(0, 0, 60, 60);
//		UserPassWordInput.setCompoundDrawables(drawable_password[0], null, null, null);

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.forgetPassword: {// 忘记密码
		// Toast.makeText(LoginActivity.this, "抱歉，该功能暂未开放！",
		// Toast.LENGTH_SHORT).show();
			Intent i = new Intent();
			i.setClass(this, TestLayoutActivity.class);
			startActivity(i);
			break;
		}
		case R.id.loginButton:
			// Toast.makeText(LoginActivity.this, "敬请期待！", Toast.LENGTH_SHORT)
			// .show();
			startLoading();
			break;
		}
	}

	/**
	 * 判断帐号和密码是否为空，开始loading
	 */
	String user;
	String password;

	public void startLoading() {
		UserMailInput = (EditText) findViewById(R.id.user);
		user = UserMailInput.getText().toString();
		UserPassWordInput = (EditText) findViewById(R.id.password);
		password = UserPassWordInput.getText().toString();

		if (user.length() != 0 && password.length() != 0) {// 用户名和密码不能为空

			// showLoading();
			// startLogin(user, password);
			showLoading();
			mtask.execute(user, password);
			savePass(user, password);

		} else {
			Toast.makeText(LoginActivity.this, "用户名和密码不能为空！",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 开始登录，新建后台子线程
	 */
	public void startLogin() {

		client.setMailUser(user);// 将帐号和密码传给后台
		client.setMailPass(password);
		Thread clientThread = new Thread(client);
		clientThread.start();

	}

	/**
	 * 存储帐号密码
	 * 
	 * @param user
	 * @param password
	 */
	public void savePass(String user, String password) {
		editor = pref.edit();
		if (rememberPass.isChecked()) { // 检查复选框是否被选中
			editor.putBoolean("remember_password", true);
			editor.putString("account", user);
			editor.putString("password", password);
		} else {
			editor.clear();
		}
		editor.commit();
	}

	/**
	 * Handler处理器
	 */
	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case toListActivity: // 在这里可以进行UI操作
				System.out.println("执行handler跳转到邮件列表！");
				((MyApp) self.getApplication()).mailClient = self.client;// 当手机内存不够用时，application里面的变量有可能会被释放吊，此处是将activity里的对象重新赋值到application对象中；
				Intent goToList = new Intent(self, ListActivity.class);
				self.startActivity(goToList);
				self.pd.dismiss();
				System.out.println("成功跳转！");
				break;
			case toExit:
				// System.exit(0);
				self.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case toContentActivity:
				((MyApp) self.getApplication()).mailClient = self.client;// 当手机内存不够用时，application里面的变量有可能会被释放吊，此处是将activity里的对象重新赋值到application对象中；
				ListActivity.toContentActivity();
				break;
			default:
				break;
			}
		}
	};

	ProgressDialog pd;

	/**
	 * 显示Loading
	 */
	public void showLoading() {
		pd = new ProgressDialog(LoginActivity.this);
		pd.setTitle("登录");
		pd.setMessage("Loading...");
		pd.setCancelable(true);
		pd.show();
	}

	// /**
	// * 注销Loading
	// */
	// public void dismissLoading() {
	// pd.dismiss();
	// }

	public class MyTask extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			Log.d("Login", "AsynTask OnCreate ...");

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String mailUser = params[0];
			String mailPass = params[1];
			if (connectToPopServ() == 0) {// ret = 0
				if (getResp() == 0) {// 服务器返回 +ok 则 getResp() = 0
					if (this.login(mailUser, mailPass) == 0) {
						startLogin();
					} else {
						System.out.println("帐号密码不正确，程序退出！");
						// System.exit(1);
					}
				}
			} else {
				System.out.println("Can't connect to pop server ");
			}

			return "";
		}

		protected void onProgressUpdate(Integer... Progress) {
		}

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

		}

		Socket sock = null;
		private DataInputStream in = null;
		private BufferedReader reader = null;
		private DataOutputStream out = null;

		/**
		 * 创建与服务器的连接
		 * 
		 * @return
		 */
		private int connectToPopServ() {
			int ret = 0;
			try {
				this.sock = new Socket("pop3.163.com", 110);// 创建与服务器的连接
				this.sock.setSoTimeout(10 * 000);
				// this.sock.connect(new InetSocketAddress("pop.163.com", 110),
				// 10*000);
				in = new DataInputStream(this.sock.getInputStream());
				reader = new BufferedReader(new InputStreamReader(in));
				out = new DataOutputStream(this.sock.getOutputStream());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("与服务器连接失败，返回ret = -1");
				ret = -1;
			}
			return ret;
		}

		/**
		 * 判断服务器是否返回+ok
		 * 
		 * @return
		 */
		public int getResp() {// 判断服务器是否返回 +ok
			int ret = 0;
			String line = null;
			try {
				line = reader.readLine();
				System.out.println("服务器返回：" + line);
				if (line.startsWith("+OK")) { // 服务器返回 +ok 则 ret = 0
					ret = 0;
				} else {
					ret = 1;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = -1;
			}

			return ret;
		}

		/**
		 * 判断用户帐号密码是否正确； 正确输出 “登录邮箱帐号成功”
		 * 
		 * @return
		 */
		public int login(String mailUser, String mailPass) {
			int ret = 0;
			ret = this.sendCmd("user " + mailUser);
			if (ret == 0) {
				ret = this.getResp();
				if (ret == 0) {
					ret = this.sendCmd("pass " + mailPass);
					if (ret == 0) {
						ret = this.getResp();
						if (ret == 0) {
							System.out.println("登录邮箱帐号成功！");

						}
					}
				}
			}

			return ret;
		}

		/**
		 * 输出配置文件含pass则密码改为******； 其他直接输出
		 * 
		 * @param cmd
		 * @return
		 */
		public int sendCmd(String cmd) {
			int ret = 0;
			try {

				System.out.println(cmd);

				this.out.write((cmd + "\r\n").getBytes("UTF-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = -1;
			}

			return ret;
		}

	}
}
