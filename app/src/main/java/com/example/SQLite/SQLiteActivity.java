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
     * 绑定ID和监听事件
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
        // 获得SQLiteDatabase对象，读写模式
        sqldb = dbhelper.getWritableDatabase();
        // ContentValues类似HashMap，区别是ContentValues只能存简单数据类型，不能存对象
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", 20);
        values.put("gender", "男");
        // 执行插入操作
        sqldb.insert(TABLE_NAME, null, values);
    }

    private void delete() {
        String value = nameEdit.getText().toString();
        sqldb = dbhelper.getWritableDatabase();
        // 第二个参数是WHERE语句（即执行条件，删除哪条数据）
        // 第三个参数是WHERE语句中占位符（即"?"号）的填充值
        // 删除 name=value的数据
        sqldb.delete(TABLE_NAME, "name=?", new String[]{value});
    }

    private void modify() {
        SQLiteDatabase sqldb = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", "zyt");
        // 第二个参数是修改的字段及修改的值(已经存放到ContentValues中)
        // 第三个参数是WHERE语句
        // 第四个参数是WHERE语句中占位符的填充值
        // 如果第三四个参数为null，那就将每条记录都改掉，参考删除方法
        sqldb.update(TABLE_NAME, values, null, null);
    }

    private void show() {
        tv_show.setText("");
        String string = "";
        // 得到数据库对象
        sqldb = dbhelper.getReadableDatabase();
        // 创建游标
        Cursor mCursor = sqldb.query(TABLE_NAME, new String[]{"id", "name",
                        "age", "gender"}, "age>?", new String[]{"10"}, null, null,
                null);
        // 游标置顶
        mCursor.moveToFirst();
        // android中数据库处理使用cursor时，游标不是放在为0的下标，而是放在为-1的下标处开始的。
        // 也就是说返回给cursor查询结果时，不能够马上从cursor中提取值。
        if (mCursor.moveToFirst()) {
            // 遍历
            do {
                String name = mCursor.getString(mCursor.getColumnIndex("id"))
                        + "/" + mCursor.getString(mCursor.getColumnIndex("name"))
                        + "/" + mCursor.getString(mCursor.getColumnIndex("age"))
                        + "/" + mCursor.getString(mCursor.getColumnIndex("gender"));
                string = string + "\r\n" + name;
                System.out.println(name);
            } while (mCursor.moveToNext());
        }
        tv_show.setText(string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序后，关闭数据库资源
        sqldb.close();
    }
}