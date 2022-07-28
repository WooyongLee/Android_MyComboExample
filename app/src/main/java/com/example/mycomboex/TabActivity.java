package com.example.mycomboex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mycomboex.databinding.TabLayoutBinding;
import com.example.mycomboex.ui.tab.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabActivity extends AppCompatActivity
{
    private TabLayoutBinding binding;
    @Override
    public void onBackPressed()
    {
        Log.d("TabActivity", "Back Pressed");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("TabActivity", "onCreate() Call");
        super.onCreate(savedInstanceState);

        // Binding 먼저 설정해준 뒤에 UI에 접근하도록 ..
        binding = TabLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 현재 Context에 대한 fragment manager 설정해서 Adapter 객체 생성
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;

        // View Pager에 대한 Adapter 설정
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        
        // tab에 ViewPager Setup함
        tabs.setupWithViewPager(viewPager);

        Intent intentMain = getIntent();
        String strCurrentTime = intentMain.getStringExtra("CURRENT_TIME");
        binding.textView1.setText("Latest Call Time :: " + strCurrentTime);
    }
}
