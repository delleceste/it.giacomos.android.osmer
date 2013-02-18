import os;

def create_xml_files():

  files = os.listdir()

  for f in files:
    if f.startswith("weather") and f.endswith(".png") and f.count('_night') == 0:
      xmlfile = f.replace(".png", "");
      xmlfile = xmlfile + "_state.xml"
      
      f = open(xmlfile, "w")
      
      drawable = xmlfile.replace("_state.xml", "")
      
      nightdrawable = drawable
      
      if drawable.endswith("_1"):
        nightdrawable = nightdrawable.replace("_1", "_night_1")
      elif drawable.endswith("_2"):
        nightdrawable = nightdrawable.replace("_2", "_night_2")
      else:
        nightdrawable  = nightdrawable + "_night"

      xmltext = """<?xml version=\"1.0\" encoding=\"utf-8\"?>
      <selector xmlns:android=\"http://schemas.android.com/apk/res/android\">
      <item android:drawable=\"@drawable/""" + drawable + """\"
      android:state_checked=\"true\" />
      <item android:drawable=\"@drawable/""" + nightdrawable + """\" />
      </selector>"""
      
      print("Writing ", xmlfile, "...");
      
      f.write(xmltext)
      
      f.close()

    
    