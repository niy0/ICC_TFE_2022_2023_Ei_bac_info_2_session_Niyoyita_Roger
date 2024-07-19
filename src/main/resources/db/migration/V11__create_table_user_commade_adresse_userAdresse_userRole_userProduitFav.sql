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

-- Créez la table pour Adresse
CREATE TABLE `adresse` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `user_id` bigint(20),
    `pays` varchar(50) NOT NULL,
    `ville` varchar(50) NOT NULL,
    `localite` varchar(50),
    `rue` varchar(50) NOT NULL,
    `numero` varchar(10) NOT NULL,
    `code_postal` varchar(10) NOT NULL,
    `departement` varchar(50),
    `nom` varchar(50) NOT NULL,
    `prenom` varchar(50) NOT NULL,
    `date_creation` datetime(6) NOT NULL,
    `date_modification` datetime(6) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table de liaison entre User et Role
CREATE TABLE `user_roles` (
    `user_id` bigint(20) NOT NULL,
    `role_id` bigint(20) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table de liaison entre User et Produit
CREATE TABLE `user_produits_favoris` (
    `user_id` bigint(20) NOT NULL,
    `produit_id` bigint(20) NOT NULL,
    PRIMARY KEY (`user_id`, `produit_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`)
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
    `panier_id` bigint(20) NOT NULL,
    `statut` varchar(20) NOT NULL,
    `date_derniere_maj_statut` datetime,
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`panier_id`) REFERENCES `panier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour l'historique des statuts de Commande
CREATE TABLE `changement_statut_commande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `commande_id` bigint(20) NOT NULL,
    `nouveau_statut` varchar(20) NOT NULL,
    `date_changement_statut` datetime NOT NULL,
    FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
