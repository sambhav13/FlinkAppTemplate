
sleep 3
docker exec -i flinkapptemplate_kafka_1 kafka-topics  --bootstrap-server localhost:9092 --create --topic test --replication-factor 1 --partitions 1

