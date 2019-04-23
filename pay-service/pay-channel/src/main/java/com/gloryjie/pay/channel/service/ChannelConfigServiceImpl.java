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
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.validator.ParamValidator;
import com.gloryjie.pay.channel.dao.ChannelConfigDao;
import com.gloryjie.pay.channel.dto.ChannelConfigDto;
import com.gloryjie.pay.channel.enums.ChannelConfigStatus;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Service
public class ChannelConfigServiceImpl implements ChannelConfigService {

    @Autowired
    private ChannelConfigDao channelConfigDao;

    @Override
    public ChannelConfigDto getUsingChannelConfig(Integer appId, ChannelType channelType) {
        ChannelConfig channelConfig = channelConfigDao.loadByAppIdAndChannel(appId, channelType);
        // 是否启用进行判断
        if (channelConfig == null || channelConfig.getStatus().isStop()){
            throw BusinessException.create(ChannelError.CHANNEL_CONFIG_NOT_EXISTS, "或未启用");
        }
        return BeanConverter.covert(channelConfig, ChannelConfigDto.class);
    }

    @Override
    public List<ChannelConfigDto> getChannelConfig(Integer appId) {
        List<ChannelConfig> configList = channelConfigDao.getByAppId(appId);
        return BeanConverter.batchCovert(configList, ChannelConfigDto.class);
    }

    @Override
    public ChannelConfigDto addNewChannelConfig(ChannelConfigDto configDto) {
        // 不同渠道配置参数检查
        Map<String, Object> payConfig = new HashMap<>(configDto.getChannelConfig());
        ParamValidator.validate(BeanConverter.mapToBean(payConfig, configDto.getChannel().getConfigClass()));

        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(configDto.getAppId(), configDto.getChannel());
        if (config != null) {
            throw BusinessException.create(ChannelError.CHANNEL_CONFIG_EXISTS);
        }
        config = new ChannelConfig();
        config.setAppId(configDto.getAppId());
        config.setChannel(configDto.getChannel());
        config.setStatus(configDto.getStatus());
        config.setChannelConfig(configDto.getChannelConfig());
        // 创建并启用
        if (ChannelConfigStatus.START_USING == config.getStatus()) {
            config.setStartDate(LocalDateTime.now());
        }

        // 入库
        channelConfigDao.insert(config);

        return BeanConverter.covert(config, ChannelConfigDto.class);
    }

    @Override
    public ChannelConfigDto updateChannelConfig(ChannelConfigDto configDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(configDto.getAppId(), configDto.getChannel());
        if (config == null) {
            throw BusinessException.create(ChannelError.CHANNEL_CONFIG_NOT_EXISTS);
        }

        config.setChannelConfig(configDto.getChannelConfig());
        config.setStatus(configDto.getStatus());

        // 校验配置
        Map<String, Object> payConfig = new HashMap<>(configDto.getChannelConfig());
        ParamValidator.validate(BeanConverter.mapToBean(payConfig, configDto.getChannel().getConfigClass()));

        if (ChannelConfigStatus.START_USING == configDto.getStatus()) {
            config.setStartDate(LocalDateTime.now());
        } else if (ChannelConfigStatus.STOP_USING == configDto.getStatus()) {
            config.setStopDate(LocalDateTime.now());
        }

        channelConfigDao.update(config);

        return BeanConverter.covert(config, ChannelConfigDto.class);
    }

    @Override
    public Boolean deleteChannelConfig(Integer appId, ChannelType channelType) {
        return channelConfigDao.delete(appId, channelType) > 0;
    }


}
