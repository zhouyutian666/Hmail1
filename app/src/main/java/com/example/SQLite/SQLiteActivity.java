package com.example.SQLite;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hmail_Beta01.R;

public class SQLiteActivity extends Activity implements OnClickListener {

	private Button addBtn;
	private Button deleteBtn;
	private Button modifyBtn;
	private Button showBtn;
	private EditText nameEdit;
	private TextView tv_show;

	private DBHelper dbhelper;
	private SQLiteDatabase sqldb;

	private static final String TABLE_NAME = "student";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sqlite_activity);
		bindID();
		dbhelper = new DBHelper(this, "demodb", null, 1);
	}

	/**
	 * ��ID�ͼ����¼�
	 */
	private void bindID() {
		addBtn = (Button) findViewById(R.id.addBtn);
		deleteBtn = (Button) findViewById(R.id.deleteBtn);
		modifyBtn = (Button) findViewById(R.id.modifyBtn);
		showBtn = (Button) findViewById(R.id.showBtn);
		nameEdit = (EditText) findViewById(R.id.nameEdit);
		tv_show = (TextView) findViewById(R.id.tv_show);

		addBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		modifyBtn.setOnClickListener(this);
		showBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBtn:
			insert();
			break;
		case R.id.deleteBtn:
			delete();
			break;
		case R.id.modifyBtn:
			modify();
			break;
		case R.id.showBtn:
			show();
			break;
		default:
			break;
		}
	}

	private void insert() {
		String name = nameEdit.getText().toString();
		// ���SQLiteDatabase���󣬶�дģʽ
		sqldb = dbhelper.getWritableDatabase();
		// ContentValues����HashMap��������ContentValuesֻ�ܴ���������ͣ����ܴ����
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("age", 20);
		values.put("gender", "��");
		// ִ�в������
		sqldb.insert(TABLE_NAME, null, values);
	}

	private void delete() {
		String value = nameEdit.getText().toString();
		sqldb = dbhelper.getWritableDatabase();
		// �ڶ���������WHERE��䣨��ִ��������ɾ���������ݣ�
		// ������������WHERE�����ռλ������"?"�ţ������ֵ
		// ɾ�� name=value������
		sqldb.delete(TABLE_NAME, "name=?", new String[] { value });
	}

	private void modify() {
		SQLiteDatabase sqldb = dbhelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("name", "zyt");
		// �ڶ����������޸ĵ��ֶμ��޸ĵ�ֵ(�Ѿ���ŵ�ContentValues��)
		// ������������WHERE���
		// ���ĸ�������WHERE�����ռλ�������ֵ
		// ��������ĸ�����Ϊnull���Ǿͽ�ÿ����¼���ĵ����ο�ɾ������
		sqldb.update(TABLE_NAME, values, null, null);
	}

	private void show() {
		tv_show.setText("");
		String string = "";
		// �õ����ݿ����
		sqldb = dbhelper.getReadableDatabase();
		// �����α�
		Cursor mCursor = sqldb.query(TABLE_NAME, new String[] { "id", "name",
				"age", "gender" }, "age>?", new String[] { "10" }, null, null,
				null);
		// �α��ö�
		mCursor.moveToFirst();
		// ����
		do {
			String name = mCursor.getString(mCursor.getColumnIndex("id"))
					+ "/" + mCursor.getString(mCursor.getColumnIndex("name"))
					+ "/" + mCursor.getString(mCursor.getColumnIndex("age"))
					+ "/" + mCursor.getString(mCursor.getColumnIndex("gender"));
			string = string + "\r\n" + name;
			System.out.println(name);
		} while (mCursor.moveToNext());
		tv_show.setText(string);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�����󣬹ر����ݿ���Դ
		sqldb.close();
	}
}