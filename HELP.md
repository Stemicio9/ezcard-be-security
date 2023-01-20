# Deploy Tomcat Project in Docker container

1. [Overview](#overview)
2. [Description](#description)

## Overview
This readme describes how to deploy a Tomcat project in a Docker container.

## Description 

- Create Tomcat container with tomcat:9.0 image. The command is as follows:

  ```docker run -itd -p 8888:8080 -v /home/tomcat_content/ --name tomcat_ezcard tomcat:9.0```

- Execute following commands in order to enable manager gui
    
  ```docker exec -it tomcat_ezcard /bin/bash```  
  ```mv webapps webapps2```  
  ```mv webapps.dist/ webapps```  
  ```exit```
- Create manager-gui user as follows:

  ```docker exec -it tomcat_ezcard /bin/bash```
  ```cd conf```  
  ```nano tomcat-users.xml```  
  In this file you need to add the following lines:  
  ```<role rolename="manager-gui"/>```  
  ```<user username="admin" password="admin" roles="manager-gui"/>```  
  Then save the file and exit the container.  
  ```exit```
- Restart the container:  
  ```docker restart tomcat_ezcard```
- Allow access of the container from different hosts modifying the context.xml file.  
  ```docker exec -it tomcat_ezcard /bin/bash```  
  ```cd webapps/manager/META-INF```  
  ```nano context.xml```  
  In this file you need to comment the tag ```<Valve/>```
  Then save the file and exit the container.  
  ```exit```
- Restart the container:  
  ```docker restart tomcat_ezcard```


Now you can access to the manager gui from the browser using the following url:  
```http://<IP>:8888/manager/html``` 
where ```<IP>``` is the IP of the host where the container is running.
