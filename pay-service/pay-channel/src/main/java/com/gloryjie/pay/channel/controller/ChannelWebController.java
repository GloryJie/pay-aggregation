/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.controller
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.controller;

import com.gloryjie.pay.channel.dto.ChannelConfigDto;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.service.ChannelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jie
 * @since 0.1
 */
@RestController
@RequestMapping("/web/app")
public class ChannelWebController {

    @Autowired
    private ChannelConfigService channelConfigService;

    @GetMapping("/{appId}/channel")
    public Map<ChannelType, ChannelConfigDto> getAlreadyConfigChannel(@PathVariable("appId") Integer appId) {
        List<ChannelConfigDto> configDtoList = channelConfigService.getChannelConfig(appId);
        return configDtoList.stream().collect(Collectors.toMap(ChannelConfigDto::getChannel, item -> item));
    }

    @PostMapping("/{appId}/channel")
    public ChannelConfigDto addNewConfig(@PathVariable("appId") Integer appId, @Valid @RequestBody ChannelConfigDto configDto) {
        configDto.setAppId(appId);
        return channelConfigService.addNewChannelConfig(configDto);
    }

    @PutMapping("/{appId}/channel")
    public ChannelConfigDto updateConfig(@PathVariable("appId") Integer appId, @Valid @RequestBody ChannelConfigDto configDto) {
        configDto.setAppId(appId);
        return channelConfigService.updateChannelConfig(configDto);
    }

    @DeleteMapping("/{appId}/channel/{channel}")
    public Boolean deleteConfig(@PathVariable("appId") Integer appId, @PathVariable("channel") ChannelType channelType) {
        return channelConfigService.deleteChannelConfig(appId, channelType);
    }
}
