package com.example.hmail_Beta01;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_loading);
		addLoading(R.id.image_loading);// ���ؽ��潥��
		gotoLogin(LoadingActivity.this);
		
	}
	
	public void addLoading(int loadingId) {// ���ؽ�����ʾ
		ImageView loading = (ImageView) findViewById(loadingId);
		loadingFade(loading);
	}

	public void loadingFade(ImageView view) {// ���붯��
		Animation fade_in = AnimationUtils.loadAnimation(LoadingActivity.this,
				R.animator.fade_in);
		view.setAnimation(fade_in);
	}
	
	public void gotoLogin(LoadingActivity LA){
		Intent loginActivity = new Intent(LA,LoginActivity.class);
		delayTime(loginActivity);
	}
	
	public void delayTime(final Intent i){
		Timer timer = new Timer();
		  TimerTask task = new TimerTask() {
		   @Override
		   public void run() {
		    startActivity(i); //ִ��
		    finish();
		   }
		  };
		timer.schedule(task, 1000 * 2);
	}
}
