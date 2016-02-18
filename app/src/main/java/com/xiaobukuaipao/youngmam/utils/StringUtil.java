package com.xiaobukuaipao.youngmam.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiaobu1 on 15-4-14.
 */
public class StringUtil {

    private StringUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * MD5编码的key
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cacheKey = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 字符串是否为空
     * @param str
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 构建标签Tag
     */
    public static String buildTag(String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(tag);
        return sb.toString();
    }

    /**
     * 构建微博分享--福利社
     */
    public static String buildWeiboShareWelfare(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    /**
     * 构建微博活动分享
     * @param shareContent
     */
    public static String buildWeiboShareActivity(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("在");
        sb.append("#");
        sb.append("花样妈妈");
        sb.append("#");
        sb.append("上发现了一个有趣的活动,快来看看吧");
        sb.append("~~");
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    public static String buildWeiboShareTheme(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("妈妈们正在讨论");
        sb.append("\"");
        sb.append(shareContent);
        sb.append("\"");
        sb.append("的内容, 速去围观吧");
        sb.append("~~");
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    public static String buildWeiboShareGift(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("我在");
        sb.append("#");
        sb.append("花样妈妈");
        sb.append("#");
        sb.append("上发现了一件有趣的礼品,快来看看吧");
        sb.append("~~");
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    public static String buildWeiboShareArticle(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append("花样妈妈");
        sb.append("#");
        sb.append("上的这组照片真的很赞, 快去看看吧");
        sb.append("~~");
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    public static String buildWeiboShareQuestion(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append("花样妈妈");
        sb.append("#");
        sb.append("上的这个问题觉得很不错, 快去看看吧");
        sb.append("~~");
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

    public static String buildWeiboShareTopic(String shareContent, String targetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("在");
        sb.append("#");
        sb.append("花样妈妈");
        sb.append("#");
        sb.append("上发现了一个有趣的专题,快来看看吧");
        sb.append("~~");
        if (!StringUtil.isEmpty(shareContent)) {
            sb.append(shareContent.length() > 20 ? shareContent.substring(0, 20) : shareContent);
        }
        sb.append(">>");
        sb.append(targetUrl);
        sb.append(" (");
        sb.append("@");
        sb.append("花样妈妈youngmam");
        sb.append(")");

        return sb.toString();
    }

}
