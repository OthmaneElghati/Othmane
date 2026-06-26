# Plateforme Intelligente de Gestion des Missions Humanitaires et des Benevoles au Maroc

Plateforme web complete pour la gestion des missions humanitaires, des benevoles, des beneficiaires, des donations, des convois et des evenements au Maroc.

## Technologies

### Backend
- Java 21, Spring Boot 3.2.5, Spring Security, JWT, Spring Data JPA, Hibernate, Lombok, OpenPDF, Swagger/OpenAPI

### Frontend
- React 18, Vite, Bootstrap 5, React Router, Axios, Chart.js, Leaflet, SweetAlert2, i18next

### Base de donnees
- MySQL 8 (ou H2 pour le developpement)

## Fonctionnalites

- Authentification JWT avec 5 roles (Super Admin, Gestionnaire, Benevole, Beneficiaire, Donateur)
- CRUD complet pour tous les modules (Missions, Benevoles, Beneficiaires, Donations, Convois, Evenements)
- Dashboard avec statistiques et graphiques (Chart.js)
- Carte interactive (Leaflet) centree sur le Maroc
- Generation de rapports PDF
- Systeme de notifications
- Support multilingue (Francais, Arabe, Anglais)
- Intelligence de decision (scoring de priorite, recommandation de benevoles, niveau d'urgence, loyaute des donateurs)
- Donnees de demonstration realistes (30 missions, 50 benevoles, 100 beneficiaires, 100 donations, 20 convois, 15 evenements)
- Design responsive et professionnel

## Pre-requis

- Java 21 (OpenJDK)
- Maven 3.6+
- Node.js 18+
- npm 9+
- MySQL 8 (optionnel, H2 disponible pour le developpement)

## Installation

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd Othmane
```

### 2. Configuration de la base de donnees

#### Option A: MySQL (production)
```bash
mysql -u root -p < database/init.sql
```

Configurer les identifiants dans `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

#### Option B: H2 (developpement sans MySQL)
```bash
cd backend
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### 3. Demarrer le Backend

```bash
cd backend
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn clean package -DskipTests
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 java -jar target/backend-1.0.0.jar
```

Le backend demarre sur `http://localhost:8080`

### 4. Demarrer le Frontend

```bash
cd frontend
npm install
npm run dev
```

Le frontend demarre sur `http://localhost:5173`

## Comptes de demonstration

| Role | Email | Mot de passe |
|------|-------|-------------|
| Super Admin | admin@humanitaire.ma | admin123 |
| Gestionnaire | manager@humanitaire.ma | manager123 |
| Benevole | volunteer@humanitaire.ma | volunteer123 |
| Donateur | donor@humanitaire.ma | donor123 |

## API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Endpoints principaux

| Methode | Endpoint | Description |
|---------|----------|-------------|
| POST | /api/auth/login | Connexion |
| POST | /api/auth/register | Inscription |
| GET | /api/dashboard/stats | Statistiques du dashboard |
| GET/POST/PUT/DELETE | /api/missions | Gestion des missions |
| GET/POST/PUT/DELETE | /api/volunteers | Gestion des benevoles |
| GET/POST/PUT/DELETE | /api/beneficiaries | Gestion des beneficiaires |
| GET/POST/PUT/DELETE | /api/donations | Gestion des donations |
| GET/POST/PUT/DELETE | /api/convoys | Gestion des convois |
| GET/POST/PUT/DELETE | /api/events | Gestion des evenements |
| GET/POST/DELETE | /api/notifications | Gestion des notifications |
| GET | /api/reports/generate/{type} | Generation de rapports PDF |

## Structure du projet

```
Othmane/
├── backend/
│   ├── src/main/java/com/humanitaire/backend/
│   │   ├── config/          # Configuration (Security, CORS, DataSeeder)
│   │   ├── controller/      # Controleurs REST
│   │   ├── dto/             # Objets de transfert de donnees
│   │   ├── entity/          # Entites JPA
│   │   ├── exception/       # Gestion des exceptions
│   │   ├── repository/      # Repositories Spring Data JPA
│   │   ├── security/        # JWT, UserDetails, Filtres
│   │   └── service/         # Logique metier
│   ├── src/main/resources/
│   │   ├── application.properties      # Configuration MySQL
│   │   └── application-h2.properties   # Configuration H2
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/      # Composants reutilisables (Layout)
│   │   ├── context/         # Context API (Auth, Language)
│   │   ├── pages/           # Pages de l'application
│   │   ├── services/        # Services API (Axios)
│   │   ├── App.jsx          # Routeur principal
│   │   ├── main.jsx         # Point d'entree
│   │   └── index.css        # Styles globaux
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── database/
│   └── init.sql             # Script d'initialisation
└── README.md
```

## Ouvrir dans un IDE

### IntelliJ IDEA
1. File > Open > selectionner le dossier `backend/`
2. Configurer le SDK Java 21
3. Maven: Reload Project

### Visual Studio Code
1. Ouvrir le dossier racine du projet
2. Installer les extensions recommandees (Java Extension Pack, ES7+ React)
3. Terminal: `cd frontend && npm install`
