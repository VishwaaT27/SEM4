import datetime

t1 = datetime.datetime(2026,3,10,10,0,0)
t2 = datetime.datetime(2026,3,10,12,30,0)

print(int((t2 - t1).total_seconds()))