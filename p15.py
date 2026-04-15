import datetime

today = datetime.date.today()

for i in range(1,6):
    print(today + datetime.timedelta(days=i))