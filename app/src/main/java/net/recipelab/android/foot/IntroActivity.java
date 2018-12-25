package net.recipelab.android.foot;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class IntroActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인트로는 전체화면으로 실행
        if (Build.VERSION.SDK_INT < 16) {
            // gradle 설정에 최소 SDK는 21로 되어 있어 무조건 실행되지 않음
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // 전체화면 실행(상단 상태바 없앰)
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // 액션바 없앰
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_intro);

        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                finish();
            }
        };

        handler = new Handler();
        // 지연후 메인액티비티 실행
        handler.postDelayed(runnable, 0);
    }
}
