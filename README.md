# Jmeter-PTS

#### This project contains code testing and reporting Jmeter tests placed inside jmx folder

* run command in following sequence
```shell
docker build -t knovel-jmeter:1.0 .
```
* you can run the test by providing **only filename** from any jmx files inside jmx folder. for example,
```shell
docker run  knovel-jmeter:1.0 SearchSubstancesInternalSolr
```

This docker image expects following environment variable when running container successfully.

```shell
export SMTP_HOST_NAME=""
export SMTP_AUTH_USER=""
export SMTP_AUTH_PWD=""
export FROM=""
export TO=""
```
* You can provide them inside **launch.sh** file or make sure to avail them inside container before running.
* When you run the container it will run the jmeter test for provided file and mail the results to variable set in environment.


#### Mail Utility
Above environment variable are requied to send mail from **SesMailer.jar**.

If you want to change mail behaviour, make changes in **SimpleMail.java** file and build the project and get the jar-with-dependencies from target, rename it to SesMailer.jar and place in project root directory. then build image again.


