package com.worldpeace.issuesmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView myListView = (ListView) findViewById(R.id.issuesListView);

        final ArrayList<Map<String,Object>> issuesList = new ArrayList<Map<String,Object>>();

        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                issuesList,
                R.layout.activity_main,
                new String[]{"severityImage", "issueDescription", "resolvedImage"},
                new int[]{R.id.severityImage, R.id.issueDescription, R.id.resolvedImage}
        );

        myListView.setAdapter(simpleAdapter);

        // let's read the issues from Google Firebase!!
        DatabaseReference dbRef;
        dbRef = FirebaseDatabase.getInstance().getReference("/issues");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                issuesList.clear();

                for (DataSnapshot issue : snapshot.getChildren()) {
                    Map<String, Object> issueRow = new HashMap<String, Object>();

                    int severityImageId;

                    switch (issue.child("severity").getValue(String.class)) {
                        case "minor" :
                            severityImageId = R.drawable.yellow_led;
                            break;
                        case "moderate" :
                            severityImageId = R.drawable.orange_led;
                            break;
                        case "major" :
                            severityImageId = R.drawable.red_led;
                            break;
                        default:
                            severityImageId = R.drawable.black_question_mark_led;
                    }
                    issueRow.put("severityImage", severityImageId);
                    issueRow.put("issueDescription", issue.child("description").getValue(String.class));
                    issueRow.put("resolvedImage",
                            issue.child("resolved").getValue(String.class).equals("yes") ?
                                    R.drawable.green_check_mark : R.drawable.red_hourglass);
                    issuesList.add(issueRow);
                }

                simpleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Failed to read from Firebase.", error.toException());
            }
        });

    }
}
