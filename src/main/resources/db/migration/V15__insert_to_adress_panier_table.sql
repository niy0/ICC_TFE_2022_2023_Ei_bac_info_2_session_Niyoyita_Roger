-- Insertion de paniers pour les utilisateurs avec les IDs spécifiés

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 2);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 52);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 53);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 54);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 55);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 56);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 57);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 58);

insert into `panier` (`actif`, `date_creation`, `date_modification`, `user_id`)
VALUES (1, NOW(), NOW(), 59);


-- Insérer les adresses pour chaque utilisateur de type 'User'

-- Adresse pour User1
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Paris', 'Centre-Ville', 'Rue de Rivoli', '10', '75001', 'Île-de-France', 'User1', 'User1', NOW(), NOW(), u.id
from `user` u WHERE u.id = 52;

-- Adresse pour User2
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Marseille', 'Centre-Ville', 'Avenue des Champs-Élysées', '20', '13001', 'Bouches-du-Rhône', 'User2', 'User2', NOW(), NOW(), u.id
from `user` u WHERE u.id = 53;

-- Adresse pour User3
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Lyon', 'Centre-Ville', 'Boulevard de la Croisette', '30', '69001', 'Rhône', 'User3', 'User3', NOW(), NOW(), u.id
from `user` u WHERE u.id = 54;

-- Adresse pour User4
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Toulouse', 'Centre-Ville', 'Rue de la Paix', '40', '31000', 'Haute-Garonne', 'User4', 'User4', NOW(), NOW(), u.id
from `user` u WHERE u.id = 55;

-- Adresse pour User5
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Nice', 'Centre-Ville', 'Rue Saint-Honoré', '50', '06000', 'Alpes-Maritimes', 'User5', 'User5', NOW(), NOW(), u.id
from `user` u WHERE u.id = 56;

-- Insérer les adresses pour chaque utilisateur de type 'Employee'

-- Adresse pour Employee1
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Paris', 'Centre-Ville', 'Rue de Rivoli', '60', '75001', 'Île-de-France', 'Employee1', 'Employee1', NOW(), NOW(), u.id
from `user` u WHERE u.id = 57;

-- Adresse pour Employee2
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Marseille', 'Centre-Ville', 'Avenue des Champs-Élysées', '70', '13001', 'Bouches-du-Rhône', 'Employee2', 'Employee2', NOW(), NOW(), u.id
from `user` u WHERE u.id = 58;

-- Adresse pour Employee3
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Lyon', 'Centre-Ville', 'Boulevard de la Croisette', '80', '69001', 'Rhône', 'Employee3', 'Employee3', NOW(), NOW(), u.id
from `user` u WHERE u.id = 59;

-- Ajouter des adresses pour les utilisateurs existants

-- Adresse pour l'utilisateur avec l'email 'user@test.com' (id=1)
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Paris', 'Centre-Ville', 'Rue de Rivoli', '12', '75001', 'Île-de-France', u.nom, u.prenom, NOW(), NOW(), u.id
from `user` u WHERE u.id = 1;

-- Adresse pour l'utilisateur avec l'email 'employee@test.com' (id=2)
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Marseille', 'Centre-Ville', 'Avenue des Champs-Élysées', '34', '13001', 'Bouches-du-Rhône', u.nom, u.prenom, NOW(), NOW(), u.id
from `user` u WHERE u.id = 2;

-- Adresse pour l'utilisateur avec l'email 'admin@test.com' (id=3)
insert into `adresse` (`pays`, `ville`, `localite`, `rue`, `numero`, `code_postal`, `departement`, `nom`, `prenom`, `date_creation`, `date_modification`, `utilisateur_id`)
select 'France', 'Lyon', 'Centre-Ville', 'Boulevard de la Croisette', '56', '69001', 'Rhône', u.nom, u.prenom, NOW(), NOW(), u.id
from `user` u WHERE u.id = 3;
