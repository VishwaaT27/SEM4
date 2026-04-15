from collections import deque

s = "level"
d = deque(s)

flag = True
while len(d) > 1:
    if d.popleft() != d.pop():
        flag = False
        break

print("Palindrome" if flag else "Not Palindrome")