package fr.clubinfo.proxy;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SendAllCommand implements SimpleCommand {

    private final ProxyServer server;

    public SendAllCommand(ProxyServer proxy) {
        this.server = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission(Config.PERMISSION_PROXY_SENDALL)) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Vous n'avez pas la permission d'exécuter cette commande."));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length != 1) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Usage: /sendall <serveur>"));
            return;
        }

        String serverName = args[0];
        Optional<RegisteredServer> targetServer = server.getServer(serverName);

        if (targetServer.isEmpty()) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Le serveur '" + serverName + "' est introuvable."));
            return;
        }

        RegisteredServer destination = targetServer.get();

        int count = 0;
        for (Player player : server.getAllPlayers()) {
            player.createConnectionRequest(destination).connect();
            player.sendMessage(Component.text(Config.COLOR_INFO + "Vous avez été redirigé vers le serveur '" + serverName + "'."));
            count++;
        }

        invocation.source().sendMessage(Component.text(Config.COLOR_SUCCESS + count + " joueur(s) envoyé(s) vers '" + serverName + "'."));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 1) {
            return server.getAllServers().stream()
                    .map(s -> s.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(Config.PERMISSION_PROXY_SENDALL);
    }
}
