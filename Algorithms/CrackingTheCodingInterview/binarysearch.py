import math

def binarySearch(array, targetVal, start, end):
	mid = int(math.floor((start+end)/float(2)))
	if targetVal == array[mid]:
		return mid
	elif targetVal < array[mid]:
		end = mid-1
		return binarySearch(array, targetVal, start,end) 
	else:
		start = mid+1
		return binarySearch(array, targetVal, start,end) 

if __name__ == '__main__':
	array = [5,12,17,23,38,44,77,87,90]
	targetVal = 90
	start = 0
	end = len(array)-1
	print str(binarySearch(array, targetVal, start, end))
	
