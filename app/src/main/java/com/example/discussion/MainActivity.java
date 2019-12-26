package com.example.discussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ListView lvDiscussion;
    ArrayList<String> listOfDiscussion = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    FloatingActionButton fab;

    String UserName,Topic;


    private DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvDiscussion = (ListView) findViewById(R.id.lvDiscussion);
        arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,listOfDiscussion);
        lvDiscussion.setAdapter(arrayAdapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        getUserName();
        //dbr.child("j").removeValue();

        setTitle("TOPICS");
        Spannable text = new SpannableString(getTitle());
        text.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(text);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        lvDiscussion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),DiscussionActivity.class);
                i.putExtra("selected_topic", ((TextView)view).getText().toString());
                i.putExtra("user_name",UserName);
                startActivity(i);
            }
        });

    }


    private void getUserName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText userName = new EditText(this);
        userName.setHint("USERNAME");

        builder.setView(userName);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserName = userName.getText().toString();
                if (UserName.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Username",Toast.LENGTH_SHORT).show();
                    getUserName();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUserName();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        //builder.show();

    }


    public void fabclic(View view) {
        getTopic();
    }

    public void getTopic(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText topic = new EditText(this);
        topic.setHint("TOPIC");

        builder.setView(topic);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Topic = topic.getText().toString().toUpperCase();
                if (Topic.isEmpty()){
                    return;
                }else {
                dbr.child(Topic).push().setValue("");
            }}
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

    }
}
