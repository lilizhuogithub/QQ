package com.lizhuo.qqclient.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 提供和消息相关的服务方法
 */

public class MessageClientService {
    /**
     *
     * @param content  内容
     * @param senderId  发送用户的Id
     * @param getterId  接收用户的Id
     */
    public void sendMessageToOne (String content, String senderId, String getterId) {
        //构建message
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + "对" + getterId + "说" + content);
        //发送给服务端
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content, String senderId) {
        //构建message
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_To_All_MES);    //群发消息
        message.setContent(content);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + "对大家说" + content);

        //发送给服务端
        try {

            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
