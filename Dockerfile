# Pull base image.
FROM ubuntu:16.04


# Install Base Packages
RUN apt-get update
RUN	apt-get install -y openjdk-8-jdk wget supervisor zip git curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash



# Installing Derby
ENV DERBY_INSTALL=/db-derby-10.14.2.0-bin
ENV DERBY_HOME=/db-derby-10.14.2.0-bin
ENV CLASSPATH=/$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:.
RUN wget http://apache.mirror.amaze.com.au/db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.tar.gz
RUN tar xzf db-derby-10.14.2.0-bin.tar.gz
RUN rm -Rf /db-derby-10.14.2.0-bin.tar.gz

# Install NVM
ENV NVM_DIR=/root/.nvm
ENV NODE_VERSION=v12.16.3
RUN . $HOME/.nvm/nvm.sh && nvm install $NODE_VERSION && nvm alias default $NODE_VERSION && nvm use default



# Install DBTool
RUN apt-get install -y make g++
COPY package.json /app/package.json
RUN . $HOME/.nvm/nvm.sh && cd /app/ && npm install && cd /

COPY webpack.config.js /app/
COPY tsconfig.json /app/
COPY src/ /app/src
RUN . $HOME/.nvm/nvm.sh && cd /app/ && npm run build && cd /

COPY docker.env /app/.env


# Setup DBTool cli
RUN echo ". $HOME/.nvm/nvm.sh && node /app/bin/dbrunner \$@" > /bin/dbrunner
RUN chmod +x /bin/dbrunner

WORKDIR /app

# Makes the container say open 
ENTRYPOINT ["tail", "-f", "/dev/null"]