package com.ligaoqi.simplechatautoreplyapp.util;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.ligaoqi.simplechatautoreplyapp.NavigationController;

public class NavigationUtils {
    public static NavigationController getNavigationController(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity instanceof NavigationController) {
            return (NavigationController) activity;
        } else {
            throw new ClassCastException("Activity must implement NavigationController");
        }
    }
}
