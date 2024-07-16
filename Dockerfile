# Utiliser une image de base avec Java 17 (par exemple, OpenJDK)
FROM openjdk:17-jdk-alpine

# Créer un utilisateur non root
RUN addgroup -S spring && adduser -S spring -G spring

# Définir l'utilisateur pour les commandes suivantes
USER spring:spring

# Copier le jar de l'application Spring Boot
COPY target/todos.jar todos.jar

# Commande d'entrée pour démarrer l'application
ENTRYPOINT ["java","-jar","/todos.jar"]
