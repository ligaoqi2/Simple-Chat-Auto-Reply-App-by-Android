package com.ligaoqi.simplechatautoreplyapp.ui.main;

import static com.ligaoqi.simplechatautoreplyapp.util.NavigationUtils.getNavigationController;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ligaoqi.simplechatautoreplyapp.NavigationController;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.adapter.ContactAdapter;
import com.ligaoqi.simplechatautoreplyapp.data.Contact;
import com.ligaoqi.simplechatautoreplyapp.data.DefaultChatRepository;

import java.io.IOException;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    public Context mainFragmentContext;

    public RecyclerView mainRecyclerView;

    public ContactAdapter contactAdapter;

    private ArrayList<Contact> mContactList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainFragmentContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition exitTransition = TransitionInflater.from(mainFragmentContext).inflateTransition(R.transition.slide_top);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainRecyclerView = view.findViewById(R.id.contacts);

        mContactList.clear();

        NavigationController navigationController = getNavigationController(this);
        navigationController.updateAppBar(false, false, (name, icon) -> {
        });

        // Data Generation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                mContactList.addAll(DefaultChatRepository.getInstance(getContext()).getContacts());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        contactAdapter = new ContactAdapter(mContactList, getContext(), id -> {
            navigationController.openChat(id, null);
        });

        // Adapter
        mainRecyclerView.setAdapter(contactAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mainRecyclerView.setLayoutManager(linearLayoutManager);
        mainRecyclerView.setHasFixedSize(true);
    }
}
