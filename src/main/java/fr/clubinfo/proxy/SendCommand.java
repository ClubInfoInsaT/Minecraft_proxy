package fr.clubinfo.proxy;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SendCommand implements SimpleCommand {

    private final ProxyServer server;

    public SendCommand(ProxyServer proxy) {
        this.server = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission(Config.PERMISSION_PROXY_SEND)) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Vous n'avez pas la permission d'exécuter cette commande."));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length != 2) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Usage: /send <joueur> <serveur>"));
            return;
        }

        String targetName = args[0];
        String serverName = args[1];

        Optional<Player> targetPlayer = server.getPlayer(targetName);
        if (targetPlayer.isEmpty()) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Aucun joueur n'existe pas"));
            return;
        }

        Optional<RegisteredServer> targetServer = server.getServer(serverName);
        if (targetServer.isEmpty()) {
            invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Le serveur '" + serverName + "' est introuvable."));
            return;
        }

        targetPlayer.get().createConnectionRequest(targetServer.get()).connect().thenAccept(result -> {
            if (result.isSuccessful()) {
                invocation.source().sendMessage(Component.text(Config.COLOR_SUCCESS + "Le joueur '" + targetName + "' a été envoyé sur '" + serverName + "'."));
                targetPlayer.get().sendMessage(Component.text(Config.COLOR_INFO + "Vous avez été redirigé vers le serveur '" + serverName + "'."));
            } else {
                invocation.source().sendMessage(Component.text(Config.COLOR_ERROR + "Erreur lors de l'envoi du joueur '" + targetName + "' vers le serveur '" + serverName + "'."));
            }
        });
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 1) {
            // Suggestion des joueurs connectés
            return server.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Suggestion des serveurs disponibles
            return server.getAllServers().stream()
                    .map(s -> s.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(Config.PERMISSION_PROXY_SEND);
    }


}
