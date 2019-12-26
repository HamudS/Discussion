package com.example.discussion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DiscussionActivity extends AppCompatActivity {

    Button btnSendMsg;
    EditText etMsg;
    ListView lvDiscussion;
    ArrayList<String> listConversation = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    String UserName, SelectedTopic, user_msg_key,msg;

    private DatabaseReference dbr;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        btnSendMsg = (Button) findViewById(R.id.btnSndMsg);
        etMsg = (EditText) findViewById(R.id.etMsg);
        lvDiscussion = (ListView) findViewById(R.id.lvConversation);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listConversation);
        lvDiscussion.setAdapter(arrayAdapter);

        UserName = getIntent().getExtras().get("user_name").toString();
        SelectedTopic = getIntent().getExtras().get("selected_topic").toString();

        setTitle(SelectedTopic);
        Spannable text = new SpannableString(getTitle());
        text.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(text);


        dbr = FirebaseDatabase.getInstance().getReference().child(SelectedTopic);


        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msg=etMsg.getText().toString();
                if (msg.isEmpty()){
                    Toast.makeText(DiscussionActivity.this, "You did not enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    Map<String, Object> map = new HashMap<String, Object>();
                    user_msg_key = dbr.push().getKey();
                    dbr.updateChildren(map);


                    DatabaseReference dbr2 = dbr.child(user_msg_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("msg", msg);
                    etMsg.setText("");
                    map2.put("user", UserName);
                    dbr2.updateChildren(map2);
                }

            }
        });

        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConvesation(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConvesation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateConvesation(DataSnapshot dataSnapshot){
        String msg, user, conversation;
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            msg = (String) ((DataSnapshot)i.next()).getValue();
            user = (String) ((DataSnapshot)i.next()).getValue();

            conversation = user + ": " + msg;
            arrayAdapter.insert(conversation, 0);
            arrayAdapter.notifyDataSetChanged();
        }

    }
}
