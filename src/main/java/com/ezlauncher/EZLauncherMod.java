package com.ezlauncher;

import com.ezlauncher.api.CheckHandler;
import com.ezlauncher.api.DownloadHandler;
import com.ezlauncher.api.ServerListHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

@Mod(EZLauncherMod.MODID)
public class EZLauncherMod {
    public static final String MODID = "ezlauncher";
    private static HttpServer server;
    private static final int API_PORT = 8080;

    public EZLauncherMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onServerStarting(ServerStartedEvent event) {
        try {
            server = HttpServer.create(new InetSocketAddress(API_PORT), 0);
            server.createContext("/api/servers", new ServerListHandler());
            server.createContext("/api/check", new CheckHandler());
            server.createContext("/api/download", new DownloadHandler());
            server.start();
            System.out.println("[EZLauncher] API démarrée sur le port " + API_PORT);
        } catch (IOException e) {
            System.err.println("[EZLauncher] Erreur au démarrage de l'API : " + e.getMessage());
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (server != null) {
            server.stop(0);
            System.out.println("[EZLauncher] API arrêtée");
        }
    }
}
