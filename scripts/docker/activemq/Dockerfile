FROM centos:7

RUN yum install -y \
    java-1.8.0-openjdk \
    java-1.8.0-openjdk-devel

RUN mkdir /opt/activemq
WORKDIR /opt/activemq
COPY apache-actvemq-5.15.13-bin.tar.gz /opt/activemq
RUN tar xvzf apache-activemq-5.15.13/bin
WORKDIR /opt/activemq/apache-activemq-5.15.13/bin
ENV JAVA_HOME /etc/alternatives/java
ENV PATH="/opt/activemq/apache-activemq-5.15.13/bin:${PATH}"

EXPOSE 61616
EXPOSE 8161

CMD["/bin/sh","-c","/opt/activemq/apache-activemq-5.15.13/bin/activemq console"]


#docker build -f DockerFile -t activemq .
#docker -D run -i -d -p 61616:61616 -p 8161:8161 activemq

