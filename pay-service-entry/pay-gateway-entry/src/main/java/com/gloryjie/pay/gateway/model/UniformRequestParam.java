/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.model
 *   Date Created: 2019/3/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * 统一请求参数
 *
 * @author Jie
 * @since
 */
@Data
public class UniformRequestParam {

    @NotNull
    private Integer appId;

    @NotBlank
    private String uri;

    @NotBlank
    private String nonce;

    @NotNull
    private Long timestamp;

    private Map<String, String> bizData;

    public String toSignString() {
        Map<String, String> param = new HashMap<>();
        param.put("appId", String.valueOf(appId));
        param.put("uri", uri);
        param.put("timestamp", String.valueOf(timestamp));
        param.put("nonce", nonce);
        if (bizData != null && bizData.size() > 0) {
            param.putAll(bizData);
        }


        List<String> keyList = new ArrayList<>(param.keySet());
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String key : keyList) {
            String value = param.get(key);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                builder.append(index == 0 ? "" : "&").append(key).append("=").append(value);
                ++index;
            }
        }
        return builder.toString();

    }

}
