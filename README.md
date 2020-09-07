# TimeServerGUI
 
Mateusz Domalążek 215705
Politechnika Łódzka

Project structure:

sample - package with client module:
class Client - search for available connections
class TCP - connection to the selected address and executing tasks (time calculations)
class LastServer - store information about the last session
class Controller - gui controller
class MulticastReceiver - waiting for multicast message "OFFER" (port 4446)
class MulticastSender - sending multicast message "DISCOVERY" (port 7)
sample.fxml - the user interface of a JavaFX application

server - package with server module
class TimeServer - server class
class DiscoveryThread - listening for "DISCOVERY" (port 7) messages and sending "OFFER" (port 4446)
class TCPClientThread - sending information about the current time
class TCPListenerThread - waiting for the client

Language: Java
Development environment: IntelliJ IDEA 2018.3.6 (Community Edition)
