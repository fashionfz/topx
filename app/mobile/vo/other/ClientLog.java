package mobile.vo.other;

import mobile.model.MobileClientLog;
import mobile.vo.MobileVO;
import play.libs.Json;
import utils.Assets;

import com.fasterxml.jackson.databind.JsonNode;

public class ClientLog implements MobileVO {

    private Long id;

    private String device;

    private Long createTime;

    private String description;

    private String logFileUrl;

    public static ClientLog create(MobileClientLog po) {
        ClientLog vo = new ClientLog();
        vo.setCreateTime(po.getCreateTime().getTime());
        vo.setDescription(po.getDescription());
        vo.setDevice(po.getDevice());
        vo.setId(po.getId());
        vo.setLogFileUrl(Assets.at(po.getLogFileUrl()));

        return vo;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogFileUrl() {
        return logFileUrl;
    }

    public void setLogFileUrl(String logFileUrl) {
        this.logFileUrl = logFileUrl;
    }

}
