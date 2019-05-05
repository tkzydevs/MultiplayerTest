package tkzy.test.multiplayertest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void exitApp(View view) {
        System.exit(0);
    }

    public void startGame_singlePlayer(View view) {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        startActivity(intent);
    }

    public void startGame_multiPlayer(View view) {
        Intent intent = new Intent(MenuActivity.this, OnlineLoginActivity.class);
        startActivity(intent);
    }

}
