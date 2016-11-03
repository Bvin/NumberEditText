package cn.bvin.app.sample.numbereditor;

import java.text.NumberFormat;
import java.util.Currency;

import cn.bvin.widget.numbereditor.NumberConvertor;

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
