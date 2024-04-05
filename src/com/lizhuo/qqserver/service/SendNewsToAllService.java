package com.lizhuo.qqserver.service;


import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;
import com.lizhuo.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class SendNewsToAllService implements Runnable{

    @Override
    public void run() {
        //为了实现可以推送多次新闻,使用while
        while (true) {
            System.out.println("请输入服务器要推送的新闻/消息[使用exit退出新闻服务进程]");
            String news = Utility.readString(100);
            //构建一个消息, 群发
            if (news.equals("exit")) {
                break;
            }
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_To_All_MES);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人说  " + news);

            //遍历当前所有的通信线程,得到socket,并发送message
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId = iterator.next().toString();
                try {
                    ObjectOutputStream oos =
                            new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
