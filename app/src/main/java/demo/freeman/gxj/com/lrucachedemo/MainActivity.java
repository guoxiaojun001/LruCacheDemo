package demo.freeman.gxj.com.lrucachedemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import demo.freeman.gxj.com.lrucachedemo.adapter.NewsAdapter;
import demo.freeman.gxj.com.lrucachedemo.module.NewsBean;


/**
 * 通过异步加载，避免阻塞UI线程
 * 通过LruCache，将已下载图片放到内存中（一级缓存）
 * 通过判断ListView的滑动状态，决定何时加载图片（让复杂的ListView也能流畅的加载）
 * 不仅仅是ListView，任何控件都可以使用异步加载
 */

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private static String URL =
            "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(URL);
    }

    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {
        /**
         * 实现网络的异步访问
         */
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJosnData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsbean) {
            super.onPostExecute(newsbean);
            NewsAdapter adapter = new NewsAdapter(newsbean, MainActivity.this, mListView);
            mListView.setAdapter(adapter);
        }

        /**
         * 通过InputStream解析网页返回的数据
         *
         * @param is
         * @return
         */
        private String readString(InputStream is) {
            InputStreamReader isr;
            String result = "";
            try {
                String line = "";
                //字节流转换为字符流
                isr = new InputStreamReader(is, "utf-8");
                //以Buffer的形式读取
                BufferedReader br = new BufferedReader(isr);
                while ((line = br.readLine()) != null) {
                    result += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         * 将url对应的JSON格式数据转化为我们所封装的NewsBean对象
         *
         * @param url
         * @return
         */
        private List<NewsBean> getJosnData(String url) {
            List<NewsBean> newsBeanList = new ArrayList<NewsBean>();
            try {
                String jsonString = readString(new URL(url).
                        openConnection().getInputStream());
				Log.i("msg", jsonString);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        NewsBean newsBean = new NewsBean();
                        newsBean.setNewsIconUrl(jsonObject.getString("picSmall"));
                        newsBean.setNewsTitle(jsonObject.getString("name"));
                        newsBean.setNewsContent(jsonObject.getString("description"));
                        newsBeanList.add(newsBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newsBeanList;
        }

    }
}
