from node import Node

class LinkedList:
	
	def __init__(self, head):
		self.head = head

	def addFirst(self, n):
		n.next = self.head
		self.head = n
		
	def getLength(self):
		print 'get length called.'
		tmp = self.head
		print tmp
		i = 0
		while tmp != None:
			print 'while loop entered.'
			tmp = tmp.next
			i +=1
		print 'while loop exited.'
		return i
			
