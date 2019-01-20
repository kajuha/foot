package net.recipelab.android.foot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btDeviceStart = (Button)findViewById(R.id.bt_device_start);
        btDeviceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "장치가 시작되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btDeviceControl = (Button)findViewById(R.id.bt_device_control);
        btDeviceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "장치의 속도를 조절합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btDbReset = (Button)findViewById(R.id.bt_db_reset);
        btDbReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "운동량이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btDbHistory = (Button)findViewById(R.id.bt_db_history);
        btDbHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
//                finish();
            }
        });
    }
}
