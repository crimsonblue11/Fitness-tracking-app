/**
 * Custom activity subclass to manage fragments with a stack.
 */

package com.example.mdp_cw2.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.LinkedList;

public class FragmentManagerActivity extends AppCompatActivity {
    /**
     * Stack of fragments.
     */
    private LinkedList<AppFragment> fragmentStack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentStack = new LinkedList<>();
    }

    /**
     * Method called to show the first fragment added to the stack.
     * @param fragment Fragment to show
     * @param containerId ID of the container to inflate the fragment into
     */
    public void showFirstFragment(AppFragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // add fragment and show
        // no need to worry about previous fragments, since this is the first
        fragmentStack.add(fragment);
        transaction.add(containerId, fragment).show(fragment).commit();
        fragment.onFragmentShown();
    }

    /**
     * Method to switch to another fragment when a fragment is already being shown.
     * @param fragment New fragment to show
     * @param containerId ID of the container to inflate the fragment into
     */
    public void switchFragment(AppFragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AppFragment tempFrag = fragmentStack.getLast();

        if(fragment.getClass() == tempFrag.getClass()) {
            // if fragments are the same, do nothing
            return;
        }

        // add new fragment and hide previous
        fragmentStack.addLast(fragment);
        transaction.hide(tempFrag);
        tempFrag.onFragmentClosed();
        transaction.add(containerId, fragment).show(fragment).commit();
        fragment.onFragmentShown();
    }

    /**
     * Method to go back to the previous fragment on the stack.
     */
    public void previousFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AppFragment tempFrag = fragmentStack.getLast();

        // hide and remove current fragment
        fragmentStack.removeLast();
        transaction.remove(tempFrag);
        tempFrag.onFragmentClosed();

        if(fragmentStack.isEmpty()) {
            // finish activity if all fragments are gone
            finish();
            return;
        }

        AppFragment prevFrag = fragmentStack.getLast();
        transaction.show(prevFrag).commit();
        tempFrag.onFragmentShown();
    }
}
