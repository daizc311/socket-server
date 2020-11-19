package cn.dreamccc;

import cn.dreamccc.handler.SimpleSocketHandler;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>cn.dreamccc.Main</h2>
 *
 * @author DreamCcc
 * @date 2020/11/19 22:30
 */
@Slf4j
public class Main {


    @SneakyThrows
    public static void main(String[] args) {
        // 接受外部参数
        Map<String, String> argMap = args2Map(args);

        // 获取监听端口
        String portStr = argMap.getOrDefault("p", "8080");
        int port = Integer.parseInt(portStr);

        // 获取Mysql连接池
        DataSource dataSource = connectMysqlConnectionPool();

        // 开启Socket
        openSocket(port);
    }

    private static DataSource connectMysqlConnectionPool() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
        String username = "root";
        String password = "1234567890";


        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(jdbcUrl);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(10);
        druidDataSource.setInitialSize(5);
        druidDataSource.setMinIdle(3);
        return druidDataSource;
    }


    private static void openSocket(int port) throws IOException {

        // 创建ServerSocket并与端口绑定
        ServerSocket serverSocket = new ServerSocket(port);
        log.info("LISTEN {}...", port);
        while (true) {
            try {
                // 接收到一个请求
                Socket socket = serverSocket.accept();
                // 启动一个线程处理这个请求
                Thread thread = new Thread(() -> new SimpleSocketHandler(socket).handle());
                thread.start();
                if (serverSocket.isClosed()) {
                    break;
                }
            } catch (IOException e) {
                log.error("创建Socket发生异常:", e);
            }
        }
        log.info("Exit...");
    }



    private static Map<String, String> args2Map(String[] args) {
        return Arrays.stream(args)
                .map(s -> s.split("="))
                .filter(strings -> strings.length > 1)
                .filter(strings -> !Objects.equals(strings[0], ""))
                .filter(strings -> !Objects.equals(strings[1], ""))
                .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
    }
}
