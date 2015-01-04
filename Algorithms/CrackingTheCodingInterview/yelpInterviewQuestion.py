#given a matrix with sorted rows which contain only 0's and 1's, count the number of 1's
#naive approach is O(nxm). We can go for a better approach with binary search.#
## Given a 2-d matrix, containing only 0s and 1s and every row is sorted, find row containing max 1s.
# 
# 0 0 1 1
# 0 0 0 0
# 1 1 1 1
# 0 0 0 1
# 
# 
# [ 0 0 1 1 ] => Number of 1s
import math
def countOnesInRow(row, start, end):
	#print start
	#print end
	mid = math.floor((start+end)/float(2))
	#print mid
	if start==end:
		if row[int(end)] == 0:
			return 0
		if row[int(end)] == 1:
			return len(row)
	if row[int(mid)] == 1:
		end = mid-1
		if row[int(end)] == 0:
			return (len(row)-end)
		return countOnesInRow(row, start, end)
	elif row[int(mid)] == 0:
		start = mid+1
		#print 'inside mid eq 0'
		if row[int(start)] == 1:
		#	print 'inside second: '+str(start)+': '+str(len(row))
			return (len(row)-start)			
		return countOnesInRow(row, start, end)

if __name__ == '__main__':
	f = open('matrix.txt')
	maxRow = 0
	maxOnes = 0
	for i, line in enumerate(f):
		linelist = list(line)
		row = []
		for x in linelist:
			if x == '0':
				row.append(0)
			elif x == '1':
				row.append(1)
		#print 'entered for loop'
		print row
		ones = countOnesInRow(row, 0, len(row)-1)
		#print ones
		if maxOnes < ones:
			maxRow = i
			maxOnes = ones
	print maxRow
	print maxOnes
