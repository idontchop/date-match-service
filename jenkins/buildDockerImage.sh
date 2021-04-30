echo "Building Docker image ${IMAGE}, version ${VERSION}..."
docker build --build-arg IMAGE=${IMAGE} --build-arg VERSION=${VERSION} -t $IMAGE .
docker image rm 192.168.1.55:5000/$IMAGE:latest
docker tag $IMAGE 192.168.1.55:5000/$IMAGE:latest
docker push 192.168.1.55:5000/$IMAGE:latest
