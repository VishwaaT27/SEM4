from collections import OrderedDict

s = "swiss"
od = OrderedDict()

for ch in s:
    od[ch] = od.get(ch, 0) + 1

for k, v in od.items():
    if v == 1:
        print(k)
        break