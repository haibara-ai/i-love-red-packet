#/bin/bash

# >> 	append stdout to log.txt
# 2>&1	merge stderr to stdout
# &	run in background
java -jar server.jar 16888 >> log.txt 2>&1 &