package com.lizhuo.qqserver.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类一个对象和客户端保持通信
 */

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    //为了更方便得到Socket，使用get方法
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        System.out.println("服务端和客户端" + userId + "保持通信, 读取数据...");
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();    //循环读取客户发过来的消息

                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线用户列表
                    /*
                    在线用户列表形式 100 200 紫霞仙子
                     */
                    System.out.println(message.getSender() + "要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();    //获取管理线程中所有在线用户的列表,并以空格间隔
                    //构建一个Message对象, 返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {    //私聊消息
                    //根据message 获取getter id, 然后再得到对应的线程
                    ServerConnectClientThread serverConnectClientThread =
                            ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream oos =
                            new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);    //如果客户不在线, 可以保存到数据库, 这样就可以离线留言功能

                } else if (message.getMesType().equals(MessageType.MESSAGE_To_All_MES)) {
                    //需要遍历 除了自己之的所有线程,把它们的socket得到,然后把message进行转发即可
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出在线用户的Id
                        String onLineUserId = iterator.next();

                        if (!(onLineUserId.equals(message.getSender()))) {    //排除群发消息的这个用户
                            //进行转发消息message
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }


                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //根据getterId 获取对应的线程, 将message对象转发
                    ServerConnectClientThread serverConnectClientThread =
                            ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);


                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {    //退出
                    System.out.println(message.getSender() + " 退出");
                    //将这个客户端对应的线程,从集合中删除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();    //关闭连接
                    //退出线程
                    break;
                } else {
                    System.out.println("其它类型的Message, 暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
