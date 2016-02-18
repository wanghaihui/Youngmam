package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-7-10.
 */
public class MineGift {

    private Gift gift;
    private GiftMeta giftMeta;

    public MineGift(Gift gift, GiftMeta giftMeta) {
        this.gift = gift;
        this.giftMeta = giftMeta;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }
    public Gift getGift() {
        return this.gift;
    }

    public void setGiftMeta(GiftMeta giftMeta) {
        this.giftMeta = giftMeta;
    }
    public GiftMeta getGiftMeta() {
        return this.giftMeta;
    }
}
