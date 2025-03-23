-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `announcements` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `title` varchar(100) NOT NULL,
                                 `content` text NOT NULL,
                                 `created_by` int NOT NULL,
                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `FK_announcements_created_by` (`created_by`),
                                 CONSTRAINT `FK_announcements_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `attendance` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `user_id` int NOT NULL,
                              `group_id` int NOT NULL,
                              `check_in` datetime NOT NULL,
                              `check_out` datetime DEFAULT NULL,
                              `username` varchar(50) DEFAULT NULL,
                              `is_leave` tinyint DEFAULT '0' COMMENT '是否请假：0否，1是',
                              PRIMARY KEY (`id`),
                              KEY `FK_attendance_user_id` (`user_id`),
                              KEY `FK_attendance_group_id` (`group_id`),
                              CONSTRAINT `FK_attendance_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `FK_attendance_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `equipment` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `name` varchar(100) NOT NULL,
                             `description` text,
                             `level` int NOT NULL,
                             `image_url` varchar(255) DEFAULT NULL,
                             `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `status` int DEFAULT '0',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `equipment_borrow` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `user_id` int NOT NULL,
                                    `equipment_id` int NOT NULL,
                                    `borrow_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    `return_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `FK_equipment_borrow_user_id` (`user_id`),
                                    KEY `FK_equipment_borrow_equipment_id` (`equipment_id`),
                                    CONSTRAINT `FK_equipment_borrow_equipment_id` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `FK_equipment_borrow_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `group_daily_attendance` (
                                          `id` int NOT NULL AUTO_INCREMENT,
                                          `group_id` int NOT NULL,
                                          `user_id` int NOT NULL,
                                          `date` date NOT NULL,
                                          `attendance` tinyint NOT NULL DEFAULT '0' COMMENT '0: 未打卡, 1: 打卡',
                                          `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `unique_attendance` (`group_id`,`user_id`,`date`),
                                          KEY `FK_group_daily_attendance_user_id` (`user_id`),
                                          CONSTRAINT `FK_group_daily_attendance_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`) ON DELETE CASCADE,
                                          CONSTRAINT `FK_group_daily_attendance_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `leave_record` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `user_id` int NOT NULL,
                                `group_id` int NOT NULL,
                                `leave_date` date NOT NULL,
                                `reason` varchar(500) NOT NULL,
                                `status` tinyint NOT NULL,
                                `create_time` datetime NOT NULL,
                                `approve_time` datetime DEFAULT NULL,
                                `approver_id` int DEFAULT NULL,
                                `approver_comment` varchar(500) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `pre_registered_users` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `username` varchar(50) NOT NULL,
                                        `group_id` int NOT NULL,
                                        `status` enum('未注册','已注册') NOT NULL DEFAULT '未注册',
                                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `username` (`username`),
                                        KEY `FK_pre_registered_users_group_id` (`group_id`),
                                        CONSTRAINT `FK_pre_registered_users_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `team_recruitment` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `title` varchar(100) NOT NULL,
                                    `description` text NOT NULL,
                                    `contact` varchar(100) NOT NULL,
                                    `created_by` int NOT NULL,
                                    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`),
                                    KEY `FK_team_recruitment_created_by` (`created_by`),
                                    CONSTRAINT `FK_team_recruitment_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user_groups` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `name` varchar(50) NOT NULL,
                               `description` text,
                               `admin_A` int DEFAULT NULL,
                               `admin_B` int DEFAULT NULL,
                               `admin_C` int DEFAULT NULL,
                               `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                               `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `name` (`name`),
                               KEY `FK_user_groups_admin_A` (`admin_A`),
                               KEY `FK_user_groups_admin_B` (`admin_B`),
                               KEY `FK_user_groups_admin_C` (`admin_C`),
                               CONSTRAINT `FK_user_groups_admin_A` FOREIGN KEY (`admin_A`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                               CONSTRAINT `FK_user_groups_admin_B` FOREIGN KEY (`admin_B`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                               CONSTRAINT `FK_user_groups_admin_C` FOREIGN KEY (`admin_C`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `username` varchar(50) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `role` enum('USER','LEADER','ADMIN') NOT NULL DEFAULT 'USER',
                         `group_id` int NOT NULL,
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`),
                         KEY `FK_users_group_id` (`group_id`),
                         CONSTRAINT `FK_users_group_id` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


-- 插入 ADMIN 用户组，确保 ID 为 1
INSERT INTO `user_groups` (`id`, `name`, `description`, `created_at`, `updated_at`) 
VALUES (1, 'ADMIN', 'Administrator group', NOW(), NOW());

-- 插入 ADMIN 用户，确保 ID 为 1
INSERT INTO `users` (`id`, `username`, `password`, `role`, `group_id`, `created_at`, `updated_at`) 
VALUES (1, 'nullcat', '1234', 'ADMIN', 1, NOW(), NOW());


-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
