from collections import deque

nums = [1,2,3,4,5,6]
k = 3

d = deque()
window_sum = 0

for i in range(len(nums)):
    d.append(nums[i])
    window_sum += nums[i]

    if len(d) > k:
        window_sum -= d.popleft()

    if len(d) == k:
        print(window_sum, end=" ")