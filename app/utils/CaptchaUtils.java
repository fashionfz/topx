/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;

import common.Constants;

/**
 * @author ZhouChun
 * @ClassName: CaptchaUtil
 * @Description: 验证码生成器
 * @date 13-10-22 下午4:23
 */
public class CaptchaUtils {
    private static final ALogger LOGGER = Logger.of(CaptchaUtils.class);

    private static final int WIDTH = 70;
    private static final int HEIGHT = 23;

    private static final List<Color> COLORS = new ArrayList<>(2);
    private static final List<Font> FONTS = new ArrayList<>(3);

    private static final int DEFAULT_LENGTH = 4;

    private static final char[] DEFAULT_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w',
            'x', 'y', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'W', 'X', 'Y', '2',
            '3', '4', '5', '6', '7', '8' };

    private static DefaultTextProducer textProducer = null;

    private static DefaultWordRenderer wordRenderer = null;

    static {
        COLORS.add(Color.BLACK);
        FONTS.add(new Font("Arial", Font.ITALIC, 20));
        textProducer = new DefaultTextProducer(DEFAULT_LENGTH, DEFAULT_CHARS);
        wordRenderer = new DefaultWordRenderer(COLORS, FONTS);
    }

    /**
     * 生成验证码信息
     * 
     * @return
     */
    public static ByteArrayOutputStream gennerateCaptcha(String key) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Captcha captcha = new Captcha.Builder(WIDTH, HEIGHT).addText(textProducer, wordRenderer)
                    .addBackground(new FlatColorBackgroundProducer(Color.WHITE)).build();
            Cache.set(Constants.CACHE_CAPTCHA + key, captcha.getAnswer(), 10 * 60);

            LOGGER.debug("存入验证码key:" + Constants.CACHE_CAPTCHA + key + "  value:" + captcha.getAnswer());

            BufferedImage image = captcha.getImage();
            ImageIO.write(image, "jpeg", out);
        } catch (Exception e) {
            LOGGER.error("生成验证码错误", e);
        }
        return out;
    }

    /**
     * 验证验证码是否正确
     * 
     * @param key
     * @param captcha
     * @return
     */
    public static boolean validateCaptcha(String key, String captcha) {
        String answer = (String) Cache.get(Constants.CACHE_CAPTCHA + key);

        LOGGER.debug("取出验证码key:" + Constants.CACHE_CAPTCHA + key + "value:" + answer);

        if (StringUtils.isBlank(captcha)) {
            LOGGER.error("验证码为空");
            return false;
        }
        if (StringUtils.isBlank(answer)) {
            LOGGER.error("验证码未缓存或者失败");
            return false;
        }
        return captcha.equalsIgnoreCase(answer);
    }

    /**
     * 作废验证码
     */
    public static void invalidCaptcha(String key) {
        Cache.remove(Constants.CACHE_CAPTCHA + key);
    }

}
