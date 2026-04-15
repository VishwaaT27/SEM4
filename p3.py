from collections import Counter

list1 = [1,2,3,4,5]
list2 = [1,2,2,3,4,4,5]

c1 = Counter(list1)
c2 = Counter(list2)

print(c2 - c1)