package cn.dreamccc.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

@Slf4j
public class EchoSocketHandler extends SimpleSocketHandler {


    public EchoSocketHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void handle() {

        // 循环接受消息
        while (socket.isConnected()) {
            try {
                String s = reader.readLine();
                if (s.equals("bye")) {
                    this.close();
                    break;
                }

                // 业务逻辑
                this.echo(s);
            } catch (Exception e) {
                log.error(remoteAddressStr + ":循环接受消息出现异常", e);
                close();
                return;
            }
        }
    }


    public void echo(String msg) {
        try {
            log.info("接收到消息=> {}", msg);
            writer.write("ok: " + msg + "\n");
            writer.flush();
        } catch (IOException e) {
            log.error(remoteAddressStr + ":处理消息失败,尝试关闭连接", e);
            this.close();
        }
    }

}
