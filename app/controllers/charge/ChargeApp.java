/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.charge;

import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.charge.chargefromalipay;
import views.html.charge.chargefrombank;
import views.html.charge.chargefromcredit;
import views.html.charge.chargetransfer;
import views.html.charge.chargewithdrawbankchoice;
import views.html.charge.chargewithdrawtocard;
import controllers.base.BaseApp;

/**
 * @author ZhouChun
 * @ClassName: ChargeApp
 * @Description: 充值中心
 * @date 13-11-5 上午10:23
 */
public class ChargeApp extends BaseApp {

    /**
     * 充值中心
     * @return
     */
    @Transactional
    public static Result chargeCenter() {
        return ok(views.html.charge.chargecenter.render());
    }

    /**
     * 充值
     * @return
     */
    public static Result chargeChoice() {
        return ok(views.html.charge.chargechoice.render());
    }

    /**
     * 支付宝付款
     * @return
     */
    public static Result fromAlipay() {
        return ok(chargefromalipay.render());
    }

    /**
     * 网银充值
     * @return
     */
    public static Result fromBank() {
        return ok(chargefrombank.render());
    }

    /**
     * 信用卡充值
     * @return
     */
    public static Result fromCredit() {
        return ok(chargefromcredit.render());
    }


    /**
     * 提现选择
     * @return
     */
    public static Result withdrawChoice() {
        return ok(chargewithdrawbankchoice.render());
    }

    /**
     * 提现到支付宝
     * @return
     */
    public static Result withdrawToAlipay() {
        return ok(views.html.charge.chargewithdrawtoalipay.render());
    }

    /**
     * 提现到网银
     * @return
     */
    public static Result withdrawToBank() {
        return ok(chargewithdrawtocard.render());
    }

    /**
     * 转账
     * @return
     */
    public static Result transfer() {
        return ok(chargetransfer.render());
    }

    /**
     * 提现选择
     * @return
     */
    public static Result withdraw() {
        String t = request().getQueryString("t");
        if("2".equals(t)) {
            return redirect(routes.ChargeApp.withdrawToBank());
        } else {
            return redirect(routes.ChargeApp.withdrawToAlipay());
        }
    }

    @Transactional
    public static Result pay() {
        String t = request().getQueryString("t");
        if("2".equals(t)) {
            return redirect(routes.ChargeApp.fromBank());
        } else if("3".equals(t)) {
            return redirect(routes.ChargeApp.fromCredit());
        } else {
            return redirect(routes.ChargeApp.fromAlipay());
        }
    }
    
    @Transactional(readOnly = true)
    public static Result iframecrossdomain() {
    	return ok(views.html.charge.iframecrossdomain.render());
    }

}
