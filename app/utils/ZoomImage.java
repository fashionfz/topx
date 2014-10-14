/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package utils;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import play.Logger;
import scalax.io.support.FileUtils;

/**
 * 一个实用的图像放大缩小程序
 *
 * @author YuLimin（代码来自这位），csdn上的某人
 *
 */
public class ZoomImage {
	
    private static Component component = new Canvas();
//    /**
//     * 测试用例
//     *
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        long t1 = System.currentTimeMillis();
//        ZoomImage zoomImage = new ZoomImage();
//        // 缩小四倍
//        zoomImage.createZoomSizeImage("C:/1.jpg", "C:/1_70.jpg", 70, 70);
//        System.out.println(System.currentTimeMillis() - t1);
//        // 放大四倍
//        // zoomImage.zoom("C:/",4,false);
//    }

//    public void createZoomSizeImage(String originalFileName, String targetFileName, double zoomRatio) throws IOException {
//        File file = new File(originalFileName);
//        BufferedImage image = ImageIO.read(file);
//        int width = new Double(image.getWidth(null) * zoomRatio).intValue();
//        int height = new Double(image.getHeight(null) * zoomRatio).intValue();
//        this.createZoomSizeImage(image, targetFileName, width, height);
//    }

    public void createZoomImageUnderMaxSize(String originalFileName, String targetFileName, int maxWidth, int maxHeight) throws IOException {
        File file = new File(originalFileName);
        BufferedImage image = ImageIO.read(file);
        if (image.getWidth() <= maxWidth && image.getHeight() <= maxHeight) {
            try {
                FileInputStream in = new FileInputStream(originalFileName);
                FileOutputStream out = new FileOutputStream(targetFileName);
                FileUtils.copy(in, out);
                in.close();
                out.close();
            }  catch (Exception e) {
                Logger.error("file move error", e);
            }
        } else {
            double heigthRatio = maxHeight / (double)image.getHeight(); 
            double widthRatio = maxWidth / (double)image.getWidth();
            
            double ratio = Math.min(heigthRatio, widthRatio);
            this.createZoomSizeImage(image, targetFileName, ratio);
        }
    }
    
    public void createZoomSizeImage(String originalFileName, String targetFileName, int width, int height) throws IOException {
        File file = new File(originalFileName);
        BufferedImage image = ImageIO.read(file);
        if (image.getWidth() == width && image.getHeight() == height) {
            try {
                FileInputStream in = new FileInputStream(originalFileName);
                FileOutputStream out = new FileOutputStream(targetFileName);
                FileUtils.copy(in, out);
                in.close();
                out.close();
            }  catch (Exception e) {
                Logger.error("file move error", e);
            }
        } else {
            this.createZoomSizeImage(image, targetFileName, width, height);
        }
    }

    private void createZoomSizeImage(BufferedImage image, String targetFileName, int width, int height) throws IOException {
        AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(width, height);
        FilteredImageSource filteredImageSource = new FilteredImageSource(image.getSource(), areaAveragingScaleFilter);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(component.createImage(filteredImageSource), 0, 0, null);
        try {
            File targetFile = new File(targetFileName);
            Logger.of(ZoomImage.class).debug("图片文件保存地址:"+targetFileName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            ImageIO.write(bufferedImage, "jpg", new File(targetFileName));
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按比例进行放大缩小图像，zoomRatio = 1为原大，zoomRatio > 1为放大，zoomRatio < 1 为缩小
     * @param image
     * @param targetFileName
     * @param zoomRatio
     */
    private void createZoomSizeImage(BufferedImage image, String targetFileName, double zoomRatio) {
        int width = new Double(image.getWidth(null) * zoomRatio).intValue();
        int height = new Double(image.getHeight(null) * zoomRatio).intValue();
        AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(width, height);
        FilteredImageSource filteredImageSource = new FilteredImageSource(image.getSource(), areaAveragingScaleFilter);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(component.createImage(filteredImageSource), 0, 0,  null);
        try {
            File targetFile = new File(targetFileName);
            Logger.debug("图片文件保存地址:"+targetFileName);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            ImageIO.write(bufferedImage, "jpg", new File(targetFileName));
            graphics.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}