package com.labnoratory.sample_app;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

enum Tabs {
    Authenticate,
    EncryptAsymmetrically,
    EncryptSymmetrically,
    EncryptWithPassword,
}

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, getTabConfigurationStrategy()).attach();
    }

    @NonNull
    private static TabLayoutMediator.TabConfigurationStrategy getTabConfigurationStrategy() {
        return (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setTag(Tabs.Authenticate).setText("Authenticate");
                    break;
                case 1:
                    tab.setTag(Tabs.EncryptSymmetrically).setText("Encrypt\nsymmetrically");
                    break;
                case 2:
                    tab.setTag(Tabs.EncryptAsymmetrically).setText("Encrypt\nasymmetrically");
                    break;
                case 3:
                    tab.setTag(Tabs.EncryptWithPassword).setText("Encrypt\nwith password");
                    break;
            }
        };
    }

    private static class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter(FragmentActivity fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new AuthenticateFragment();
                case 1:
                    return new EncryptSymmetricallyFragment();
                case 2:
                    return new EncryptAsymmetricallyFragment();
                case 3:
                    return new EncryptSymmetricallyWithPasswordFragment();
            }
            return new Fragment();
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}