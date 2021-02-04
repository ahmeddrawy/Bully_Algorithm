# Bully Algorithm
This is my implementation for [Bully Algorithm](https://en.wikipedia.org/wiki/Bully_algorithm)
### Description
Using peer to peer architecture and java socket programming

producing one excutable file to be used with multi instances.

### Built using Peer to Peer Architecture
#### If new process enters the system 
1. check if there's no coordinator in coordinator port
   1. if there's no coordinator, then current process declares that it's the coordinator
2. if there's a coordinator, process sends a message declaring itself,
3. Coordinator assigns the new Process a port to listen to
4. Coordinator dispatch task amoung other process to check the min value of list
5. Coordinator collect the return values and find the min amoung them and print to the GUI outpit
6. Coordinator sends a list of alive processes in system to the new process
7. Coordinator sends the new process port to the other processes in system

####  Coordinator down
 Coordinator sends an alive message for all alive processes each period of time, if any process found that a coordinator down sends an election message to other processes

#### Winning the Elections
Eventually the process with the highest priority - port in our case - wins an election.
The winning process will notify all the other running processes that it is the coordinator.

## How To Compile & Run

1. Start Command Prompt.
2. Navigate to the folder that holds your class files:
    C:\>cd \mywork
3. Set path to include JDK’s bin.  For example:
    C:\mywork> path c:\Program Files\Java\jdk1.8.0_25\bin;%path%
4. Compile your class(es):
   C:\mywork> javac *.java
5. Create jar file:
    C:\mywork> jar cvfe bully_algorithm.jar bully_algorithm *.class

6. run your jar:
    C:\mywork> bully_algorithm.jar
    or
    C:\mywork> java -jar bully_algorithm.jar





