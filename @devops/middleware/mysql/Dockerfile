FROM mysql:8.0.32

MAINTAINER massless

# It can be passed in via the '--build-arg' parameter during build:
#   docker build --build-arg TIME_ZONE=Asia/Shanghai Dockerfile
#
# Zone Code Reference: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
ARG TIME_ZONE=Asia/Shanghai

# Copy the configuration file into the corresponding directory of mysql.
COPY ./mysql/my.cnf /etc/mysql/conf.d/my.cnf

# Set time zone && Set file permissions
RUN /bin/cp /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && echo ${TIME_ZONE} >/etc/timezone \
    && chmod 644 /etc/mysql/conf.d/my.cnf

# Put in the SQL script that needs to be executed on initialized.
COPY ./nacos/init-nacos-2.2.0.sql /docker-entrypoint-initdb.d
#COPY ./xxl-job/init-xxl-job-2.3.0.sql /docker-entrypoint-initdb.d
