package com.example.mdp_cw2.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.LinkedList;

public class FragmentManagerActivity extends AppCompatActivity {
    private LinkedList<AppFragment> fragmentStack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentStack = new LinkedList<>();
    }

    public void showFirstFragment(AppFragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragmentStack.add(fragment);
        transaction.add(containerId, fragment).show(fragment).commit();
        fragment.onFragmentShown();
    }

    public void switchFragment(AppFragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AppFragment tempFrag = fragmentStack.getLast();

        if(fragment.getClass() == tempFrag.getClass()) {
            // if fragments are the same, do nothing
            return;
        }

        fragmentStack.addLast(fragment);
        transaction.hide(tempFrag);
        tempFrag.onFragmentClosed();
        transaction.add(containerId, fragment).show(fragment).commit();
        fragment.onFragmentShown();
    }

    public void previousFragment(boolean refreshPrevious) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AppFragment tempFrag = fragmentStack.getLast();

        fragmentStack.removeLast();
        transaction.remove(tempFrag);
        tempFrag.onFragmentClosed();

        if(fragmentStack.isEmpty()) {
            // finish activity if all fragments are gone
            finishAffinity();
            return;
        }

        AppFragment prevFrag = fragmentStack.getLast();
        transaction.show(prevFrag).commit();
        tempFrag.onFragmentShown();
    }

    public AppFragment getCurrentFragment() {
        return fragmentStack.getLast();
    }


}
