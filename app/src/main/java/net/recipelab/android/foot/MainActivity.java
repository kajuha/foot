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
import android.widget.TextView;

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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MySqliteOpenhelper mySqliteOpenhelper;
    private SQLiteDatabase sqliteDatabase;
    String dbName = "foot.db";
    int dbVersion = 1;
    String tableName = "foot_count";
    String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    ArrayList<History> history_day;
    ArrayList<BarEntry> entries;
    ArrayList<String> labels_arr;

    TextView tvSubject;
    TextView tvCount;
    TextView tvCalories;

    public enum DateType {
        DAY, WEEK, MONTH
    }

    DateType dateType;

    void createDB(SQLiteDatabase sqliteDatabase) {
        // 모든 레코드 삭제
        sqliteDatabase.execSQL("delete from " + tableName);
        // 년
        int year;
        if (true) {
            year = 2018;
        } else {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        // 월
        ArrayList<Integer> months = new ArrayList<Integer>();
        if (true) {
            months.add(11);
            months.add(12);
        } else {
            ArrayList<Integer> lotto_months = new ArrayList<Integer>();
            for (int i=0; i<12; i++) {
                lotto_months.add(i);
            }
            // 뽑을 랜덤 월 개수
            int pick_month_num = 12;
            for (int i=0; i<pick_month_num; i++) {
                months.add(lotto_months.remove(new Random().nextInt(lotto_months.size())) + 1);
            }
        }
        // 뽑은 랜덤 월 정렬
        Collections.sort(months);
        // 월별 일을 계산
        for (int i=0; i<months.size(); i++) {
            // 일
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            Date date = null;
            try {
                String temp = String.format("%04d-%02d-01 00:00:00.000", year, months.get(i));
                date = simpleDateFormat.parse(temp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            ArrayList<Integer> lotto_days = new ArrayList<Integer>();
            for (int j=0; j<lastDay; j++) {
                lotto_days.add(j);
            }
            // 뽑을 랜덤 일 개수
            int pick_day_num = 15;
            if (pick_day_num > lastDay) {
                pick_day_num = lastDay;
            }
            ArrayList<Integer> days = new ArrayList<Integer>();
            for (int j=0; j<pick_day_num; j++) {
                days.add(lotto_days.remove(new Random().nextInt(lotto_days.size())) + 1);
            }
            // 뽑은 랜덤 일 정렬
            Collections.sort(days);
            Log.e("kajuha", "create DB start");
            for (int j=0; j<days.size(); j++) {
                String dateFormat = String.format("%04d-%02d-%02d 00:00:00.000", year, months.get(i), days.get(j));
                Log.e("kajuha", dateFormat);
                int count = new Random().nextInt(3000);
                String sql = String.format("insert into foot_count (datetime, count) values('%s', %d);", dateFormat, count);
                sqliteDatabase.execSQL(sql);
            }
            Log.e("kajuha", "create DB finish");
        }
    }

    public class History {
        public int _id;
        public String _datetime;
        public int _count;

        public History(int id, String datetime, int count) {
            this._id = id;
            this._datetime = datetime;
            this._count = count;
        }
    }

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

        // DB에 데이터가 없으면 생성
        int dbCount = 0;
        Cursor cs = sqliteDatabase.rawQuery("select * from foot_count order by id desc limit 7;", null);
        while(cs.moveToNext()) {
            dbCount++;
        }
        if (dbCount == 0) {
            createDB(sqliteDatabase);
        }

        // 검색
        history_day = new ArrayList<History>();
        Cursor cursor = sqliteDatabase.rawQuery("select * from foot_count order by id desc limit 7;", null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String datetime = cursor.getString(1);
            int count = cursor.getInt(2);
            history_day.add(new History(id, datetime, count));
            Log.e("kajuha", String.format("id : %d, datetime : %s, count : %d", id, datetime, count));
        }
        Collections.reverse(history_day);

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

        labels_arr = new ArrayList<String>();
        for (int i=0; i<history_day.size(); i++) {
            labels_arr.add(history_day.get(i)._datetime.substring(5, 10));
        }
        String[] labels = labels_arr.toArray(new String[labels_arr.size()]);
        chart.getXAxis().setValueFormatter(new LabelFormatter(labels));

        // 축 입력값
        entries = new ArrayList<>();
        for (int i=0; i<history_day.size(); i++) {
            entries.add(new BarEntry(i, history_day.get(i)._count));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColor(Color.rgb(201, 255, 165));
        BarData barData = new BarData(barDataSet);
        chart.setData(barData);

        // UI 컨트롤 갱신
        dateType = DateType.DAY;
        tvSubject = (TextView) findViewById(R.id.tv_subject);
        tvCount = (TextView) findViewById(R.id.tv_count);
        tvCalories = (TextView) findViewById(R.id.tv_calories);
        if (history_day.size() != 0) {
            tvSubject.setText(labels_arr.get(labels_arr.size() - 1));
            int count = history_day.get(history_day.size() - 1)._count;
            tvCount.setText("" + count);
            int calories = (int) (count * 0.04);
            tvCalories.setText("" + calories);
        } else {
            tvSubject.setText("Today");
            int count = 0;
            tvCount.setText("" + count);
            int calories = 0;
            tvCalories.setText("" + calories);
        }

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
                int idx = (int)e.getX();
                if (dateType == DateType.DAY) {
                    if (history_day.size() != 0) {
                        tvSubject.setText(labels_arr.get(idx));
                        int count = history_day.get(idx)._count;
                        tvCount.setText("" + count);
                        int calories = (int)(count * 0.04);
                        tvCalories.setText("" + calories);
                    } else {
                        tvSubject.setText("Today");
                        int count = 0;
                        tvCount.setText("" + count);
                        int calories = 0;
                        tvCalories.setText("" + calories);
                    }
                }
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
            }
        });

        Button btWeek = (Button)findViewById(R.id.bt_week);
        btWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Button btMonth = (Button)findViewById(R.id.bt_month);
        btMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
