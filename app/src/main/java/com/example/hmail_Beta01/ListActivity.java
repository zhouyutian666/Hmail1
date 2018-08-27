package com.example.hmail_Beta01;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.MyAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListActivity extends Activity {
	ArrayList<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
	private MailClient mailClient = null;
	ListView mailListView;
	static int positionID;
	static ListActivity listself = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_list);
		createDownloadFile();
		getMailList();
		listself = this;
		this.mailClient = ((MyApp)this.getApplication()).mailClient;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.returnLogin:
			finish();
			break;
		case R.id.exit:
			AlertDialog.Builder builder = new Builder(this);
			builder.setIcon(R.drawable.ic_launcher);// ����ͼ��
			builder.setTitle("�˳�");// ���öԻ���ı���
			builder.setMessage("��ȷ��Ҫ�˳���");// ���öԻ��������
			builder.setPositiveButton("ȷ��", new OnClickListener() { // ���������ȷ����ť

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mailClient.setOrder("4");
							showLoading();

						}
					});
			builder.setNegativeButton("ȡ��", new OnClickListener() { // ȡ����ť

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
			builder.show(); // ����showһ�²��ܿ����Ի��򣬸�Toastһ���ĵ���
			break;
		default:
			break;
		}
	}
/**
 * �½������ļ���
 */
	public void createDownloadFile() {
		File file = new File("/mnt/sdcard/MailBeta_zyt/DownLoad");
		// �ж��ļ����Ƿ���ڣ���������ھʹ��������򲻴���
		if (!file.exists()) {
			// ͨ��file��mkdirs()��������<span
			file.mkdirs();
		}
	}
/**
 * �õ����߳���ÿһ���ʼ�����
 * @return
 */
	public ArrayList<Map<String, Object>> getItem() {

		System.out.println("��ʼ�ʼ��б�ֵ��");
		for (int i = 1; i <= mailClient.mailnumber; i++) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mailnumber", mailClient.mailNumberArray[i]);
			map.put("mailtitle", mailClient.mailTitleArray[i]);
			map.put("maildate", mailClient.mailDateArray[i]);
			map.put("mailfrom", mailClient.mailFromArray[i]);
			map.put("mailstate", mailClient.mailStateArray[i]);// �ַ��Ѷ���δ��
			map.put("mailexistfujian", mailClient.mailFuJianNumArray[i]);// û��Ϊnull
			item.add(map);
			System.out.println("�ʼ��ļ��ˣ�=============================="
					+ mailClient.mailFromArray[i]);
			System.out.println("������Ŀ��============="
					+ mailClient.mailFuJianNumArray[i]);
		}

		System.out.println("�ʼ��б�ֵ��ϣ�");
		return item;
	}
/**
 * list�б������Զ���adapter
 */
	public void getMailList() {
		getItem();
		mailListView = (ListView) findViewById(R.id.list_view);
		MyAdapter myAdapter = new MyAdapter(item, this);
		mailListView.setAdapter(myAdapter);

		mailListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View argl,
							int position, long id) {
						// TODO Auto-generated method stub
						// Toast.makeText(ListActivity.this, "�����ڴ���",
						// Toast.LENGTH_SHORT).show();
						showContentLoading();
						positionID = position;
						String stringposition = Integer.toString(position + 1);// position
																				// ��һ��λ����0����������
						mailClient.setNumber(stringposition);// �����߳��д�����Ҫ��ȡ�ʼ������
						mailClient.setOrder("2");
					}
				});
	}

	/**
	 * ͨ��handler������һ��ҳ��
	 */
	public static void toContentActivity() {
		Intent intent = new Intent();
		intent.putExtra("ID", positionID);
		intent.setClass(listself, ContentActivity.class);
		pd_c.dismiss();
		dismissNOLLoading();
		listself.startActivity(intent);
	}

	/**
	 * ��ʾLoading
	 */
	public void showLoading() {
		ProgressDialog pd = new ProgressDialog(ListActivity.this);
		pd.setTitle("�˳�");
		pd.setMessage("�����˳�...");
		pd.setCancelable(false);
		pd.show();
	}

	/**
	 * ��ʾ�Ķ��ʼ�����Loading
	 */
	static ProgressDialog pd_c;

	public void showContentLoading() {
		pd_c = new ProgressDialog(ListActivity.this);
		pd_c.setTitle("�ʼ�����");
		pd_c.setMessage("���ڼ���...");
		pd_c.setCancelable(false);
		pd_c.show();
	}
	/**
	 * ע����һ����һ�ⰴť��loading
	 */
	public static void dismissNOLLoading(){
		if(ContentActivity.showNOLLoading == 1){
			ContentActivity.pd_nextOrlast.dismiss();
		}
	}
}
