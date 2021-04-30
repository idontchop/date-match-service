echo "Building Docker image ${IMAGE}, version ${VERSION}..."
docker build --build-arg IMAGE=date-match-service --build-arg VERSION=0.0.1-SNAPSHOT -t date-match-service .
docker tag $IMAGE 192.168.1.55:5000/$IMAGE:latest
docker push 192.168.1.55:5000/$IMAGE
