# Projet SparkStreaming

## Description
Ce projet consiste à faire 3 taches de Data Engineering:

### Data Ingestion
Créer 3 `générateur de données` qui fonctionnent en parallèle et qui écrient le résultat dans un `Java Socket` d'une façon continue. Les données générées sont des donées de température capturées en temps réel et ils ont le format JSON suivant:
```
{
	"data": {
		"deviceId": "11c1310e-c0c2-461b-a4eb-f6bf8da2d23c",
		"temperature": 12,
		"location": {
			"latitude": "52.14691120000001",
			"longitude": "11.658838699999933"
		}
	}
}
```
Note: j'ai utilisé `Redis Queue` pour écrire les données générées et puis ces données sont lus du `Redis Queue` afin de les écrires dans un `Java Socket`.

### Data Transformation
Créer un `spark streaming job` qui lit ces résultats lu du `Java Socket` et les sauvegarde en local en format parquet.

### Data Analysis
Créer un `spark job` qui query les données parquet et affiche les résultats.

## Commande à lancer pour faire tourner ce projet

- Lancer REDIS sur Docker
```docker run --name redis_container -p 6379:6379 -d redis```

- Lancer 3 `IOTSimulator` qui simulent des données de température en parallèle et push ces données sur un seul Redis Queue
```
docker build -t java_tasks .
docker run -d java_tasks:latest
```

- Faire ouvrir le `Socket` de la port 4999
```netcat -lk 4999```

- Lancer `AggregateComponent` qui pop les données de Redis Queue et les écrit dans le `Socket`
```
mvn clean package (need to have maven installed I am also using Java8)
java -cp target/java_tasks-1.0-SNAPSHOT-jar-with-dependencies.jar AggregateComponent &
```

- Lancer `WriteToParquet` qui traite les données reçu par le `Socket` en temps réel (SparkStreaming) et les sauvegarde en fichiers parquet.
```
sbt clean compile
sbt "run-main com.scala_tasks.sparkstreaming.WriteToParquet"
```

- Dirty Fix: il faut copier/coller les messages reçu par le `Socket` dans le `Socket`. C'est pour re-envoyer les données dans le `Socket` afin que le SparkStreaming job recoie ces données.

- Exécuter le spark job `QueryParquet` qui query les données du parquet et affiche le résultat
```sbt "run-main com.scala_tasks.sparkstreaming.QueryParquet 2021-12-03"```
