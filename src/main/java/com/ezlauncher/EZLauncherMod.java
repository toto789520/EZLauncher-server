package com.ezlauncher;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

@Mod(modid = EZLauncherMod.MODID, version = EZLauncherMod.VERSION)
public class EZLauncherMod {
    public static final String MODID = "ezlauncher";
    public static final String VERSION = "1.0";
    private static HttpServer server;
    private static final int API_PORT = 8080;

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
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

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        if (server != null) {
            server.stop(0);
            System.out.println("[EZLauncher] API arrêtée");
        }
    }
}
