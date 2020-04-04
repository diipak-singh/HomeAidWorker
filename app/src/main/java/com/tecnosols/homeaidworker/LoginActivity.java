package com.tecnosols.homeaidworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText name, workType, Address;

    private Button signup;
    private TextView login;

    private static final int MY_REQUEST_CODE = 007, ANOTHER_REQUEST_CODE = 001;
    List<AuthUI.IdpConfig> providers;
    FirebaseUser user;
    DatabaseReference dref;
    String wName, wType, wAddress;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.editText_name);
        workType = findViewById(R.id.editText_work);
        Address = findViewById(R.id.editText_address);
        signup = findViewById(R.id.button_signup);
        login = findViewById(R.id.textView_alreadySignIn);

        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Loading.. Please   Wait!");
        pd.setCancelable(false);

        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
               /*  new AuthUI.IdpConfig.EmailBuilder().build()
                new AuthUI.IdpConfig.GoogleBuilder().build()*/
        );

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pd.show();
                wName = name.getText().toString().trim();
                wType = workType.getText().toString().trim();
                wAddress = Address.getText().toString().trim();

                if (wName.isEmpty()) {
                    name.setError("Please enter your name.");
                    name.requestFocus();
                    return;
                }

                if (wType.isEmpty()) {
                    workType.setError("Please enter your work type.");
                    workType.requestFocus();
                    return;
                }

                if (wAddress.isEmpty()) {
                    Address.setError("Please enter your address.");
                    Address.requestFocus();
                    return;
                }

                showSignInOptions();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOptionsDirectly();
            }
        });
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(), MY_REQUEST_CODE
        );
    }

    private void showSignOptionsDirectly() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(), ANOTHER_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Successfully LoggedIn", Toast.LENGTH_SHORT).show();
                saveWorkerData();
            }
        }

        if (requestCode == ANOTHER_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Successfully LoggedIn", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
            }
        }
    }

    private void saveWorkerData() {
        pd.show();
        dref = FirebaseDatabase.getInstance().getReference().child("worker_detail");
        user = FirebaseAuth.getInstance().getCurrentUser();


        String id = user.getUid();
        String wPhone = user.getPhoneNumber();
        String isApproved = "NO";

        workerDetail wd = new workerDetail(wName, wPhone, wType, wAddress, user.getUid(), isApproved);
        dref.child(id).setValue(wd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    pd.cancel();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
