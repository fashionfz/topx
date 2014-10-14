package common;

/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-10-31
 */
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import mobile.service.core.AccessStatisticsService;
import mobile.util.MobileUtil;
import models.RememberMeInfo;
import models.service.CurrentUserFilter;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Logger.ALogger;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Results;
import play.mvc.SimpleResult;
import system.filter.SecurityFilter;
import system.filter.SecurityUtils;
import system.models.OperateLog;
import utils.TimeZoneUtils;
import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;
import ext.schedule.ScheduleCenter;
import ext.sns.config.ConfigManager;
import ext.usercenter.UserAuthService;
import ext.usercenter.UserAuthURLFilter;
import ext.usercenter.UserCenterException;

/**
 * @ClassName: Global
 * @Description: 全局设置
 * @date 2013-10-31 下午3:51:52
 * @author ShenTeng
 * 
 */
public class Global extends GlobalSettings {
	
    private static final ALogger LOGGER = Logger.of(Global.class);

    public static String payCenterUrl;
    
    public static String keyWordCountUrl;
    
    public static String mobileDownloadUrl = "/wap";

    private static String userCenterExceptionRedirectUrl;
    
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] { GzipFilter.class };
    }

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        UserAuthURLFilter.init();
        TimeZoneUtils.load("timezone.yml");
        ConfigManager.loadConfig();
        ScheduleCenter.emailSchedule();
        AccessStatisticsService.startStatisticsThread();
        play.cache.Cache.remove(Constants.CACHE_EXPERT_TOPS);
        play.cache.Cache.remove(Constants.CACHE_COUNTRY);
        play.cache.Cache.remove(Constants.CACHE_INDUSTRY);
        payCenterUrl = ConfigFactory.getString("payCenter.client.serverUrl");
        userCenterExceptionRedirectUrl = ConfigFactory.getString("userCenter.exception.redirectUrl"); 
        keyWordCountUrl = ConfigFactory.getString("keyWordCount.client.serverUrl");
        
        //初始化权限数据
        SecurityUtils.initAuthData();
        //初始化权限验证 add by 2014-08-26
        SecurityUtils.initAuth();
        SecurityUtils.initNotAuth();
    }
    
    @Override
    public void onStop(Application app) {
        super.onStop(app);
        AccessStatisticsService.forceWriteStatistics();
    }

    @Override
    public Action<?> onRequest(Request request, Method actionMethod) {

        return new Action.Simple() {
            @Override
            public Promise<SimpleResult> call(Context ctx) throws Throwable {
                Session session = ctx.session();
                Request request = ctx.request();
                Response response = ctx.response();
                StringBuffer strbuff = new StringBuffer("/requesttime:")
                	.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()))
                	.append("    /ip:")
                	.append(request.remoteAddress())
                	.append("    /uri:")
                	.append(request.uri());
                LOGGER.info(strbuff.toString());

                if (!request.path().equals(mobileDownloadUrl)
                		&& !request.path().startsWith("/user/auth")
                		&& !MobileUtil.isMobileUrlPrefix(request.path()) 
                		&& MobileUtil.JudgeIsMoblie(request)
                		&& request.cookie("accessPc") == null){
                	return Promise.pure(Results.redirect(mobileDownloadUrl));
                }
                
               
                
                // 过滤无效的移动端url
                if (MobileUtil.isMobileUrlPrefix(request.path())
                        && !MobileUtil.isMobileUrlPrefixAndDevice(request.path())) {
                    return onHandlerNotFound(request);
                }
                
                // 统计移动端接口访问
                AccessStatisticsService.recordAccess(request.path());
                
                // --------刷新登录信息， 必须先执行---------
                if (Logger.isDebugEnabled()) {
                    LOGGER.debug("refreshLoginInfo - session(before call controller):" + session.toString());
                    LOGGER.debug("refreshLoginInfo - is user login(before call controller): "
                            + UserAuthService.isLogin(session));
                }
                UserAuthService.refreshLoginInfo(request, response, session);
                // ---------------------------------
                // 记住我自动登录
                RememberMeInfo.rememberMeAutoLogin(request, response, session);
                SimpleResult authResult = UserAuthURLFilter.filter(session, request);
                if (null != authResult) {
                    return Promise.pure(authResult);
                }
                
                SimpleResult currentUserFilterResult = CurrentUserFilter.filter(session, request);
                if (null != currentUserFilterResult) {
                    return Promise.pure(currentUserFilterResult);
                }
                
                //权限验证 add by 2014-08-26
        		SimpleResult res = SecurityFilter.filter(session,request);
        		if(null != res){
                    OperateLog log = SecurityFilter.optLog.get();
                    if(log!=null){
                    	OperateLog.save(log);
                    }
        			return Promise.pure(res);
        		}
                
                Promise<SimpleResult> call = null;
                try {
                    call = delegate.call(ctx);
                    OperateLog log = SecurityFilter.optLog.get();
                    if(log!=null){
                    	OperateLog.save(log);
                    }
                } catch (UserCenterException e) {
                    LOGGER.error("用户中心异常", e);
                    UserAuthService.removeTokenInSession(session);
                    if (MobileUtil.isMobileUrlPrefix(request.path())) {
                        ObjectNodeResult objectNodeResult = new ObjectNodeResult();
                        objectNodeResult.error("系统错误", "100001");
                        SimpleResult result = Results.ok(objectNodeResult.getObjectNode());
                        return Promise.pure(result);
                    } else {
                        return Promise.pure(Results.redirect(userCenterExceptionRedirectUrl));
                    }
                }catch(Exception e){
                	OperateLog log = SecurityFilter.optLog.get();
                	if(log!=null){
                    	log.setResult(false);
                    	log.setDescrible(e.toString());
                    	OperateLog.save(log);
                	}
                	throw e;
                }
                
                return call;
            }

        };
    }

    @Override
    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        if (MobileUtil.isMobileUrlPrefix(request.path())) {
            ObjectNodeResult objectNodeResult = new ObjectNodeResult();
            objectNodeResult.error("请求资源不存在", "100004");
            SimpleResult result = Results.ok(objectNodeResult.getObjectNode());
            return Promise.pure(result);
        } else {
            SimpleResult result = Results.ok(views.html.common.pagenotfound.render());
            return Promise.pure(result);
        }
    }

    @Override
    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        LOGGER.error("global on error.", t);
        if (MobileUtil.isMobileUrlPrefix(request.path())) {
            ObjectNodeResult objectNodeResult = new ObjectNodeResult();
            objectNodeResult.error("系统错误", "100001");
            SimpleResult result = Results.ok(objectNodeResult.getObjectNode());
            return Promise.pure(result);
        } else {
            SimpleResult result = Results.ok(views.html.common.error.render(null, null, null));
            return Promise.pure(result);
        }
    }

    @Override
    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
        if (MobileUtil.isMobileUrlPrefix(request.path())) {
            ObjectNodeResult objectNodeResult = new ObjectNodeResult();
            objectNodeResult.error("BadRequest:" + error, "100001");
            SimpleResult result = Results.ok(objectNodeResult.getObjectNode());
            return Promise.pure(result);
        } else {
            return super.onBadRequest(request, error);
        }
    }
    
    

}
