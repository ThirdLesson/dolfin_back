FROM tomcat:9.0.107-jdk17-temurin

# WAR 파일 복사 (Gradle build 후 build/libs/ 폴더에 생성된 경우)
COPY build/libs/dolfin_back-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
