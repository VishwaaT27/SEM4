from collections import Counter

nums = [5,3,5,2,3,1,4,1,2]
c = Counter(nums)

for k, v in c.items():
    if v == 1:
        print(k)