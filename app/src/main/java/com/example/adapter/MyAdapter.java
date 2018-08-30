package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hmail_Beta01.R;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {

	private List<Map<String, Object>> datas;
	private Context mContext;

	/**
	 * 构造函数
	 * 
	 * @param datas
	 *            需要绑定到view的数据
	 * @param mContext
	 *            传入上下文
	 */
	public MyAdapter(List<Map<String, Object>> datas, Context mContext) {
		this.datas = datas;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listview_item, null);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.stateImageView = (ImageView) convertView
					.findViewById(R.id.mailImage_weidu);
			holder.fuJianImageView = (ImageView) convertView
					.findViewById(R.id.fjImg);
			holder.fromTextView = (TextView) convertView
					.findViewById(R.id.mailFrom);
			holder.dateTextView = (TextView) convertView
					.findViewById(R.id.mailDate);
			holder.titleTextView = (TextView) convertView
					.findViewById(R.id.mailTitle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String,Object> map = datas.get(position);
		holder.fromTextView.setText(map.get("mailfrom")
				.toString());
		holder.dateTextView.setText(map.get("maildate")
				.toString());
		holder.titleTextView.setText(map.get("mailtitle")
				.toString());
		if (map.get("mailexistfujian") == null) {// 有无附件
			holder.fuJianImageView.setVisibility(View.GONE);
		}else {
			holder.fuJianImageView.setVisibility(View.VISIBLE);
		}
		// 从传入的数据中提取数据并绑定到指定的view中
		if (map.get("mailstate") == "未读") {// 邮件阅读状态
			holder.stateImageView.setBackgroundResource(R.drawable.wd);
			holder.fuJianImageView.setBackgroundResource(R.drawable.fj);
			holder.fromTextView.setTextColor(Color.parseColor("#000000"));
			holder.dateTextView.setTextColor(Color.parseColor("#000000"));
			holder.titleTextView.setTextColor(Color.parseColor("#000000"));
			// holder.mImageView.setImageResource((Integer)
			// datas.get(position).get(
			// "img"));
			
			// holder.mButton.setText(datas.get(position).get("button").toString());
		} else {
			holder.stateImageView.setBackgroundResource(R.drawable.yd);
			holder.fuJianImageView.setBackgroundResource(R.drawable.fj_1);
			holder.fuJianImageView.setColorFilter(Color.parseColor("#a7a7a7"));
			holder.fromTextView.setTextColor(Color.parseColor("#a7a7a7"));
			holder.dateTextView.setTextColor(Color.parseColor("#a7a7a7"));
			holder.titleTextView.setTextColor(Color.parseColor("#a7a7a7"));
			// holder.mImageView.setImageResource((Integer)
			// datas.get(position).get(
			// "img"));
			
			// holder.mButton.setText(datas.get(position).get("button").toString());
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView fuJianImageView;
		ImageView stateImageView;
		TextView fromTextView;
		TextView dateTextView;
		TextView titleTextView;
		
	}

}
