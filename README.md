# TimeServerGUI
 
Mateusz Domalążek 215705<br />
Politechnika Łódzka<br />
<br />
Project structure:<br />
<br />
sample - package with client module: <br />
class Client - search for available connections<br />
class TCP - connection to the selected address and executing tasks (time calculations)<br />
class LastServer - store information about the last session<br />
class Controller - gui controller<br />
class MulticastReceiver - waiting for multicast message "OFFER" (port 4446)<br />
class MulticastSender - sending multicast message "DISCOVERY" (port 7)<br />
sample.fxml - the user interface of a JavaFX application<br />
<br />
server - package with server module<br />
class TimeServer - server class<br />
class DiscoveryThread - listening for "DISCOVERY" (port 7) messages and sending "OFFER" (port 4446)<br />
class TCPClientThread - sending information about the current time<br />
class TCPListenerThread - waiting for the client<br />
<br />
Language: Java<br />
Development environment: IntelliJ IDEA 2018.3.6 (Community Edition)<br />

<b>Note:</b><br />
run a server using console before starting the first client:<br />
<i>javac TimeServer.java</i><br />
<i>java TimeServer</i><br />

![Alt text](img.gif?raw=true "Title")<br />

Useful links:<br />
https://stackoverflow.com/questions/50603906/logger-output-to-textarea-in-javafx<br />
https://www.baeldung.com/java-broadcast-multicast<br />
https://michieldemey.be/blog/network-discovery-using-udp-broadcast/<br />
https://docs.oracle.com/javase/tutorial/networking/nifs/listing.html<br />
https://docs.oracle.com/javase/tutorial/networking/nifs/parameters.html<br />
