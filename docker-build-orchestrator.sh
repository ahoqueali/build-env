./mvnw clean package
docker build -t ahoqueorg/orchestrator-service --build-arg service_version=v1 .
