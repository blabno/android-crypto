package com.labnoratory.sample_app;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private static final List<AbstractTab> TABS = Collections.unmodifiableList(Arrays.asList(
            new AuthenticateFragment(),
            new EncryptSymmetricallyFragment(),
            new EncryptAsymmetricallyFragment(),
            new EncryptSymmetricallyWithPasswordFragment(),
            new SignFragment()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, getTabConfigurationStrategy()).attach();
    }

    @NonNull
    private static TabLayoutMediator.TabConfigurationStrategy getTabConfigurationStrategy() {
        return (tab, position) -> Optional.ofNullable(position >= TABS.size() ? null : TABS.get(position))
                .map(AbstractTab::getTitle)
                .ifPresent(tab::setText);
    }

    private static class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter(FragmentActivity fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return Optional.ofNullable(position >= TABS.size() ? null : TABS.get(position))
                    .map(abstractTab -> (Fragment) abstractTab)
                    .orElseGet(Fragment::new);
        }

        @Override
        public int getItemCount() {
            return TABS.size();
        }
    }
}