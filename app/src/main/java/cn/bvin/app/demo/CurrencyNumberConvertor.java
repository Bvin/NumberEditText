package cn.bvin.app.demo;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import cn.bvin.widget.number_edittext.NumberConvertor;

/**
 * Created by bvin on 2015/12/25.
 */
public class CurrencyNumberConvertor implements NumberConvertor {

    NumberFormat mNumberFormat;

    public CurrencyNumberConvertor(Currency currency) {
        mNumberFormat = NumberFormat.getCurrencyInstance();
        mNumberFormat.setCurrency(currency);
    }

    @Override
    public String convert(double value) {
        return mNumberFormat.format(value);
    }
}
