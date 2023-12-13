import sys

items_list = sys.argv[1:]
with open('pantry.txt', 'a') as file:
    for item in items_list:
        file.write(item + '\n')