CREATE TABLE `marque` (
    `id` bigint(20) AUTO_INCREMENT,
    `nom` varchar(40) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `produit`
DROP COLUMN `marque`,
ADD COLUMN `marque_id` bigint(20) DEFAULT NULL,
ADD CONSTRAINT `fk_produit_marque`
    FOREIGN KEY (`marque_id`) REFERENCES `marque` (`id`);

