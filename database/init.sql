-- ============================================
-- Plateforme Humanitaire du Maroc
-- Script d'initialisation de la base de données
-- ============================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS humanitaire_maroc
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE humanitaire_maroc;

-- Les tables sont créées automatiquement par Hibernate (ddl-auto=update)
-- Ce script est fourni pour référence et pour la configuration initiale

-- ============================================
-- NOTES
-- ============================================
-- 1. Les tables sont générées automatiquement au démarrage de l'application
--    grâce à spring.jpa.hibernate.ddl-auto=update
-- 2. Les données de démonstration sont insérées automatiquement par DataSeeder.java
--    au premier lancement (quand la table roles est vide)
-- 3. Comptes de démonstration:
--    - admin@humanitaire.ma / admin123 (Super Administrateur)
--    - manager@humanitaire.ma / manager123 (Gestionnaire de Missions)
--    - volunteer@humanitaire.ma / volunteer123 (Bénévole)
--    - donor@humanitaire.ma / donor123 (Donateur)

-- Accorder les privilèges (optionnel, selon votre configuration MySQL)
-- GRANT ALL PRIVILEGES ON humanitaire_maroc.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;
