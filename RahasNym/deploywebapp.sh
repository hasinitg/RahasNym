#! bin/bash
cd SPClient
mvn clean install
sh deployclient.sh
cd ..
cd ServiceProvider
mvn clean install
cd ..
unzip ServiceProvider/target/amazingshop.war -d ServiceProvider/target/amazingshop
cp SPClient/target/*.jar ServiceProvider/target/amazingshop/
#uncomment below when deploying with client
cd ServiceProvider/target/amazingshop
###cd ServiceProvider/target/
zip -r amazingshop.war *
cp amazingshop.war ../../../../../MyResearch/apache-tomcat-7.0.42/webapps/
###cp amazingshop.war ../../../../MyResearch/SP/apache-tomcat-7.0.42/webapps/
#deploy idp
cd ../../../IdentityProvider/target/
cp IDP.war ../../../../MyResearch/apache-tomcat-7.0.42/webapps/
####cd ../../../../MyResearch/IDP/apache-tomcat-7.0.42
####zip -r apache-tomcat-7.0.42.zip *
####cd ../../SP/apache-tomcat-7.0.42
#####zip -r apache-tomcat-7.0.42.zip *