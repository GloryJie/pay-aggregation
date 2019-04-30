/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.service
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.service;

import com.gloryjie.pay.app.constant.AppConstant;
import com.gloryjie.pay.app.dao.AppDao;
import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.dto.AppUpdateParam;
import com.gloryjie.pay.app.enums.AppStatus;
import com.gloryjie.pay.app.enums.AppType;
import com.gloryjie.pay.app.error.AppError;
import com.gloryjie.pay.app.model.App;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.cipher.Rsa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.gloryjie.pay.app.constant.AppConstant.MAX_LEVEL;

/**
 * @author Jie
 * @since
 */
@Service
public class AppServiceImpl implements AppService {


    @Autowired
    private AppDao appDao;

    @Override
    public AppDto createMasterApp(String name, String desc, Long createUserNo, Long responseUserNo) {
        // 不允许同名
        App app = appDao.getMasterByName(name);
        if (app != null) {
            throw BusinessException.create(AppError.SAME_APP_NAME_ALREADY_EXISTS);
        }
        app = new App();
        app.setAppId(generateMasterAppId());
        app.setName(name);
        app.setDescription(desc);
        app.setType(AppType.MASTER);
        app.setStatus(AppStatus.START);
        app.setUsePlatformConfig(false);
        app.setCreateUserNo(createUserNo);
        app.setResponsibleUserNo(responseUserNo);

        Map<String, String> keyPair = Rsa.generateRsaKeyPair();
        app.setNotifyPrivateKey(keyPair.get(Rsa.PRIVATE_KEY));
        app.setNotifyPublicKey(keyPair.get(Rsa.PUBLIC_KEY));
        app.setLevel(1);

        appDao.insert(app);

        return BeanConverter.covert(app, AppDto.class);
    }

    @Override
    public AppDto createSubApp(String name, String desc, Integer parentAppId, Long createUserNo, Long responseUserNo) {
        // 父级应用的存在性
        App app = appDao.getByAppId(parentAppId);
        if (app == null) {
            throw ExternalException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "父应用不存在");
        }
        // 应用树内名字不可重复, 则在树的id范围内查找该名字
        Integer rootAppId = computeRootAppId(parentAppId);
        Integer maxAppId = computeTreeMaxAppId(parentAppId);
        app = appDao.getSubByName(name, rootAppId, maxAppId);
        if (app != null) {
            throw BusinessException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "应用名已存在");
        }

        app = new App();
        app.setAppId(generateSubAppId(parentAppId));
        app.setName(name);
        app.setDescription(desc);
        app.setParentAppId(parentAppId);
        app.setUsePlatformConfig(true);
        app.setLevel(computeSubAppLevel(app.getAppId()));
        app.setStatus(AppStatus.START);
        app.setType(AppType.SUBORDINATE);
        app.setCreateUserNo(createUserNo);
        app.setResponsibleUserNo(responseUserNo);
        // 子应用也需要独立的交易钥对
        Map<String, String> keyPair = Rsa.generateRsaKeyPair();
        app.setNotifyPrivateKey(keyPair.get(Rsa.PRIVATE_KEY));
        app.setNotifyPublicKey(keyPair.get(Rsa.PUBLIC_KEY));

        appDao.insert(app);
        return BeanConverter.covertIgnore(app, AppDto.class);
    }

    @Override
    public List<AppDto> queryMasterAppList() {
        // TODO: 2019/2/7 是否一次性全部获取待后续优化
        List<App> appList = appDao.getMasterAppList();
        return BeanConverter.batchCovertIgnore(appList, AppDto.class);
    }

    @Override
    public Boolean updateAppInfo(AppUpdateParam updateParam) {
        // 同名检查
        if (StringUtils.isNotBlank(updateParam.getName()) && appDao.getMasterByName(updateParam.getName()) != null) {
            throw BusinessException.create(AppError.SAME_APP_NAME_ALREADY_EXISTS);
        }
        App app = BeanConverter.covert(updateParam, App.class);
        return appDao.update(app) > 0;
    }

    @Override
    public AppDto getSingleAppInfo(Integer appId) {
        App app = appDao.getByAppId(appId);
        return app == null ? null : BeanConverter.covert(app, AppDto.class);
    }

    @Override
    public List<AppDto> getAppTreeAllNode(Integer rootAppId) {
        List<App> appList = appDao.getAppTree(computeRootAppId(rootAppId), computeTreeMaxAppId(rootAppId));
        return BeanConverter.batchCovertIgnore(appList, AppDto.class);
    }

    /**
     * 产生平台app的应用id
     *
     * @return
     */
    private Integer generateMasterAppId() {
        Integer maxAppId = appDao.getMasterMaxAppId();
        // 为null，则表示一个app都没有
        if (maxAppId == null) {
            return AppConstant.FIRST_APP_ID;
        } else {
            return maxAppId + AppConstant.MASTER_APP_STEP;
        }
    }

    /**
     * 产生子应用
     *
     * @param parentAppID
     * @return
     */
    private Integer generateSubAppId(Integer parentAppID) {
        // 获取左数第四位, 即层级深度
        int pDepth = computeSubAppLevel(parentAppID);
        if (pDepth < 1 || pDepth >= MAX_LEVEL) {
            throw BusinessException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "已不能创建更深层次的应用");
        }
        // 下一层次的最小节点id, 如 10120000
        int min = computeNextLevelMinAppId(parentAppID);
        // 下一层次的最大id, 如 10129999
        int max = computeNextLevelMaxAppId(parentAppID);
        // 获取下一层次的最大appId + 1即为新的appId, 若不存在则为 min + 1, 如 10120001, 代表第二层的第一个节点
        Integer maxAppId = appDao.getSubMaxAppId(min, max);
        return maxAppId == null ? min + 1 : maxAppId + 1;
    }

    /**
     * 计算app深度
     *
     * @param appId
     * @return
     */
    private Integer computeSubAppLevel(Integer appId) {
        return appId / 10000 % 10;
    }

    /**
     * 计算下一层次的最小 appid
     *
     * @param parentAppId
     * @return
     */
    private Integer computeNextLevelMinAppId(Integer parentAppId) {
        // 下一层次的最小节点id, 如 parentAppId = 10110000, 则则返回 10120000
        return (parentAppId / 10000 + 1) * 10000;
    }

    /**
     * 计算下一层次的最大 appid
     *
     * @param parentAppId
     * @return
     */
    private Integer computeNextLevelMaxAppId(Integer parentAppId) {
        // 下一层次的最大节点id, 如 parentAppId = 10110000, 则则返回 10129999
        return computeNextLevelMinAppId(parentAppId) + AppConstant.MAX_SUB_APP_ID_STEP;
    }

    /**
     * 计算应用树内允许的最大id
     *
     * @param appId
     * @return
     */
    private Integer computeTreeMaxAppId(Integer appId) {
        return (appId / 100000 * 100000) + 99999;
    }

    /**
     * 根据传递的id计算出当前节点在树的根id
     *
     * @param appId
     * @return
     */
    private Integer computeRootAppId(Integer appId) {
        // 获取根节点appId, 如节点id = 10130012, 则rootAppId = 10110000
        return (appId / 100000 * 10 + 1) * 10000;
    }


}
