package com.lizhuo.qqclient.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接收一个Socket对象


    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，因此我们while循环
        while (true) {
            try {
                System.out.println("客户端线程，等待从服务端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送Message对象，线程会阻塞在这里
                Message ms = (Message) ois.readObject();
                //如果读取到的是 服务端返回的在线用户列表
                if (ms.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线列表信息,并显示
                    String[] onlineUsers = ms.getContent().split(" ");
                    System.out.println("\n=======当前在线用户列表======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户 " + onlineUsers[i]);
                    }
                } else if (ms.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\n" + ms.getSender() + "对" + ms.getGetter() + "说" + ms.getContent() + "______此刻时间:" + ms.getSendTime());

                } else if (ms.getMesType().equals(MessageType.MESSAGE_To_All_MES)) {
                    //显示在客户端控制台
                    System.out.println("\n" + ms.getSender() + "对大家说" + ms.getContent() + "______此刻时间:" + ms.getSendTime());


                } else if (ms.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //让用户指定路径
                    System.out.println("\n" + ms.getSender() + " 给 " + ms.getGetter() + " 发文件 " + ms.getSrc() + " 到我的电脑目录 " + ms.getDest());

                    //取出message的文件字节数组, 通过文件输出流写入到磁盘中
                    FileOutputStream fileOutputStream = new FileOutputStream(ms.getDest());
                    fileOutputStream.write(ms.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功");

                } else {
                    System.out.println("是其他类型的message, 暂时不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    //为了更方便得到Socket，使用get方法
    public Socket getSocket() {
        return socket;
    }
}
