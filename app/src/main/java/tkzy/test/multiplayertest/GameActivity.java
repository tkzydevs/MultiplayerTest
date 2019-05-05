package tkzy.test.multiplayertest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class GameActivity extends AppCompatActivity {

    // Variables
    /*
     * It can have 3 values
     *
     * 1: Game can be played
     * 2: Game over
     * 3: Match draw
     * */
    private int gameState;
    private int activePlayer = 1;
    private ArrayList<Integer> player1 = new ArrayList<>();
    private ArrayList<Integer> player2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameState = 1;
    }

    public void gameBoardClick(View view) {
        ImageView selectedImage = (ImageView) view;

        int selectedBlock = 0;

        switch (selectedImage.getId()) {

            case R.id.ivFrame11: selectedBlock = 1;
                break;
            case R.id.ivFrame12: selectedBlock = 2;
                break;
            case R.id.ivFrame13: selectedBlock = 3;
                break;
            case R.id.ivFrame21: selectedBlock = 4;
                break;
            case R.id.ivFrame22: selectedBlock = 5;
                break;
            case R.id.ivFrame23: selectedBlock = 6;
                break;
            case R.id.ivFrame31: selectedBlock = 7;
                break;
            case R.id.ivFrame32: selectedBlock = 8;
                break;
            case R.id.ivFrame33: selectedBlock = 9;
                break;

        }

        playGame(selectedBlock, selectedImage);
    }

    private void playGame(int selectedBlock, ImageView selectedImage) {
        if(gameState == 1) {
            if (activePlayer == 1) {
                selectedImage.setImageResource(R.mipmap.ic_wrong_round);
                player1.add(selectedBlock);
                activePlayer = 2;
                autoplay();
            }
            else if (activePlayer == 2) {
                selectedImage.setImageResource(R.mipmap.ic_right_round);
                player2.add(selectedBlock);
                activePlayer = 1;
            }

            selectedImage.setEnabled(false);
            checkWinner();
        }
    }

    private void autoplay() {
        ArrayList<Integer> emptyBlocks = new ArrayList<>();

        for (int i = 1; i <= 9; ++i) {
            if (!(player1.contains(i) || player2.contains(i))) {
                emptyBlocks.add(i);
            }
        }

        if (emptyBlocks.size() == 0) {
            checkWinner();
            if (gameState == 1) {
                showAlert("Draw");
            }
            gameState = 3;  // Draw
        }
        else {
            Random random = new Random();
            int randomIndex = random.nextInt(emptyBlocks.size());
            int selectedBlock = emptyBlocks.get(randomIndex);

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
    }

    private void showAlert(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
        builder.setTitle(title)
                .setMessage(getString(R.string.start_new_game))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setNegativeButton("Lobby", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    private void resetGame() {
        gameState = 1;
        activePlayer = 1;
        player1.clear();
        player2.clear();

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
            if (winner == 1)
                showAlert("Winner Winner Chicken Dinner");

            if (winner == 2)
                showAlert("AI won");


            gameState = 2;
        }
    }

}
