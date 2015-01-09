import sys
import traceback
def readFromSTDIN():
	for l in sys.stdin:
		print l

	for k in sys.stdin.read():
		print k

	x = input(sys.stdout.write('Are you finished?'))
	print x

def exceptionHandling():
	x = input('First Number:')
	y = input('Second Number:')
	a = 0	
	try:
		a = x/float(y)
	except:
		#both the following methods are correct for exeption handling. However, the latter gives more info.
		#e = sys.exc_info()[0]
		#print e
		print traceback.format_exc()
	else:
		print a		
		

if __name__ == '__main__':
	#readFromSTDIN()
	exceptionHandling()