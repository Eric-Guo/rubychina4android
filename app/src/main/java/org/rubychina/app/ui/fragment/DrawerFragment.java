package org.rubychina.app.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.rubychina.app.R;
import org.rubychina.app.helper.MyBitmapDisplayer;
import org.rubychina.app.ui.LoginActivity;
import org.rubychina.app.ui.MainActivity;
import org.rubychina.app.ui.adapter.DrawerAdapter;
import org.rubychina.app.utils.UserUtils;

/**
 * Created by mac on 14-1-27.
 */
public class DrawerFragment extends Fragment {
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.avatar).displayer(new MyBitmapDisplayer(100, 2000))
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();


    private TextView login, email;
    private ListView mListView;
    private ImageView avatarView;
    private RelativeLayout bgRelativeLayout;

    private DrawerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_drawer, null);

        login = (TextView)contentView.findViewById(R.id.tv_login);
        email = (TextView)contentView.findViewById(R.id.tv_email);
        bgRelativeLayout = (RelativeLayout)contentView.findViewById(R.id.rl_dr_bg);

        login.setText(UserUtils.getUserLogin());
        email.setText(UserUtils.getUserEmail());
        bgRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserUtils.logined()) {
                    getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), MainActivity.ACTION_FOR_LOGIN);
                    getActivity().overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
                }
            }
        });

        avatarView = (ImageView)contentView.findViewById(R.id.iv_avatar);

        imageLoader.displayImage(UserUtils.getUserAvatar(),avatarView, options);

        mListView = (ListView) contentView.findViewById(R.id.listView);
        mAdapter = new DrawerAdapter(mListView);
        mListView.setAdapter(mAdapter);
        mListView.setItemChecked(0, true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
            }
        });
        return contentView;

    }
}
