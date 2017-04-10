 # the compiler: gcc for C program, define as g++ for C++
CC = gcc

  # compiler flags:
  #  -g    adds debugging information to the executable file
  #  -Wall turns on most, but not all, compiler warnings
CFLAGS  = -g -Wall

  # the build target executable:
TARGET1 = Mserver
TARGET2 = Mclient

all: Mserver Mclient

Mserver: udpserver.c
	$(CC) $(CFLAGS) -o  Mserver udpserver.c -lm

Mclient: udpclient.c
	$(CC) $(CFLAGS) -o Mclient udpclient.c
