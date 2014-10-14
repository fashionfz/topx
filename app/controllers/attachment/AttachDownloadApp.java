package controllers.attachment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import controllers.base.ObjectNodeResult;
import models.Attach;
import models.AttachOfFeedback;
import models.AttachOfIndustry;
import models.AttachOfRequire;
import models.AttachOfService;
import models.AttachOfSuggestion;
import play.Logger;
import play.db.jpa.Transactional;
import play.mvc.Result;
import scalax.io.support.FileUtils;
import utils.Assets;

/**
 * 附件下载
 * @ClassName: AttachDownloadApp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-5 下午16:49:25
 * 
 */
public class AttachDownloadApp extends AttachmentApp {
	//用户投诉上传凭证图像保存路径
	public static final String PROOF_UPLOAD_PATH = getUploadPath();
	
	/**
	 * 附件下载
	 * @param type
     *  feedback：反馈， suggestion：建议，service：服务，require：需求，industry：行业
	 * @param attachId 附件id
	 * @return
	 * @throws MalformedURLException 
	 * @throws FileNotFoundException 
	 */
	@Transactional
    public static Result fileDownload(Long attachId,String type) throws MalformedURLException, FileNotFoundException {
		ObjectNodeResult result = new ObjectNodeResult();
		Attach attach = null;
		if (StringUtils.equals("suggestion", type)) {
			attach = Attach.queryById(attachId, AttachOfSuggestion.class);
		} else if (StringUtils.equals("service", type)) {
			attach = Attach.queryById(attachId, AttachOfService.class);
		} else if (StringUtils.equals("require", type)) {
			attach = Attach.queryById(attachId, AttachOfRequire.class);
		} else if (StringUtils.equals("feedback", type)) {
			attach = Attach.queryById(attachId, AttachOfFeedback.class);
		} else {
			attach = Attach.queryById(attachId, AttachOfIndustry.class);
		}

		if (attach == null) {
			result = result.error("下载失败，文件没有找到！", "-333");
			return ok(result.getObjectNode());
		}
		String filePath = PROOF_UPLOAD_PATH + StringUtils.substring(attach.path, "topx/uploadfile/".length());
		play.mvc.Http.Response response = response();

		// filePath是指欲下载的文件的路径。
		File file = new File(filePath);
		// 取得文件名。
		String filename = attach.fileName;
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("UTF-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setHeader("Content-Length", "" + file.length());
		response.setContentType("application/octet-stream");

		byte[] buffer = {};
		try {
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
			buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("下载文件" + filePath + "出错。", e);
			}
		}

		return ok(buffer);
	}
	
	

}
