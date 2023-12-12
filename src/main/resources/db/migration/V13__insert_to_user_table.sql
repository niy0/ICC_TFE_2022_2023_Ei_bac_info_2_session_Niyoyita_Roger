-- Insérer un utilisateur avec le rôle User
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
VALUES ('User', 'User', 'user@test.com', '$2a$10$/U1bGxi2w5l4VTuu1.deR.ImiDIOw6nIiCPp30rMmReLjl7oeicH.', 1, '123456789', 'AUTRE' , NOW(), NOW());

-- Insérer un utilisateur avec le rôle Employee
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
VALUES ('Employee', 'Employee', 'employee@test.com', '$2a$10$/CImy.8zc4TUeBD2eJpbSe2XJakv9A0dTxCo64ePtbURjT3zyou8q', 1, '987654321', 'FEMME', NOW(), NOW());

-- Insérer un utilisateur avec le rôle Admin
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
VALUES ('Admin', 'Admin', 'admin@test.com', '$2a$10$vuqVI9hNEUMb/Bb/kF6fZuKh5i8OuW3FUPHM.pRCF9Pkns2IV9.TG', 1, '555555555', 'HOMME' , NOW(), NOW());

-- Associer l'utilisateur 1 au rôle 1 (User)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES (1, 1);

-- Associer l'utilisateur 2 au rôle 2 (Employee)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES (2, 2);

-- Associer l'utilisateur 3 au rôle 3 (Admin)
INSERT INTO `user_roles` (`user_id`, `role_id`)
VALUES (3, 3);

-- Associer les utilisateurs a leur panier
INSERT INTO `panier` ( `date_creation`, `date_modification`, `user_id`) VALUES (NOW(), NOW(),1);
INSERT INTO `panier` ( `date_creation`, `date_modification`, `user_id`) VALUES (NOW(), NOW(),2);
INSERT INTO `panier` ( `date_creation`, `date_modification`, `user_id`) VALUES (NOW(), NOW(),3);