# NumberEditText
数值控件，可通过增减微调数值，也可手动输入。中间的输入框可以手动输入，两边的加减可以微调数值。

![截图](https://raw.githubusercontent.com/Bvin/NumberEditText/master/sample/src/main/assets/Screenshot1.jpeg)

1.设置增减量
    setZoom(Zoomable zoom)，内置有一个BigDecimal实现增减的抽象类，只需要给定精度。

    ```
      setZoom(new NumberEditText.Zoomer() {
                @Override
                public int scale() {
                    return 2;//精度：0表示加减1，1表示加减0.1，2表示加减0.01
                }
            });
    ```

    也可以完全自己定义增减量，甚至可以设置增减量不一样

    ```
    setZoom(new NumberEditText.Zoomable() {
                @Override
                public double increase(double origin) {
                    return 5;//增量5，点一下+就加5个值
                }
    
                @Override
                public double decrease(double origin) {
                    return 10;//减量是10.点一下-就减10
                }
            })
    ```
    如果没有设置，缺省为对值整数增减量为1

2.设置数值显示规则
    setNumberConvertor(NumberConvertor numberConvertor)，比如可以在数值前面加上前缀或后缀单位什么的。
完全可以实现自己的NumberConvertor，可以将number转换成任意你想要的数据，缺省为数字字符串也就是String.of(value)。

    ```
    setNumberConvertor(new NumberConvertor() {
                @Override
                public String convert(double value) {
                    return NumberFormat.getIntegerInstance().format(value);
                }
            });
    ```

3.设置和获取当前值
    设置方法：public void setCurrentValue(double value)，这里的值将会转换指定的NumberConvertor来显示在输入框里面，
    获取方法public double getCurrentValue()获取当前值。
  
4.设置最大值最小值，设置后输入框里的数值将自动校正，到达最大值或最小值两边的加减控件将无法再继续加减。
    设置最大值:public void setMaxValue(double maxValue)
    设置最小值:public void setMinValue(double minValue)
    
5.设置输入超时，当用户输入后一段时间停止输入将自动帮助用户确认当前值。
    public void setInputTimeout(long inputTimeout)
    
6.设置快速加减控件的速度，也就是长按加减递增递减的变化速度，参数是一个long型的毫秒数，值越小速度越快，反之越慢。
    public void setSpeedOfQuickControl(long speedOfQuickControl)
    
7.判断当前是否是空值，当没有输入返回true,当用户输入后清空时也返回true，有值返回false。
    public boolean isEmptyValue()
    
8.设置加减微调控件监听器，每加一次见一次都会触发监听器回掉。
    public void setZoomListener(ZoomListener zoomListener)
    
    ```
        setZoomListener(new NumberEditText.ZoomListener() {
                    @Override
                    public void onValueIncreased(double value) {
                        //增值一次
                    }
        
                    @Override
                    public void onValueDecreased(double value) {
                        //减值一次
                    }
                });
    
    ```
    
9.设置值改变监听器，当用户输入值变化，或者通过加减控件引发的值变化。ZoomListener是OnValueChangeListener的子集，
也就是说触发ZoomListener一定会触发OnValueChangeListener，但触发OnValueChangeListener不一定会触发ZoomListener，
因为还有手动输入会引发值得变化。
    public void setOnValueChangeListener(OnValueChangeListener l)
    
    ```
    setOnValueChangeListener(new NumberEditText.OnValueChangeListener() {
                @Override
                public void onValueChanged(double value) {
                    Toast.show(value);
                }
            });
    ```
10.设置值触达范围监听器，也就是值抵达/超过最大值或最小值监听器。当用户通过加减控件把值加到最大值或减到最小值将触发此监听器，
加减控件将不能超过最大值最小值设定，手动输入抵达或超过最小或最大值也会触发，超出范围后按下确认或失去焦点或超时将自动校正。
    public void setOnValueReachRangeListener(OnValueReachRangeListener l) 

    ```
    setOnValueReachRangeListener(new NumberEditText.OnValueReachRangeListener() {
                @Override
                public void onValueReachMax(double input, double max) {
                    Toast.makeText(MainActivity.this, "您当前的年龄"+input+"岁不符合,不能超过"+max+"岁", Toast.LENGTH_SHORT).show();
                }
    
                @Override
                public void onValueReachMin(double input, double min) {
                    Toast.makeText(MainActivity.this, "您当前的年龄"+input+"岁不符合,不能小于"+min+"岁", Toast.LENGTH_SHORT).show();
                }
            });
    ```
    