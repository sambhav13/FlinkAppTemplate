
sleep 4
docker exec -i flinkapptemplate_kafka_1 kafka-console-producer --broker-list :9092 --topic test < src/it/resources/data.json
sleep 4
