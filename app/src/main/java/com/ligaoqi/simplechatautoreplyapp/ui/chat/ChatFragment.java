package com.ligaoqi.simplechatautoreplyapp.ui.chat;

import static com.ligaoqi.simplechatautoreplyapp.util.NavigationUtils.getNavigationController;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ligaoqi.simplechatautoreplyapp.NavigationController;
import com.ligaoqi.simplechatautoreplyapp.R;
import com.ligaoqi.simplechatautoreplyapp.VoiceCallActivity;
import com.ligaoqi.simplechatautoreplyapp.adapter.MessageAdapter;
import com.ligaoqi.simplechatautoreplyapp.data.Contact;
import com.ligaoqi.simplechatautoreplyapp.data.DefaultChatRepository;
import com.ligaoqi.simplechatautoreplyapp.data.Message;

import java.io.IOException;
import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static final String ARG_ID = "id";
    private static final String ARG_FOREGROUND = "foreground";
    private static final String ARG_PREPOPULATE_TEXT = "prepopulate_text";

    public Context chatFragmentContext;
    private RecyclerView chatRecyclerView;
    private FragmentManager parentFragmentManager;

    private NavigationController navigationController;

    private Boolean foreground = false;

    private MessageAdapter messageAdapter;

    private LinearLayoutManager linearLayoutManager;

    private Long id;

    private Contact chatContact;
    private ChatEditText chatEditText;

    private ImageView photo;
    private ImageButton sendBtn;
    private ImageButton voiceCallBtn;

    private Uri photoUri;

    private String mimetype;

    private ArrayList<Message> mMessageList = new ArrayList<>();

    public static Fragment newInstance(Long id, Boolean foreground, String prepopulateText) {
        ChatFragment fragment = new ChatFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putBoolean(ARG_FOREGROUND, foreground);
        args.putString(ARG_PREPOPULATE_TEXT, prepopulateText);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        chatFragmentContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition enterTransition = TransitionInflater.from(chatFragmentContext).inflateTransition(R.transition.slide_bottom);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 绑定视图
        chatRecyclerView = view.findViewById(R.id.messages);
        chatEditText = view.findViewById(R.id.input);
        photo = view.findViewById(R.id.photo);
        sendBtn = view.findViewById(R.id.send);
        voiceCallBtn = view.findViewById(R.id.voice_call);

        id = getArguments() != null ? getArguments().getLong(ARG_ID) : null;
        if (id == null) {
            parentFragmentManager = getFragmentManager();
            if (parentFragmentManager != null) {
                parentFragmentManager.popBackStack();
            }
            return;
        }

        String prepopulateText = getArguments() != null ? getArguments().getString(ARG_PREPOPULATE_TEXT) : null;
        navigationController = getNavigationController(this);

        if(foreground){
            try {
                DefaultChatRepository.getInstance(getContext()).activateChat(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                DefaultChatRepository.getInstance(getContext()).deactivateChat(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 更新顶端状态栏到具体对话
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                chatContact = DefaultChatRepository.getInstance(getContext()).findContact(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(chatContact == null){
                Toast.makeText(getContext(), "Contact not found", Toast.LENGTH_SHORT).show();
                parentFragmentManager.popBackStack();
            }else{
                navigationController.updateAppBar(true, false, (name, icon) -> {
                    name.setText(chatContact.getName());
                    Glide.with(getContext()).load(chatContact.getIconUri()).into(icon);
                });
            }
        }

        // 更新消息内容
        messageAdapter = new MessageAdapter(mMessageList, getContext(), uri -> {
            navigationController.openPhoto(uri);
        });

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.scrollToPosition(mMessageList.size() - 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                mMessageList.addAll(DefaultChatRepository.getInstance(getContext()).findMessages(id, messageAdapter, mMessageList, linearLayoutManager));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        chatRecyclerView.setAdapter(messageAdapter);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        if(prepopulateText != null){
            chatEditText.setText(prepopulateText);
        }

        chatEditText.setOnImageAddedListener((contentUri, mimeType, label) -> {

            mimetype = mimeType;
            photoUri = contentUri;

            if(chatEditText.getText() == null || chatEditText.getText().toString().trim().isEmpty()){
                chatEditText.setText(label);
            }
        });

        sendBtn.setOnClickListener(v -> {
            try {
                send(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        voiceCallBtn.setOnClickListener(v -> {
            voiceCall();
        });

        chatEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEND){
                try {
                    send(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }else{
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        foreground = getArguments().getBoolean(ARG_FOREGROUND);
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationController.updateAppBar(true, false, (name, icon) -> {
            name.setText(null);
            icon.setImageDrawable(null);
        });
        foreground = false;
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void send(Long id) throws IOException {
        if(chatEditText.getText() != null){
            Editable text = chatEditText.getText();
            if(text.length() > 0){
                if (id != null && id != 0L) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        DefaultChatRepository.getInstance(getContext()).sendMessage(id, text.toString(), photoUri, mimetype);
                    }

                    photoUri = null;
                    mimetype = null;
                }

                text.clear();
            }
        }
    }

    private void voiceCall(){
        if (chatContact == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), VoiceCallActivity.class);
        intent.putExtra(VoiceCallActivity.EXTRA_NAME, chatContact.getName());
        intent.putExtra(VoiceCallActivity.EXTRA_ICON, chatContact.getIconUri());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat, menu);
        MenuItem item = menu.findItem(R.id.action_show_as_bubble);
        if(item != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    item.setVisible(DefaultChatRepository.getInstance(getContext()).canBubble(id));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_as_bubble) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    DefaultChatRepository.getInstance(getContext()).showAsBubble(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(isAdded()){
                parentFragmentManager.popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
