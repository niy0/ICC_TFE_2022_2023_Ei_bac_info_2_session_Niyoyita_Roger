-- Nouvelle table pour methode_commande
CREATE TABLE `methode_commande` (
    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insération des valeurs pour methode_commande
INSERT INTO `methode_commande` (`name`) VALUES ('DELIVERY');
INSERT INTO `methode_commande` (`name`) VALUES ('PICKUP');

-- Nouvelle colonne method_commande_id dans la table commande
ALTER TABLE `commande`
ADD COLUMN `method_commande_id` bigint(20) NOT NULL,
ADD CONSTRAINT `fk_commande_methode_commande` FOREIGN KEY (`method_commande_id`) REFERENCES `methode_commande`(`id`);
