import json


def tran_coord(coord_li):
    lat = (float(coord_li[0])+90)
    lon = (float(coord_li[1])+180)

    return [lat,lon]



path = "%s_location.json" %"mid"
new_path = "%s_location.json"%"new"
with open(path, "r", encoding="utf-8")as f:
    data_dic = json.load(f)

data_li = []
for key, item in data_dic.items():
    if item:
        data_li.append((key, item))


new_data_li = sorted(data_li, key=lambda x: float(x[1][0][1])+float(x[1][0][0]))

new_data_dic = {}
for key, item in new_data_li:
    # item = data_dic[key]
    if not item:
        continue
    cordinate = tran_coord(item[0])
    view = item[1]
    type = item[2]

    southwest = tran_coord(view[0])
    northeast = tran_coord(view[1])
    s, w = southwest
    n, e = northeast
    
    # if e-w > 180:
    #    e, w = w, e

    if not e > w:
        print(key, w, e)
        if 360 - w > e:
            e = 360
        else:
            w = 0
        southwest = s, w
        northeast = n, e
    
    
    new_item = {
        "coord":cordinate,
        "southwest":southwest,
        "northeast":northeast,
        "loc_type":type
    }
    new_data_dic[key] = new_item

with open(new_path, "w", encoding="utf-8")as f:
    json.dump(new_data_dic, f)


# northeast = (str(northeast_dic['lat']), str(northeast_dic['lng']))
# southwest = (str(southwest_dic['lat']), str(southwest_dic['lng']))
# view = (southwest, northeast)
# cordinate = (str(cord_dic['lat']), str(cord_dic['lng']))