from collections import namedtuple

Employee = namedtuple("Employee", ["name", "salary"])

emps = [
    Employee("A",50000),
    Employee("B",70000),
    Employee("C",60000)
]

print(max(emps, key=lambda x: x.salary))