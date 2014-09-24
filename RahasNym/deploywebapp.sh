#! bin/bash
unzip ServiceProvider/target/amazingshop.war -d ServiceProvider/target/amazingshop
cp SPClient/lib/*.jar ServiceProvider/target/amazingshop/
cd ServiceProvider/target/amazingshop
zip -r amazingshop.war *
cp amazingshop.war ../../../../../MyResearch/apache-tomcat-7.0.42/webapps/