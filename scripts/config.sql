CREATE DATABASE IF NOT EXISTS identity_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS academic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS matching_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS resource_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS institution_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'identity_app'@'%' IDENTIFIED BY 'identity';
ALTER USER 'identity_app'@'%' IDENTIFIED BY 'identity';

GRANT ALL PRIVILEGES ON identity_db.* TO 'identity_app'@'%';
GRANT ALL PRIVILEGES ON academic_db.* TO 'identity_app'@'%';
GRANT ALL PRIVILEGES ON matching_db.* TO 'identity_app'@'%';
GRANT ALL PRIVILEGES ON resource_db.* TO 'identity_app'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'identity_app'@'%';
GRANT SELECT ON institution_db.* TO 'identity_app'@'%';