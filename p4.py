from collections import defaultdict

words = ["python","java","go","c","ruby","php"]
d = defaultdict(list)

for w in words:
    d[len(w)].append(w)

print(dict(d))