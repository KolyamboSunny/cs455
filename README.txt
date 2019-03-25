Homework 2: Scaling 
by Nikolai Sannikov

1) unpack the tar file
% tar -xvf Nikolai-Sannikov-HW2-PC.tar

2) build the code using gradle (optional)
% gradle build -x test
or 
% gradle build

3)switch to the build root directory
% cd build/classes/java/main

4) run server
% java cs455.scaling.server.Server <port_number> <thread_pool_size> <batch_size> <batch_time (sec)>

5) run client(s)
% java cs455.scaling.client.Client <Registry_host> <Registry_port_number> <messaging_rate (msg/sec)>

