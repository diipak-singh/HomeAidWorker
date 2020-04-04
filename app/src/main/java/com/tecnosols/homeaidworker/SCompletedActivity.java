package com.tecnosols.homeaidworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SCompletedActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView counter_text_view;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseReference dref3;
    final List<ServiceDetail> serviceDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_completed);

        recyclerView = findViewById(R.id.recyclerView_completed);
        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        counter_text_view = findViewById(R.id.counter_text);
        counter_text_view.setText("Tasks Completed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getCompletedServics();
    }

    private void getCompletedServics() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dref3 = FirebaseDatabase.getInstance().getReference().child("admin2workers/" + user.getUid());

        dref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    serviceDetails.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ServiceDetail sd = ds.getValue(ServiceDetail.class);
                        serviceDetails.add(new ServiceDetail(sd.userImg, sd.userName, sd.userPhone, sd.userCity, sd.userAddress, sd.itemImg, sd.itemName, sd.itemCatg, sd.itemPrice, sd.serviceDay, sd.serviceTime, sd.currentDay, sd.currentTime, sd.otherDetail, sd.serviceId, sd.userId, sd.paymentStatus, sd.isCompleted, sd.rating));
                    }

                    Collections.reverse(serviceDetails);
                    adapter = new ServiceAdapterC((ArrayList<ServiceDetail>) serviceDetails, SCompletedActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(SCompletedActivity.this, "No Services booked yet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
