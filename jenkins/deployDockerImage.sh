docker stop $IMAGE
docker rm $IMAGE
docker run -d --rm --name $IMAGE -p 8931:8931 --net=host 192.168.1.55:5000/$IMAGE
