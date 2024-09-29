-- Création de la table associée pour stocker les produits notés par les utilisateurs
create TABLE IF NOT EXISTS `user_produits_notes` (
    `user_id` BIGINT NOT NULL,
    `produit_id` BIGINT NOT NULL,
    `note` INT NOT NULL CHECK (`note` >= 1 AND `note` <= 5), -- Assurez-vous que la note est entre 1 et 5
    PRIMARY KEY (`user_id`, `produit_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON delete CASCADE ON update CASCADE,
    FOREIGN KEY (`produit_id`) REFERENCES `produit`(`id`) ON delete CASCADE ON update CASCADE
);

-- Création de la table associée pour stocker les tokens de réinitialisation de mot de passe
create TABLE IF NOT EXISTS `password_reset_token` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `token` VARCHAR(255) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `expiry_date` TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON delete CASCADE
);


