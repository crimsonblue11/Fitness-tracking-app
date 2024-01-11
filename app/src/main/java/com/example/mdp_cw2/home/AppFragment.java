package com.example.mdp_cw2.home;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

public abstract class AppFragment extends Fragment {
    public void onFragmentShown() {
    }

    public void onFragmentClosed() {
        closeKeyboard();
    }

    public void closeKeyboard() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow((getActivity().getCurrentFocus() == null) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
