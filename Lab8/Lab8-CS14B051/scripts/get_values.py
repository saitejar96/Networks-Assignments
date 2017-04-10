import commands

req_url = "wget -r 192.168.0.7:8000/"
file_names = ["512KB","1MB","2MB"]

for x in range(0,3):
	a1 = 0
	a2 = 0
	a3 = 0
	a4 = 0
	reqX = ""
	reqX = req_url+file_names[x]
	for y in range(0,5):
		try:
			s = commands.getstatusoutput(reqX)
			abc = len(s[1].split('\n'))-1
			s1 = (s[1].split('\n'))[abc]
			s2 = (s[1].split('\n'))[abc-1]
			a1 += float(s1.split()[5][:-1])
			a2 += float(s1.split()[6][1:])
			a3 += float(s2.split()[4][:-1])
			a4 = a3-a1
		except:
			y=y-1
	print(str(a1/5)+","+str(a2/5)+","+str(a3/5)+","+str(a4/5))