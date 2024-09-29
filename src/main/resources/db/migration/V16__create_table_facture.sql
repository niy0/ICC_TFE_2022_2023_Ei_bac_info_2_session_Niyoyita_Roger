-- Créez la table pour Facture
create TABLE `facture` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `numero_facture` varchar(100) NOT NULL UNIQUE, -- Numéro de facture unique
    `date_facture` datetime NOT NULL, -- Date à laquelle la facture est générée
    `montant_ht` DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Montant hors taxes
    `montant_tva` DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Montant de la TVA
    `montant_ttc` DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Montant toutes taxes comprises
    `commande_id` bigint(20), -- Lien avec la commande
    `adresse_facturation_id` bigint(20), -- Lien avec l'adresse de facturation
    `adresse_livraison_id` bigint(20), -- Lien avec l'adresse de livraison (si différent)
    FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`), -- Relation avec la commande
    FOREIGN KEY (`adresse_facturation_id`) REFERENCES `adresse` (`id`), -- Relation avec l'adresse de facturation
    FOREIGN KEY (`adresse_livraison_id`) REFERENCES `adresse` (`id`) -- Relation avec l'adresse de livraison
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Modification de la table produit pour ajouter le tauxTVA
alter table `produit`
ADD COLUMN `taux_tva` DECIMAL(5, 2) NOT NULL DEFAULT 0.00; -- Ajout du taux de TVA avec un maximum de 5 chiffres dont 2 après la virgule

-- Modification de la table catégorie pour ajouter le tauxTVA
alter table `categorie` ADD COLUMN `taux_tva` DECIMAL(5, 2) NOT NULL DEFAULT 0.00;
