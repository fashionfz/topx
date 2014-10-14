package ext.MessageCenter.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * 
 * @title TestUtil.java
 * @package com.helome.messagecenter.utils
 * @description 测试需要以后删除或修改
 * @author beyond.zhang   
 * @update 2014-3-13 上午10:20:49
 * @version V1.0
 */
public class TestUtil {

	public	static byte[] readFileToByte(File file) throws IOException {
		FileInputStream fis = null;  
        ByteArrayOutputStream ops = null;
        byte[] b = null;  
        byte[] temp = new byte[2048];  
        try {
			fis = new FileInputStream(file);
			ops = new ByteArrayOutputStream(2048);  
			 int n;
			 while ((n = fis.read(temp)) != -1) { 
				 ops.write(temp, 0, n);
			 }
			   b = ops.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally { 
			if (ops != null) {  
				 ops.close();
			}
			if (fis != null) {
				 fis.close();
			}
		}
        return b;
	}


}
