@echo off
title Game Server Console
:start
echo Starting GameServer.
echo.

java -version:1.8 -server -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Xmx2G -cp config/xml;../serverslibs/*; l2p.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
