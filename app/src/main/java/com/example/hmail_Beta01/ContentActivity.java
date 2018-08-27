package com.example.hmail_Beta01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentActivity extends Activity {
	private MailClient mailclient = null;
	TextView title;
	String fujianNumber = null;
	static int toast = 0;
	int mailId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_content);
		this.mailclient = ((MyApp)this.getApplication()).mailClient;
		receive();
	}

	public void receive() {
		Intent intent = getIntent();// 得到上一个文件传入的ID号
		Bundle i = intent.getExtras();
	    mailId = i.getInt("ID");// 将得到的ID号传递给变量num
		mailId = mailId + 1;// position从0开始，所以需要 +1
		String mailId_string = Integer.toString(mailId);

		title = (TextView) findViewById(R.id.contentActivity_title);
		TextView date = (TextView) findViewById(R.id.contentActivity_date);
		TextView content = (TextView) findViewById(R.id.contentActivity_content);
		TextView downloadFileName = (TextView) findViewById(R.id.contentActivity_downloadFileName);

		TextView contentActivity_xq = (TextView) findViewById(R.id.contentActivity_from_xq);// 详情按钮界面
		TextView from_xq = (TextView) findViewById(R.id.contentActivity_from_xq);
		ImageView downloadSmallImg_xq = (ImageView) findViewById(R.id.contentActiity_downloadSmallImg_xq);
		TextView downloadNumber_xq = (TextView) findViewById(R.id.contentActivity_downloadNumber_xq);

		TextView contentActivity_yc = (TextView) findViewById(R.id.contentActivity_yc);
		TextView fajianren_yc = (TextView) findViewById(R.id.contentActivity_fajianren_yc);// 隐藏按钮界面
		TextView from_yc = (TextView) findViewById(R.id.contentActivity_from_yc);
		TextView shoujianren_yc = (TextView) findViewById(R.id.contentActivity_shoujianren_yc);
		TextView to_yc = (TextView) findViewById(R.id.contentActivity_to_yc);
		title.setText(mailclient.mailTitleArray[mailId]);
		date.setText(mailclient.mailDetailedDateArray[mailId]);
		content.setText(mailclient.mailContentArray[mailId]);
		downloadFileName.setText(mailclient.mailFuJianNameArray[mailId][1]);// 只显示第一个附件名称

		from_xq.setText(mailclient.mailFromArray[mailId]);
		downloadNumber_xq.setText(mailclient.mailFuJianNumArray[mailId]);
		fujianNumber = mailclient.mailFuJianNumArray[mailId];

		from_yc.setText(mailclient.mailFromArray[mailId]);
		to_yc.setText(mailclient.mailToArray[mailId]);// 
														
	}

	public void onClick(View view) {
		TextView contentActivity_xq = (TextView) findViewById(R.id.contentActivity_xq);// 详情按钮界面
		TextView from_xq = (TextView) findViewById(R.id.contentActivity_from_xq);
		ImageView downloadSmallImg_xq = (ImageView) findViewById(R.id.contentActiity_downloadSmallImg_xq);
		TextView downloadNumber_xq = (TextView) findViewById(R.id.contentActivity_downloadNumber_xq);

		TextView contentActivity_yc = (TextView) findViewById(R.id.contentActivity_yc);
		TextView fajianren_yc = (TextView) findViewById(R.id.contentActivity_fajianren_yc);// 隐藏按钮界面
		TextView from_yc = (TextView) findViewById(R.id.contentActivity_from_yc);
		TextView shoujianren_yc = (TextView) findViewById(R.id.contentActivity_shoujianren_yc);
		TextView to_yc = (TextView) findViewById(R.id.contentActivity_to_yc);
		
		
		switch (view.getId()) {
		case R.id.returnList:
			finish();
			break;
		case R.id.next_mail:
//			Toast.makeText(this, "尽请期待", Toast.LENGTH_SHORT).show();
//			System.out.println("mailId:"+ mailId +"\n" +"num2:" + mailclient.num2);
			if(mailId < mailclient.num2){
				showNextOrLastLoading();
				mailId++;
				ListActivity.positionID++;
				nextOrLastMail( mailId);
			}else{
				Toast.makeText(this, "这是最新一封邮件！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.last_mail:
//			Toast.makeText(this, "尽请期待", Toast.LENGTH_SHORT).show();
			if(mailId > 1){
				showNextOrLastLoading();
				mailId--;
				ListActivity.positionID--;
				nextOrLastMail( mailId);
			}else{
				Toast.makeText(this, "这是最新一封邮件！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.contentActivity_xq:// 点击详情
			// Toast.makeText(this, "尽请期待", Toast.LENGTH_SHORT).show();
			contentActivity_xq.setVisibility(View.GONE);
			from_xq.setVisibility(View.GONE);
			downloadSmallImg_xq.setVisibility(View.GONE);
			downloadNumber_xq.setVisibility(View.GONE);

			contentActivity_yc.setVisibility(View.VISIBLE);
			fajianren_yc.setVisibility(View.VISIBLE);// 隐藏按钮界面
			from_yc.setVisibility(View.VISIBLE);
			shoujianren_yc.setVisibility(View.VISIBLE);
			to_yc.setVisibility(View.VISIBLE);
			break;
		case R.id.contentActivity_yc:// 点击隐藏
			// Toast.makeText(this, "尽请期待", Toast.LENGTH_SHORT).show();
			contentActivity_xq.setVisibility(View.VISIBLE);
			from_xq.setVisibility(View.VISIBLE);
			downloadSmallImg_xq.setVisibility(View.VISIBLE);
			downloadNumber_xq.setVisibility(View.VISIBLE);

			contentActivity_yc.setVisibility(View.GONE);
			fajianren_yc.setVisibility(View.GONE);// 隐藏按钮界面
			from_yc.setVisibility(View.GONE);
			shoujianren_yc.setVisibility(View.GONE);
			to_yc.setVisibility(View.GONE);
			break;
		case R.id.contentActivity_downloadBut:// 点击下载
			// Toast.makeText(this, "尽请期待", Toast.LENGTH_SHORT).show();
			if (fujianNumber != null) {
				mailclient.setOrder("3");
				do {
					if (toast == 1) {
						Toast.makeText(
								this,
								"附件下载已完成！\n文件目录：\n/sdcard/MailBeta_zyt/DownLoad",
								Toast.LENGTH_LONG).show();
					}
				} while (toast == 0);
			} else {
				Toast.makeText(this, "没有附件！", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	/**
	 * 向子线程传递上一封或下一封邮件序号
	 * @param id
	 */
	public void nextOrLastMail( int id){
		String mailid_string = Integer.toString(id);
		mailclient.setNumber(mailid_string);
		mailclient.setOrder("2");
	}
	
	
	static ProgressDialog pd_nextOrlast;
	static int showNOLLoading = 0;
	/**
	 * 上一封和下一封loading
	 */
	public void showNextOrLastLoading() {
		pd_nextOrlast = new ProgressDialog(ContentActivity.this);
		pd_nextOrlast.setTitle("邮件详情");
		pd_nextOrlast.setMessage("正在加载...");
		pd_nextOrlast.setCancelable(true);
		pd_nextOrlast.show();
		showNOLLoading = 1;
	}
}
