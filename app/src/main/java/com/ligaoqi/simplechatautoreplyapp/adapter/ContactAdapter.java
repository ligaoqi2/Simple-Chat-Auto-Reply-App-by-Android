package com.ligaoqi.simplechatautoreplyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.data.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseQuickAdapter<Contact, BaseViewHolder> {

    private Context mContactContext;
    private ArrayList<Contact> mContactList = new ArrayList<>();
    private final OnChatClickedListener onChatClick;

    public interface OnChatClickedListener {
        void onChatClicked(long id);
    }

    public ContactAdapter(@Nullable List<Contact> data, Context context, @NonNull OnChatClickedListener onChatClicked) {
        super(R.layout.item_chat, data);
        setHasStableIds(true);
        onChatClick = onChatClicked;
        mContactContext = context;
        setOnItemClickListener((adapter, view, position) -> {
            Contact contact = getItem(position);
            if (contact != null) {
                onChatClicked.onChatClicked(contact.getId());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Contact> list){
        mContactList.clear();
        mContactList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Contact contact) {
        helper.setText(R.id.name, contact.getName());
        Glide.with(mContactContext).load(contact.getIconUri()).into((ImageView) helper.getView(R.id.icon));
    }
}
