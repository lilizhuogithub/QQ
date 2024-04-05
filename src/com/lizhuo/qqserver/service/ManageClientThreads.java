package com.lizhuo.qqserver.service;

import com.sun.security.ntlm.Server;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类用于管理和客户端通信的线程
 */

public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    //添加线程对象到 hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userId 返回ServerConnectClientThread 线程
    public static ServerConnectClientThread getServerConnectClientThread (String userId) {
        return hm.get(userId);
    }

    //从集合中, 移除某个线程集合
    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }

    //返回在线用户列表
    public static String getOnlineUser() {
        //集合遍历,遍历hashmap的key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";   //加上" "是为了键之间需要用分隔符（空格）来分隔
        }
        return onlineUserList;
    }

}
