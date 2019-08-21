Source-To-Image (S2I)

```
https://github.com/openshift/source-to-image
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



### Resources

Follow this:

https://developers.redhat.com/blog/2017/02/23/getting-started-with-openshift-java-s2i/
https://github.com/redhat-helloworld-msa/ola
https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_java_s2i_for_openshift/index

pom.xml is detected then it will be jee as language
https://docs.openshift.com/enterprise/3.0/dev_guide/new_app.html
