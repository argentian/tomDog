package org.wlf.tomdog;

import java.io.*;
import java.net.Socket;

/**
 * 专门处理多个客户端请求的 多线程类
 */
public class HandlerRequestThread implements Runnable{

    private InputStream inputStream = null;
    //标准输出流
    private PrintStream printStream = null;

    /**
     * 应用的根路径
     *D:\myWeb
     */
    private static final String WEB_ROOT = "D:"+ File.separator +"myWeb" + File.separator;
    /**
     * 通过构造函数获得socket，并通过socket获取客户端的输入输出流
     * @param socket
     */
    public HandlerRequestThread(Socket socket) {
        try {
            inputStream =  socket.getInputStream();
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("--------------------处理用户请求。。。------------------");
        try {
            String fileName = parseRequestHead(this.inputStream);
            getFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析请求头，获取客户端气球资源名称
     * @param inputStream 输入流
     * @return 请求资源名称
     */
    public String parseRequestHead(InputStream inputStream) throws IOException {
        //客户端发起请求，会将一些请求数据包含在请求头中
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //读取请求数据 数据包含：请求方式（get/post），请求资源名称，请求版本协议（http/1.1）
        String headContext = bufferedReader.readLine();
        if (headContext.equals("")){
            return "index.html";
        }
        String[] heads = headContext.split(" ");

        return heads[1].endsWith("/")?"index.html":heads[1];
    }

    public  void getFile(String fileName) throws IOException {
        File file = new File(WEB_ROOT + fileName);
        if (!file.exists()){
            sendError("404","=====请求的资源"+ fileName +"不存在====");
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        byte[] buff = new byte[(int)file.length()];
        bis.read(buff);

        printStream.println("HTTP/1.1 200 OK");
        printStream.println();
        printStream.write(buff);
        printStream.flush();
        printStream.close();
    }

    /**
     *输出404错误信息
     * @param erroeNumber 错误编号
     * @param errorMsg 错误信息
     */
    public void sendError(String erroeNumber,String errorMsg){
        StringBuffer sb = new StringBuffer("<html><head><title>错误页面</title>");
        sb.append("<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'>");
        sb.append("<body>");
        sb.append("<center><h1><font color='red'>"+ erroeNumber +"</font><h1></center>");
        sb.append("<hr color=red>");
        sb.append("<p>" + errorMsg + "</p>");
        sb.append("</body></html>");
        printStream.println("HTTP/1.1 404 Not Found");
        printStream.println();
        printStream.println(sb.toString());
        printStream.flush();
        printStream.close();
    }
}