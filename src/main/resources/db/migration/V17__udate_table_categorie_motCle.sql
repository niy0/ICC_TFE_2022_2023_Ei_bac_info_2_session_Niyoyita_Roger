-- Met à jour certaines catégories existantes avec le taux de TVA
update `categorie`
SET `cat_nom` = 'Viandes et poissons surgelés', `taux_tva` = 5.5
where `id` = 1;

update `categorie`
SET `cat_nom` = 'Boissons non alcoolisées', `taux_tva` = 5.5
where `id` = 4;

update `categorie`
SET `cat_nom` = 'Aliments pour bébés', `taux_tva` = 5.5
where `id` = 8;

-- Ajoute les autres catégories avec leur taux de TVA respectif
update `categorie`
SET `taux_tva` = 5.5
where `id` = 2; -- Produits laitiers

update `categorie`
SET `taux_tva` = 5.5
where `id` = 3; -- Épicerie sèche

update `categorie`
SET `taux_tva` = 5.5
where `id` = 5; -- Produits surgelés

update `categorie`
SET `taux_tva` = 20.0
where `id` = 6; -- Hygiène personnelle

update `categorie`
SET `taux_tva` = 20.0
where `id` = 7; -- Entretien ménager

update `categorie`
SET `taux_tva` = 20.0
where `id` = 9; -- Produits de beauté

update `categorie`
SET `taux_tva` = 20.0
where `id` = 10; -- Articles pour animaux de compagnie

update `categorie`
SET `taux_tva` = 20.0
where `id` = 11; -- Articles ménagers

-- Ajoute de nouvelles catégories avec le taux de TVA
insert into `categorie` (`cat_nom`, `taux_tva`) VALUES
( 'Alcool', 20.0),
( 'Produits pour bébés non alimentaires', 20.0);

-- Ajout de nouveaux mots-clés à la table `mot_cle`
insert into `mot_cle` (`nom`) VALUES
('Viandes surgelées'),
('Poissons surgelés'),
('Produits laitiers'),
('Épicerie sèche'),
('Boissons alcoolisées'),
('Boissons non alcoolisées'),
('Alcool'),
('Produits surgelés'),
('Hygiène personnelle'),
('Entretien ménager'),
('Aliments pour bébés'),
('Produits pour bébés non alimentaires'),
('Produits de beauté'),
('Animaux'),
('Articles ménagers');