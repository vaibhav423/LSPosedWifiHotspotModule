#!/bin/bash

# This script initializes the Gradle wrapper if it doesn't already exist

echo "Checking if Gradle wrapper exists..."

if [ ! -f "./gradlew" ] || [ ! -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "Gradle wrapper not found, initializing..."
  
  # Create directory structure
  mkdir -p gradle/wrapper
  
  # Download wrapper jar and properties files
  echo "Downloading Gradle wrapper JAR..."
  wget -q -O gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.jar
  
  echo "Downloading Gradle wrapper properties..."
  echo "distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6.1-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists" > gradle/wrapper/gradle-wrapper.properties
  
  echo "Downloading gradlew script..."
  wget -q -O gradlew https://raw.githubusercontent.com/gradle/gradle/master/gradlew
  chmod +x gradlew
  
  echo "Gradle wrapper initialized successfully"
else
  echo "Gradle wrapper already exists"
fi
