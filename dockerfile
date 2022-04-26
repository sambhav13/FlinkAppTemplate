FROM ubuntu:18.04

WORKDIR /usr/src/app

COPY . /usr/src/app

ENV SCALA_VERSION 2.11.7

ENV SBT_VERSION 1.1.6

#RUN apt-get update && apt-get install -y apt-transport-https

ENV DEBIAN_FRONTEND noninteractive
RUN  apt-get update  -y \
  && apt-get install -y software-properties-common \
  && add-apt-repository ppa:openjdk-r/ppa \
  && apt-get install -y openjdk-8-jdk \
  && apt-get install -y wget \
  &&  apt-get install -y  curl \
  && rm -rf /var/lib/apt/lists/*


# java
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# Install Scala
ENV SCALA_VERSION 2.11.7
ENV SCALA_DEB http://www.scala-lang.org/files/archive/scala-$SCALA_VERSION.deb

RUN \
    wget --quiet --output-document=scala.deb $SCALA_DEB && \
    dpkg -i scala.deb && \
    rm -f *.deb

# RUN echo deb https://dl.bintray.com/sbt/debian / | tee -a /etc/apt/sources.list.d/sbt.list && \
#     apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
#     apt-get update && \
#     apt-get install sbt


 RUN apt-get install -y ca-certificates curl
# RUN echo deb https://dl.bintray.com/sbt/debian / |  tee -a /etc/apt/sources.list.d/sbt.list
# RUN curl -sL https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823 | apt-key add
# RUN apt-get update
# RUN apt-get -y install sbt

# Install SBT
# RUN \
#      apt-get update -y \
# 	&& apt-get install -y sbt \
#   && sbt sbtVersion

# RUN echo deb https://repo.scala-sbt.org/scalasbt/debian all main | tee /etc/apt/sources.list.d/sbt.list
# RUN echo deb https://repo.scala-sbt.org/scalasbt/debian / | tee /etc/apt/sources.list.d/sbt_old.list
# RUN curl -sL https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823 | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
# RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
# RUN apt update
# RUN apt install sbt
RUN apt-get update
RUN apt-get install -y zip unzip
RUN curl -s https://get.sdkman.io | bash

RUN wget https://github.com/sbt/sbt/releases/download/v1.1.5/sbt-1.1.5.tgz
RUN tar xzvf sbt-1.1.5.tgz -C /usr/share/
RUN /bin/bash -c source /root/.sdkman/bin/sdkman-init.sh
RUN update-alternatives --install /usr/bin/sbt sbt /usr/share/sbt/bin/sbt 9999
RUN sdk install sbt



# Install Scala Build Tool sbt
#RUN apt-get install -y --force-yes sbt

ENTRYPOINT ["tail", "-f", "/dev/null"]