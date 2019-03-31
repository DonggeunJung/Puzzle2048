package com.example.puzzle2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    J2048View mPuzzle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPuzzle = findViewById(R.id.puzzle);
        mPuzzle.readData();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRestart :
                mPuzzle.restartGame();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPuzzle.saveData();
    }
}
