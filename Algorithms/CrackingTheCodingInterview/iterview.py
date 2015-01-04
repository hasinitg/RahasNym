import sys
def computeDotProduct():
	x = 0
	y = 0
	n = []
	p = []
	q = []
	r = []
	a = raw_input()
	b = a.split(' ')
	x = int(b[0])
	y = int(b[1])
	for i in range(x):
		d = raw_input()
		e = d.split(' ')
		n.append(int(e[0]))
		p.append(int(e[1]))
	
	for j in range(y):
		d = raw_input()
		e = d.split(' ')
		q.append(int(e[0]))
		r.append(int(e[1]))
	dotproduct = 0
	for k,l in enumerate(n):
		for m, s in enumerate(q):
			if l == s:
				dotproduct+= p[k]*r[m]
	print str(dotproduct)	
if __name__ == '__main__':
	computeDotProduct()
		
		
	
	
