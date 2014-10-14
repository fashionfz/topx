/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-5
 */
package ext.paycenter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;

/**
 * 
 * 
 * @ClassName: PayService
 * @Description: 付款中心相关的服务
 * @date 2013-12-5 下午6:49:18
 * @author ShenTeng
 * 
 */
public class PayService {

    private static final ALogger LOGGER = Logger.of(PayService.class);

    /**
     * 禁止实例化
     */
    private PayService() {
    }

    /**
     * 根据token转账
     * 
     * @param token 登录用户token
     * @param email 收款方邮箱地址
     * @param payment 支付金额，精确到小数点后的两位
     * @param serveduration 通话时长
     * @param isIgnoreLocked 是否忽略账户锁定  true为无视
     * @return
     */
    public static TransferResult transferByToken(String token, String email, BigDecimal payment, Long serveduration,
            boolean isIgnoreLocked) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(email) || null == payment || null == serveduration) {
            throw new IllegalArgumentException("参数不能为空。token = " + token + ", email = " + email + ", payment = "
                    + payment + ", serveduration = " + serveduration);
        }

        String paymentString = payment.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        PCResult<Void> result = PCClient.transferax(token, email, paymentString, String.valueOf(serveduration),
                isIgnoreLocked);
        if (result.balanceNotEnough()) {
            return TransferResult.BALANCE_NOT_ENOUGH;
        } else if (result.isSuccess()) {
            return TransferResult.SUCCESS;
        } else if (result.noMatchData()) {
            return TransferResult.INVALID_TOKEN;
        } else if (result.accountLocked()) {
            return TransferResult.ACCOUNT_LOCKED;
        } else {
            LOGGER.error("call transferax error result = " + result);
        }

        return TransferResult.UNKNOWN_ERROR;
    }

    /**
     * 根据email转账
     * 
     * @param email 支付方邮箱
     * @param toEmail 收款方邮箱地址
     * @param payment 支付金额，精确到小数点后的两位
     * @param serveduration 通话时长
     * @param isIgnoreLocked 是否忽略账户锁定    true为无视
     * @return
     */
    public static TransferResult transferByEmail(String email, String toEmail, BigDecimal payment, Long serveduration,
            boolean isIgnoreLocked) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(toEmail) || null == payment || null == serveduration) {
            throw new IllegalArgumentException("参数不能为空。email = " + email + ", toEmail = " + toEmail + ", payment = "
                    + payment + ", serveduration = " + serveduration);
        }

        String paymentString = payment.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        PCResult<Void> result = PCClient.transferByEmail(email, toEmail, paymentString, String.valueOf(serveduration),
                isIgnoreLocked);
        if (result.balanceNotEnough()) {
            return TransferResult.BALANCE_NOT_ENOUGH;
        } else if (result.isSuccess()) {
            return TransferResult.SUCCESS;
        } else if (result.noMatchData()) {
            return TransferResult.INVALID_TOKEN;
        } else if (result.accountLocked()) {
            return TransferResult.ACCOUNT_LOCKED;
        } else {
            LOGGER.error("call transferByEmail error result = " + result);
        }

        return TransferResult.UNKNOWN_ERROR;
    }

    /**
     * 根据token获取账户余额
     * 
     * @param token
     * @return
     */
    public static GetBalanceResult getBalanceByToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("参数不能为空。 token = " + token);
        }
        PCResult<String> result = PCClient.getBalance(token);
        if (result.noMatchData()) {
            return new GetBalanceResult(GetBalanceResult.STATE.INVALID_TOKEN, null);
        } else if (result.isSuccess()) {
            return new GetBalanceResult(GetBalanceResult.STATE.SUCCESS, result.data);
        } else {
            LOGGER.error("call getBalance error result = " + result);
        }
        return new GetBalanceResult(GetBalanceResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 根据email获取账户余额
     * 
     * @param email 用户注册的email
     * @return
     */
    public static GetBalanceResult getBalanceByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("参数不能为空。 email = " + email);
        }

        PCResult<String> result = null;
        try {
            result = PCClient.getBalanceBySystem(email);
        } catch (PayCenterException e) {
            LOGGER.error("call getBalanceBySystem error.", e);
            return new GetBalanceResult(GetBalanceResult.STATE.UNKNOWN_ERROR, null);
        }

        if (result.noMatchData()) {
            return new GetBalanceResult(GetBalanceResult.STATE.INVALID_TOKEN, null);
        } else if (result.isSuccess()) {
            return new GetBalanceResult(GetBalanceResult.STATE.SUCCESS, result.data);
        } else {
            LOGGER.error("call getBalanceBySystem error result = " + result);
        }

        return new GetBalanceResult(GetBalanceResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 锁定账户
     * 
     * @param email 用户注册的email
     */
    public static LockAccountResult lockAccount(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("参数不能为空。 email = " + email);
        }

        PCResult<String> result = PCClient.lock(email);

        if (result.isSuccess()) {
            return new LockAccountResult(LockAccountResult.STATE.SUCCESS, result.data);
        } else {
            LOGGER.error("call lock error result = " + result);
        }

        return new LockAccountResult(LockAccountResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 解锁账户
     * 
     * @param email 用户注册的email
     */
    public static UNLockAccountResult unlockAccount(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("参数不能为空。 email = " + email);
        }

        PCResult<String> result = PCClient.unlock(email);

        if (result.isSuccess()) {
            return new UNLockAccountResult(UNLockAccountResult.STATE.SUCCESS, result.data);
        } else {
            LOGGER.error("call unlock error result = " + result);
        }

        return new UNLockAccountResult(UNLockAccountResult.STATE.UNKNOWN_ERROR, null);
    }

    /**
     * 获取账户锁定状态
     * 
     * @param email 用户注册的email
     */
    public static GetLockStatusResult getLockStatus(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("参数不能为空。 email = " + email);
        }

        PCResult<Boolean> result = PCClient.getLockStatus(email);

        if (result.isSuccess()) {
            return result.data ? GetLockStatusResult.LOCKED : GetLockStatusResult.UNLOCKED;
        } else {
            LOGGER.error("call unlock error result = " + result);
        }

        return GetLockStatusResult.UNKNOWN_ERROR;
    }

    public enum TransferResult {
        /* 成功 */
        SUCCESS,
        /* 余额不足 */
        BALANCE_NOT_ENOUGH,
        /* token无效 */
        INVALID_TOKEN,
        /* 账户被锁定 */
        ACCOUNT_LOCKED, UNKNOWN_ERROR
    }

    public static class GetBalanceResult {

        public STATE state;

        /**
         * 账户余额，小数点后两位
         */
        public BigDecimal balance;

        private GetBalanceResult(STATE state, String balanceString) {
            this.state = state;
            if (StringUtils.isBlank(balanceString)) {
                this.balance = null;
            } else {
                this.balance = new BigDecimal(balanceString).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }

        public enum STATE {
            /* 成功 */
            SUCCESS,
            /* token无效 */
            INVALID_TOKEN, UNKNOWN_ERROR
        }
    }

    public static class LockAccountResult {

        public STATE state;

        /**
         * 账户余额，小数点后两位
         */
        public BigDecimal balance;

        private LockAccountResult(STATE state, String balanceString) {
            this.state = state;
            if (StringUtils.isBlank(balanceString)) {
                this.balance = null;
            } else {
                this.balance = new BigDecimal(balanceString).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }

        public enum STATE {
            /* 成功 */
            SUCCESS, UNKNOWN_ERROR
        }
    }

    public static class UNLockAccountResult {

        public STATE state;

        /**
         * 账户余额，小数点后两位
         */
        public BigDecimal balance;

        private UNLockAccountResult(STATE state, String balanceString) {
            this.state = state;
            if (StringUtils.isBlank(balanceString)) {
                this.balance = null;
            } else {
                this.balance = new BigDecimal(balanceString).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }

        public enum STATE {
            /* 成功 */
            SUCCESS, UNKNOWN_ERROR
        }
    }

    public static enum GetLockStatusResult {
        LOCKED, UNLOCKED, UNKNOWN_ERROR
    }

}
