package com.example.makeupscore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText et_score_first, et_score_second;
    Button btn_calc, btn_reset;
    TextView tv_result;

    private SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_score_first = findViewById(R.id.et_score_first);
        et_score_second = findViewById(R.id.et_score_second);
        btn_calc = findViewById(R.id.btn_calc);
        btn_reset = findViewById(R.id.btn_reset);
        tv_result = findViewById(R.id.tv_result);

        initDatabase();
        loadLastResult();

        btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scoreFirstStr = et_score_first.getText().toString().trim();
                String scoreSecondStr = et_score_second.getText().toString().trim();

                if (scoreFirstStr.isEmpty() || scoreSecondStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入完整数据", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isLegalNumber(scoreFirstStr) || !isLegalNumber(scoreSecondStr)) {
                    Toast.makeText(MainActivity.this, "输入包含非法字符，仅允许数字和小数点", Toast.LENGTH_SHORT).show();
                    return;
                }

                double scoreFirst;
                double scoreSecond;
                try {
                    scoreFirst = Double.parseDouble(scoreFirstStr);
                    scoreSecond = Double.parseDouble(scoreSecondStr);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "输入格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (scoreFirst <= 0 || scoreSecond <= 0) {
                    Toast.makeText(MainActivity.this, "请输入大于0的正数", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (scoreFirst > 1000 || scoreSecond > 1000) {
                    Toast.makeText(MainActivity.this, "成绩数值超出合理范围（0~1000）", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 专属算法：n个正数平均值 + 1.0
                int count = 2;
                double average = (scoreFirst + scoreSecond) / count;
                double finalResult = average + 1.0;

                DecimalFormat df = new DecimalFormat("0.00");
                String averageStr = df.format(average);
                String finalStr = df.format(finalResult);

                String resultText = "输入成绩：" + df.format(scoreFirst) + "、" + df.format(scoreSecond)
                        + "\n有效数据个数：" + count
                        + "\n平均值：" + averageStr
                        + "\n专属修正值：+1.00"
                        + "\n最终结果：" + finalStr;

                tv_result.setText(resultText);
                saveResult(resultText);
                Toast.makeText(MainActivity.this, "计算完成，结果已保存", Toast.LENGTH_SHORT).show();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_score_first.setText("");
                et_score_second.setText("");
                tv_result.setText("计算结果将在此展示");
                clearDatabase();
                Toast.makeText(MainActivity.this, "已重置清空所有数据", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLegalNumber(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!(Character.isDigit(c) || c == '.')) {
                return false;
            }
        }
        return input.matches("^[0-9]+(\\.[0-9]+)?$");
    }

    private void initDatabase() {
        dbHelper = new SQLiteOpenHelper(this, "makeup_score.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE score_record (_id INTEGER PRIMARY KEY AUTOINCREMENT, result_text TEXT NOT NULL)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS score_record");
                onCreate(db);
            }
        };
    }

    private void saveResult(String resultText) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("score_record", null, null);
        ContentValues values = new ContentValues();
        values.put("result_text", resultText);
        db.insert("score_record", null, values);
    }

    private void loadLastResult() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("score_record", new String[]{"result_text"},
                null, null, null, null, "_id DESC", "1");
        if (cursor.moveToFirst()) {
            tv_result.setText(cursor.getString(0));
        }
        cursor.close();
    }

    private void clearDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("score_record", null, null);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
