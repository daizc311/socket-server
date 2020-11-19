package cn.dreamccc.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;

@Slf4j
public class SimpleSocketHandler implements SocketHandler{
    final Socket socket;
    final String remoteAddressStr;
    final BufferedWriter writer;
    final BufferedReader reader;

    public SimpleSocketHandler(Socket socket) {
        this.socket = socket;
        String tempStr = "DEFAULT";
        BufferedWriter tempWriter = null;
        BufferedReader tempReader = null;
        SocketChannel channel = socket.getChannel();

        // 尝试获取远端地址
        if (channel != null) {
            try {
                tempStr = channel.getRemoteAddress().toString();
            } catch (Exception e) {
                log.error("获取远程地址失败", e);
            }
        }
        remoteAddressStr = tempStr;

        // 尝试打开输入/输出流
        try {
            tempWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"));
            tempReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
        } catch (IOException e) {
            log.error(remoteAddressStr + ":打开输入/输出流失败", e);
        }
        this.writer = tempWriter;
        this.reader = tempReader;

        // 发送欢迎消息
        this.helloMessage();
    }

    @Override
    public void handle() {

        // 循环接受消息
        while (socket.isConnected()) {
            try {
                String msg = reader.readLine();
                if (msg.equals("bye")) {
                    this.close();
                    break;
                }

                // 业务逻辑
                log.info("接收到消息=> {}", msg);
            } catch (Exception e) {
                log.error(remoteAddressStr + ":循环接受消息出现异常", e);
                close();
                return;
            }
        }
    }

    @Override
    public void helloMessage() {

        try {
            writer.write("hello\n");
            writer.flush();
            log.info(remoteAddressStr + ":连接成功");
        } catch (IOException e) {
            log.error(remoteAddressStr + ":发送欢迎消息失败,尝试关闭连接", e);
            this.close();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
            log.info("服务器主动断开连接");
        } catch (IOException e) {
            log.error("服务器主动断开连接失败", e);
        }
    }
}
