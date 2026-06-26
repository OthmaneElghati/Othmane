# Plateforme Intelligente de Gestion des Missions Humanitaires et des Bénévoles au Maroc

**Intelligent Humanitarian Mission and Volunteer Management Platform for Morocco**

## Description

Plateforme web professionnelle dédiée aux organisations humanitaires marocaines. Elle permet la gestion complète des missions humanitaires, bénévoles, bénéficiaires, donations, convois logistiques, événements, rapports et notifications.

## Stack Technologique

### Backend
- **Java 21** + **Spring Boot 3.2.5**
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate
- MySQL 8 (base de données)
- OpenAPI/Swagger (documentation API)
- Maven (build tool)

### Frontend
- **React 19** + Vite
- Bootstrap 5 + Bootstrap Icons
- Chart.js (graphiques)
- Leaflet.js (cartes interactives)
- Axios (requêtes HTTP)
- SweetAlert2 (notifications)
- AOS (animations)
- React Router DOM

## Fonctionnalités

- **Dashboard** avec statistiques et graphiques (Chart.js)
- **Gestion des Missions** : CRUD, filtrage par région/statut/priorité
- **Gestion des Bénévoles** : inscription, compétences, disponibilité, recommandation
- **Gestion des Bénéficiaires** : niveaux d'urgence, suivi familial
- **Gestion des Donations** : paiement (CMI, PayPal, Stripe), historique
- **Convois Logistiques** : suivi GPS, statuts, visualisation sur carte
- **Événements** : création, gestion des participants
- **Carte Interactive** (Leaflet.js) : missions, convois, routes
- **Rapports PDF** : missions, donations, bénévoles, mensuel
- **Notifications** en temps réel
- **Multi-langue** : Français, Arabe, Anglais
- **Authentification JWT** avec RBAC (Admin, Manager, Bénévole, Donateur)

## Prérequis

| Logiciel | Version |
|----------|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| MySQL | 8.0+ |

## Installation Rapide

### 1. Base de données

```bash
mysql -u root -p
```
```sql
CREATE DATABASE humanitaire_maroc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ou exécuter le script complet :
```bash
mysql -u root -p < database/schema.sql
```

### 2. Backend

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Le backend démarre sur `http://localhost:8080`

API Swagger: `http://localhost:8080/swagger-ui.html`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Le frontend démarre sur `http://localhost:5173`

## Comptes de Démonstration

| Rôle | Email | Mot de passe |
|------|-------|-------------|
| Admin | admin@humanitaire.ma | admin123 |
| Manager | manager@humanitaire.ma | manager123 |
| Bénévole | benevole@humanitaire.ma | benevole123 |
| Donateur | donateur@humanitaire.ma | donateur123 |

## Structure du Projet

```
├── backend/
│   ├── src/main/java/com/humanitaire/backend/
│   │   ├── config/          # Configuration (Security, CORS, Swagger, DataSeeder)
│   │   ├── controller/      # Contrôleurs REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entités JPA
│   │   ├── exception/       # Gestion des exceptions
│   │   ├── repository/      # Repositories Spring Data
│   │   ├── security/        # JWT, UserDetails
│   │   └── service/         # Services métier
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/      # Composants réutilisables (Layout)
│   │   ├── context/         # Context API (Auth, Language)
│   │   ├── pages/           # Pages de l'application
│   │   ├── services/        # Services API (Axios)
│   │   ├── App.jsx          # Routage principal
│   │   └── main.jsx         # Point d'entrée
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── database/
│   └── schema.sql           # Script de création BDD
└── README.md
```

## Configuration

### Backend (application.properties)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/humanitaire_maroc?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
```

### Frontend (vite.config.js)

Le proxy API est configuré pour rediriger `/api` vers `http://localhost:8080`.

## Build Production

### Backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
```

Les fichiers de production sont dans `frontend/dist/`.

## API Documentation

Une fois le backend lancé, accédez à la documentation Swagger :
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Données de Démonstration

Le `DataSeeder` crée automatiquement au premier lancement :
- 4 utilisateurs (admin, manager, bénévole, donateur)
- 30 missions dans différentes régions du Maroc
- 50 bénévoles avec compétences variées
- 100 bénéficiaires
- 100 donations
- 20 convois logistiques
- 15 événements

## Régions du Maroc

La plateforme couvre les 12 régions :
- Rabat-Salé-Kénitra
- Casablanca-Settat
- Marrakech-Safi
- Fès-Meknès
- Tanger-Tétouan-Al Hoceïma
- Souss-Massa
- Oriental
- Béni Mellal-Khénifra
- Drâa-Tafilalet
- Laâyoune-Sakia El Hamra
- Dakhla-Oued Ed-Dahab
- Guelmim-Oued Noun

## Licence

Projet de Fin d'Études - Génie Informatique
