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

import com.gloryjie.pay.app.dao.AppDao;
import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.dto.AppUpdateParam;
import com.gloryjie.pay.app.enums.AppStatus;
import com.gloryjie.pay.app.enums.AppType;
import com.gloryjie.pay.app.error.AppError;
import com.gloryjie.pay.app.model.App;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.util.BeanConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jie
 * @since
 */
@Service
public class AppServiceImpl implements AppService {

    /**
     * 第一个APPID
     * 前三位为master应用标识，101为第一个
     * 第四位为层次，1标识第一层
     * 后四位子应用id, 0000表示没有一个子应用
     */
    private static final int FIRST_APP_ID = 10110000;

    /**
     * 创建master应用步长
     */
    private static final int MASTER_APP_STEP = 100000;

    @Autowired
    private AppDao appDao;

    @Override
    public AppDto createMasterApp(String name, String desc) {
        // 不允许同名
        App app = appDao.getByName(name);
        if (app != null) {
            throw BusinessException.create(AppError.SAME_APP_NAME_ALREADY_EXISTS);
        }
        app = new App();
        Integer maxAppId = appDao.getMaxAppId();
        // 为null，则表示一个app都没有
        if (maxAppId == null) {
            app.setAppId(FIRST_APP_ID);
        } else {
            app.setAppId(maxAppId + MASTER_APP_STEP);
        }
        app.setName(name);
        app.setDescription(desc);
        app.setType(AppType.MASTER);
        app.setStatus(AppStatus.START);
        app.setUsePlatformConfig(false);
        // TODO: 2019/2/7 需要生成一对公私钥对
        app.setNotifyPrivateKey("privateKey");
        app.setNotifyPublicKey("publicKey");
        app.setLevel(1);

        appDao.insert(app);

        return BeanConverter.covert(app, AppDto.class);
    }

    @Override
    public List<AppDto> queryMasterAppList() {
        // TODO: 2019/2/7 是否一次性全部获取待后续优化
        List<App> appList = appDao.getMasterAppList();
        return BeanConverter.batchCovert(appList, AppDto.class);
    }

    @Override
    public Boolean updateAppInfo(AppUpdateParam updateParam) {
        // 同名检查
        if (StringUtils.isNotBlank(updateParam.getName()) && appDao.getByName(updateParam.getName()) != null) {
            throw BusinessException.create(AppError.SAME_APP_NAME_ALREADY_EXISTS);
        }
        App app = BeanConverter.covert(updateParam, App.class);
        return appDao.update(app) > 0;
    }
}
