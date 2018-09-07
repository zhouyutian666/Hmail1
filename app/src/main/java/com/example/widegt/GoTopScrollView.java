package com.example.widegt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

public class GoTopScrollView extends ScrollView implements View.OnClickListener {
	// չʾ�ö���ͼƬ��ť
	private ImageView goTopBtn;
	// ��Ļ�߶� //Ĭ�ϸ߶�Ϊ500
	private int screenHeight = 500;

	public GoTopScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 1.0
	// ���û��������ٳ���
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	// 2.0
	// ���ù����ö���ť�Լ����������¼�
	public void setImgeViewOnClickGoToFirst(ImageView goTopBtn) {
		this.goTopBtn = goTopBtn;
		this.goTopBtn.setOnClickListener(this);
	}

	// 3.0
	// ��д�����ı䷵�صĻص�
	// l oldl �ֱ����ˮƽλ��
	// t oldt ����ǰ���ϽǾ���Scrollview����ľ���
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		/**
		 * �������볬��500px,�����ö���ť,������Ϊ�Զ������� ������������û�������ʹ���û��� ����û�û������ʹ��Ĭ�ϵ�
		 */
		// 3.1
		// �� ��ǰ�����ϽǾ��붥����� ����ĳ��ֵ��ʱ��������ö���ť���� ���С��ĳ��ֵ������
		if (screenHeight != 0) {
			if (t > screenHeight) {
				goTopBtn.setVisibility(VISIBLE);
			} else {
				goTopBtn.setVisibility(GONE);
			}
		}
	}

	// 4.0
	// �ö���ť�ĵ���¼�����
	@Override
	public void onClick(View v) {
		// ������ScrollView�Ķ���
		this.smoothScrollTo(0, 0);
		goTopBtn.setVisibility(GONE);
	}
}
