package com.lizhuo.qqclient.view;

import com.lizhuo.qqclient.service.FileClientService;
import com.lizhuo.qqclient.service.MessageClientService;
import com.lizhuo.qqclient.service.UserClientService;
import com.lizhuo.qqclient.utils.Utility;

import java.sql.SQLOutput;

/**
 * 客户端的菜单界面
 */
@SuppressWarnings("all")
public class QQView {

    private boolean loop = true;
    private String key;
    private UserClientService userClientService = new UserClientService();   //对象用于登录服务/注册用户
    private MessageClientService messageClientService = new MessageClientService();   //用于私发群聊消息
    private FileClientService fileClientService = new FileClientService();    //该对象用于传送文件


    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("******客户端退出系统******");
    }

    //显示主菜单
    private void mainMenu() {
        while (loop) {
            System.out.println("==========欢迎登录网络通信系统=========");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);
            //根据用户的输入，来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.print("请输入你的用户号:");
                    String userId = Utility.readString(50);
                    System.out.print("请输入你的密  码:");
                    String pwd = Utility.readString(50);
                    //需要到服务端去验证该用户是否合法
                    if (userClientService.chekUser(userId, pwd)) {    //**这个的同时就会建立与服务器的链接socket通信**

                        System.out.println("=========欢迎(用户" + userId + "登陆成功)============");
                        while (loop) {
                            System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);

                            switch (key) {
                                case "1":
                                    userClientService.onlineFriend();
                                    break;
                                case "2":
                                    System.out.println("请输入相对大家说的话: ");
                                    String s = Utility.readString(100);
                                    //调用方法,将消息封装成message对象, 发送给服务端
                                    messageClientService.sendMessageToAll(s, userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号: ");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话: ");
                                    String content = Utility.readString(100);
                                    //编写一个方法, 将消息发送给服务器端
                                    messageClientService.sendMessageToOne(content, userId, getterId);

                                    break;
                                case "4":
                                    System.out.println("请输入你想把文件传输给的在线用户");
                                    getterId = Utility.readString(50);
                                    System.out.println("请输入发送文件的路径");
                                    String src = Utility.readString(100);
                                    System.out.println("请输入你想把文件发送到的路径");
                                    String dest = Utility.readString(100);

                                    fileClientService.sendFileToOne(src, dest, userId, getterId);

                                    break;
                                case "9":
                                    //给服务器端发送一个结束的message
                                    userClientService.logout();
                                    break;
                            }
                        }
                    } else {   //登录服务器失败
                        System.out.println("登录服务器失败~~~~~");
                    }
                    break;
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                    break;
            }
        }

    }

}
