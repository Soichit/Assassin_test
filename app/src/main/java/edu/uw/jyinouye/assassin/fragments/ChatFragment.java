package edu.uw.jyinouye.assassin.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import edu.uw.jyinouye.assassin.Assassin;
import edu.uw.jyinouye.assassin.R;
import edu.uw.jyinouye.assassin.util.Chat;
import edu.uw.jyinouye.assassin.util.FirebaseListAdapter;

/**
 * A Fragment to handle chat
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private String mUserName;
    private Firebase mGroup;
    private Firebase mGroupChat;
    private EditText mInputText;
    private ListView mListView;
    private ChatListAdapter mChatListAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Assassin assassin = (Assassin) getActivity().getApplication();
        mUserName = assassin.getPlayer().getUserName();
        Log.v(TAG, "mUsername: " + mUserName);
        mGroup = assassin.getGroup();
        mGroupChat = mGroup.child("chat");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        mListView = (ListView) v.findViewById(R.id.chat_list);

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        mInputText = (EditText) v.findViewById(R.id.messageInput);
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        v.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return v;
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUserName);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            Log.v("ChatFragment", "push chat");
            mGroupChat.push().setValue(chat);
            mInputText.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mGroupChat.limitToLast(50), getActivity(), R.layout.content_chat, mUserName);
        mListView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ChatListAdapter extends FirebaseListAdapter<Chat> {

        private String mUserId;

        public ChatListAdapter(Query ref, Activity activity, int layout, String mUserId) {
            super(ref, Chat.class, layout, activity);
            this.mUserId = mUserId;
        }

        @Override
        protected void populateView(View v, Chat chat) {
            // Map a Chat object to an entry in our listview
            String author = chat.getAuthor();
            TextView authorText = (TextView) v.findViewById(R.id.author);
            authorText.setText(author + ": ");
            // If the message was sent by this user, color it differently
            if (author != null && author.equals(mUserId)) {
                authorText.setTextColor(Color.RED);
            } else {
                authorText.setTextColor(Color.BLUE);
            }
            ((TextView) v.findViewById(R.id.message)).setText(chat.getMessage());
        }
    }
}
