/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-1
 */
package ext.usercenter;

import static play.Play.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;
import play.mvc.With;

/**
 * 
 * 
 * @ClassName: UserAuth
 * @Description: 用户鉴权标签
 * @date 2013-11-1 下午6:56:35
 * @author ShenTeng
 * 
 */
public class UserAuth {

    private static final String DEFAULT_NO_LOGIN_REDIRECT_URL = application().configuration().getString("userCenter.auth.defaultNoLoginRedirectUrl");
    
    private static final String DEFAULT_REDIRECT_URL_TAG = "_DEFAULT_REDIRECT_URL_";
    
    public enum Type {
        Redirect, Content
    }

    @With(AuthenticatedAction.class)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Authenticated {
        Type type() default Type.Redirect;

        String value() default DEFAULT_REDIRECT_URL_TAG;
    }

    public static class AuthenticatedAction extends Action<Authenticated> {

        public F.Promise<SimpleResult> call(Context ctx) {
            try {

                Type type = configuration.type();
                String value = configuration.value();
                value = DEFAULT_REDIRECT_URL_TAG.equals(value) ? DEFAULT_NO_LOGIN_REDIRECT_URL : value;

                boolean login = UserAuthService.isLogin(ctx.session());

                if (!login) {
                    switch (type) {
                    case Redirect:
                    default:
                        return F.Promise.pure((SimpleResult) redirect(value));
                    case Content:
                        return F.Promise.pure((SimpleResult) ok(value));
                    }
                } else {
                    return delegate.call(ctx);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

}
