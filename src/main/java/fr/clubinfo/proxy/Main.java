package fr.clubinfo.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "proxy", name = "proxy", version = "1.0-SNAPSHOT")
public final class Main {

    private final ProxyServer proxy;

    @Inject
    public Main(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        CommandManager commandManager = proxy.getCommandManager();

        CommandMeta hubMeta = commandManager.metaBuilder("hub")
                .aliases("lobby")
                .build();
        SimpleCommand hubCommand = new HubCommand(proxy);
        commandManager.register(hubMeta, hubCommand);

        CommandMeta sendMeta = commandManager.metaBuilder("send")
                .build();
        SimpleCommand sendCommand = new SendCommand(proxy);
        commandManager.register(sendMeta, sendCommand);

        CommandMeta sendAllMeta = commandManager.metaBuilder("sendall")
                .build();
        SimpleCommand sendAllCommand = new SendAllCommand(proxy);
        commandManager.register(sendAllMeta, sendAllCommand);

        logger.info(Config.PROXY_PREFIX + "Proxy initialized");

    }
}
