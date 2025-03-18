package org.swdc.rmdisk.core.ftp;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FTPSessionContext {

    /**
     * 命令和处理器的映射表
     */
    private Map<String, FTPMessageHandler> commandHandlers = new ConcurrentHashMap<>();

    /**
     * 连接和会话的映射表
      */
    private Map<Object, FTPSession> connectionSessions = new ConcurrentHashMap<>();


    /**
     * 第三方上下文对象，用于存放自定义的上下文信息
     */
    private Object thirdPartyContext;

    public FTPSessionContext() {
    }

    /**
     * 设置第三方上下文对象
     * @param thirdPartyContext
     */
    public void setThirdPartyContext(Object thirdPartyContext) {
        this.thirdPartyContext = thirdPartyContext;
    }

    /**
     * 获取第三方上下文对象
     * @return
     * @param <T>
     */
    public <T> T getThirdPartyContext() {
        return (T)thirdPartyContext;
    }

    public FTPSession getSession(String address) {
        if (connectionSessions.containsKey(address)) {
            return connectionSessions.get(address);
        }
        FTPSession session = new FTPSession();
        connectionSessions.put(address, session);
        return session;
    }

    public void registerCommandHandler(String command, FTPMessageHandler handler) {
        commandHandlers.put(command, handler);
    }

    public FTPMessageHandler getCommandHandler(String command) {
        return commandHandlers.get(command);
    }

    public void removeSession(String address) {
        connectionSessions.remove(address);
    }

    public void removeSessionById(Long userId) {
        for(FTPSession session : connectionSessions.values()) {
            if(session.getUserId().equals(userId)) {
                connectionSessions.remove(session.getSessionToken());
                return;
            }
        }
    }

    public void removeAllSessions() {
        connectionSessions.clear();
    }

    public List<String> getRegisteredCommands() {
        return commandHandlers.keySet().stream().toList();
    }

}
