-- Insérer les utilisateurs de type 'User'

-- Mot de passe pour User1 : @user1user1 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User1', 'User1', 'user1@test.com', '$2a$10$4NSguSzV5NI2l.xdEfeqmOmmXXzwbhzRBVWxcCtMtIPB71.FDf0Su', 1, '0612345678', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user1@test.com'
);

-- Mot de passe pour User2 : @user2user2 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User2', 'User2', 'user2@test.com', '$2a$10$rm3AZnMN8kgZwBg9cCgkluXIgmIIrSIUlGsO59AQizMDDSuR7vkry', 1, '0623456789', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user2@test.com'
);

-- Mot de passe pour User3 : @user3user3 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User3', 'User3', 'user3@test.com', '$2a$10$KmBuLv9p74iVg1uYarC6GOC70Bx/gbtku5Rg0hqgx7MAPnyyC28vK', 1, '0634567890', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user3@test.com'
);

-- Mot de passe pour User4 : @user4user4 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User4', 'User4', 'user4@test.com', '$2a$10$dFb7HLlHs/a0fPYzglWuleWVH2Z2w/4b0fvCXo.25wRVi/czRTqtO', 1, '0645678901', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user4@test.com'
);

-- Mot de passe pour User5 : @user5user5 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User5', 'User5', 'user5@test.com', '$2a$10$vWOrnv4S/mSqtaO5AV.SZunao1CEDPgoCYl1vE.uchVETy//WBHD.', 1, '0656789012', 'HOMME', NOW(), NOW()
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

-- Mot de passe pour Employee1 : @employee1employee1 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee1', 'Employee1', 'employee1@test.com', '$2a$10$KU45p9svyu9t/tLSsZDazuTWj.ATbQdAzX1jWqfO70YKz72i.R.kC', 1, '0667890123', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee1@test.com'
);

-- Mot de passe pour Employee2 : @employee2employee2 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee2', 'Employee2', 'employee2@test.com', '$2a$10$4Ko6r96ycSJGh.nd.np9bOjUQoj6hyXKqB.2ZDiiUMFSoVzC63fNO', 1, '0678901234', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee2@test.com'
);

-- Mot de passe pour Employee3 : @employee3employee3 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee3', 'Employee3', 'employee3@test.com', '$2a$10$QgXPPika3k9H6eSAbfTs5.wueSeIGJL8E/CxC/7s7ugF9nA73Rgr6', 1, '0689012345', 'FEMME', NOW(), NOW()
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
