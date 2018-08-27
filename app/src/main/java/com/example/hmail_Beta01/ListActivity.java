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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
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
			builder.setIcon(R.drawable.ic_launcher);// 设置图标
			builder.setTitle("退出");// 设置对话框的标题
			builder.setMessage("你确定要退出吗？");// 设置对话框的内容
			builder.setPositiveButton("确定", new OnClickListener() { // 这个是设置确定按钮

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mailClient.setOrder("4");
							showLoading();

						}
					});
			builder.setNegativeButton("取消", new OnClickListener() { // 取消按钮

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
			builder.show(); // 必须show一下才能看到对话框，跟Toast一样的道理
			break;
		default:
			break;
		}
	}
/**
 * 新建下载文件夹
 */
	public void createDownloadFile() {
		File file = new File("/mnt/sdcard/MailBeta_zyt/DownLoad");
		// 判断文件夹是否存在，如果不存在就创建，否则不创建
		if (!file.exists()) {
			// 通过file的mkdirs()方法创建<span
			file.mkdirs();
		}
	}
/**
 * 得到子线程中每一封邮件数据
 * @return
 */
	public ArrayList<Map<String, Object>> getItem() {

		System.out.println("开始邮件列表赋值！");
		for (int i = 1; i <= mailClient.mailnumber; i++) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mailnumber", mailClient.mailNumberArray[i]);
			map.put("mailtitle", mailClient.mailTitleArray[i]);
			map.put("maildate", mailClient.mailDateArray[i]);
			map.put("mailfrom", mailClient.mailFromArray[i]);
			map.put("mailstate", mailClient.mailStateArray[i]);// 字符已读和未读
			map.put("mailexistfujian", mailClient.mailFuJianNumArray[i]);// 没有为null
			item.add(map);
			System.out.println("邮件寄件人：=============================="
					+ mailClient.mailFromArray[i]);
			System.out.println("附件数目：============="
					+ mailClient.mailFuJianNumArray[i]);
		}

		System.out.println("邮件列表赋值完毕！");
		return item;
	}
/**
 * list列表，加载自定义adapter
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
						// Toast.makeText(ListActivity.this, "尽请期待！",
						// Toast.LENGTH_SHORT).show();
						showContentLoading();
						positionID = position;
						String stringposition = Integer.toString(position + 1);// position
																				// 第一个位置是0，和数组差不多
						mailClient.setNumber(stringposition);// 向子线程中传递需要读取邮件的序号
						mailClient.setOrder("2");
					}
				});
	}

	/**
	 * 通过handler来打开下一个页面
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
	 * 显示Loading
	 */
	public void showLoading() {
		ProgressDialog pd = new ProgressDialog(ListActivity.this);
		pd.setTitle("退出");
		pd.setMessage("正在退出...");
		pd.setCancelable(false);
		pd.show();
	}

	/**
	 * 显示阅读邮件正文Loading
	 */
	static ProgressDialog pd_c;

	public void showContentLoading() {
		pd_c = new ProgressDialog(ListActivity.this);
		pd_c.setTitle("邮件详情");
		pd_c.setMessage("正在加载...");
		pd_c.setCancelable(false);
		pd_c.show();
	}
	/**
	 * 注销上一封下一封按钮的loading
	 */
	public static void dismissNOLLoading(){
		if(ContentActivity.showNOLLoading == 1){
			ContentActivity.pd_nextOrlast.dismiss();
		}
	}
}
