package com.ligaoqi.simplechatautoreplyapp.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class CompoundBottomTarget extends SimpleTarget<Drawable> {

    private final TextView view;

    public CompoundBottomTarget(TextView view, int width, int height) {
        super(width, height);
        this.view = view;
    }

    @Override
    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, resource);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, placeholder);
    }

}
