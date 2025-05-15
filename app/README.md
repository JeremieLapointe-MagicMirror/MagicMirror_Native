# MagicMirror Native App

<img src="assets/app_banner.png" alt="MagicMirror Native App: Contrôlez votre miroir intelligent depuis votre appareil mobile" width="100%">

**MagicMirror Native App** est une application Android native développée pour contrôler à distance votre MagicMirror. Elle offre une interface utilisateur intuitive et élégante pour surveiller et gérer votre miroir intelligent depuis n'importe où.

## ✨ Fonctionnalités

- **🔍 Surveillance en temps réel** : visualisez l'état actuel de votre miroir
- **🌡️ Monitoring de température** : affichage de la température du système Raspberry Pi
- **📱 Interface intuitive** : design épuré et navigation simple
- **📊 Vue détaillée des miroirs** : information complète sur chaque miroir connecté
- **⚙️ Paramètres avancés** : contrôle précis de votre installation
- **🔐 Accès sécurisé** : connexion avec authentification
- **🌙 Mode administrateur** : fonctionnalités supplémentaires pour les administrateurs

## 📱 Captures d'écran

<div align="center">
  <img src="assets/screenshot_login.png" alt="Écran de connexion" width="200">
  <img src="assets/screenshot_mirrors.png" alt="Liste des miroirs" width="200">
  <img src="assets/screenshot_details.png" alt="Détails du miroir" width="200">
</div>

## 🛠️ Technologies utilisées

- **Kotlin** : langage de programmation principal
- **Jetpack Compose** : framework UI moderne pour Android
- **MQTT** : protocole de communication pour l'IoT
- **Retrofit** : client HTTP pour les appels API
- **Material Design 3** : guidelines de design pour une interface moderne

## 🔌 Communication

L'application communique avec votre MagicMirror de deux façons :

### API REST
- Gestion des utilisateurs et authentification
- Récupération de la liste des miroirs
- Mise à jour des paramètres des miroirs

### MQTT
- Communication en temps réel avec le miroir
- Réception des données de température
- Surveillance de l'état du capteur PIR
- Contrôle instantané de l'affichage

## 🛡️ Sécurité

- Communication chiffrée via SSL/TLS
- Stockage sécurisé des identifiants
- Validation des certificats MQTT
- Tokens d'authentification JWT

## 📦 Installation

1. Téléchargez la dernière version de l'APK depuis la [page des releases](https://github.com/username/magicmirror-native/releases)
2. Activez l'installation d'applications depuis des sources inconnues dans les paramètres de votre appareil
3. Ouvrez le fichier APK téléchargé et suivez les instructions d'installation
4. Lancez l'application et connectez-vous avec vos identifiants

## ⚙️ Configuration

### Configuration du serveur API

Par défaut, l'application utilise l'URL suivante pour se connecter à l'API :
```
https://magicmirrorapi.jeremielapointe.ca/api/
```

### Configuration MQTT

Les paramètres MQTT par défaut sont :
- **Serveur** : mirrormqtt.jeremielapointe.ca
- **Port** : 8883 (SSL)
- **Topics** :
  - `serial/temperature` : température du système
  - `serial/etatpir` : état du capteur de mouvement

## 🧩 Modes d'écran disponibles

L'application permet de configurer trois modes d'affichage pour votre miroir :

- **Automatique** : l'écran s'active et se désactive en fonction du capteur de mouvement
- **Toujours allumé** : l'écran reste constamment allumé
- **Mode veille** : l'écran reste éteint jusqu'à ce que le mode soit changé

## 🚀 Développement

### Prérequis

- Android Studio Arctic Fox (2021.3.1) ou supérieur
- JDK 11 ou supérieur
- SDK Android 31 (Android 12) ou supérieur

### Configuration

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/username/magicmirror-native.git
   ```

2. Ouvrez le projet dans Android Studio

3. Synchronisez le projet avec les fichiers Gradle

4. Construisez et exécutez l'application

### Structure du projet

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/magicmirror_native/
│   │   │   ├── api/             # Services API et client Retrofit
│   │   │   ├── components/      # Composants UI réutilisables
│   │   │   ├── models/          # Modèles de données
│   │   │   ├── mqtt/            # Service MQTT
│   │   │   ├── repository/      # Couche d'accès aux données
│   │   │   ├── screens/         # Écrans de l'application
│   │   │   └── ui/theme/        # Thème et styles
│   │   ├── AndroidManifest.xml
│   │   └── res/                 # Ressources
│   └── androidTest/             # Tests d'interface
└── build.gradle.kts             # Configuration de build
```

## 🔄 Mises à jour futures

- Support pour les notifications push
- Widgets pour l'écran d'accueil Android
- Support d'Apple HomeKit
- Interface pour gérer les modules installés
- Galerie de photos et personnalisation
- Support pour Google Assistant et Amazon Alexa

## ⚠️ Dépannage

- **Échec de connexion à l'API** : Vérifiez votre connexion internet et que le serveur API est accessible
- **Problèmes de MQTT** : Assurez-vous que le broker MQTT est accessible et que vos identifiants sont corrects
- **L'application se ferme subitement** : Vérifiez les journaux et rapportez le problème via GitHub Issues

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE.md) pour plus de détails.

## 👏 Remerciements

- [Square](https://github.com/square) pour Retrofit
- [Eclipse Paho](https://github.com/eclipse/paho.mqtt.android) pour le client MQTT
- [JetBrains](https://github.com/JetBrains) pour Kotlin
- La communauté Android pour le support et les ressources

---

<div align="center">
  <p>Développé avec ❤️ à Rivière-du-Loup, Québec.</p>
</div>