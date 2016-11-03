package cn.bvin.widget.numbereditor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalUtil {

    /**
     * 处理 Double 的大数据减法
     * @param minuend 被减数
     * @param subtrahend 减数
     * @return
     */
    public static BigDecimal subtraction(Double minuend, Double subtrahend) {
        BigDecimal bigMinuend = new BigDecimal(minuend);
        BigDecimal bigSubtrahend = new BigDecimal(subtrahend);
        return bigMinuend.subtract(bigSubtrahend);
    }

    /**
     * 处理 Double 的大数据除法，不要随意外部调用，会出现无法除尽异常
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale the scale of the result returned.
     * @param roundingMode rounding mode to be used to round the result.
     * @return
     */
    public static BigDecimal divide(Double dividend, Double divisor, int scale, RoundingMode roundingMode) {
        BigDecimal bigDividend = BigDecimal.valueOf(dividend);
        BigDecimal bigDivisor = BigDecimal.valueOf(divisor);
        return bigDividend.divide(bigDivisor, scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据除法，不要随意外部调用，会出现无法除尽异常
     * @param dividend 被除数
     * @param divisor 除数
     * @return
     */
    public static BigDecimal divide(Double dividend, Double divisor) {
        BigDecimal bigDividend = BigDecimal.valueOf(dividend);
        BigDecimal bigDivisor = BigDecimal.valueOf(divisor);
        return bigDividend.divide(bigDivisor);
    }

    /**
     * 处理 Double 的大数据乘法
     * @param multiplier
     * @param multiplicand
     * @return
     */
    public static BigDecimal multiply(Double multiplier, Double multiplicand) {
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        return bigMultiplier.multiply(bigMultiplicand);
    }

    public static BigDecimal multiply(Double multiplier, Double multiplicand, int scale, RoundingMode roundingMode) {
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        return bigMultiplier.multiply(bigMultiplicand, new MathContext(scale, roundingMode));
    }

    public static BigDecimal add(Double addend, Double augend) {
        BigDecimal bigAddend = BigDecimal.valueOf(addend);
        BigDecimal bigAugend = BigDecimal.valueOf(augend);
        return bigAddend.add(bigAugend);
    }

}
