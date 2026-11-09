#!/bin/bash

echo "========== POM RACINE =========="
cat pom.xml

echo ""
echo "========== STRUCTURE GATEWAY =========="
find api-gateway/src -type f 2>/dev/null

echo ""
echo "========== STRUCTURE IDENTITY =========="
find identity-service/src/main/java -type f 2>/dev/null

FILES=(
"api-gateway/src/main/java/com/uadb/backend/gateway/GatewayApplication.java"
"identity-service/src/main/java/com/uadb/backend/identity/controller/InscriptionController.java"
"identity-service/src/main/java/com/uadb/backend/identity/controller/AdminController.java"
"identity-service/src/main/java/com/uadb/backend/identity/controller/AuthentificationController.java"
"identity-service/src/main/java/com/uadb/backend/identity/config/SecurityConfig.java"
)

for FILE in "${FILES[@]}"; do
    if [ -f "$FILE" ]; then
        echo ""
        echo "========== $FILE =========="
        cat "$FILE"
    fi
done
