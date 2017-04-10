 # the compiler: gcc for C program, define as g++ for C++
CC = gcc

  # compiler flags:
  #  -g    adds debugging information to the executable file
  #  -Wall turns on most, but not all, compiler warnings
CFLAGS  = -g -Wall

  # the build target executable:
TARGET1 = Fserver
TARGET2 = Fclient

all: Fserver Fclient

Fserver: tcpserver.c
	$(CC) $(CFLAGS) -o Fserver tcpserver.c

Fclient: tcpclient.c
	$(CC) $(CFLAGS) -o Fclient tcpclient.c