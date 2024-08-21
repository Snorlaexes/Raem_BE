# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine AS builder

# For Timezone
RUN apk add --no-cache tzdata

# Set Timezone
RUN cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# Set the working directory (automatically creates /app if it doesn't exist)
WORKDIR /home/app

# Copy the entire project to the container
COPY . .

# Give executable permissions to gradlew
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Copy the built jar file to the working directory
RUN cp build/libs/raem-0.0.1-SNAPSHOT.jar raem.jar

# Expose the application port
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "raem.jar"]