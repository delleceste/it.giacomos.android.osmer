import json
from lxml import etree

f = open('json_webcam.json', 'r')
jstr = f.read()
f.close()

_id = 0

# list of dicts
dec = json.loads(jstr)

# root element
markers = etree.Element('markers')

for d in dec:
    _nome = d['nome']
    _wid = str(_id)
    _id = _id + 1
    _lat = d['lat']
    _lon = d['lon']
    _link = d['url']
    _testo = d['descrizione'];
    _category = 'meteo.fvg'
    
    marker = etree.Element('marker', nome=_nome, webcam_id=_wid, lat=_lat, lon=_lon, link=_link, testo=_testo, category=_category)
    
    markers.append(marker)
    
out = etree.tostring(markers)

fout = open("xml_webcams_out.xml", "w")
fout.write(out)
fout.close()
    
