-- Créez la table pour Panier
CREATE TABLE `panier` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `actif` tinyint(1),
    `date_creation` datetime(6) NOT NULL,
    `date_modification` datetime(6),
    `user_id` bigint(20) -- Colonne pour stocker la relation avec l'utilisateur
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour User
CREATE TABLE `user` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `nom` varchar(50) NOT NULL,
    `prenom` varchar(50) NOT NULL,
    `email` varchar(255) unique NOT NULL,
    `password` varchar(255) NOT NULL,
    `is_logged_in` tinyint(1) NOT NULL,
    `telephone` varchar(15),
    `sexe` varchar(10) NOT NULL,
    `date_creation` datetime(6),
    `date_modification` datetime(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour Role (table parente de user_role)
CREATE TABLE `role` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `nom` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour Commande
CREATE TABLE `commande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `user_id` bigint(20),
    `prenom` varchar(50) NOT NULL,
    `nom` varchar(50) NOT NULL,
    `email` varchar(255) NOT NULL,
    `rue` varchar(100) NOT NULL,
    `numero` varchar(10) NOT NULL,
    `localite` varchar(50),
    `ville` varchar(50) NOT NULL,
    `code_postal` varchar(10) NOT NULL,
    `departement` varchar(50),
    `pays` varchar(50) NOT NULL,
    `date_commande` datetime NOT NULL,
    `statut` varchar(20) NOT NULL,
    `method_commande` ENUM('DELIVERY', 'PICKUP') NOT NULL,
    `date_derniere_maj_statut` datetime,
    `montant_commande` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour l'historique des statuts de Commande
CREATE TABLE `changement_statut_commande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `commande_id` bigint(20) NOT NULL,
    `nouveau_statut` varchar(20) NOT NULL,
    `date_changement_statut` datetime NOT NULL,
    FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour LigneDeCommande
CREATE TABLE `lignedecommande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `quantite` int NOT NULL,
    `prix_unitaire` double NOT NULL,
    `montant_total` double,
    `produit_id` bigint(20),
    `panier_id` bigint(20),
    `commande_id` bigint(20), -- Ajout de la relation avec la commande
    FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`),
    FOREIGN KEY (`panier_id`) REFERENCES `panier` (`id`),
     FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
