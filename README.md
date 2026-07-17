# EZLauncher Server

Plugin Forge pour exposer une API HTTPS et gérer les modpacks pour le launcher EZLauncher.

## 📌 Fonctionnalités
- Expose une API HTTPS sur le port **8080** avec les endpoints :
  - `GET /api/servers` : Liste des serveurs (avec MOTD, version, hash, etc.).
  - `GET /api/check?player={pseudo}&hash={hash_local}` : Vérifie la whitelist et le hash du modpack.
  - `GET /api/download` : Télécharge le modpack (`mode_client.zip`).
- Calcule le **hash SHA-256** du dossier `mode_client/`.
- Vérifie la **whitelist** via `whitelist.json`.
- Zippe automatiquement le dossier `mode_client/` si `mode_client.zip` n'existe pas.

## 🛠️ Installation
1. **Compiler le mod** :
   - Exécutez `gradlew build` (nécessite Gradle et JDK 8+).
   - Le mod sera généré dans `build/libs/EZLauncher-1.0.jar`.

2. **Installer sur le serveur** :
   - Placez le fichier `.jar` dans le dossier `mods/` de votre serveur Forge.
   - Redémarrez le serveur.

3. **Configuration** :
   - Assurez-vous que :
     - Le dossier `mode_client/` existe (ou le fichier `mode_client.zip`).
     - Les fichiers `server-icon.png` et `server-icon-big.png` (optionnel) sont présents.
     - Le fichier `whitelist.json` est présent si le serveur est privé.

## 📡 Endpoints API

### `GET /api/servers`
**Réponse** :
```json
{
  "servers": [
    {
      "ip": "127.0.0.1",
      "port": 25565,
      "name": "Mon Serveur",
      "motd": "Bienvenue !",
      "version": "1.19.2-forge-43.2.0",
      "smallIcon": "/server-icon.png",
      "bigIcon": "/server-icon-big.png",
      "hash": "a1b2c3...",
      "isPrivate": true
    }
  ]
}
```

### `GET /api/check?player={pseudo}&hash={hash_local}`
**Réponse** :
```json
{
  "whitelisted": true,
  "hashMatches": true,
  "upToDate": true,
  "downloadUrl": "/api/download"
}
```

### `GET /api/download`
Télécharge le fichier `mode_client.zip` (ou zippe `mode_client/` si le fichier n'existe pas).

## ⚠️ Notes
- Le port de l'API est **8080** par défaut (modifiable dans `EZLauncherMod.java`).
- Le mod nécessite **Forge 1.19.2-43.2.0** (à adapter selon votre version).
- Pour les serveurs **privés**, activez la whitelist dans `server.properties` (`white-list=true`).
