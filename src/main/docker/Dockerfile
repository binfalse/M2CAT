FROM tomcat:8-jre8
MAINTAINER martin scharm

# remove the default tomcat application
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war

# add the M2CAT web application as the new default web app
COPY M2CAT-example-context.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
COPY M2CAT-${project.version}.war /usr/local/tomcat/webapps/ROOT.war
