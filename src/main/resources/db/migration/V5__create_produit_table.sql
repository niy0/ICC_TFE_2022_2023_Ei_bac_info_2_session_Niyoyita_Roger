CREATE TABLE `produit` (
    `id` bigint(20) AUTO_INCREMENT,
    `nom` varchar(255) NOT NULL,
    `description` varchar(2000) NOT NULL,
    `prix` double NOT NULL,
    `quantite` int NOT NULL,
    `marque` varchar(40),
    `image_principale` LONGBLOB,
    `categorie_id` bigint(20),
    `disponibilite` boolean NOT NULL DEFAULT 1,
    `date_creation` datetime(6) NOT NULL,
    `date_modification` datetime(6),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
