#! bin/bash
cp ../../MySecuritySite/CryptoLib/target/CryptoLib-1.0-SNAPSHOT.jar lib/
cp ../RahasNymLib/target/RahasNymLib-1.0-SNAPSHOT.jar lib/
cp lib/*.jar target/
jar -ufm target/CryptoLib-1.0-SNAPSHOT.jar manifest.mf
jar -ufm target/RahasNymLib-1.0-SNAPSHOT.jar manifest.mf
jar -ufm target/SPClient-1.0-SNAPSHOT.jar manifest.mf
jar -ufm target/commons-logging-1.1.1.jar manifest.mf
jar -ufm target/commons-httpclient-3.1.jar manifest.mf
jar -ufm target/commons-codec-1.2.jar manifest.mf
jar -ufm target/json-20090211.jar manifest.mf
jar -ufm target/plugin.jar manifest.mf
jarsigner -storepass 7786@hasi target/CryptoLib-1.0-SNAPSHOT.jar hasini
jarsigner -storepass 7786@hasi target/RahasNymLib-1.0-SNAPSHOT.jar hasini
jarsigner -storepass 7786@hasi target/SPClient-1.0-SNAPSHOT.jar hasini
jarsigner -storepass 7786@hasi target/commons-logging-1.1.1.jar hasini
jarsigner -storepass 7786@hasi target/commons-httpclient-3.1.jar hasini
jarsigner -storepass 7786@hasi target/commons-codec-1.2.jar hasini
jarsigner -storepass 7786@hasi target/json-20090211.jar hasini
jarsigner -storepass 7786@hasi target/plugin.jar hasini
#cp target/*.jar ../../../MyResearch/apache-tomcat-7.0.42/webapps/amazingshop/
