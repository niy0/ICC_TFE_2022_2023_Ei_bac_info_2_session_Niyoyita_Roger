CREATE TABLE `produit_mots_cles` (
    `produit_id` BIGINT(20) NOT NULL,
    `mot_cle_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`produit_id`, `mot_cle_id`),
    FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`mot_cle_id`) REFERENCES `mot_cle` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;