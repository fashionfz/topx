package utils;

/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;

/**
 * 
 * 
 * 
 * @ClassName: FileTypeUtil
 * @Description: 文件类型识别
 * @date 2014-3-11 下午6:08:27
 * @author ShenTeng
 * 
 */
public class FileTypeUtil {

    public static final String MIME_TYPE_MP4 = "video/mp4";
    public static final String MIME_TYPE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_FLV = "video/x-flv";

    public static List<String> getMimeTypes(File file) {

        if (MimeUtil.getMimeDetector(MagicMimeMimeDetector.class.getName()) == null) {
            MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
        }

        @SuppressWarnings("unchecked")
        Collection<MimeType> mimeTypes = MimeUtil.getMimeTypes(file);

        List<String> types = new ArrayList<>();
        for (MimeType mt : mimeTypes) {
            types.add(mt.toString().toLowerCase());
        }

        return types;
    }

}
