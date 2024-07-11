FROM eclipse-temurin:21

RUN mkdir /opt/app
WORKDIR /opt/app
COPY TF2CC_MM/gradle /opt/app/gradle
COPY TF2CC_MM/src /opt/app/src
COPY TF2CC_MM/build.gradle.kts /opt/app/build.gradle.kts
COPY TF2CC_MM/settings.gradle.kts /opt/app/settings.gradle.kts
COPY TF2CC_MM/gradlew /opt/app/gradlew

RUN ./gradlew fatJar
RUN cp build/libs/TF2CC_MM-*.jar /opt/app/tf2cc_mm.jar

ENTRYPOINT ["java", "-jar", "/opt/app/tf2cc_mm.jar"]
CMD ["-p=8088"]
