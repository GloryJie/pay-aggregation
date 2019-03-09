/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.util
 *   Date Created: 2019/3/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 签名工具类
 *
 * @author Jie
 * @since
 */
public class SignUtil {

    /**
     * 直接将param转换成代签名字符串， a=1&b=2 的格式
     *
     * @param param
     * @return
     */
    public static String toSignStr(Map<String, Object> param) {
        Map<String, String> allParam = transformToStringValue(param);

        List<String> keyList = new ArrayList<>(allParam.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String key : keyList) {
            String value = allParam.get(key);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
                builder.append(index == 0 ? "" : "&").append(key).append("=").append(value);
                ++index;
            }
        }
        return builder.toString();
    }

    /**
     * 讲Map中的value转换成String
     * Map -> 先按照key排序，然后转出json
     * List -> json
     * LocateDateTime -> yyyy-HH-mm MM-hh-ss 因为直接转json会存在date和time之间有个T
     * 其他 -> json, 使用String.value的原因是JsonUtil对于基础数据类型会有引号，二回出现 a="b"的情况，而实际需要 a=b
     * 可能还会存在其他特殊情况，需要慢慢寻找
     *
     * @param param
     * @return
     */
    private static Map<String, String> transformToStringValue(Map<String, Object> param) {
        Map<String, String> transformMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            if (value instanceof Map) {
                TreeMap treeMap = new TreeMap((Map) value);
                transformMap.put(key, JsonUtil.toJson(treeMap));
            } else if (value instanceof List) {
                transformMap.put(key, JsonUtil.toJson(value));
            } else if (value instanceof LocalDateTime) {
                transformMap.put(key, DateTimeUtil.parse((LocalDateTime) value));
            } else {
                transformMap.put(key, String.valueOf(value));
            }
        }
        return transformMap;
    }

}
