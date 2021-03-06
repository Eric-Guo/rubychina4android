package org.rubychina.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.rubychina.app.R;
import org.rubychina.app.model.Topic;
import org.rubychina.app.model.TopicReply;
import org.rubychina.app.ui.adapter.TopicFragmentPagerAdapter;
import org.rubychina.app.ui.fragment.MyReplyFragment;
import org.rubychina.app.ui.fragment.Topic.TopicRepliesFragment;
import org.rubychina.app.ui.fragment.Topic.TopicViewFragment;
import org.rubychina.app.utils.ApiUtils;
import org.rubychina.app.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 14-1-31.
 */
public class TopicActivity extends FragmentActivity {
    private Topic topic = new Topic();

    private Menu mMenu;

    private ViewPager pager;

    MyReplyFragment myReplyFragment;

    private TopicFragmentPagerAdapter topicFragmentPagerAdapter;

    private List<Fragment> mFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_topic2);

        pager = (ViewPager)findViewById(R.id.pager);

        topicFragmentPagerAdapter = new TopicFragmentPagerAdapter(getSupportFragmentManager(), mFragments);

        pager.setAdapter(topicFragmentPagerAdapter);

        topic.id = getIntent().getStringExtra("topic_id");
        if (topic.id != null) {
            Bundle bundle = new Bundle();
            bundle.putString("topic_id", topic.id);
            myReplyFragment = new MyReplyFragment();
            myReplyFragment.setArguments(bundle);
            fetchData();
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }
            @Override
            public void onPageSelected(int i) {
                System.out.println(i);
                mMenu.clear();
                switch (i){
                    case 2:
                        getMenuInflater().inflate(R.menu.topic_reply_menu, mMenu);
                        break;
                    default:
                        getMenuInflater().inflate(R.menu.topic_menu, mMenu);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topic_menu, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reply:
                pager.setCurrentItem(2);
                return true;
            case R.id.action_send:
                myReplyFragment.sendReply();
                return true;
            case R.id.action_refresh:
                mFragments.clear();
                fetchData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_to_right,R.anim.anim_right_to_left);
    }

    private void fetchData(){
        ApiUtils.get(ApiUtils.TOPIC_VIEW + topic.id + ".json", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
                Gson gson = new Gson();
                topic = gson.fromJson(response, Topic.class);
                mFragments.add(new TopicViewFragment(topic));
                try {
                    String replies = JsonUtils.getString(new JSONObject(response), "replies");
                    Type listType = new TypeToken<List<TopicReply>>(){}.getType();
                    List<TopicReply> topicReplies = gson.fromJson(replies, listType);
                    mFragments.add(new TopicRepliesFragment(topicReplies));
                    System.out.println(topicReplies.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mFragments.add(myReplyFragment);

                pager.setAdapter(new TopicFragmentPagerAdapter(getSupportFragmentManager(), mFragments));

            }
        });
    }

}
