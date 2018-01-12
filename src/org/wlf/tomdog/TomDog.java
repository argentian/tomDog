package org.wlf.tomdog;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TomDog {

    private static int port = 9090;
    /**
     * 服务启动的方法
     * 创建socket
     */
    public void start(int portArgs){
        try {
            System.out.println("--------------------服务器启动。。。------------------");
            ServerSocket serverSocket = new ServerSocket(portArgs);
            System.out.println("--------------------监听"+ portArgs +"端口。。。------------------");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("--------------------有客户端的请求。。。------------------");
                //创建一个固定大小的线程池来处理多个客户访问
                ExecutorService executors =  Executors.newFixedThreadPool(100);
                //将任务提交给线程池
                executors.submit(new HandlerRequestThread(socket));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 程序入口
     * @param args
     */
    public static void main(String[] args) {
        //动态设置服务器端口（通过命令行传递）
        int p = (args.length>0)?Integer.parseInt(args[0]):port;
        new TomDog().start(p);

    }


}