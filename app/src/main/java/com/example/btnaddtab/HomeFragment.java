package com.example.btnaddtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements TabLayoutMediator.TabConfigurationStrategy {

    View view;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titles.add("最新");
        titles.add("熱門");
        addToViewPager2Adapter();
        new TabLayoutMediator(tabLayout, viewPager2, this).attach();
    }

    public void initViews(){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);
    }

    private void addToViewPager2Adapter() {
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);

        fragmentArrayList.add(new NewestFragment());
        fragmentArrayList.add(new HitFragment());

        viewPager2Adapter.setData(fragmentArrayList);
        viewPager2.setAdapter(viewPager2Adapter);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(titles.get(position));
    }
}