FROM openjdk:17-oracle AS build
WORKDIR /01-CoWorkBooking-Backend
COPY target/hexagonal-0.0.1-SNAPSHOT.jar hexagonal-architecture-springboot-mongodb-co-work-booking-app.jar
ENTRYPOINT ["java","-jar","hexagonal-architecture-springboot-mongodb-co-work-booking-app.jar"]