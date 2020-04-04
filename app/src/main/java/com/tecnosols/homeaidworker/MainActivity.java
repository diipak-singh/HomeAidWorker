package com.tecnosols.homeaidworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView counter_text_view;
    ArrayList<workerDetail> workerDetails = new ArrayList<>();
    String wName, wPhone, wType, wAdress, wId, isApproved;
    private Dialog approvalDialog;
    private Button req_approval;
    private TextView status;
    DatabaseReference dref;

    private RecyclerView recyclerView;
    boolean is_in_action_mode = false;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseReference dref3;
    public static ArrayList<ServiceDetail> selection_list = new ArrayList<>();
    final List<ServiceDetail> serviceDetails = new ArrayList<>();
    int counter = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView_allreq);

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        counter_text_view = findViewById(R.id.counter_text);
        counter_text_view.setText("Tasks Assigned");
        status = findViewById(R.id.textView_status);

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loding Service Details");
        pd.setCancelable(false);

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getWorkerData();

        approvalDialog = new Dialog(MainActivity.this);
        approvalDialog.setContentView(R.layout.dialog_layout);
        approvalDialog.setCancelable(false);
        approvalDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        req_approval = approvalDialog.findViewById(R.id.button_approve);

        req_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendApprovalRequest();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    private void getWorkerData() {
        dref = FirebaseDatabase.getInstance().getReference().child("worker_detail");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        workerDetail wd = ds.getValue(workerDetail.class);
                        if (user.getUid().matches(wd.wId)) {
                            wName = wd.wName;
                            wPhone = wd.wPhone;
                            wType = wd.wType;
                            wAdress = wd.wAdress;
                            wId = wd.wId;
                            isApproved = wd.isApproved;
                        }

                    }
                    if (isApproved.matches("NO")) {
                        status.setText("Not Approved");
                        approvalDialog.show();
                    } else if (isApproved.matches("SENT")) {
                        status.setText("Approval request sent");
                        Toast.makeText(getApplicationContext(), "Waiting from admin, to accept your Approval Request.", Toast.LENGTH_SHORT).show();
                    } else if (isApproved.matches("YES")) {
                        status.setText("Approved as " + wType);
                        pd.show();
                        getAllServices();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendApprovalRequest() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("approval_requests");
        String isApproved1 = "SENT";
        workerDetail wd = new workerDetail(wName, wPhone, wType, wAdress, wId, isApproved1);

        dref.child(wId).child("isApproved").setValue("SENT");
        databaseReference.child(wId).setValue(wd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(MainActivity.this, "Request Sent, Please wait for approval.", Toast.LENGTH_SHORT).show();
                    approvalDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_settings_power_black_24dp)
                    .setTitle("Logging Out")
                    .setMessage("Are you sure, you want to LogOut.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finishAffinity();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }

        if (item.getItemId() == R.id.work_done) {
            Intent in1=new Intent(getApplicationContext(),SCompletedActivity.class);
            startActivity(in1);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }

        return super.onOptionsItemSelected(item);
    }

    private void getAllServices() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dref3 = FirebaseDatabase.getInstance().getReference().child("admin2workers/" + user.getUid());

        dref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pd.cancel();
                if (dataSnapshot.exists()) {
                    serviceDetails.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ServiceDetail sd = ds.getValue(ServiceDetail.class);
                        if(sd.isCompleted.matches("NO")){
                            serviceDetails.add(new ServiceDetail(sd.userImg, sd.userName, sd.userPhone, sd.userCity, sd.userAddress, sd.itemImg, sd.itemName, sd.itemCatg, sd.itemPrice, sd.serviceDay, sd.serviceTime, sd.currentDay, sd.currentTime, sd.otherDetail, sd.serviceId, sd.userId,sd.paymentStatus,sd.isCompleted,sd.rating));
                        }

                    }

                    Collections.reverse(serviceDetails);
                    adapter = new ServiceAdapter((ArrayList<ServiceDetail>) serviceDetails, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(MainActivity.this, "No Services booked yet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.cancel();

            }
        });

    }
}
