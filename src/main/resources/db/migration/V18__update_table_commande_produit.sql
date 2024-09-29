-- Modification de la table Commande pour ajouter les frais de commande
alter table `commande`
ADD COLUMN `frais_commande` DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

-- Modification de la table Produit pour ajouter le compteur de vues
alter table `produit`
ADD COLUMN `vues` INT NOT NULL DEFAULT 0;


