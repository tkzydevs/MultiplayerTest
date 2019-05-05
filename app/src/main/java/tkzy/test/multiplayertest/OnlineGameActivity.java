package tkzy.test.multiplayertest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("FieldCanBeLocal")
public class OnlineGameActivity extends AppCompatActivity {

    // Widgets
    private TextView mPlayer1, mPlayer2;

    // Variables
    private String playerSession = "";
    private String userName = "";
    private String otherPlayer = "";
    private String loginUID = "";
    private String requestType = "";
    private String mySign = "X";
    private int gameState = 0;
    private int activePlayer = 1;

    private ArrayList<Integer> player1 = new ArrayList<>();
    private ArrayList<Integer> player2 = new ArrayList<>();

    // Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        userName = getIntent().getExtras().get("user_name").toString();
        loginUID = getIntent().getExtras().get("login_uid").toString();
        otherPlayer = getIntent().getExtras().get("other_player").toString();
        requestType = getIntent().getExtras().get("request_type").toString();
        playerSession = getIntent().getExtras().get("player_session").toString();

        mPlayer1 = findViewById(R.id.tvPlayer1);
        mPlayer2 = findViewById(R.id.tvPlayer2);

        gameState = 1;

        if (requestType.equals("From")) {
            mySign = "O";
            mPlayer1.setText("Your Turn");
            mPlayer2.setText("Your Turn");

            reference.child("playing").child(playerSession).child("turn").setValue(otherPlayer);
        }
        else {
            mySign = "X";
            mPlayer1.setText("Turn: " + otherPlayer);
            mPlayer2.setText("Turn: " + otherPlayer);

            reference.child("playing").child(playerSession).child("turn").setValue(otherPlayer);
        }

        reference.child("playing").child(playerSession).child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String value = dataSnapshot.getValue(String.class);
                    if (value.equals(userName)) {
                        mPlayer1.setText("Your turn");
                        mPlayer2.setText("Your turn");
                        setEnableClick(true);
                        activePlayer = 1;
                    }
                    else if(value.equals(otherPlayer)) {
                        mPlayer1.setText("Turn: " + otherPlayer);
                        mPlayer2.setText("Turn: " + otherPlayer);
                        setEnableClick(false);
                        activePlayer = 2;
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

        reference.child("playing").child(playerSession).child("game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    player1.clear();
                    player2.clear();
                    activePlayer = 2;

                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if(map != null) {
                        String value = "";
                        String firstPlayer = userName;
                        for (String key : map.keySet()) {
                            value = (String) map.get(key);
                            if (value.equals(userName)) {
                                activePlayer = 2;
                            }
                            else {
                                activePlayer = 1;
                            }

                            firstPlayer = value;
                            String[] splitID = key.split(":");
                            OtherPlayer(Integer.parseInt(splitID[1]));
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

    private void OtherPlayer(int selectedBlock) {
        ImageView selectedImage = findViewById(R.id.ivFrame11);
        switch (selectedBlock) {
            case 1: selectedImage = findViewById(R.id.ivFrame11);
                break;
            case 2: selectedImage = findViewById(R.id.ivFrame12);
                break;
            case 3: selectedImage = findViewById(R.id.ivFrame13);
                break;

            case 4: selectedImage = findViewById(R.id.ivFrame21);
                break;
            case 5: selectedImage = findViewById(R.id.ivFrame22);
                break;
            case 6: selectedImage = findViewById(R.id.ivFrame23);
                break;

            case 7: selectedImage = findViewById(R.id.ivFrame31);
                break;
            case 8: selectedImage = findViewById(R.id.ivFrame32);
                break;
            case 9: selectedImage = findViewById(R.id.ivFrame33);
                break;
        }

        playGame(selectedBlock, selectedImage);
    }

    private void resetGame() {
        gameState = 1;
        activePlayer = 1;
        player1.clear();
        player2.clear();

        reference.child("playing").child(playerSession).removeValue();

        ImageView imageView;

        imageView = findViewById(R.id.ivFrame11);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame12);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame13);
        imageView.setImageResource(0);
        imageView.setEnabled(true);


        imageView = findViewById(R.id.ivFrame21);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame22);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame23);
        imageView.setImageResource(0);
        imageView.setEnabled(true);


        imageView = findViewById(R.id.ivFrame31);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame32);
        imageView.setImageResource(0);
        imageView.setEnabled(true);

        imageView = findViewById(R.id.ivFrame33);
        imageView.setImageResource(0);
        imageView.setEnabled(true);
    }

    public void gameBoardClick(View view) {
        ImageView selectedImage = (ImageView) view;

        if (playerSession.length() <= 0) {
            Intent intent = new Intent(OnlineGameActivity.this, OnlineLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            int selectedBlock = 0;
            switch (selectedImage.getId()) {
                case R.id.ivFrame11: selectedBlock = 1; break;
                case R.id.ivFrame12: selectedBlock = 2; break;
                case R.id.ivFrame13: selectedBlock = 3; break;

                case R.id.ivFrame21: selectedBlock = 4; break;
                case R.id.ivFrame22: selectedBlock = 5; break;
                case R.id.ivFrame23: selectedBlock = 6; break;

                case R.id.ivFrame31: selectedBlock = 7; break;
                case R.id.ivFrame32: selectedBlock = 8; break;
                case R.id.ivFrame33: selectedBlock = 9; break;
            }

            reference.child("playing").child(playerSession).child("game")
                    .child("block:" + selectedBlock).setValue(userName);

            reference.child("playing").child(playerSession)
                    .child("turn").setValue(otherPlayer);

            setEnableClick(false);
            activePlayer = 2;

            playGame(selectedBlock, selectedImage);
        }
    }

    private void playGame(int selectedBlock, ImageView selectedImage) {
        if (gameState == 1) {
            if (activePlayer == 1) {
                selectedImage.setImageResource(R.mipmap.ic_wrong_round);
                player1.add(selectedBlock);
            }
            else if (activePlayer == 2) {
                selectedImage.setImageResource(R.mipmap.ic_right_round);
                player2.add(selectedBlock);
            }

            selectedImage.setEnabled(false);
            checkWinner();
        }
    }

    private void checkWinner() {
        int winner = 0;

        /*
         * Checking if Player 1 is the winner
         * */
        if (player1.contains(1) && player1.contains(2) && player1.contains(3))
            winner = 1;
        if (player1.contains(4) && player1.contains(5) && player1.contains(6))
            winner = 1;
        if (player1.contains(7) && player1.contains(8) && player1.contains(9))
            winner = 1;

        if (player1.contains(1) && player1.contains(4) && player1.contains(7))
            winner = 1;
        if (player1.contains(2) && player1.contains(5) && player1.contains(8))
            winner = 1;
        if (player1.contains(3) && player1.contains(6) && player1.contains(9))
            winner = 1;

        if (player1.contains(1) && player1.contains(5) && player1.contains(9))
            winner = 1;
        if (player1.contains(3) && player1.contains(5) && player1.contains(7))
            winner = 1;


        /*
         * Checking if Player 2 is the winner
         * */
        if (player2.contains(1) && player2.contains(2) && player2.contains(3))
            winner = 2;
        if (player2.contains(4) && player2.contains(5) && player2.contains(6))
            winner = 2;
        if (player2.contains(7) && player2.contains(8) && player2.contains(9))
            winner = 2;

        if (player2.contains(1) && player2.contains(4) && player2.contains(7))
            winner = 2;
        if (player2.contains(2) && player2.contains(5) && player2.contains(8))
            winner = 2;
        if (player2.contains(3) && player2.contains(6) && player2.contains(9))
            winner = 2;

        if (player2.contains(1) && player2.contains(5) && player2.contains(9))
            winner = 2;
        if (player2.contains(3) && player2.contains(5) && player2.contains(7))
            winner = 2;

        if (winner != 0 && gameState == 1) {
            if (winner == 1) {
                showAlert(otherPlayer + " got Winner Winner Chicken Dinner");
            }
            else if (winner == 2) {
                showAlert("Winner Winner Chicken Dinner");
            }

            gameState = 2;
        }

        ArrayList<Integer> emptyBlocks = new ArrayList<>();
        for (int i = 1; i <= 9; ++i) {
            if (!(player1.contains(i) || player2.contains(i))) {
                emptyBlocks.add(i);
            }
        }

        if (emptyBlocks.size() == 0) {
            if (gameState == 1) {
                showAlert("Draw");
            }

            gameState = 3;
        }
    }

    private void showAlert(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
        builder.setTitle(title)
                .setMessage("Start a new game?")
                .setNegativeButton("Lobby", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OnlineGameActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setEnableClick(Boolean state) {
        ImageView iv;
        iv = findViewById(R.id.ivFrame11);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame12);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame13);   iv.setClickable(state);

        iv = findViewById(R.id.ivFrame21);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame22);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame23);   iv.setClickable(state);

        iv = findViewById(R.id.ivFrame31);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame32);   iv.setClickable(state);
        iv = findViewById(R.id.ivFrame33);   iv.setClickable(state);
    }

}