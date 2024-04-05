package com.lizhuo.qqclient.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;
import com.lizhuo.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */

public class UserClientService {
    //因为我们可能仔其他地方用到user信息，因此做出成员属性
    private User u = new User();
    //因为Socket在其它地方也可能使用，因此作出属性
    private Socket socket;

    //根据UserId 和 pwd  到服务器验证该用户是否合法
    public boolean chekUser(String userId, String pwd) {
        boolean b = false;
        //创建User对象
        u.setPasswd(pwd);
        u.setUserId(userId);

        try {
            //连接到服务器 发送User对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);   //发送User对象

            //验证userId 和 pwd

            //读取从服务器回复的登录是否通过的Message信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            //登陆成功
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录OK
                //创建一个和服务器保持通信的线程
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();
                //这里为了后边客户端的扩展，我们将线程放入到集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            } else {
                socket.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    //向服务器请求在线用户列表
    public void onlineFriend () {
        //发送一个Message , 类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        try {
            ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());//从管理线程的集合中取出userId对应的线程
            Socket socket = clientConnectServerThread.getSocket();    //通过这个线程关联得到socket
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());    //获取socket对应的ObjectOutputStream对象
            oos.writeObject(message);   //发送一个Message对象,向服务端要求在线用户
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //退出客户端,并向服务端发送一个退出系统的message对象
    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());    //一定要指定我是那个客户端的id

        try {
            //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统 ");
            System.exit(0);    //结束客户端进程
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
