/*
 * udpserver.c
 *
 * This is an example TCP/IP UDP socket server.
 * It will read packets sent to 'portno' and write them back
 * to the sender.  It has no reliability functions.
 * It will loop forever and must be killed in order to make
 * it terminate.
 *
 * syntax:
 *	% udpserver portno &
 *
 * Start the server first and then start the udpclient.c app to
 * send packets to it.  Use the debug switch with udpclient.c to
 * see if any of the packets disappear.  They won't disappear on
 * localhost, but they might go away if they are crossing a busy 
 * router.
 */

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/times.h>
#include <errno.h>
#include <signal.h>
#include <stdlib.h>
#include <math.h>

#define TRUE 1
#define FALSE 0

#define ACK  'z'


#define MAXBUF	10 * 1024 

int sock; 

main(argc,argv)
char **argv;
{
	struct sockaddr_in server;
	struct sockaddr_in from;
	char buf[MAXBUF];
	int ackvar = ACK;
	int fromlen;
	extern int errno;
	int port;
	extern int onintr();
	int current = 0;
	char expression[10];
	char op1[MAXBUF];
	char op2[MAXBUF];
	char ans[MAXBUF];

	if (argc != 2) {
		printf("syntax error: udpserver portno &\n");
		exit(1);
	}
	port = atoi(argv[1]);
	fromlen = sizeof( struct sockaddr_in ); 

	signal(SIGINT, onintr);

	/* create INTERNET, udp datagram socket
	*/
	sock = socket(AF_INET, SOCK_DGRAM, 0); 

	if ( sock < 0 ) {
		perror("udp server: socket call\n");
		exit(1);
	}

	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;   /* ok from any machine */
	server.sin_port = htons(port);      /* specific port */

	/* bind protocol to socket
	*/
	if (bind(sock, &server, sizeof(server))) {
		perror("binding udp socket\n");
		exit(1);
	}
	for ( ;; ) {     /* do forever */
		int rc;
		int s1=0,s2,s3,e1=2,e2,e3;
		int i=0;
		strcpy(buf,"");

		if ((rc=recvfrom(sock, buf, MAXBUF, 0, &from, &fromlen)) < 0 ) {
			printf("server error: errno %d\n",errno);
			perror("reading datagram");
			exit(1);
		}

		//printf("udpserver: got packet %d: ", current);
		//printFrom(&from);
		int o1 = 2;
		int o2 = 2;
		printf("Received : %s\n",buf);
//strcpy(buf,"add 6 7");
   		sscanf( buf, "%s %d  %d", expression,&o1,&o2 );

		
		/*strncpy(expression, buf, 3);
		expression[3] = '\0';
		for(i=0;i<strlen(buf);i++)
		{
			if(buf[i]==' ')
				break;
		}
		i++;
		s2=i;
		for(i=s2;i<strlen(buf);i++)
		{
			if(buf[i]==' ')
				break;
		}
		i++;
		s3=i;
		e2=i-2;
		e3 = strlen(buf)-1;
		strncpy(op1, buf+s2, e2-s2+1);
		strncpy(op2, buf+s3, e3-s3+1);*/
		
		//strcpy(expression,"sub");
		//printf("op1:%d\n",o1);
		//printf("op1:%d\n",o2);
		if(strcmp(expression,"add")==0)
		{
			int an = o1+o2;
			sprintf(buf, "%d", an);
		}
		else if(strcmp(expression,"sub")==0)
		{
			int an = o1-o2;
			sprintf(buf, "%d", an);
		}
		else if(strcmp(expression,"mul")==0)
		{
			int an = o1*o2;
			sprintf(buf, "%d", an);
		}
		else if(strcmp(expression,"div")==0)
		{
			float an = (float)o1/(float)o2;
			sprintf(buf, "%f", an);
		}
		else if(strcmp(expression,"exp")==0)
		{
			int an = pow(o1,o2);
			sprintf(buf, "%d", an);
		}
		else
		{
			sprintf(buf, "%s","ERROR:OPERATION NOT SUPPORTED");
		}

		//printf("555555555555555555555 %s\n",buf);
		if( sendto(sock,buf,MAXBUF, 0, &from,sizeof (struct sockaddr_in)) < 0 ) {
			perror("sendto");
			if (errno == ENOBUFS)
				continue;
			exit(1);
		}
		current++;

	}
	/* can't get here, but just in case: close sockets
	*/
	close(sock);
	return(0);
}


printFrom(from)
struct sockaddr_in *from;
{
	char *ns;
	struct hostent *gethostbyaddr();
	struct hostent *h;
	char *inet_ntoa();

	fprintf(stdout,": client port %d: ",from->sin_port&0xffff);
	/* convert inet number to inet network string
	*/
	ns = inet_ntoa(from->sin_addr);
	/*
	h = gethostbyaddr(ns,sizeof(ns),AF_INET);
	*/
	fprintf(stdout,"client ip addr: %s\n",ns);
}

onintr()
{
	close(sock);
	exit(0);
}
