package com.ligaoqi.simplechatautoreplyapp.ui.photo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ligaoqi.simplechatautoreplyapp.NavigationController;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.util.NavigationUtils;

public class PhotoFragment extends Fragment {
    private static final String ARG_PHOTO = "photo";

    private ImageView photoView;

    public static Fragment newInstance(Uri photo){
        PhotoFragment fragment = new PhotoFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition fadeTransition = null;
        fadeTransition = new Fade();
        setEnterTransition(fadeTransition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoView = (ImageView)view.findViewById(R.id.photo);

        Uri photo = getArguments() != null ? getArguments().<Uri>getParcelable(ARG_PHOTO) : null;

        if (photo == null) {
            if (isAdded()) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                }
            }
            return;
        }

        NavigationUtils.getNavigationController(this).updateAppBar(true, true, new NavigationController.AppBarUpdater() {
            @Override
            public void update(TextView name, ImageView icon) {
                // do nothing
            }
        });

        Glide.with(this).load(photo).into(photoView);
    }
}
