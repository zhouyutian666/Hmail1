package com.example.hmail_Beta01;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {
	public MailClient mailClient = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("MyApp", "MyApp OnCreate ...");
		//mailClient = new MailClient();
	}
	
	
}
