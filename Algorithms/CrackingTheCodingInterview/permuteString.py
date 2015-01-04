import numpy as np
def permuteString(string):
	if len(string) == 1:
		return string
	firstLetter = string[0]
	substring = string[1:]
	#print substring
	substringpermutations = permuteString(substring)
	listperm = []
	for item in substringpermutations:
		for i in range(len(item)+1):
			itemmod = item
			perm = itemmod[:i]+firstLetter+itemmod[i:]
			listperm.append(perm)
	return listperm

if __name__ == '__main__':
	permutations = permuteString('abc')
	for p in permutations:
		print p
