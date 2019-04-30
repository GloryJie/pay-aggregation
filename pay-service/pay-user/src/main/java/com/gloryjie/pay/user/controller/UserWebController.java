/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.user.controller
 *   Date Created: 2019/3/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.user.controller;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.user.dto.UserInfoDto;
import com.gloryjie.pay.user.enums.UserType;
import com.gloryjie.pay.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Response<UserInfoDto> getCurrentUserInfo() {
        return Response.success(getCurrentUser());
    }

    /**
     * 添加平台用户
     *
     * @param infoDto
     * @return
     */
    @PostMapping("/platform")
    public Response<UserInfoDto> addPlatformUser(@RequestBody @Valid UserInfoDto infoDto) {
        infoDto.setType(UserType.PLATFORM_USER);
        infoDto.setCreatedUserNo(getCurrentUser().getCreatedUserNo());
        return Response.success(userService.addUser(infoDto));
    }

    /**
     * 获取平台用户列表
     * @param startPage
     * @param pageSize
     * @return
     */
    @GetMapping("/platform")
    public Response<PageInfo<UserInfoDto>> getPlatformUserInfoList(@RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return Response.success(userService.listAllUserByType(UserType.PLATFORM_USER, null, startPage, pageSize));
    }


    /**
     * 添加子商户负责人
     *
     * @param infoDto
     * @return
     */
    @PostMapping("/sub_merchant")
    public Response<UserInfoDto> addSubMerchantUser(@RequestBody @Valid UserInfoDto infoDto) {
        infoDto.setType(UserType.SUB_MERCHANT_USER);
        infoDto.setCreatedUserNo(getCurrentUser().getCreatedUserNo());
        return Response.success(userService.addUser(infoDto));
    }

    /**
     * 获取子商户用户列表
     * @param startPage
     * @param pageSize
     * @param appId
     * @return
     */
    @GetMapping("/sub_merchant")
    public Response<PageInfo<UserInfoDto>> getSubMerchantUserList(@RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                  @RequestParam(value = "appId")Integer appId) {
        return Response.success(userService.listAllUserByType(UserType.SUB_MERCHANT_USER, appId, startPage, pageSize));
    }


    private UserInfoDto getCurrentUser() {
        return (UserInfoDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
