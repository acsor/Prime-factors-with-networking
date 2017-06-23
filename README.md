# Prime factors with networking
This repository is an implementation of the MIT OCW 6.005 course problem assignment 5
(2011 version, now outdated and archived [here](http://dspace.mit.edu/handle/1721.1/106923)). Visit
[this page](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-005-software-construction-spring-2016/)
for the currently active version.

The prime-factors server with networking decomposes very large integers into prime factors, by using multithreading
and local server connections.
This implementation contains some minor modifications with respect to the problem assignment. These aren't many nor big,
however, so the final outcome is not much different from what was originally requested.

You can find instructions for better understanding this problem set (and even providing your own implementation)
in the PDF file of this directory.

## Basic usage
To run this project the [IntelliJ IDEA IDE](https://www.jetbrains.com/idea/) is recommended.

This project is comprised of a server and a client component:
1. To run the server component that spawns prime-factoring servers, open and then run
the main() method of `MasterServer.java`.
2. To connect to the master server (the server that accepts client connections) you cannot open a terminal and
communicate with the server directly (as was requested by the problem assignment). Instead, you need to open
`MasterClient.java` and run once again the main() method.
