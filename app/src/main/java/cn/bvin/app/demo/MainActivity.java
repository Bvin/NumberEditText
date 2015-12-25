package cn.bvin.app.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import cn.bvin.widget.number_edittext.NumberConvertor;
import cn.bvin.widget.number_edittext.NumberEditText;
import cn.bvin.widget.number_edittext.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        NumberEditText moneyEditText = (NumberEditText) findViewById(R.id.view);
        moneyEditText.setHint("输入价格");
        moneyEditText.setZoom(new NumberEditText.Zoomer() {
            @Override
            public int scale() {
                return 2;
            }
        });
        moneyEditText.setNumberConvertor(new CurrencyNumberConvertor(Currency.getInstance(Locale.CHINA)));
        moneyEditText.setOnValueChangeListener(new NumberEditText.OnValueChangeListener() {
            @Override
            public void onValueChanged(double value) {
                ((TextView) findViewById(R.id.textView)).setText("您输入的成交价是"+value);
            }
        });

        NumberEditText ageEditText = (NumberEditText) findViewById(R.id.view2);
        ageEditText.setHint("输入年龄");
        ageEditText.setMinValue(18);
        ageEditText.setMaxValue(35);
        ageEditText.setSpeedOfQuickControl(250);
        ageEditText.setNumberConvertor(new NumberConvertor() {
            @Override
            public String convert(double value) {
                return NumberFormat.getIntegerInstance().format(value);
            }
        });
        ageEditText.setOnValueReachRangeListener(new NumberEditText.OnValueReachRangeListener() {
            @Override
            public void onValueReachMax(double input, double max) {
                Toast.makeText(MainActivity.this, "您当前的年龄"+input+"岁不符合,不能超过"+max+"岁", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueReachMin(double input, double min) {
                Toast.makeText(MainActivity.this, "您当前的年龄"+input+"岁不符合,不能小于"+min+"岁", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
