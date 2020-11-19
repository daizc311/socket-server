package cn.dreamccc.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface SocketHandler {

    void handle();

    void helloMessage();

    void close();
}
