./mvnw clean package
docker build -t ahoqueorg/orchestrator-service:0.1 --build-arg service_version=v1 .
