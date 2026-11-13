#!/bin/bash
set -Eeuo pipefail

: "${MYSQL_ROOT_PASSWORD:?MYSQL_ROOT_PASSWORD must be set}"
: "${MYSQL_USER:?MYSQL_USER must be set}"
: "${MYSQL_PASSWORD:?MYSQL_PASSWORD must be set}"

if [[ ! "$MYSQL_USER" =~ ^[A-Za-z0-9_]+$ ]]; then
    echo "MYSQL_USER may contain only letters, digits, and underscores." >&2
    exit 1
fi

password_hex="$(printf '%s' "$MYSQL_PASSWORD" | od -An -v -tx1 | tr -d ' \n')"
if [[ -z "$password_hex" ]]; then
    echo "MYSQL_PASSWORD must not be empty." >&2
    exit 1
fi

MYSQL_PWD="${MYSQL_ROOT_PASSWORD}" mysql --protocol=socket -uroot <<SQL
CREATE DATABASE IF NOT EXISTS identity_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS academic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS matching_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS resource_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS institution_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET @app_password = CONVERT(0x${password_hex} USING utf8mb4);
SET @app_user_sql = CONCAT(
    'CREATE USER IF NOT EXISTS ''${MYSQL_USER}''@''%'' IDENTIFIED BY ',
    QUOTE(@app_password)
);
PREPARE app_user_statement FROM @app_user_sql;
EXECUTE app_user_statement;
DEALLOCATE PREPARE app_user_statement;

SET @app_user_sql = CONCAT(
    'ALTER USER ''${MYSQL_USER}''@''%'' IDENTIFIED BY ',
    QUOTE(@app_password)
);
PREPARE app_user_statement FROM @app_user_sql;
EXECUTE app_user_statement;
DEALLOCATE PREPARE app_user_statement;

GRANT ALL PRIVILEGES ON identity_db.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON academic_db.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON matching_db.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON resource_db.* TO '${MYSQL_USER}'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO '${MYSQL_USER}'@'%';
GRANT SELECT ON institution_db.* TO '${MYSQL_USER}'@'%';
FLUSH PRIVILEGES;
SQL
