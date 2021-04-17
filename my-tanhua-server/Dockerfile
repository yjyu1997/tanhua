FROM www.yusora.top/library/jdk1.8:latest

COPY *.jar /app.jar

EXPOSE 18081

RUN mkdir -p /Users/heyu/Documents/rsa

COPY ./rsa.pub /Users/heyu/Documents/rsa/rsa.pub


ENTRYPOINT ["java","-jar","/app.jar"]