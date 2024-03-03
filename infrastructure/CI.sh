echo -e "=======================Building JAR for payment ms=======================\n"

cd ..
cd tiny-saga-payment-ms

# Run Maven clean install
./mvnw clean install -DskipTests

docker build -t tiny-saga-payment-ms .

echo -e "=======================JAR Building completed for payment ms=======================\n"

echo -e "=======================Building JAR for inventory ms=======================\n"

cd ..
cd tiny-saga-inventory-ms

# Run Maven clean install
./mvnw clean install -DskipTests

docker build -t tiny-saga-inventory-ms .

echo -e "=======================JAR Building completed for inventory ms=======================\n"

echo -e "=======================Building JAR for order ms=======================\n"

cd ..
cd tiny-saga-order-ms

# Run Maven clean install 
./mvnw clean install -DskipTests

docker build -t tiny-saga-order-ms .

echo -e "=======================JAR Building completed for order ms=======================\n"

echo -e "=======================Building JAR for wallet ms=======================\n"

cd ..
cd tiny-saga-wallet-ms

# Run Maven clean install
./mvnw clean install -DskipTests

docker build -t tiny-saga-wallet-ms .

echo -e "=======================JAR Building completed for wallet ms=======================\n"

echo -e "=======================Building JAR for orchestrator ms=======================\n"

cd ..
cd tiny-saga-orchestrator-ms

# Run Maven clean install
./mvnw clean install -DskipTests

docker build -t tiny-saga-orchestrator-ms .

echo -e "=======================JAR Building completed for orchestrator ms=======================\n"