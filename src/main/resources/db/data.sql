-- Creazione automatica dei ruoli del WebSite
INSERT IGNORE INTO `roles` (`id`, `name`) VALUES
    ('1', 'ROLE_USER'),
    ('2', 'ROLE_SELLER'),
    ('3', 'ROLE_REVISOR'),
    ('4', 'ROLE_ADMIN');