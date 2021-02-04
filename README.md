# Bully Algorithm
This is my implementation for [Bully Algorithm](https://en.wikipedia.org/wiki/Bully_algorithm)
### Description
Using peer to peer architecture and java socket programming, Simulating bully algorithm using interprocess communication without using threads or interprocess communication libraries 

Multiple instances of your application should run (different processes) and communicate with each other
Providing simple GUI Swing to trace and kill the instance of the app

### Built using Peer to Peer Architecture
#### If new process enters the system 
1. check if there's no coordinator in coordinator port
   1. if there's no coordinator, then current process declares that it's the coordinator
2. if there's a coordinator, process sends a message declaring itself,
3. Coordinator assigns the new Process a port to listen to
4. Coordinator dispatch task amoung other process to check the min value of list
4. Coordinator sends a list of alive processes in system to the new process
5. Coordinator sends the new process port to the other processes in system

####  Coordinator down
 Coordinator sends an alive message for all alive processes each period of time, if any process found that a coordinator down sends an election message to other processes

#### Winning the Elections
Eventually the process with the highest priority - port in our case - wins an election.
The winning process will notify all the other running processes that it is the coordinator.


