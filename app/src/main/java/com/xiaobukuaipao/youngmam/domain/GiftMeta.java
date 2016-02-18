package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-10.
 */
public class GiftMeta {
    private String addrId;
    private long createTime;
    private String itemId;
    private int status;
    private String txId;
    private String userId;

    public GiftMeta(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ADDRID)) {
            addrId = jsonObject.getString(GlobalConstants.JSON_ADDRID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CREATETIME)) {
            createTime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ITEMID)) {
            itemId = jsonObject.getString(GlobalConstants.JSON_ITEMID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_STATUS)) {
            status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TXID)) {
            txId = jsonObject.getString(GlobalConstants.JSON_TXID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_USERID)) {
            userId = jsonObject.getString(GlobalConstants.JSON_USERID);
        }
    }

    public void setAddrId(String addrId) {
        this.addrId = addrId;
    }
    public String getAddrId() {
        return this.addrId;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getCreateTime() {
        return this.createTime;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return this.itemId;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return this.status;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
    public String getTxId() {
        return this.txId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return this.userId;
    }

}
