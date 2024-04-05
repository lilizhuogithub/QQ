package com.lizhuo.qqclient.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;

import java.io.*;

/**
 * 该类完成文件传输
 */

public class FileClientService {
    /**
     *
     * @param src
     * @param dest
     * @param senderId
     * @param getterId
     */
    public void sendFileToOne (String src, String dest, String senderId, String getterId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);   //将src文件读书到程序的字节数组
            //将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();    //gaun
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //提示信息
        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件: " + src + " 对方的电脑目录 " + dest );

        //**发送**
        try {

            //发送message核心代码就是这两个代码
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
