package com.example.hmail_Beta01;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
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
	 * ����Ƿ���ڼ�¼��������
	 */
	public void isRememberPass() {

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		UserMailInput = (EditText) findViewById(R.id.user);
		UserPassWordInput = (EditText) findViewById(R.id.password);
		rememberPass = (CheckBox) findViewById(R.id.checkBox1);
		isRemember = pref.getBoolean("remember_password", false);

		if (isRemember) { // ���˺ź����붼���õ��ı�����
			String account = pref.getString("account", "");
			String password = pref.getString("password", "");
			UserMailInput.setText(account);
			UserPassWordInput.setText(password);
			rememberPass.setChecked(true);
		}
		Drawable[] drawable_user = UserMailInput.getCompoundDrawables();
		// �����±�0~3,������:��������
		drawable_user[0].setBounds(0, 0, 60, 60);
		UserMailInput.setCompoundDrawables(drawable_user[0], null, null, null);

		Drawable[] drawable_password = UserPassWordInput.getCompoundDrawables();
		// �����±�0~3,������:��������
		drawable_password[0].setBounds(0, 0, 60, 60);
		UserPassWordInput.setCompoundDrawables(drawable_password[0], null, null, null);

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.forgetPassword: {// ��������
		// Toast.makeText(LoginActivity.this, "��Ǹ���ù�����δ���ţ�",
		// Toast.LENGTH_SHORT).show();
			Intent i = new Intent();
			i.setClass(this, TestLayoutActivity.class);
			startActivity(i);
			break;
		}
		case R.id.loginButton:
			// Toast.makeText(LoginActivity.this, "�����ڴ���", Toast.LENGTH_SHORT)
			// .show();
			startLoading();
			break;
		}
	}

	/**
	 * �ж��ʺź������Ƿ�Ϊ�գ���ʼloading
	 */
	String user;
	String password;

	public void startLoading() {
		UserMailInput = (EditText) findViewById(R.id.user);
		user = UserMailInput.getText().toString();
		UserPassWordInput = (EditText) findViewById(R.id.password);
		password = UserPassWordInput.getText().toString();

		if (user.length() != 0 && password.length() != 0) {// �û��������벻��Ϊ��

			// showLoading();
			// startLogin(user, password);
			showLoading();
			mtask.execute(user, password);
			savePass(user, password);

		} else {
			Toast.makeText(LoginActivity.this, "�û��������벻��Ϊ�գ�",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ��ʼ��¼���½���̨���߳�
	 */
	public void startLogin() {

		client.setMailUser(user);// ���ʺź����봫����̨
		client.setMailPass(password);
		Thread clientThread = new Thread(client);
		clientThread.start();

	}

	/**
	 * �洢�ʺ�����
	 * 
	 * @param user
	 * @param password
	 */
	public void savePass(String user, String password) {
		editor = pref.edit();
		if (rememberPass.isChecked()) { // ��鸴ѡ���Ƿ�ѡ��
			editor.putBoolean("remember_password", true);
			editor.putString("account", user);
			editor.putString("password", password);
		} else {
			editor.clear();
		}
		editor.commit();
	}

	/**
	 * Handler������
	 */
	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case toListActivity: // ��������Խ���UI����
				System.out.println("ִ��handler��ת���ʼ��б�");
				((MyApp) self.getApplication()).mailClient = self.client;// ���ֻ��ڴ治����ʱ��application����ı����п��ܻᱻ�ͷŵ����˴��ǽ�activity��Ķ������¸�ֵ��application�����У�
				Intent goToList = new Intent(self, ListActivity.class);
				self.startActivity(goToList);
				self.pd.dismiss();
				System.out.println("�ɹ���ת��");
				break;
			case toExit:
				// System.exit(0);
				self.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case toContentActivity:
				((MyApp) self.getApplication()).mailClient = self.client;// ���ֻ��ڴ治����ʱ��application����ı����п��ܻᱻ�ͷŵ����˴��ǽ�activity��Ķ������¸�ֵ��application�����У�
				ListActivity.toContentActivity();
				break;
			default:
				break;
			}
		}
	};

	ProgressDialog pd;

	/**
	 * ��ʾLoading
	 */
	public void showLoading() {
		pd = new ProgressDialog(LoginActivity.this);
		pd.setTitle("��¼");
		pd.setMessage("Loading...");
		pd.setCancelable(true);
		pd.show();
	}

	// /**
	// * ע��Loading
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
				if (getResp() == 0) {// ���������� +ok �� getResp() = 0
					if (this.login(mailUser, mailPass) == 0) {
						startLogin();
					} else {
						System.out.println("�ʺ����벻��ȷ�������˳���");
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
		 * �����������������
		 * 
		 * @return
		 */
		private int connectToPopServ() {
			int ret = 0;
			try {
				this.sock = new Socket("pop3.163.com", 110);// �����������������
				this.sock.setSoTimeout(10 * 000);
				// this.sock.connect(new InetSocketAddress("pop.163.com", 110),
				// 10*000);
				in = new DataInputStream(this.sock.getInputStream());
				reader = new BufferedReader(new InputStreamReader(in));
				out = new DataOutputStream(this.sock.getOutputStream());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("�����������ʧ�ܣ�����ret = -1");
				ret = -1;
			}
			return ret;
		}

		/**
		 * �жϷ������Ƿ񷵻�+ok
		 * 
		 * @return
		 */
		public int getResp() {// �жϷ������Ƿ񷵻� +ok
			int ret = 0;
			String line = null;
			try {
				line = reader.readLine();
				System.out.println("���������أ�" + line);
				if (line.startsWith("+OK")) { // ���������� +ok �� ret = 0
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
		 * �ж��û��ʺ������Ƿ���ȷ�� ��ȷ��� ����¼�����ʺųɹ���
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
							System.out.println("��¼�����ʺųɹ���");

						}
					}
				}
			}

			return ret;
		}

		/**
		 * ��������ļ���pass�������Ϊ******�� ����ֱ�����
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
