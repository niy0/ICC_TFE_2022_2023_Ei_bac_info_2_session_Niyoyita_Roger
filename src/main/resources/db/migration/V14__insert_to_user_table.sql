-- Insérer les utilisateurs de type 'User'

-- Mot de passe pour User1 : @user1user1
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User1', 'User1', 'user1@test.com', SHA2('@user1user1', 256), 1, '0612345678', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user1@test.com'
);

-- Mot de passe pour User2 : @user2user2
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User2', 'User2', 'user2@test.com', SHA2('@user2user2', 256), 1, '0623456789', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user2@test.com'
);

-- Mot de passe pour User3 : @user3user3
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User3', 'User3', 'user3@test.com', SHA2('@user3user3', 256), 1, '0634567890', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user3@test.com'
);

-- Mot de passe pour User4 : @user4user4
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User4', 'User4', 'user4@test.com', SHA2('@user4user4', 256), 1, '0645678901', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user4@test.com'
);

-- Mot de passe pour User5 : @user5user5
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User5', 'User5', 'user5@test.com', SHA2('@user5user5', 256), 1, '0656789012', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user5@test.com'
);

-- Associer chaque utilisateur avec le rôle 1 (User)
insert into `user_roles` (`user_id`, `role_id`)
select u.id, 1 from `user` u WHERE u.email = 'user1@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 1 from `user` u WHERE u.email = 'user2@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 1 from `user` u WHERE u.email = 'user3@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 1 from `user` u WHERE u.email = 'user4@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 1 from `user` u WHERE u.email = 'user5@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);

-- Insérer les utilisateurs de type 'Employee'

-- Mot de passe pour Employee1 : @employee1employee1
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee1', 'Employee1', 'employee1@test.com', SHA2('@employee1employee1', 256), 1, '0667890123', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee1@test.com'
);

-- Mot de passe pour Employee2 : @employee2employee2
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee2', 'Employee2', 'employee2@test.com', SHA2('@employee2employee2', 256), 1, '0678901234', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee2@test.com'
);

-- Mot de passe pour Employee3 : @employee3employee3
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee3', 'Employee3', 'employee3@test.com', SHA2('@employee3employee3', 256), 1, '0689012345', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee3@test.com'
);

-- Associer chaque utilisateur avec le rôle 2 (Employee)
insert into `user_roles` (`user_id`, `role_id`)
select u.id, 2 from `user` u WHERE u.email = 'employee1@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 2
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 2 from `user` u WHERE u.email = 'employee2@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 2
);

insert into `user_roles` (`user_id`, `role_id`)
select u.id, 2 from `user` u WHERE u.email = 'employee3@test.com'
AND NOT EXISTS (
    select 1 from `user_roles` ur WHERE ur.user_id = u.id AND ur.role_id = 2
);

