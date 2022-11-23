-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `address` tinytext,
    `created_at` datetime(6) DEFAULT NULL,
    `email` varchar(50) DEFAULT NULL,
    `first_name` varchar(50) NOT NULL,
    `is_deleted` bit(1) DEFAULT NULL,
    `last_name` varchar(50) DEFAULT NULL,
    `password` varchar(255) NOT NULL,
    `phone_number` varchar(15) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `user_id` varchar(255) DEFAULT NULL,
    `roles_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
    KEY `FKbgvg7xuekkcqmpvi3tgkxk85j` (`roles_id`),
    CONSTRAINT `FKbgvg7xuekkcqmpvi3tgkxk85j` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- -----------------------------------------------------
-- Table `login_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `login_details` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `login_user_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKor7i1mesrmrjxv6lkfxqwgxwa` (`login_user_id`),
    CONSTRAINT `FKor7i1mesrmrjxv6lkfxqwgxwa` FOREIGN KEY (`login_user_id`) REFERENCES `users` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- -----------------------------------------------------
-- Table `device_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `device_details` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `is_active` bit(1) DEFAULT NULL,
    `token` varchar(255) DEFAULT NULL,
    `user_id` varchar(255) DEFAULT NULL,
    `login_device_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKghm75p3888wgfyj8ds4a98g8f` (`login_device_id`),
    CONSTRAINT `FKghm75p3888wgfyj8ds4a98g8f` FOREIGN KEY (`login_device_id`) REFERENCES `login_details` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `description` mediumtext,
    `name` varchar(50) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ofx66keruapi6vyqpv6f2or37` (`name`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `roles_permissions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles_permissions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `page_name` varchar(100) DEFAULT NULL,
    `page_permission` varchar(100) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK1dktt0alekj7s4ukj7roixfwj` (`page_name`,`page_permission`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `roles_has_roles_permissions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles_has_roles_permissions` (
    `roles_id` bigint NOT NULL,
    `roles_permissions_id` bigint NOT NULL,
    PRIMARY KEY (`roles_id`,`roles_permissions_id`),
    KEY `FK2cs5m1nguawqombhlewf58mmn` (`roles_permissions_id`),
    CONSTRAINT `FK2cs5m1nguawqombhlewf58mmn` FOREIGN KEY (`roles_permissions_id`) REFERENCES `roles_permissions` (`id`),
    CONSTRAINT `FKl0n3ae6hxdixp2hv4s4ex4lq1` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Trigger `users_BEFORE_INSERT`
-- ----------------------------------------------------
DELIMITER $$
CREATE
TRIGGER test
BEFORE INSERT ON `users`
FOR EACH ROW
BEGIN
IF (NEW.user_id IS NULL) THEN
  -- Find max existed id
  SELECT
    MAX(id) INTO @max_user_id
  FROM
    users;

  IF (@max_user_id IS NULL) THEN
    -- Set first user_id
    SET NEW.user_id = CONCAT('ADA_', '1');
  ELSE
    -- Set next user_id
    SET NEW.user_id = CONCAT('ADA_',@max_user_id+1);
  END IF;
END IF;
END$$
DELIMITER ;


-- -----------------------------------------------------
-- Script `give all permissions to SUPER_ADMIN`
-- ----------------------------------------------------
SELECT id INTO @var FROM `roles` WHERE name = 'SUPER_ADMIN';
SELECT @var;
DROP PROCEDURE IF EXISTS proc_cursor_to_loopAndInsert;
DELIMITER ;;
CREATE PROCEDURE proc_cursor_to_loopAndInsert()
BEGIN
  DECLARE perm_id INT;
  DECLARE permission_id_cursor CURSOR FOR SELECT id from roles_permissions;
    OPEN   permission_id_cursor;
  loop_through_rows: LOOP
    FETCH  NEXT FROM permission_id_cursor INTO perm_id;
   INSERT INTO roles_has_roles_permissions(roles_id,roles_permissions_id)
  values(@var,perm_id);
  END LOOP;
  CLOSE permission_id_cursor;
execute proc_cursor_to_loopAndInsert;
END;
;;



  INSERT INTO `roles_permissions` (`created_at`,`page_name`, `page_permission`,`updated_at`) VALUES (CURRENT_TIMESTAMP(),'manage_users', 'view',CURRENT_TIMESTAMP());
  INSERT INTO `roles_permissions` (`created_at`,`page_name`, `page_permission`,`updated_at`) VALUES (CURRENT_TIMESTAMP(),'manage_users', 'view & edit',CURRENT_TIMESTAMP());

  INSERT INTO `roles` (`created_at`, `description`, `name`, `updated_at`) VALUES (CURRENT_TIMESTAMP(), 'super_admin', 'SUPER_ADMIN', CURRENT_TIMESTAMP());

  INSERT INTO `users` (`created_at`, `email`, `first_name`, `is_deleted`, `last_name`, `password`, `updated_at`,`roles_id`) VALUES (CURRENT_TIMESTAMP(),'naveen.babbar@digimantra.com', 'Naveen',0,'Babbar','$2a$10$9tVW54hJCLL0mjsQclwGE.Ik2U/Pwa45sk6Cs/BUpzTb3y6eAwRn2',CURRENT_TIMESTAMP(),
   (SELECT id from `roles` where name ='SUPER_ADMIN' ));