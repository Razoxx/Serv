#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Xmx1G -cp config/xml:../serverslibs/*: l2p.gameserver.GameServer > log/stdout.log 2>&1
	
	[ $? -ne 2 ] && break
	sleep 30;
done
