package com.lizhuo.qqserver.service;

import com.lizhuo.qqcommon.Message;
import com.lizhuo.qqcommon.MessageType;
import com.lizhuo.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务端，在监听9999，等待客户端的链接，并保持通信
 */
public class QQServer {
    private ServerSocket ss = null;
    //创建一个集合存放多个用户,如果是这些用户登录,就认为是合法的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { //在静态代码块，初始化 validUsers

        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
        validUsers.put("菩提老祖", new User("菩提老祖", "123456"));

    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        if (user == null) {
            return false;
        }
        if (!user.getPasswd().equals(passwd)) {
            return false;
        }
        return true;

    }

    public QQServer() {
        //注意: 端口可以写在配置文件
        try {
            System.out.println("服务端在9999端口监听....");
            ss = new ServerSocket(9999);
            new Thread(new SendNewsToAllService()).start();    //推送新闻
            while (true) {   //当和客户端链接后,会继续监听,因此为while
                Socket socket = ss.accept();   //如果没有客户端链接,就会阻塞在这里
                //得到socket关联的对象输入流
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();   //读取客户端发送的User对象
                //创建一个Message对象,准备回复客户端
                Message message = new Message();

                //验证
                if (checkUser(u.getUserId(), u.getPasswd())) {  //登录通过
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复客户端
                    oos.writeObject(message);

                    //创建一个线程,和客户端保持通信,该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象,放入到一个集合中,进行管理.
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

                } else {
                    //如果登录失败,我们就不能启动和服务器通信的线程,
                    System.out.println("用户 id " + u.getUserId() + " pwd " + u.getPasswd() + "验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果服务器退出了while，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
