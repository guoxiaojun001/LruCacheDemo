package demo.freeman.gxj.com.lrucachedemo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import demo.freeman.gxj.com.lrucachedemo.ImageLoader;
import demo.freeman.gxj.com.lrucachedemo.R;
import demo.freeman.gxj.com.lrucachedemo.module.NewsBean;

public class NewsAdapter extends BaseAdapter implements OnScrollListener{

	private List<NewsBean> mList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private int mStart,mEnd;
	public static String [] URLS;
	private boolean mFirstIn;

	public NewsAdapter(List<NewsBean> data,Context context,ListView listview) {
		mList = data;
		mInflater = LayoutInflater.from(context);
		mImageLoader = new ImageLoader(listview);
		URLS = new String[data.size()];
		for( int i = 0; i < data.size(); i++)	{
			//将图片的url传递到数组中
			URLS[i] = data.get(i).getNewsIconUrl();
		}
		mFirstIn = true;
		listview.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_layout, null);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ivIcon.setImageResource(R.mipmap.ic_launcher);

		String url = mList.get(position).newsIconUrl;
		holder.ivIcon.setTag(url);
		//避免每次都创建 mImageLoader
		mImageLoader.showImageByAsyncTask(holder.ivIcon, url);

		holder.tvTitle.setText(mList.get(position).getNewsTitle());
		holder.tvContent.setText(mList.get(position).getNewsContent());
		return convertView;
	}
	class ViewHolder{
		TextView tvTitle,tvContent;
		ImageView ivIcon;
	}

	//将加载图片事件 转移到滑动事件中
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//滑动的时候调用
		mStart = firstVisibleItem;//第一个可见元素
		mEnd = firstVisibleItem + visibleItemCount;
		//第一次启动 加载的时候调用
		if (mFirstIn && visibleItemCount > 0)	{
			mImageLoader.loadImages(mStart, mEnd);
			mFirstIn = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//list在滑动状态切换的时候调用3
		if (scrollState == SCROLL_STATE_IDLE)	{
			// 停止状态， 加载可见项
			mImageLoader.loadImages(mStart, mEnd);//在此处加载图片
		} else {
			// 停止任务
			mImageLoader.cancelAllTask();
		}
	}

}
