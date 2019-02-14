/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service;

import com.gloryjie.pay.channel.dto.ChannelConfigDto;
import com.gloryjie.pay.channel.enums.ChannelType;

import java.util.List;

/**
 * 渠道配置服务
 *
 * @author Jie
 * @since 0.1
 */
public interface ChannelConfigService {

    List<ChannelConfigDto> getChannelConfig(Integer appId);

    ChannelConfigDto addNewChannelConfig(ChannelConfigDto configDto);

    ChannelConfigDto updateChannelConfig(ChannelConfigDto configDto);

    Boolean deleteChannelConfig(Integer appId, ChannelType channelType);
}
