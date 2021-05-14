#!/bin/bash
java -XX:-OmitStackTraceInFastThrow -server  -XX:-OmitStackTraceInFastThrow -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+UnlockExperimentalVMOptions -Dlogging.path=/data/wincovid21-ingestion/wincovid21-ingestion/  -Xms16384m -XX:+UseG1GC -Xmx16384m -jar -Dspring.profiles.active=prod  /data/wincovid21-ingestion/wincovid21-ingestion/target/wincovid21-ingestion.jar  &> /data/wincovid21-ingestion/wincovid21-ingestion/console.log & disown

### In case want to use JDK16
### /data/tools/jdk16/jdk-16.0.1/bin/java -XX:-OmitStackTraceInFastThrow -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:+UseZGC -Xlog:gc -XX:+UnlockDiagnosticVMOptions -XX:-ZProactive -Xlog:safepoint,classhisto*=trace,age*,gc*=info:file=/data/wincovid21-ingestion/wincovid21-ingestion/gclogs/gc-%t.log:time,tid,tags:filecount=50,filesize=50m -Xms16384m -Xmx16384m -jar -Dspring.profiles.active=prod  /data/wincovid21-ingestion/wincovid21-ingestion/target/wincovid21-ingestion.jar  &> /data/wincovid21-ingestion/wincovid21-ingestion/console.log & disown