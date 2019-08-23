Source-To-Image (S2I)

```
https://github.com/openshift/source-to-image
```

### Locally

```bash
s2i ...
```

### On OpenShift Cluster

```bash
oc start-build ...
```

See details: https://github.com/fabric8io-images/s2i/tree/master/java/examples/maven

### Login

```bash
oc login --insecure-skip-tls-verify https://192.168.99.102:8443
```

Local source must be in a git repository that has a remote repository that the server can see.

```bash
git remote add origin https://github.com/altfatterz/openshift-pipeline-demo.git
git push -u origin master
```

```bash
oc apply -f openjdk-s2i-imagestream.json
```

Create a new application

```bash
oc new-app redhat-openjdk18-openshift~https://github.com/altfatterz/openshift-pipeline-demo.git
```

This will use the `myproject/redhat-openjdk18-openshift:latest` builder image to create a Docker image using the created `openshift-pipeline-demo` build configuration.

![openshift-pipeline-demo-build-configuration](openshift-pipeline-demo-build-configuration)

The created Docker image is pushed into the created `openshift-pipeline-demo` image stream.

![openshift-pipeline-demo-image-stream](openshift-pipeline-demo-image-stream)

And using the created Docker image a `deployment` and `service` is created.

The application is not yet exposed, we can expose it using
    
```bash
oc expose svc/openshift-pipeline-demo
``` 

A route will be created

```bash
oc get routes

NAME                      HOST/PORT                                                 PATH   SERVICES                  PORT       TERMINATION   WILDCARD
openshift-pipeline-demo   openshift-pipeline-demo-myproject.192.168.99.102.nip.io          openshift-pipeline-demo   8080-tcp                 None
```

```bash
http openshift-pipeline-demo-myproject.192.168.99.102.nip.io

HTTP/1.1 200
Cache-control: private
Content-Length: 12
Content-Type: text/plain;charset=UTF-8
Date: Tue, 20 Aug 2019 19:18:37 GMT
Set-Cookie: 3afb8aeb25cd951d3bb53876aaceba9f=616b02395ed6f5ae86c7036058a160d0; path=/; HttpOnly

Hello World!
```

Get build configurations

```bash
oc get bc

openshift-pipeline-demo   Source   Git@master   3
```

Start a new build for the provided build config

```bash
oc start-build openshift-pipeline-demo
```

Revert an application back to a previous deployment

```bash
oc rollback openshift-pipeline-demo
```

Note that this will disable the `openshift-pipeline-demo:latest` image triggers which we can re-enable again via:

```bash
oc set triggers dc/openshift-pipeline-demo --auto
```

#### Image Stream Tags

An image stream tag is a named pointer to an image in an image stream. It is often abbreviated as `istag`.



### Artifactory locally

https://bintray.com/jfrog/product/artifactory/download

```bash
docker pull docker.bintray.io/jfrog/artifactory-pro:latest
```

```bash
docker images | grep artifactory

docker.bintray.io/jfrog/artifactory-pro     latest              bb814c649d8d        2 days ago          971MB
```

```bash
docker run --name artifactory -d -p 8081:8081 docker.bintray.io/jfrog/artifactory-pro:latest
```

### Artifactory on OpenShift


```bash
oc new-app docker.bintray.io/jfrog/artifactory-pro
```

```bash
minishift openshift service artifactory-pro
|-----------|-----------------|----------|--------------------------------------------------------|--------|
| NAMESPACE |      NAME       | NODEPORT |                       ROUTE-URL                        | WEIGHT |
|-----------|-----------------|----------|--------------------------------------------------------|--------|
| myproject | artifactory-pro |          | http://artifactory-pro-myproject.192.168.99.102.nip.io |        |
|-----------|-----------------|----------|--------------------------------------------------------|--------|
```

or

```bash
minishift openshift service s2i-java-example --in-browser
```



export the `settings.xml` from OpenShift

Copy into the `configuration` folder.

Try that it works locally

```bash
mvn -s settings.xml clean install
```

then start a new build

```bash
oc start-build openshift-pipeline-demo
```

you will see in the logs:

```bash
...
Using custom maven settings from /tmp/src/configuration/settings.xml
...
```

When running the build we can also access the repository with internal DNS hostname:

Instead of 

``` 
http://artifactory-pro-myproject.192.168.99.102.nip.io
```

we can use 

```
http://artifactory-pro.myproject.svc:8081
```


More details how the `S2I Centos images` are created using:

```bash
https://github.com/fabric8io-images/s2i/tree/master/java/images
```

How the maven setup works:

```bash
https://github.com/fabric8io-images/s2i/blob/master/java/images/centos/s2i/assemble
```

### OC CLI

Comes handy when learning the OpenShift CLI commands: source <(oc completion zsh) (put it in your ~/.zshrc). If you don't use ZSH, then  more details here: oc completion -h

```bash
oc completion -h
```

```bash
source <(oc completion zsh)
```

Add it to your `~/.zshrc`

More details here:

```bash
oc completion -h
```



```bash
--> Found Docker image bb814c6 (3 days old) from docker.bintray.io for "docker.bintray.io/jfrog/artifactory-pro"

    * An image stream tag will be created as "artifactory-pro:latest" that will track this image
    * This image will be deployed in deployment config "artifactory-pro"
    * Port 8081/tcp will be load balanced by service "artifactory-pro"
      * Other containers can access this service through the hostname "artifactory-pro"
    * This image declares volumes and will default to use non-persistent, host-local storage.
      You can add persistent volumes later by running 'oc set volume dc/artifactory-pro --add ...'

--> Creating resources ...
    imagestream.image.openshift.io "artifactory-pro" created
    deploymentconfig.apps.openshift.io "artifactory-pro" created
    service "artifactory-pro" created
--> Success
    Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose svc/artifactory-pro'
    Run 'oc status' to view your app.
```

```bash
oc expose svc/artifactory-pro
```

Default credentials: admin/password

#### Minishift

listing cached images

```bash
minishift image list

openshift/origin-control-plane:v3.11.0
openshift/origin-docker-registry:v3.11.0
openshift/origin-haproxy-router:v3.11.0
```



#### OpenShift Pipeline



Jenkins (Ephemeral) has been created.

The tutorial at `https://github.com/openshift/origin/blob/master/examples/jenkins/README.md`  
contains more information about using this template.


### Resources

Follow this:

https://developers.redhat.com/blog/2017/02/23/getting-started-with-openshift-java-s2i/
https://github.com/redhat-helloworld-msa/ola
https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_java_s2i_for_openshift/index

pom.xml is detected then it will be jee as language
https://docs.openshift.com/enterprise/3.0/dev_guide/new_app.html

Improving Build Time of Java Builds on OpenShift
https://blog.openshift.com/improving-build-time-java-builds-openshift/

Decrease Maven Build times in OpenShift Pipelines using a Persistent Volume Claim
https://blog.openshift.com/decrease-maven-build-times-openshift-pipelines-using-persistent-volume-claim/

Interesting Artifactory HA deployment
https://github.com/jfrog/artifactory-docker-examples/tree/master/openshift/artifactory

How to Create a Builder Image with S2I
https://medium.com/@hakdogan/how-to-create-a-builder-image-with-s2i-da41384c8c81