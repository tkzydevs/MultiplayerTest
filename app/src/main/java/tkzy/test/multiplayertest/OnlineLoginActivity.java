package tkzy.test.multiplayertest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
public class OnlineLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    ListView mOnlineUsers, mRequests;
    ArrayList<String> listOnlineUsers = new ArrayList<>();
    ArrayList<String> listRequests = new ArrayList<>();
    ArrayAdapter arrayAdapter, requestsAdapter;

    // Widgets
    private TextView mLoggedInUser, mOnlineFriends, mRequestFromFriends;

    // Variables
    String loginUserID, userName, loginUID;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_login);

        mAuth = FirebaseAuth.getInstance();

        mLoggedInUser = findViewById(R.id.tvLoginUser);
        mOnlineFriends = findViewById(R.id.tvOnlineFriends);
        mRequestFromFriends = findViewById(R.id.tvRequestFromFriends);

        mOnlineFriends.setText("Please wait...");
        mRequestFromFriends.setText("Please wait...");

        mOnlineUsers = findViewById(R.id.lvOnlineUsers);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOnlineUsers);
        mOnlineUsers.setAdapter(arrayAdapter);

        mRequests = findViewById(R.id.lvRequestOfOnlineUser);
        requestsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listRequests);
        mRequests.setAdapter(requestsAdapter);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    loginUID = user.getUid();
                    loginUserID = user.getEmail();
                    mLoggedInUser.setText(loginUserID);
                    userName = getUserName(loginUserID);
                    reference.child("users").child(userName).child("request").setValue(loginUID);
                    requestsAdapter.clear();
                    acceptRequests();
                }
                else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Collections.singletonList(
                                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                                            )
                                    )
                                    .setTheme(R.style.AppTheme)
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            }
        };

        reference.getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateLoggedInUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mOnlineUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String request2User = ((TextView) view).getText().toString();
                confirmRequest(request2User, "To");
            }
        });

        mRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String requestFromUser = ((TextView) view).getText().toString();
                confirmRequest(requestFromUser, "From");
            }
        });
    }

    private String getUserName(String username) {
        String val = username.substring(0, username.indexOf('@'));
        val = val.replace(".", "");
        return val;
    }

    private void confirmRequest(final String otherPlayer, final String requestType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.connect_player_dialog, null);

        builder.setView(dialogView);
        builder.setTitle("Start Game?");
        builder.setMessage("Connect with " + otherPlayer);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("users").child(otherPlayer).child("request").push().setValue(loginUserID);
                if (requestType.equalsIgnoreCase("From")) {
                    startGame(otherPlayer + ":" + userName, otherPlayer, "From");
                }
                else {
                    startGame(userName + ":" + otherPlayer, otherPlayer, "To");
                }
            }
        });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void startGame(String playerGameID, String otherPlayer, String requestType) {
        reference.child("playing").child(playerGameID).removeValue();
        Intent intent = new Intent(OnlineLoginActivity.this, OnlineGameActivity.class);
        intent.putExtra("player_session", playerGameID);
        intent.putExtra("user_name", userName);
        intent.putExtra("other_player", otherPlayer);
        intent.putExtra("login_uid", loginUID);
        intent.putExtra("request_type", requestType);
        startActivity(intent);
        finish();
    }

    private void updateLoggedInUsers(DataSnapshot dataSnapshot) {
        String key;
        Set<String> set = new HashSet<>();

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            key = ((DataSnapshot) i.next()).getKey();
            if(!key.equalsIgnoreCase(userName)) {
                set.add(key);
            }
        }

        arrayAdapter.clear();
        arrayAdapter.addAll(set);
        arrayAdapter.notifyDataSetChanged();

        mOnlineFriends.setText("Send Request to: ");
        mRequestFromFriends.setText("Accept request from: ");
    }

    private void acceptRequests() {
        reference.child("users").child(userName)
                .child("request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
                                String value = "";
                                for (String key : map.keySet()) {
                                    value = (String) map.get(key);
                                    requestsAdapter.add(getUserName(value));
                                    requestsAdapter.notifyDataSetChanged();
                                    reference.child("users").child(userName).child("request")
                                            .setValue(loginUID);
                                }
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                mLoggedInUser.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    finish();
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}
