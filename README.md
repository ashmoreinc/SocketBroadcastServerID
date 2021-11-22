# SocketBroadcastServerID
A small script which can be run as server or client. The server listens on a given for a UDP Broadcast and responds with an acknowledgement. The client broadcasts over the network and waits for those acks and collates all the responders addresses.

# Why
This was built for a client device to find servers hosting the same application on the same network and then allowing the client a choice to connect to any of the servers who repsonded.

A small part in a bigger project but with possible applications beyond it.

# Security
Note: No verification is built in to this and there are no security based countermeasures. Spoofing the server would not be hard AT ALL.
