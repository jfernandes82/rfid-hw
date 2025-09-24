package com.cf.tech.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class TagOperationAdapter extends FragmentStateAdapter {

    private final List<Fragment> mFragments;

    public TagOperationAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> pFragments) {
        super(fragmentActivity);
        mFragments = pFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
