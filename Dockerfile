FROM amazoncorretto:21
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
HEALTHCHECK --interval=5s --timeout=5m \
  CMD curl -f http://localhost/ || exit 1