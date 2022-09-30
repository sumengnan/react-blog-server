package com.su.blog.util.date;

/**
 * @description: 时间格式enum
 **/
public enum DateStyle {

    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss", false),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm", false),
    YYYY_MM_DD("yyyy-MM-dd", false),
    YYYY_MM("yyyy-MM", false),

    YYYYMMDDHHMMSS("yyyyMMddHHmmss", false),
    YYYYMMDDHHMM("yyyyMMddHHmm", false),
    YYYYMMDD("yyyyMMdd", false),
    YYYYMM("yyyyMM", false),

    CN_YYYY_MM_DD_HH_MM_SS("yyyy年MM月dd日 HH:mm:ss", false),
    CN_YYYY_MM_DD_HH_MM("yyyy年MM月dd日 HH:mm", false),
    CN_YYYY_MM_DD("yyyy年MM月dd日", false),
    CN_YYYY_MM("yyyy年MM月", false),

    EN_YYYY_MM_DD_HH_MM_SS("yyyy/MM/dd HH:mm:ss", false),
    EN_YYYY_MM_DD_HH_MM("yyyy/MM/dd HH:mm", false),
    EN_YYYY_MM_DD("yyyy/MM/dd", false),
    EN_YYYY_MM("yyyy/MM", false),

    E_MMM_DD_KK_MM_SS_Z_Y("E MMM dd HH:mm:ss Z yyyy", false),

    MM_DD_HH_MM_SS("MM-dd HH:mm:ss", true),
    MM_DD_HH_MM("MM-dd HH:mm", true),
    MM_DD("MM-dd", true),

    CN_MM_DD_HH_MM_SS("MM月dd日 HH:mm:ss", true),
    CN_MM_DD_HH_MM("MM月dd日 HH:mm", true),
    CN_MM_DD("MM月dd日", true),

    EN_MM_DD_HH_MM_SS("MM/dd HH:mm:ss", true),
    EN_MM_DD_HH_MM("MM/dd HH:mm", true),
    EN_MM_DD("MM/dd", true),

    HH_MM_SS("HH:mm:ss", true),
    HH_MM("HH:mm", true);

    //日期格式
    private String value;
    //
    private boolean isShowOnly;

    DateStyle(String value, boolean isShowOnly) {
        this.value = value;
        this.isShowOnly = isShowOnly;
    }

    public String getValue() {
        return value;
    }

    public boolean isShowOnly() {
        return isShowOnly;
    }
}
