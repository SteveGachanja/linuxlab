FROM maven:3.5.3-jdk-8-alpine AS builder
WORKDIR /workspace/app
COPY settings.xml /root/.m2/settings.xml

COPY mwalletapi/pom.xml mwalletapi/pom.xml
COPY mwalletcommons/pom.xml mwalletcommons/pom.xml
COPY mwalletcustomerslibrary/pom.xml mwalletcustomerslibrary/pom.xml
COPY mwalletadminlibrary/pom.xml mwalletadminlibrary/pom.xml
COPY mwalletcustomerAccountSummary/pom.xml mwalletcustomerAccountSummary/pom.xml
COPY mwalletaccountbalancelibrary/pom.xml mwalletaccountbalancelibrary/pom.xml
COPY mwalletchargeslibrary/pom.xml mwalletchargeslibrary/pom.xml
COPY mwalletTransactionsLibrary/pom.xml mwalletTransactionsLibrary/pom.xml
COPY mwalletAccountStatementLibrary/pom.xml mwalletAccountStatementLibrary/pom.xml
COPY mwalletUpdateAccountFlaglibrary/pom.xml mwalletUpdateAccountFlaglibrary/pom.xml
COPY mwalletBlockAmountLibrary/pom.xml mwalletBlockAmountLibrary/pom.xml
COPY pom.xml .

#RUN mvn dependency:go-offline
RUN mvn clean install

COPY mwalletapi/src mwalletapi/src
COPY mwalletcommons/src mwalletcommons/src
COPY mwalletcustomerslibrary/src mwalletcustomerslibrary/src
COPY mwalletadminlibrary/src mwalletadminlibrary/src
COPY mwalletcustomerAccountSummary/src mwalletcustomerAccountSummary/src
COPY mwalletaccountbalancelibrary/src mwalletaccountbalancelibrary/src
COPY mwalletchargeslibrary/src mwalletchargeslibrary/src
COPY mwalletTransactionsLibrary/src mwalletTransactionsLibrary/src
COPY mwalletAccountStatementLibrary/src mwalletAccountStatementLibrary/src
COPY mwalletUpdateAccountFlaglibrary/src mwalletUpdateAccountFlaglibrary/src
COPY mwalletBlockAmountLibrary/src mwalletBlockAmountLibrary/src

RUN mvn -DskipTests  package
RUN mkdir -p mwalletapi/target/dependency && (cd mwalletapi/target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jdk-alpine

ARG DEPENDENCY=/workspace/app/mwalletapi/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-Dspring.config.location=/app/","-cp","app:app/lib/*","com.coop.mwalletapi.MwalletengineApplication"]