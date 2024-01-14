# Data Environment Setup

This document will detail the steps needed to recreate a demo data pipeline environment on your local machine.
The data environment will deploy Kafka, Flink, Pinot and Trino. Additionally data will flow from Kafka topics, through 
a Flink job, into a Pinot table from where it can be queried via Trino.

### Local Flink Cluster 
Follow the below steps to deploy a local Flink cluster

1. Download the [Flink 1.13.2][flink-download] binary
2. Open a terminal window and run the following commands
```sh
# List available versions Java on your machine
/usr/libexec/java_home -V

# Set Java version to 11
export JAVA_HOME=`/usr/libexec/java_home -v 11.0`

# Navigate to downloaded Flink directory (unzipped)
cd Downloads/flink-1.13.2

# Start the Flink cluster
bin/start-cluster.sh

# To stop the cluster run the following
bin/stop-cluster.sh 
```
The Flink web interface will be available at: http://localhost:8081.

[flink-download]: https://flink.apache.org/downloads/

### Docker Containers
Navigate to the location of the 'docker-compose.yml' file and run the below set of commands.
```sh
# Build and run the containers
docker-compose --project-name demo-env up

# To bring down the containers follow the below command
docker-compose --project-name demo-env down
```
The above will include several UIs for each of Kafka, Pinot and Trino:

- Kafka UI - http://localhost:8080.
- Pinot Controller - http://localhost:9000.
- Trino UI - http://localhost:8085.

If you experience any issues (eg. with Kafka brokers, Pinot controller, or Trino coordinator), simply restart the corresponding containers. 
### Trino CLI
```sh
# Connect to the Trino container
docker exec -it [CONTAINER ID] trino --server localhost:8085 --catalog pinot --schema default

# List tables available in Pinot
show tables;

# Example query of test table in Pinot
select count(*) from test;
```

### Using the environment
Kafka topics can be created either through the UI or from your command line. For Pinot tables and schemas, we can
use the Pinot Controller UI or the Swagger API.

Flink applications should be packaged into jar files and reference the Kafka brokers using localhost and thier 
specified ports. The Flink job can then be submitted as a jar file in the Flink user interface.

The provided example flink-app will require a Kafka topic to be created, named 'test-topic', and
a Pinot table, named 'test', with a single string field. The 'produce.sh' script can be used to produce simple string data to the
topic.
