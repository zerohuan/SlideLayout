package com.luckymore.yjh.slide.slidelayoutsample.slidelayoutdemo;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luckymore.yjh.slide.view.SlideLayout;


public class MainActivity extends ActionBarActivity {
    SlideLayout slideLayout;

    int imgs[] = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slideLayout = (SlideLayout)findViewById(R.id.news_slide_bar);

        slideLayout.setViewPagerAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imgs.length;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView iv = new ImageView(getBaseContext());
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setImageResource(imgs[position]);
                container.addView(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return iv;
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
