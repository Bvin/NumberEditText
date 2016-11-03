package cn.bvin.app.sample.numbereditor;

import cn.bvin.widget.numbereditor.NumberEditText;

/**
 * Created by bvin on 2015/12/25.
 */
public class CurrencyZoom extends NumberEditText.Zoomer{
    @Override
    public int scale() {
        return 2;
    }
}
