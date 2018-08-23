package com.stringing.socketduplexing.message.constants;

import java.util.List;
import java.util.Map;

/**
 * @description 特殊消息
 * @Author Stringing
 * @Date 2018/8/8 18:48
 */
public final class MessageParam {
    private MessageParam(){}
    public static final String SERVER_SHUTDOWN = "shutdown -s";
    public static final String CLIENT_SHUTDOWN = "shutdown -c";
    public static final String CLIENT_IDENTITY = "client";
    public static final String SERVER_IDENTITY = "server";
    public static final String TALK_HEAD = "-t";
    public static final String TALK_TAIL  = "-end";
    public static final String TALK_FROM = "-f";

    //要过滤的系统命令
    public enum FilterToken{
        DETECT_TOKEN("-d");
        private String token;

        FilterToken(String s) {
        }

        @Override
        public String toString() {
            return this.token;
        }
    }
}
