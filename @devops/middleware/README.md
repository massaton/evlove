The current directory stores the deployment scripts of all middleware involved in EVLOVE, which contains the configuration files required by the docker-compose deployment script and middleware.

It is used to deploy the middleware required by the platform to a certain server through the docker-compose tool. It only applies to the demonstration, development, and test environments. It is not recommended to be used in pre-production and production environments.

Usually, the middleware only needs to be deployed once, and does not need to be connected to CI. But if there are many deployment environments or frequent modifications, this directory can be extracted, placed in a git warehouse separately, and plug into CI.



## About The Use Of Docker Network

The container in the current deployment file uses the manually created docker bridge network as the default network of the container, and provides host IP access channels through port mapping.

This is to be compatible with stand-alone deployment in macOS and Windows Systems (host network is not supported, and middleware cannot be directly connected through IP addresses when they depend on each other), and deployment scenarios where multiple docker-compose deployment files deploy middleware separately.

Except for docker-compose-filebeat.yml, other docker-compose deployment files need to manually create a docker network before deployment.

```shell
# Manually create a docker network
docker network create -d bridge evlove-net
```

The gateway and service layer services will be deployed through Swarm or Kubernetes, and use the host network and middleware to communicate through the IP of the host where the container is located and the mapped port.

In the Linux environment, it is recommended to use the docker host network for deployment to improve network communication performance.

Ref：https://docs.docker.com/network/host/



## Deployment File Description

All deployment commands are executed in the root directory of EVLOVE.

- **docker-compose-0-1.yml**
  
  The basic middleware required for deploying the 0-1 stage monolithic architecture, including MySQL and Redis.
  
  ```shell
  docker-compose -f ./@devops/middleware/docker-compose-0-1.yml build
  docker-compose -f ./@devops/middleware/docker-compose-0-1.yml up -d
  docker-compose -f ./@devops/middleware/docker-compose-0-1.yml down
  ```
  
- **docker-compose-1-N.yml**

  On the basis of the 0-1 stage, it is used to deploy the basic middleware required for the 1-N stage microservice architecture, including the registration center, configuration center, distributed task scheduling, flow control, etc.

  ```shell
  docker-compose -f ./@devops/middleware/docker-compose-1-N.yml build
  docker-compose -f ./@devops/middleware/docker-compose-1-N.yml up -d
  docker-compose -f ./@devops/middleware/docker-compose-1-N.yml down
  ```

- **docker-compose-ek.yml (Optional)**

  Used to deploy optional components Elasticsearch and Kibana.

  ```shell
  docker-compose -f ./@devops/middleware/docker-compose-ek.yml build
  docker-compose -f ./@devops/middleware/docker-compose-ek.yml up -d
  docker-compose -f ./@devops/middleware/docker-compose-ek.yml down
  ```

- **docker-compose-filebeat.yml (Optional)**

  When logs need to be collected and analyzed, this script is used to deploy the collector to the host node.

  Rely on Elasticsearch and Kibana.

  ```shell
  # Deployment method: docker-compose (executed on the collection target host)
  docker-compose -f ./@devops/middleware/docker-compose-filebeat.yml build
  docker-compose -f ./@devops/middleware/docker-compose-filebeat.yml up -d
  docker-compose -f ./@devops/middleware/docker-compose-filebeat.yml down
  
  # Deployment method: docker-stack (executed on the master node of the Swarm cluster)
  docker-compose -f ./@devops/middleware/docker-compose-filebeat.yml build
  docker stack deploy -c docker-compose-filebeat.yml filebeat
  docker stack rm filebeat
  ```

- **docker-compose-mq.yml (Optional)**

  Used to deploy optional middleware RocketMQ.
  
  ```shell
  docker-compose -f ./@devops/middleware/docker-compose-mq.yml build
  docker-compose -f ./@devops/middleware/docker-compose-mq.yml up -d
  docker-compose -f ./@devops/middleware/docker-compose-mq.yml down
  ```
  
  

