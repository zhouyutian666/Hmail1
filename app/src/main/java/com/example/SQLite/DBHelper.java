package com.example.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private String createSQL = "create table student("
			+ "id integer primary key  autoincrement not null ,"
			+ "name varchar(20) not null," + "age integer not null ,"
			+ "gender varchar(2) not null)";

	/**
	 * 
	 * @param context
	 * @param name
	 *            ���ݿ�����
	 * @param factory
	 *            ���ݿ���в�ѯ��ʱ��᷵��һ��cursor�����cursor�����������factory�в����ġ�
	 *            ��������󣬿����Զ���factory���������ص�cursor�ͻ�����Լ�������
	 * @param version
	 *            ���ݿ�汾��
	 */
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createSQL);
	}

	/**
	 * �÷����������ݿ���Ҫ������ʱ�����
	 * 
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE student ADD COLUMN other TEXT");
	}

}