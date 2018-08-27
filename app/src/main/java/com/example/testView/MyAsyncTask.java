package com.example.testView;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyAsyncTask extends AsyncTask<Integer,Integer,String>  
{  
    private TextView txt;  
    private ProgressBar pgbar;  
  
    public MyAsyncTask(TextView txt,ProgressBar pgbar)  
    {  
        super();  
        this.txt = txt;  
        this.pgbar = pgbar;  
    }  
  
  
    //�÷�����������UI�߳���,��Ҫ�����첽����,ͨ������publishProgress()����  
    //����onProgressUpdate��UI���в���  
    @Override  
    protected String doInBackground(Integer... params) {  
        DelayOperator dop = new DelayOperator();  
        int i = 0;  
        for (i = 10;i <= 100;i+=10)  
        {  
            dop.delay();  
            publishProgress(i);  
        }  
        return  i + params[0].intValue() + "";  
    }  
  
    //�÷���������UI�߳���,�ɶ�UI�ؼ���������  
    @Override  
    protected void onPreExecute() {  
        txt.setText("��ʼִ���첽�߳�~");  
    }  
  
  
    //��doBackground������,ÿ�ε���publishProgress�������ᴥ���÷���  
    //������UI�߳���,�ɶ�UI�ؼ����в���  
  
  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        int value = values[0];  
        pgbar.setProgress(value);  
    }  
}

class DelayOperator {  
    //��ʱ����,����ģ������  
    public void delay()  
    {  
        try {  
            Thread.sleep(1000);  
        }catch (InterruptedException e){  
            e.printStackTrace();;  
        }  
    }  
}