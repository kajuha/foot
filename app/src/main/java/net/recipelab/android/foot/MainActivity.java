package net.recipelab.android.foot;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ArrayList<BarEntry> entries;

    private MySqliteOpenhelper mySqliteOpenhelper;
    private SQLiteDatabase sqliteDatabase;
    String dbName = "foot.db";
    int dbVersion = 1;
    String tableName = "foot_count";
    String dateFormat = "YYYY-MM-DD HH:MM:SS.SSS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 없앰
        if (Build.VERSION.SDK_INT < 16) {
        } else {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        // 임의 DB 생성
        // 1. DB에 데이터가 아무것도 없음
        // 2. DB에 데이터가 3개만 있음
        // 3. 많이 많음
        mySqliteOpenhelper = new MySqliteOpenhelper(this, dbName, null, dbVersion);
        try {
            sqliteDatabase = mySqliteOpenhelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("kajuha", "데이터베이스를 얻어올 수 없음");
            finish();
        }
        // 모든 레코드 삭제
        sqliteDatabase.execSQL("delete from " + tableName);
        // 임의 레코드 삽입

        /////
        //aa
        ////

        // 챠트 생성
        BarChart chart = (BarChart)findViewById(R.id.chart);

        // X축 라벨
        class LabelFormatter implements IAxisValueFormatter {
            private final String[] mLabels;
            public LabelFormatter(String[] labels) {
                mLabels = labels;
            }

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mLabels[(int) value];
            }
        }
        ArrayList<String> labels_arr = new ArrayList<String>();
        labels_arr.add("12-10");
        labels_arr.add("12-11");
        labels_arr.add("12-12");
        labels_arr.add("12-13");
        labels_arr.add("12-14");
        labels_arr.add("12-15");
        labels_arr.add("12-16");
        String[] labels = labels_arr.toArray(new String[labels_arr.size()]);
        chart.getXAxis().setValueFormatter(new LabelFormatter(labels));

        // 축 입력값
        entries = new ArrayList<>();
        entries.add(new BarEntry(0, 10));
        entries.add(new BarEntry(1, 10));
        entries.add(new BarEntry(2, 20));
        entries.add(new BarEntry(3, 30));
        entries.add(new BarEntry(4, 40));
        entries.add(new BarEntry(5, 50));
        entries.add(new BarEntry(6, 60));
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColor(Color.rgb(201, 255, 165));
        BarData barData = new BarData(barDataSet);
        chart.setData(barData);

        // 축관련 표시정보 삭제
        // 차트 정보
        chart.getDescription().setText("");
        // 차트 제목
        chart.getLegend().setEnabled(false);
        // X축
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        // Y축 왼쪽, 오른쪽
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        // 확대, 축소
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        // 축관련 정보 표시
        // X축 라벨
        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        // X축 값
        chart.setDrawValueAboveBar(true);

        // 차트 다시 그리기
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.e("kajuha", String.valueOf(e.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // 터치 처리
        Button btDay = (Button)findViewById(R.id.bt_day);
        btDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("kajuha", "day");
                sqliteDatabase.execSQL("insert into mytable(name) values('Seo');");
                sqliteDatabase.execSQL("insert into mytable(name) values('Choi');");
                sqliteDatabase.execSQL("insert into mytable(name) values('Park');");
                sqliteDatabase.execSQL("insert into mytable(name) values('Heo');");
                sqliteDatabase.execSQL("insert into mytable(name) values('Kim');");
            }
        });

        Button btWeek = (Button)findViewById(R.id.bt_week);
        btWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("kajuha", "week");
                Cursor cursor = sqliteDatabase.rawQuery("select * from mytable;", null);
                while(cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    Log.e("kajuha", id + " " + name);
                }
            }
        });

        Button btMonth = (Button)findViewById(R.id.bt_month);
        btMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("kajuha", "month");
//                Cursor cursor = sqliteDatabase.rawQuery("select id from mytable order by id desc limit 1;", null);
//                while(cursor.moveToNext()) {
//                    int id = cursor.getInt(0);
//                    Log.e("kajuha", id + "");
//                }
                // 년
                int year = 2019;
                // 월
                ArrayList<Integer> lotto_months = new ArrayList<Integer>();
                for (int i=0; i<12; i++) {
                    lotto_months.add(i);
                }
                // 뽑을 랜덤 월 개수
                int pick_month_num = 12;
                ArrayList<Integer> months = new ArrayList<Integer>();
                for (int i=0; i<pick_month_num; i++) {
                    months.add(lotto_months.remove(new Random().nextInt(lotto_months.size())) + 1);
                }
                // 뽑은 랜덤 월 정렬
                Collections.sort(months);
                // 월별 일을 계산
//                Log.e("kajuha", "months.size : " + months.size());
                for (int i=0; i<months.size(); i++) {
                    // 일
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                    Date date = null;
                    try {
                        String dateFormat = String.format("%04d-%02d-01 00:00:00.000", year, months.get(i));
                        date = simpleDateFormat.parse(dateFormat);
                        Log.e("kajuha", "dateFormat " + dateFormat + " date " + date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    Log.e("kajuha", "date " + date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
//                    Log.e("kajuha", "i " + i + " " + date.toString());
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                    Log.e("kajuha", "calendar " + calendar);
                    ArrayList<Integer> lotto_days = new ArrayList<Integer>();
                    for (int j=0; j<lastDay; j++) {
                        lotto_days.add(j);
                    }
                    // 뽑을 랜덤 일 개수
                    int pick_day_num = 31;
                    if (pick_day_num > lastDay) {
                        pick_day_num = lastDay;
                    }
                    ArrayList<Integer> days = new ArrayList<Integer>();
                    for (int j=0; j<pick_day_num; j++) {
                        days.add(lotto_days.remove(new Random().nextInt(lotto_days.size())) + 1);
                    }
                    // 뽑은 랜덤 일 정렬
                    Collections.sort(days);
                    for (int j=0; j<days.size(); j++) {
                        String dateFormat = String.format("%04d-%02d-%02d 00:00:00.000", year, months.get(i), days.get(j));
                        int count = new Random().nextInt(3000);
                        String sql = String.format("insert into foot_count (datetime, count) values('%s', %d);", dateFormat, count);
                        sqliteDatabase.execSQL(sql);
                    }
                }
                Log.e("kajuha", "done");
            }
        });
    }
}
