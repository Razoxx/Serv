@echo off
title EmuRT: Game Server Registration...
:start
echo Starting Game Server Registration.
echo.
java -server -Xms64m -Xmx64m -cp config/xml;../serverslibs/*; l2p.loginserver.GameServerRegister

pause
