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
cd ServiceProvider/target/amazingshop
zip -r amazingshop.war *
cp amazingshop.war ../../../../../MyResearch/apache-tomcat-7.0.42/webapps/