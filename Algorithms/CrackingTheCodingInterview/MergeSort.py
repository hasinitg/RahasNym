import math

def merge(left, right):
	mergedLength = len(left) + len(right)
	merged = [None]*mergedLength
	i = 0
	j = 0
	for k in range(mergedLength):
		if (i != 0) and (i == (len(left))):
			merged[k:] = right[j:]
			break
		if (j != 0) and (j == (len(right))):
			merged[k:] = left[i:]
			break
		a = left[i]
		b = right[j]
		if a <= b:
			merged[k] = a	
			i+=1
		else:
			merged[k] = b
			j+=1
	return merged	


def mergeSort(array):
	if len(array) == 1:
		#print 'base case reached'
		return array
	mid = math.floor((len(array)-1)/float(2)) 
	mid = int(mid)
	#print mid
	left = array[0:mid+1]
	#print left
	sortedLeft = mergeSort(left)
	#print sortedLeft
	right = array[mid+1:len(array)]
	#print right
	sortedRight = mergeSort(right)
	#print sortedRight
	merged = merge(sortedLeft, sortedRight)
	#print merged
	return merged
	
	

if __name__ == '__main__':
	array = [3, 2, 4, 5, 1, 9, 43, 23, 12, 8]
	sorted = mergeSort(array)
	print sorted
