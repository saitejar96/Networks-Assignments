from csv import reader
from matplotlib import pyplot

for x in range(0,32):
	with open('outfile+'+str(x)+'.csv', 'r') as f:
		data = list(reader(f))
	temp = [i[1] for i in data[1::]]
	pyplot.plot(range(len(temp)), temp)
	pyplot.savefig('graph'+str(x)+'.png')
	pyplot.gcf().clear()