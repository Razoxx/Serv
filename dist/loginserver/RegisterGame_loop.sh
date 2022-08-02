#!/bin/bash

while :;
do
	java -server -Xmx16m -cp config/xml:../serverslibs/*: l2p.loginserver.GameServerRegister

	[ $? -ne 2 ] && break
	sleep 10;
done
