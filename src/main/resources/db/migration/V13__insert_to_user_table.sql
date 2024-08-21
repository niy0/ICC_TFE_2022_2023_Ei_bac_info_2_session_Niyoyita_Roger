-- Insérer un utilisateur avec le rôle User s'il n'existe pas déjà
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
SELECT 'User', 'User', 'user@test.com', '$2a$10$/U1bGxi2w5l4VTuu1.deR.ImiDIOw6nIiCPp30rMmReLjl7oeicH.', 1, '123456789', 'AUTRE', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `user` WHERE `email` = 'user@test.com'
);

-- Insérer un utilisateur avec le rôle Employee s'il n'existe pas déjà
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
SELECT 'Employee', 'Employee', 'employee@test.com', '$2a$10$/CImy.8zc4TUeBD2eJpbSe2XJakv9A0dTxCo64ePtbURjT3zyou8q', 1, '987654321', 'FEMME', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `user` WHERE `email` = 'employee@test.com'
);

-- Insérer un utilisateur avec le rôle Admin s'il n'existe pas déjà
INSERT INTO `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
SELECT 'Admin', 'Admin', 'admin@test.com', '$2a$10$vuqVI9hNEUMb/Bb/kF6fZuKh5i8OuW3FUPHM.pRCF9Pkns2IV9.TG', 1, '555555555', 'HOMME', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `user` WHERE `email` = 'admin@test.com'
);

-- Associer l'utilisateur avec l'email 'user@test.com' au rôle 1 (User)
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, 1 FROM `user` u WHERE u.email = 'user@test.com'
AND NOT EXISTS (
    SELECT 1 FROM `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

-- Associer l'utilisateur avec l'email 'employee@test.com' au rôle 2 (Employee)
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, 2 FROM `user` u WHERE u.email = 'employee@test.com'
AND NOT EXISTS (
    SELECT 1 FROM `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 2
);

-- Associer l'utilisateur avec l'email 'admin@test.com' au rôle 3 (Admin)
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, 3 FROM `user` u WHERE u.email = 'admin@test.com'
AND NOT EXISTS (
    SELECT 1 FROM `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 3
);
