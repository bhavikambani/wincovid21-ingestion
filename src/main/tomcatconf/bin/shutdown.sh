#!/bin/bash
for pid in $(ps -eaf | grep java | grep wincovid21-ingestion.jar | awk '{print $2}'); do kill -9 ${pid}; done