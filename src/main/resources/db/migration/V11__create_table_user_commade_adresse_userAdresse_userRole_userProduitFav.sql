

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
    `nom` varchar(50),
    `prenom` varchar(50),
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


