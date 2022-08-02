#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Xms64m -Xmx64m -cp config/xml:../serverslibs/*: l2p.loginserver.AuthServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 10;
done
