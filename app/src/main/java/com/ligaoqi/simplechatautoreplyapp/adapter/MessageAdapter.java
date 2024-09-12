package com.ligaoqi.simplechatautoreplyapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.view.ViewCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.data.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {
    private Context mMessageContext;
    private ArrayList<Message> mMessageList = new ArrayList<>();

    private final ColorStateList incomingTint;
    private final ColorStateList outgoingTint;
    private final int vertical;
    private final int horizontalShort;
    private final int horizontalLong;
    private final int photoSize;

    public interface OnPhotoClickedListener {
        void onPhotoClicked(Uri photo);
    }

    public MessageAdapter(@Nullable List<Message> data, Context context, @NonNull OnPhotoClickedListener onPhotoClicked) {
        super(R.layout.item_message, data);
        setHasStableIds(true);
        this.mMessageContext = context;

        this.incomingTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.incoming));
        this.outgoingTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.outgoing));

        vertical = mMessageContext.getResources().getDimensionPixelSize(R.dimen.message_padding_vertical);
        horizontalShort = mMessageContext.getResources().getDimensionPixelSize(R.dimen.message_padding_horizontal_short);
        horizontalLong = mMessageContext.getResources().getDimensionPixelSize(R.dimen.message_padding_horizontal_long);
        photoSize = mMessageContext.getResources().getDimensionPixelSize(R.dimen.photo_size);

        setOnItemClickListener((adapter, view, position) -> {
            Uri photo = (Uri) view.getTag(R.id.tag_photo);
            if (photo != null) {
                onPhotoClicked.onPhotoClicked(photo);
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Message message) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) helper.getView(R.id.message).getLayoutParams();
        TextView messageTextView = helper.getView(R.id.message);

        if (message.isIncoming()) {
            messageTextView.setBackgroundResource(R.drawable.message_incoming);
            ViewCompat.setBackgroundTintList(helper.getView(R.id.message), getIncomingTint());
            messageTextView.setPadding(horizontalLong, vertical, horizontalShort, vertical);
            lp.gravity = Gravity.START;
            messageTextView.setLayoutParams(lp);
        }else{
            messageTextView.setBackgroundResource(R.drawable.message_outgoing);
            ViewCompat.setBackgroundTintList(helper.getView(R.id.message), getOutgoingTint());
            messageTextView.setPadding(horizontalShort, vertical, horizontalLong, vertical);
            lp.gravity = Gravity.END;
            messageTextView.setLayoutParams(lp);
        }
        if(message.getPhotoUri() != null){
            messageTextView.setTag(R.id.tag_photo, message.getPhotoUri());
            Glide.with(mMessageContext).load(message.getPhotoUri()).into(new CompoundBottomTarget(messageTextView, photoSize, photoSize));
        }else{
            messageTextView.setTag(R.id.tag_photo,null);
            messageTextView.setCompoundDrawables(null, null, null, null);
        }
        helper.setText(R.id.message, message.getText());
    }

    public ColorStateList getIncomingTint() {
        return incomingTint;
    }

    public ColorStateList getOutgoingTint() {
        return outgoingTint;
    }

}
