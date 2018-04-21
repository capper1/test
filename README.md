# test

`mvn clean package`

`java -jar target/test-jar-with-dependencies.jar -db jdbc:postgresql://db_host:db_port/db_name -p db_password -u db_user`

Дополнительно:
 * настройка логгирования
 
 `-Dlogback.configurationFile=/path/to/logback.xml`