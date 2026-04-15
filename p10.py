from collections import ChainMap

d1 = {"a":10,"b":20}
d2 = {"b":30,"c":40}

cm = ChainMap(d1, d2)

print(cm["b"])