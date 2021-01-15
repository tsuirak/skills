import os
import shutil

imagelist = []
with open('imagelist.txt') as f:
    for line in f.readlines():
        imagelist = line.split(',')
        break

directories = [x[0] for x in os.walk('.')]

d_list = []

for d in directories:
    if d[2:] not in imagelist:
        print('d: ', d)
        d_list.append(d)
        os.system('rm -rf ' + d)

