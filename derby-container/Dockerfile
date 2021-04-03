# Pull base image.
FROM ubuntu:16.04




# Install Base Packages
RUN apt-get update
RUN	apt-get install -y openjdk-8-jdk wget supervisor zip git curl
#RUN curl -sL https://deb.nodesource.com/setup_5.x | bash 
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash



# Installing Derby
ENV DERBY_INSTALL=/db-derby-10.14.2.0-bin
ENV DERBY_HOME=/db-derby-10.14.2.0-bin
ENV CLASSPATH=/$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:.
RUN wget http://apache.mirror.amaze.com.au/db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.tar.gz
RUN tar xzf db-derby-10.14.2.0-bin.tar.gz
RUN rm -Rf /db-derby-10.14.2.0-bin.tar.gz

# Install Webgui
ENV NVM_DIR=/root/.nvm
ENV NODE_VERSION=v12.16.3
RUN . $HOME/.nvm/nvm.sh && nvm install $NODE_VERSION && nvm alias default $NODE_VERSION && nvm use default
COPY webgui/ /webgui/
RUN . $HOME/.nvm/nvm.sh && cd /webgui/ && npm ci && cd /

# Setup Dirs
RUN mkdir -p /dbs
RUN mkdir -p /dbbackup
RUN mkdir -p /upload
RUN rm -rf /var/lib/apt/lists/*

# Setup supervisor
RUN \
	rm -Rf /etc/supervisor/supervisord.conf &&\
	touch /etc/supervisor.conf &&\
	echo "[supervisord]" >> /etc/supervisor.conf &&\
	echo "user=root" >> /etc/supervisor.conf &&\
	echo "nodaemon=true" >> /etc/supervisor.conf &&\
	echo "[rpcinterface:supervisor]" >> /etc/supervisor.conf &&\
	echo "supervisor.rpcinterface_factory = supervisor.rpcinterface:make_main_rpcinterface" >> /etc/supervisor.conf &&\
	echo "[unix_http_server]" >> /etc/supervisor.conf &&\
	echo "file=/var/run/supervisor.sock" >> /etc/supervisor.conf &&\
	echo "chmod=0700  " >> /etc/supervisor.conf &&\
	echo "[supervisorctl]" >> /etc/supervisor.conf &&\
	echo serverurl=unix:///var/run/supervisor.sock >> /etc/supervisor.conf &&\
	echo "[program:derbydb]" >> /etc/supervisor.conf &&\
	echo "command=/bin/bash -c \"cd /dbs && . /db-derby-10.14.2.0-bin/bin/NetworkServerControl start -h 0.0.0.0 -p 1527\"" >> /etc/supervisor.conf &&\
	echo "stopwaitsecs=30" >> /etc/supervisor.conf &&\
	echo "stopsignal=KILL" >> /etc/supervisor.conf &&\
	echo "killasgroup=true" >> /etc/supervisor.conf &&\
	echo "[program:derbywebgui]" >> /etc/supervisor.conf &&\
	echo "command=/root/.nvm/versions/node/$NODE_VERSION/bin/node /webgui/index.js" >> /etc/supervisor.conf &&\
	echo "stopwaitsecs=30" >> /etc/supervisor.conf &&\
	echo "stopsignal=KILL" >> /etc/supervisor.conf &&\
	echo "killasgroup=true" >> /etc/supervisor.conf

VOLUME ["/dbs"]
EXPOSE 5000 1527
CMD supervisord -c /etc/supervisor.conf
