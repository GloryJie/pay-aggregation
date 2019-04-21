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

import com.gloryjie.pay.base.exception.error.BusinessException;
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

    /**
     * 获取正在使用的渠道配置
     * @param appId
     * @param channelType
     * @return
     * @throws BusinessException 不存在或者状态不对,则抛异常
     */
    ChannelConfigDto getUsingChannelConfig(Integer appId, ChannelType channelType) throws BusinessException;

    /**
     * 获取所有已经配置的, 状态无关
     * @param appId
     * @return
     */
    List<ChannelConfigDto> getChannelConfig(Integer appId);

    /**
     * 添加新的渠道配置
     * @param configDto
     * @return
     */
    ChannelConfigDto addNewChannelConfig(ChannelConfigDto configDto);

    /**
     * 更新渠道配置
     * @param configDto
     * @return
     */
    ChannelConfigDto updateChannelConfig(ChannelConfigDto configDto);

    /**
     * 删除渠道配置, 为实际删除
     * @param appId
     * @param channelType
     * @return
     */
    Boolean deleteChannelConfig(Integer appId, ChannelType channelType);

}
