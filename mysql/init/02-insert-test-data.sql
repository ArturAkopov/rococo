USE `rococo-auth`;

-- Вставка тестового пользователя (пароль: "password" в bcrypt)
INSERT INTO `user` (id, username, password) VALUES
(UUID_TO_BIN(UUID()), 'testuser', '$2a$10$fqEmpFwjWwEB4aUwOPDgAOENQaOXT8PZhBJMEEdb/dgL7BCGP2kwS');

-- Вставка прав для тестового пользователя
INSERT INTO `authority` (id, user_id, authority) VALUES
(UUID_TO_BIN(UUID()), (SELECT id FROM `user` WHERE username = 'testuser'), 'READ'),
(UUID_TO_BIN(UUID()), (SELECT id FROM `user` WHERE username = 'testuser'), 'WRITE');