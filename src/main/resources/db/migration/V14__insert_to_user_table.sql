-- Insérer les utilisateurs de type 'User'

-- Mot de passe pour User1 : @user1user1 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User1', 'User1', 'user1@test.com', '$2a$10$EIXZrFz5pi/ZM1uJ8D5.cu5GxOvK7z6QiPvZ6fsknnGV6U4Ixivz6', 1, '0612345678', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user1@test.com'
);

-- Mot de passe pour User2 : @user2user2 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User2', 'User2', 'user2@test.com', '$2a$10$LzJNCHa.Vrc4APBWm38T0ORi/d3Bg21A5CJUmUZ9evFMQ5yN9yoJi', 1, '0623456789', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user2@test.com'
);

-- Mot de passe pour User3 : @user3user3 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User3', 'User3', 'user3@test.com', '$2a$10$gYxSMLdYxVCTINspwO3Dquxs6Gcz4uBSYJZ.5vKxXg.VsTyLljtZm', 1, '0634567890', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user3@test.com'
);

-- Mot de passe pour User4 : @user4user4 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User4', 'User4', 'user4@test.com', '$2a$10$A7rld3Fyc5Am0cb.ZYyZeumFUCYPuNlgA2/CxM3cK7/JjHViLrRS6', 1, '0645678901', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'user4@test.com'
);

-- Mot de passe pour User5 : @user5user5 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'User5', 'User5', 'user5@test.com', '$2a$10$e4z6HGoPq1QF3GoQL5Caiu1MJTkqFeVZFbP/8UhepxnZzTytzJzzC', 1, '0656789012', 'HOMME', NOW(), NOW()
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
select 'Employee1', 'Employee1', 'employee1@test.com', '$2a$10$V7MypGH2ABEjYfDLkeEZ6udxxZkjXYe3Vb4H1KTzOtOt0/fDxwE02', 1, '0667890123', 'FEMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee1@test.com'
);

-- Mot de passe pour Employee2 : @employee2employee2 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee2', 'Employee2', 'employee2@test.com', '$2a$10$IsEoLZP1Kz9uGceJuvjFV.SWWjC4H/jKtCgS/IUz.G1fQu27itdjO', 1, '0678901234', 'HOMME', NOW(), NOW()
where not exists (
    select 1 from `user` WHERE `email` = 'employee2@test.com'
);

-- Mot de passe pour Employee3 : @employee3employee3 (BCrypt)
insert into `user` (`nom`, `prenom`, `email`, `password`, `is_logged_in`, `telephone`, `sexe`, `date_creation`, `date_modification`)
select 'Employee3', 'Employee3', 'employee3@test.com', '$2a$10$5z8SvfCpP5z3mDpG9hE3xuUOjE5Zn2R0m0S/UcRJGmbRY7c3vF/6.', 1, '0689012345', 'FEMME', NOW(), NOW()
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
