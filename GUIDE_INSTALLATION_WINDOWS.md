# Guide d'Installation sur Windows

## Guide complet pour installer et exécuter la Plateforme Humanitaire

---

## 1. Installation de JDK 21

1. Télécharger JDK 21 depuis : https://adoptium.net/temurin/releases/?version=21
2. Sélectionner **Windows x64** et **JDK** puis **msi**
3. Exécuter l'installateur `.msi`
4. Cocher "Set JAVA_HOME variable" pendant l'installation
5. Vérifier l'installation :
```cmd
java --version
```
Résultat attendu : `openjdk 21.x.x`

### Configuration manuelle (si nécessaire)

1. Panneau de configuration → Système → Paramètres système avancés → Variables d'environnement
2. Ajouter variable système :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`
3. Modifier `Path` → Ajouter : `%JAVA_HOME%\bin`

---

## 2. Installation de Maven

1. Télécharger depuis : https://maven.apache.org/download.cgi
2. Télécharger `apache-maven-3.9.x-bin.zip`
3. Extraire dans `C:\Program Files\Apache\maven`
4. Variables d'environnement :
   - Ajouter `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - Modifier `Path` → Ajouter : `%MAVEN_HOME%\bin`
5. Vérifier :
```cmd
mvn --version
```

---

## 3. Installation de Node.js

1. Télécharger depuis : https://nodejs.org/
2. Choisir la version **LTS** (18.x ou 20.x)
3. Exécuter l'installateur
4. Cocher "Automatically install necessary tools"
5. Vérifier :
```cmd
node --version
npm --version
```

---

## 4. Installation de MySQL 8

1. Télécharger MySQL Installer depuis : https://dev.mysql.com/downloads/installer/
2. Choisir "Full" installation
3. Pendant la configuration :
   - Port : `3306`
   - Mot de passe root : choisir un mot de passe (ex: `root123`)
   - Démarrage automatique : Oui
4. Vérifier :
```cmd
mysql -u root -p
```

### Création de la base de données

```sql
CREATE DATABASE humanitaire_maroc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE humanitaire_maroc;
```

---

## 5. Configuration de VS Code

1. Télécharger : https://code.visualstudio.com/
2. Extensions recommandées :
   - **Extension Pack for Java**
   - **Spring Boot Extension Pack**
   - **ES7+ React/Redux/React-Native snippets**
   - **Prettier**
   - **Auto Rename Tag**
   - **Bracket Pair Colorization**

---

## 6. Configuration d'IntelliJ IDEA

1. Télécharger : https://www.jetbrains.com/idea/download/
2. Ouvrir le dossier `backend/` comme projet Maven
3. IntelliJ détectera automatiquement le pom.xml
4. Attendre le téléchargement des dépendances
5. Configurer le JDK 21 : File → Project Structure → SDK → JDK 21

---

## 7. Démarrage du Projet

### Étape 1 : Base de données

```cmd
mysql -u root -p < database/schema.sql
```

Ou manuellement :
```sql
CREATE DATABASE humanitaire_maroc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Étape 2 : Configuration Backend

Ouvrir `backend/src/main/resources/application.properties` et modifier :
```properties
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE_MYSQL
```

### Étape 3 : Lancer le Backend

```cmd
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Le backend sera accessible sur : http://localhost:8080

Swagger UI : http://localhost:8080/swagger-ui.html

### Étape 4 : Lancer le Frontend

Ouvrir un nouveau terminal :
```cmd
cd frontend
npm install
npm run dev
```

Le frontend sera accessible sur : http://localhost:5173

---

## 8. Commandes de Build

### Backend - Build Production
```cmd
cd backend
mvn clean package -DskipTests
```

Le fichier JAR sera dans `backend/target/backend-0.0.1-SNAPSHOT.jar`

Pour exécuter :
```cmd
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Frontend - Build Production
```cmd
cd frontend
npm run build
```

Les fichiers seront dans `frontend/dist/`

---

## 9. Connexion à l'Application

Ouvrir le navigateur : http://localhost:5173

### Comptes de démonstration :

| Rôle | Email | Mot de passe |
|------|-------|-------------|
| Admin | admin@humanitaire.ma | admin123 |
| Manager | manager@humanitaire.ma | manager123 |
| Bénévole | benevole@humanitaire.ma | benevole123 |
| Donateur | donateur@humanitaire.ma | donateur123 |

---

## 10. Résolution de Problèmes Courants

### Port 8080 déjà utilisé

```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### MySQL refuse la connexion

1. Vérifier que le service MySQL est démarré :
```cmd
net start MySQL80
```

2. Vérifier les identifiants dans `application.properties`

### npm install échoue

```cmd
npm cache clean --force
rmdir /s node_modules
del package-lock.json
npm install
```

### JAVA_HOME non trouvé

```cmd
echo %JAVA_HOME%
```

Si vide, configurer la variable comme indiqué à l'étape 1.

### Maven non reconnu

Vérifier que `%MAVEN_HOME%\bin` est bien dans le `Path` système.

### Erreur "Access Denied" MySQL

```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'votre_mot_de_passe';
FLUSH PRIVILEGES;
```

### Le frontend ne se connecte pas au backend

- Vérifier que le backend tourne bien sur le port 8080
- Le proxy Vite redirige automatiquement `/api` vers `http://localhost:8080`
- En cas de problème CORS, vérifier `CorsConfig.java`

---

## 11. Architecture du Projet

```
projet/
├── backend/                    # API Spring Boot
│   ├── src/
│   │   └── main/
│   │       ├── java/          # Code Java
│   │       └── resources/     # Configuration
│   └── pom.xml               # Dépendances Maven
├── frontend/                   # Interface React
│   ├── src/
│   │   ├── components/        # Composants
│   │   ├── context/           # Gestion d'état
│   │   ├── pages/             # Pages
│   │   └── services/          # API calls
│   └── package.json           # Dépendances npm
├── database/
│   └── schema.sql             # Script SQL
├── README.md
└── GUIDE_INSTALLATION_WINDOWS.md
```

---

## 12. Vérification Complète

Checklist de validation :

- [ ] JDK 21 installé (`java --version`)
- [ ] Maven installé (`mvn --version`)
- [ ] Node.js installé (`node --version`)
- [ ] MySQL démarré et accessible
- [ ] Base de données `humanitaire_maroc` créée
- [ ] Backend compilé sans erreur
- [ ] Backend démarré sur port 8080
- [ ] Swagger accessible (http://localhost:8080/swagger-ui.html)
- [ ] Frontend installé (`npm install` réussi)
- [ ] Frontend démarré sur port 5173
- [ ] Login fonctionne avec les comptes de démo
- [ ] Dashboard affiche les statistiques
- [ ] Carte affiche les missions
