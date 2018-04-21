# test

`mvn clean package`

`-XX:+UseG1GC -XX:G1HeapRegionSize=1024 -cp "path/to/postgresql-42.2.2.jar;."`

Дополнительно:
 * настройка логгирования
 
 `-Dlogback.configurationFile="path/to/project/src/main/resources/logback.xml"`
 
 * настройка базы данных
 
 `-Ddatabase.properties="path/to/project/src/main/resources/database.properties"`