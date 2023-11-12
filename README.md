# ActiveMQ

Workshop from Benjamin ,Emilia ,Nils ,and Rachel

## Agenda

- [Presentaion](ActiveMQ.pptx)
- Quiz
- Questions board
- Demo & Excersise
- Feedback poll

***

## Student area

Go to to https://nearpod.com/student/ to login to the interactive presentaion. The code to join is **uqpv5**

Exercise can be found [here](task.md)

## Sources

- https://activemq.apache.org/
- https://activemq.apache.org/components/artemis/documentation/2.0.0/
- https://www.datadoghq.com/blog/activemq-architecture-and-metrics/
- https://www.openlogic.com/blog/what-apache-activemq
- https://www.ibm.com/topics/message-brokers

## Examples

There are a few example projects in this repository.

### Example

[This](./Example) folder contains a pretty complete projects that shows how both message producing and consuming work.
It is not interactive and just sends two messages that are then received and printed to the terminal.

You can run it using:

```bash
cd Example
./gradlew run
```

### Consumer

[This](./Consumer) folder contains a console application that consumes all incoming messages while its running. 
By default, this application will only run for 5 minutes, however you can change that if you want to.
After starting this you should start the [producer project](#producer) as well to send some messages.

You can run it using:

```bash
cd Consumer
./gradlew run
```

### Producer

[This](./Producer) folder contains a console application that you can use to send ("produce") messages. 
When it's running it will prompt you to enter a message that is then send to the configured address.

You can run it using:

```bash
cd Producer
mvn package
java -jar ./target/Producer-1.0-SNAPSHOT.jar
```
