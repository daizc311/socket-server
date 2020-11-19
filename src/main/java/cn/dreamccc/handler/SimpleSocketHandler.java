package cn.dreamccc.handler;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

@Slf4j
public class SimpleSocketHandler implements SocketHandler {

    final Socket socket;
    final String remoteAddressStr;

    public SimpleSocketHandler(Socket socket) {
        this.socket = socket;
        SocketChannel channel = socket.getChannel();
        String tempStr = "DEFAULT";
        try {
            tempStr = channel.getRemoteAddress().toString();
        } catch (IOException e) {
            log.error("获取远程地址失败", e);
        }
        remoteAddressStr = tempStr;
    }

    @Override
    public void handle() {

        BufferedWriter writer;
        BufferedReader reader;

        // 打开输入/输出流
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK"));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
        } catch (IOException e) {
            log.error(remoteAddressStr + ":打开输入/输出流失败", e);
            return;
        }


        // 发送欢迎消息
        try {
            writer.write("hello\n");
            writer.flush();
            log.info(remoteAddressStr + ":连接成功");
        } catch (IOException e) {
            log.error(remoteAddressStr + ":发送欢迎消息失败", e);
            return;
        }

        // 循环接受消息
        while (socket.isConnected()) {
            try {
                String s = reader.readLine();
                if (s.equals("bye")) {
                    try {
                        socket.close();
                        log.info("服务器主动断开连接");
                    } catch (IOException e) {
                        log.error("服务器主动断开连接失败", e);
                    }
                }

                //TODO 业务逻辑
                System.out.println(s);
                writer.write("ok: " + s + "\n");
                writer.flush();


            } catch (IOException e) {
                log.error(remoteAddressStr + ":循环接受消息出现异常", e);
                return;
            }
        }
    }
}
