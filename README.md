# SlideLayout
a Sliding layout with a scrolling ViewPager and LinearLayout contain some dots marking page's position. 
You can control it by custom attributes.

# demo
![image](https://github.com/zerohuan/SlideLayout/raw/master/slide_demo.jpg)

# Setup
dependencies {
    compile project(':slideLayout')
}

# How to Use
You can set auto play or not, dot color and interval of sliding. You must contain a ViewPager and a LinearLayout in SlideLayout and inject their id.
-----------------------------------  
<com.luckymore.yjh.slide.view.SlideLayout
        xmlns:attrs="http://schemas.android.com/apk/res-auto"
        android:id="@+id/news_slide_bar"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        attrs:dotsId="@+id/test_1"
        attrs:viewpagerId="@+id/test_2"
        attrs:autoPlay="true"
        attrs:dotRadius="3dp"
        attrs:slide_interval="4000"
        attrs:offDotColor="#7fff"
        attrs:onDotColor="#7000"
        attrs:strokeColor="#77626262"
        >

        <android.support.v4.view.ViewPager android:id="@+id/test_2"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <LinearLayout android:id="@+id/test_1"
            android:layout_width="wrap_content"
            android:layout_height="20dp"
            android:orientation="horizontal"
            android:layout_gravity="bottom|center_horizontal"
            android:gravity="center"
            android:paddingLeft="8dp"
            android:paddingRight="8dp"
            />

    </com.luckymore.yjh.slide.view.SlideLayout>

# Usage in code
After define a custom PagerAdapter, you can do like that

### slideLayout.setViewPagerAdapter(pagerAdapter);
