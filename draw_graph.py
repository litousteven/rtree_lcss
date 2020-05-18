import matplotlib.pyplot as plt
import json

with open("rank.json","r", encoding="utf-8")as f:
    rank_li = json.load(f)

plt.hist(x=rank_li)
plt.show()