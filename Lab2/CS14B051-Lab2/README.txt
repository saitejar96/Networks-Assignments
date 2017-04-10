This folder contains two subfolders-
MathServer for part 1 and
FileServer for part 2


Upon entering the directories and running make in each two executables are created in each subfolder-
Mserver and Mclient in MathServer and
Fserver and Fclient in FileServer.


Both servers can be run using ./*server --port number--
Then, both clients can be run using ./*client --hostaddress-- --port number--


For the math server I used float for division and int for other operations…..so the standard boundaries on inputs are enforced.
For the file server , of the file contains “SORRY!” as the first six characters and 6 bytes are requested…….since client receives “SORRY!”..... it translates it to “ File not found”.