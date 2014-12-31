#! bin/bash
cd ServiceProvider/target
cp amazingshop.war ../../../../MyResearch/apache-tomcat-7.0.42/webapps/
cd ../../IdentityProvider/target/
cp IDP.war ../../../../MyResearch/apache-tomcat-7.0.42/webapps/