-- Créez la table pour Panier
CREATE TABLE `panier` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `actif` tinyint(1) NOT NULL,
    `date_creation` datetime(6) NOT NULL,
    `date_modification` datetime(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Créez la table pour LigneDeCommande
CREATE TABLE `lignedecommande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `quantite` int NOT NULL,
    `prix_unitaire` double NOT NULL,
    `montant_total` double,
    `produit_id` bigint(20),
    `panier_id` bigint(20),
    FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`),
    FOREIGN KEY (`panier_id`) REFERENCES `panier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
