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

import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.channel.dto.CertificateDto;
import com.gloryjie.pay.channel.dto.ChannelConfigDto;
import com.gloryjie.pay.channel.enums.CertificateType;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.service.ChannelConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jie
 * @since 0.1
 */
@Slf4j
@RestController
@RequestMapping("/web/app")
public class ChannelWebController {

    @Autowired
    private ChannelConfigService channelConfigService;

    @GetMapping("/{appId}/channel")
    public Response<Map<ChannelType, ChannelConfigDto>> getAlreadyConfigChannel(@PathVariable("appId") Integer appId) {
        List<ChannelConfigDto> configDtoList = channelConfigService.getChannelConfig(appId);
        return Response.success(configDtoList.stream().collect(Collectors.toMap(ChannelConfigDto::getChannel, item -> item)));
    }

    @PostMapping("/{appId}/channel")
    public Response<ChannelConfigDto> addNewConfig(@PathVariable("appId") Integer appId, @Valid @RequestBody ChannelConfigDto configDto) {
        configDto.setAppId(appId);
        return Response.success(channelConfigService.addNewChannelConfig(configDto));
    }

    @PutMapping("/{appId}/channel")
    public Response<ChannelConfigDto> updateConfig(@PathVariable("appId") Integer appId, @Valid @RequestBody ChannelConfigDto configDto) {
        configDto.setAppId(appId);
        return Response.success(channelConfigService.updateChannelConfig(configDto));
    }

    @DeleteMapping("/{appId}/channel/{channel}")
    public Response<Boolean> deleteConfig(@PathVariable("appId") Integer appId, @PathVariable("channel") ChannelType channelType) {
        return Response.success(channelConfigService.deleteChannelConfig(appId, channelType));
    }


    @PostMapping("/cert")
    public Response<Boolean> uploadCert(@RequestParam("appId") Integer appId,
                                        @RequestParam("channel") ChannelType channelType,
                                        @RequestParam("certType") CertificateType certType,
                                        @RequestParam("file") MultipartFile file) {

        CertificateDto dto = new CertificateDto();
        dto.setAppId(appId);
        dto.setChannel(channelType);
        dto.setType(certType);
        // 文件类型
        if (!dto.getType().getFileSuffix().equals(file.getContentType())){
            throw ExternalException.create(ChannelError.CERT_FILE_TYPE_NOT_CORRECT);
        }
        if (file.isEmpty()){
            throw ExternalException.create(ChannelError.CERT_FILE_DATA_EMPTY);
        }

        try {
            dto.setCertData(file.getBytes());
        } catch (IOException e) {
            log.warn("read upload cert file fail", e);
            throw SystemException.create(ChannelError.READ_CERT_FILE_DATA_FAIL);
        }
        boolean result = channelConfigService.addNewChannelCert(dto);
        return Response.success(result);
    }
}
