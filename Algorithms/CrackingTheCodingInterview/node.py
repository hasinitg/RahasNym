class Node:

	def __init__(self, key, value, next, prev):
		self.key = key
		self.__value = value
		self.next = next
		self.prev = prev

	def getVal(self):
		return self.__value

	def setVal(self, val):
		self.__value = val
		



