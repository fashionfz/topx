/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-11-12
 */
package models.service.reminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.User;
import models.service.LoginUserCache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.db.jpa.JPA;
import play.libs.Json;
import play.mvc.Http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * @ClassName: RemindService
 * @Description: 提醒服务
 * @date 2013-11-12 下午5:39:15
 * @author ShenTeng
 * 
 */
public class RemindService {

    /**
     * 触发安全提醒
     * 
     * @param item 必须。配置项
     * @param oldUser 必须。安全提醒的大部分操作涉及User对象的修改，该参数为修改前的对象。
     * @param newUser 必须。安全提醒的大部分操作涉及User对象的修改，该参数为修改前的对象。如果不涉及User对象的修改，则oldUser和newUser可以相同。
     * @param params 自定义参数，无自定义参数传入null。
     */
    public static void remind(Item item, User oldUser, User newUser, Map<String, Object> params) {
        if (null == item || null == oldUser || null == newUser) {
            throw new IllegalArgumentException("参数为空。 item is null:" + (null == item) + ", oldUser is null:"
                    + (null == oldUser) + ", newUser is null:" + (null == newUser));
        }

        Map<Item, List<Option>> configMap = getCfgMapWithDefault(oldUser, new Item[] { item });
        if (configMap.get(item) == null || configMap.get(item).isEmpty()) {
            return;
        }

        List<Option> options = configMap.get(item);
        for (Option option : options) {
            RemindSender.remind(item, option, oldUser, newUser, params);
        }
    }

    /**
     * 验证提醒设置是否正确
     * 
     * @param cfg 设置json
     * @param items 设置里面包含的item
     * 
     * @return true:正确, false:错误
     */
    public static boolean verifyCfg(JsonNode cfg, Item[] items) {
        if (null == cfg) {
            return false;
        }

        boolean isSuccess = true;
        int count = 0;
        for (Item item : items) {
            if (!item.isEnable) {
                continue;
            }

            String itemVal = item.getVal();

            // 检查每个设置项均被设置
            if (cfg.hasNonNull(itemVal) && cfg.get(itemVal).isArray()) {
                JsonNode optionsNode = cfg.get(itemVal);
                Iterator<JsonNode> optionsIt = optionsNode.elements();

                // 检查每个设置项的选项值均是有效的
                Set<String> optionSet = new HashSet<>();
                while (optionsIt.hasNext()) {
                    String optionString = optionsIt.next().asText();

                    if (!Option.isContainVal(optionString)) {
                        isSuccess = false;
                        break;
                    }

                    optionSet.add(optionString);
                }
                if (!isSuccess) {
                    break;
                }

                // 检查每个条目的选项值不重复
                if (optionSet.size() != optionsNode.size()) {
                    isSuccess = false;
                    break;
                }

                count++;
            } else {
                isSuccess = false;
                break;
            }
        }

        // 检查没有多余的配置项
        if (cfg.size() > count) {
            isSuccess = false;
        }

        return isSuccess;
    }

    /**
     * 保存提醒设置
     * 
     * @param session Http session
     * @param user 当前登录用户
     * @param cfg 设置Json
     * @param items 设置包含的items
     * 
     * @return true - 保存成功, false - 保存失败
     */
    public static boolean saveCfgJson(Http.Session session, User user, JsonNode cfg, Item[] cfgItems) {
        boolean isValidCfg = verifyCfg(cfg, cfgItems);

        if (!isValidCfg) {
            return false;
        }

        JsonNode oldUserCfg = null;
        if (StringUtils.isNotBlank(user.safetyReminderConfig)) {
            oldUserCfg = Json.parse(user.safetyReminderConfig);
        }
        ObjectNode newUserCfg = Json.newObject();
        for (Item item : Item.values()) {
            String val = item.getVal();

            if (ArrayUtils.contains(cfgItems, item)) {
                newUserCfg.set(val, cfg.get(val));
            } else {
                if (null != oldUserCfg && oldUserCfg.hasNonNull(val)) {
                    newUserCfg.set(val, oldUserCfg.get(val));
                }
            }
        }

        JPA.em().createQuery("update User set safetyReminderConfig=:newCfg where id=:id")
                .setParameter("newCfg", newUserCfg.toString()).setParameter("id", user.id).executeUpdate();

        LoginUserCache.refreshBySession(session);

        return true;
    }

    /**
     * 获取Json格式的提醒设置,如果用户未设置过该项提醒,使用默认值
     * 
     * @param user 当前登录用户
     * @param items 设置包含的items
     * 
     * @return 提醒设置Json
     */
    public static JsonNode getCfgJsonWithDefault(User user, Item[] items) {
        JsonNode parse = null;
        String cfg = user.safetyReminderConfig;
        if (StringUtils.isNotBlank(cfg)) {
            parse = Json.parse(cfg);
        }
        ObjectNode result = Json.newObject();

        // 填充新增设置项，去掉被禁用设置项
        for (Item item : items) {
            if (!item.isEnable) {
                continue;
            }

            String itemVal = item.getVal();
            if (null == parse || !parse.hasNonNull(itemVal)) {
                ArrayNode options = Json.newObject().arrayNode();
                if (ArrayUtils.isNotEmpty(item.getDefaultOptions())) {
                    for (Option option : item.getDefaultOptions()) {
                        options.add(option.getVal());
                    }
                }
                result.set(itemVal, options);
            } else {
                result.set(itemVal, parse.get(itemVal));
            }
        }

        return result;
    }

    /**
     * 获取Map格式的安全提醒设置,如果用户未设置过该项提醒,使用默认值
     * 
     * @param user 当前登录用户
     * @param items 设置包含的items
     * 
     * @return 提醒设置Map
     */
    public static Map<Item, List<Option>> getCfgMapWithDefault(User user, Item[] items) {
        JsonNode itemsNode = getCfgJsonWithDefault(user, items);
        if (null == itemsNode || itemsNode.size() <= 0) {
            return new HashMap<>(0);
        }

        Map<Item, List<Option>> cfgMap = new HashMap<>();
        Iterator<Entry<String, JsonNode>> itemsNodeFields = itemsNode.fields();
        while (itemsNodeFields.hasNext()) {
            Entry<String, JsonNode> itemsNodeField = itemsNodeFields.next();
            Iterator<JsonNode> optionsIt = itemsNodeField.getValue().elements();
            List<Option> optionList = new ArrayList<>();
            while (optionsIt.hasNext()) {
                optionList.add(Option.getByVal(optionsIt.next().asText()));
            }

            cfgMap.put(Item.getByVal(itemsNodeField.getKey()), optionList);
        }

        return cfgMap;
    }

    /**
     * 判断提醒设置项是否没有任何选项
     * 
     * @param user 当前登录用户
     * @param items 设置包含的items
     * 
     * @return true - 各提醒设置项都没有选项, false - 某个提醒设置项有选项
     */
    public static boolean isEmpty(User user, Item[] items) {
        String cfg = user.safetyReminderConfig;

        if (StringUtils.isBlank(cfg)) {
            return true;
        }

        boolean isEmpty = true;
        JsonNode options = Json.parse(cfg);

        for (Item item : items) {
            if (item.isEnable && options.hasNonNull(item.getVal()) && options.get(item.getVal()).size() > 0) {
                isEmpty = false;
                break;
            }
        }

        return isEmpty;
    }

}
