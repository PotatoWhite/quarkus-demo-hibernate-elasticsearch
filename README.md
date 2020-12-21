
# run postgres
```shell
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=1234 -d postgres
```

# run elasticsearch
```shell
docker run -d --rm=true --name elasticsearch_quarkus_test -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.1
```
# run kibana
```shell
docker run --link elasticsearch_quarkus_test:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.10.1
```
