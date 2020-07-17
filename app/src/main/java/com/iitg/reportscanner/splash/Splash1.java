package com.iitg.reportscanner.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.iitg.reportscanner.Login;
import com.iitg.reportscanner.R;

import pl.droidsonroids.gif.GifImageView;

public class Splash1 extends AppCompatActivity {
    private ViewPagerIndicator mViewPagerIndicator;
    private ViewPager mViewPager;
    GifImageView data,data2,data3;
    TextView txt,nxt1;
    ImageView nxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);
        data = findViewById(R.id.data);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);
        txt = findViewById(R.id.txt);
        nxt = findViewById(R.id.next);
        nxt1 = findViewById(R.id.skip);
        mViewPagerIndicator = findViewById(R.id.view_pager_indicator);
        mViewPager = findViewById(R.id.view_pager);

        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPagerIndicator.setupWithViewPager(mViewPager);
        mViewPagerIndicator.addOnPageChangeListener(mOnPageChangeListener);

        nxt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
    private
    class MyPagerAdapter
            extends PagerAdapter
    {
        @Override
        public
        int getCount() {
            return 3;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public
        Object instantiateItem(final ViewGroup container, final int position) {
            final TextView textView = new TextView(Splash1.this);
            final Handler handler = new Handler();
            //textView.setText("Page " + position);
            container.addView(textView);
            return textView;
        }

        @Override
        public
        boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public
        void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public
        CharSequence getPageTitle(final int position) {
            return String.valueOf(position);
        }
    }

    @NonNull
    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public
        void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

        }

        @Override
        public
        void onPageSelected(final int position) {
            switch (position){
                case 0:
                    txt.setText("Be FIT and Healthy");

                    data.setVisibility(View.VISIBLE);
                    data2.setVisibility(View.INVISIBLE);
                    data3.setVisibility(View.INVISIBLE);
                    nxt.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    txt.setText("Get Proper Guidance");

                    data.setVisibility(View.INVISIBLE);
                    data2.setVisibility(View.VISIBLE);
                    data3.setVisibility(View.INVISIBLE);
                    nxt.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    txt.setText("Best Analysis");

                    data.setVisibility(View.INVISIBLE);
                    data2.setVisibility(View.INVISIBLE);
                    data3.setVisibility(View.VISIBLE);
                    nxt.setVisibility(View.VISIBLE);
                    nxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(getApplicationContext(), Login.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    });
                    break;

            }
      //      Toast.makeText(Splash1.this, "Page selected " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public
        void onPageScrollStateChanged(final int state) {

        }
    };
}
