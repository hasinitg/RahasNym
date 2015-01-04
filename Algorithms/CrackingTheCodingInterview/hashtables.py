##this implements a solution to the problem: given an array of integers, find the two integers that adds up to t=100.
##this is implemented using hash table to avoid O(n^2) overhead and have only O(n) worst case overhead.
from collections import defaultdict
def findInt():
	a = 3, 45, 67, 23, 120, 43, 24, 100, 23, 77, 60
	t = 100
	x = defaultdict(list)
	for i in a:
		j = i % 97
		x[j].append(i)
	print x
	print x.keys()
	for k in x.keys():
		#print k
		elementsList = x[k]
		for e in elementsList:
			#print e
			find = t - e
			findKey = find%97
			#targetList = []
			targetX = 1
			if findKey in x:	
				targetList = x.get(findKey)
				for target in targetList:
					if find == target:
						print  e, target
						targetList.remove(target)
						break
			elementsList.remove(e)
			
if __name__ == '__main__':
	findInt()
