# socket-server
A simple socket server Demo


一个简单的Socket服务器Demo

### Project structure 项目结构

```text

cn.dreamccc
    |- handler                   
    |    |- EchoSocketHandler          直接回显的socket处理器
    |    |- SimpleSocketHandler        socket处理器接口简单实现
    |    |- SocketHandler              socket处理器接口
    |- Main                           程序入口

```

### Project dependencies 项目依赖

- slf4j            日志门面及其简单实现
- druid            连接池
- mysql-connector  mysql连接器
