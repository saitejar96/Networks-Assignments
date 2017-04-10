import csv

data = []
with open('obs.csv','rb') as csvfile:
	spamreader = csv.reader(csvfile,delimiter=',')
	for row in spamreader:
		data.append(row)

data1=[]
data2=[]
data3=[]

print(data)

sack = ["0","1"]
ws = ["16","256"]
ccp = ["reno","cubic"]
delay = ["2","50"]
ldp = ["0.5","5"]

count = 0
for a in range(0,2):
	for b in range(0,2):
		for c in range(0,2):
			for d in range(0,2):
				for e in range(0,2):
					data1.append([sack[a],ws[b],ccp[c],delay[d],ldp[e],data[count][0],data[count][1],data[count][2],data[count][3]])
					count+=1
					data2.append([sack[a],ws[b],ccp[c],delay[d],ldp[e],data[count][0],data[count][1],data[count][2],data[count][3]])
					count+=1
					data3.append([sack[a],ws[b],ccp[c],delay[d],ldp[e],data[count][0],data[count][1],data[count][2],data[count][3]])
					count+=1

with open('obs1.csv','wb') as csvfile:
	writer = csv.writer(csvfile)
	writer.writerows(data1)

with open('obs2.csv','wb') as csvfile:
	writer = csv.writer(csvfile)
	writer.writerows(data2)

with open('obs3.csv','wb') as csvfile:
	writer = csv.writer(csvfile)
	writer.writerows(data3)