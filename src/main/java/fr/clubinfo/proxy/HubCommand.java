package fr.clubinfo.proxy;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public final class HubCommand implements SimpleCommand {

    private final ProxyServer server;

    public HubCommand(ProxyServer proxy) {
        this.server = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }

        Player player = (Player) source;

        Optional<ServerConnection> currentServer = player.getCurrentServer();

        if (currentServer.isPresent()) {
            String currentServerName = currentServer.get().getServerInfo().getName();
            if (currentServerName.equalsIgnoreCase(Config.HUB_SERVER_NAME)) {
                player.sendMessage(Component.text(Config.COLOR_ERROR + "Vous êtes déjà sur le serveur hub."));
                return;
            }
        }

        // Logique pour envoyer le joueur au hub
        Optional<RegisteredServer> hubServer = server.getServer(Config.HUB_SERVER_NAME);

        if (hubServer.isPresent()) {
            player.createConnectionRequest(hubServer.get()).connect();
            player.sendMessage(Component.text(Config.COLOR_SUCCESS + "Vous avez été redirigé vers le serveur hub."));
        } else {
            player.sendMessage(Component.text(Config.COLOR_ERROR + "Le serveur hub est introuvable."));
        }
    }
}
