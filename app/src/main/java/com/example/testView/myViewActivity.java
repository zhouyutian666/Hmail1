package com.example.testView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.hmail_Beta01.R;

public class myViewActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// »•µÙ±ÍÃ‚¿∏
		setContentView(R.layout.my_view_test_layout);


	}
}
