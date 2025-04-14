package com.example.doancuoiky;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kết nối TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.TabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Thiết lập Adapter cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            int[] tabIcons = {R.drawable.account, R.drawable.home, R.drawable.history};
            tab.setIcon(tabIcons[position]);
        }).attach();

        // Đặt tab mặc định là "Home"
        viewPager.setCurrentItem(1, false);  // Chuyển sang tab thứ 2 (Home)
    }
}
