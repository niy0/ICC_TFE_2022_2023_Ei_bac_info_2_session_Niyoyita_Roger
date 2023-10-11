-- Ajout des colonnes min_stock, middle_stock, max_stock, actif et cote à la table produit
ALTER TABLE produit
ADD COLUMN min_stock INT,
ADD COLUMN middle_stock INT,
ADD COLUMN max_stock INT,
ADD COLUMN actif BOOLEAN,
ADD COLUMN cote double,
ADD COLUMN type_prix VARCHAR(255) NOT NULL;
