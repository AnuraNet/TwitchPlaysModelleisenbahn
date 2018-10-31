# TwitchPlaysModelleisenbahn
The software for a system to control an analog model railway.

## EisenbahnBackend
The Java Backend provides an http interface to control devices. It starts the scripts on the Raspberry Pi to output the commanded signals. It also provides the twitch interface to be remote controllable by the twitch chat.

## nxc
The nxc folder contains the NXC (Not eXactly C) code for the Lego Mindstorms NXT. It controls three motors to change the speeds of the three railway tracks.

