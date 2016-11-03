/*
 * Copyright 2015 bvin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bvin.widget.numbereditor;

/**
 * 数字显示转换器.
 */
public interface NumberConvertor {
    /**
     * 转换数字为字符串.
     * @param value 输入值
     * @return 输出字符串
     */
    public String convert(double value);
}
