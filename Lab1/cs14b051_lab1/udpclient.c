/* 
 * tcpclient.c - A simple TCP client
 * usage: tcpclient <host> <port>
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <time.h> 

#define BUFSIZE 1024

/* 
 * error - wrapper for perror
 */
void error(char *msg) {
    perror(msg);
    exit(0);
}

int main(int argc, char **argv) {
    int sockfd, portno, n;
int i;
    int xyz;
    struct sockaddr_in serveraddr;
    struct hostent *server;
    char *hostname;
    char buf[BUFSIZE];
    clock_t start, end;
     double cpu_time_used=0;
int count=0;

    /* check command line arguments */
    if (argc != 3) {
       fprintf(stderr,"usage: %s <hostname> <port>\n", argv[0]);
       exit(0);
    }
    hostname = argv[1];
    portno = atoi(argv[2]);
while(count<1000){
    /* socket: create the socket */
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");

    /* gethostbyname: get the server's DNS entry */
    server = gethostbyname(hostname);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host as %s\n", hostname);
        exit(0);
    }

    /* build the server's Internet address */
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
	  (char *)&serveraddr.sin_addr.s_addr, server->h_length);
    serveraddr.sin_port = htons(portno);
    
      /* get message line from the user */
     //printf("Please enter msg: ");
      bzero(buf, BUFSIZE);
      //fgets(buf, BUFSIZE, stdin);
strcpy(buf,"saitejareddy");

      /* send the message line to the server */
      //n = write(sockfd, buf, strlen(buf));
      xyz = sizeof(serveraddr);
start =clock();
      n = sendto(sockfd, buf, strlen(buf), 0, &serveraddr, xyz);

      if (n < 0) 
	error("ERROR writing to socket");

      /* print the server's reply */
      //n = read(sockfd, buf, BUFSIZE);
      n = recvfrom(sockfd, buf, strlen(buf), 0, &serveraddr, &xyz);  
end = clock();
cpu_time_used += ((double) (end - start)) / CLOCKS_PER_SEC;
count++;  
if (n < 0) 
	error("ERROR reading from socket");
      printf("Echo from server with address %s and port %d: %s\n",hostname,portno, buf);
}
printf("\n%f\n",cpu_time_used/1000);
    close(sockfd);
    return 0;
}
