from collections import defaultdict
from math import pi, cos, sin
import json

def generateCoords(sides, radius):
    coords = []
    for i in range(sides):
        arg = ((2*pi*i)/sides)
        coords += [round((radius * cos(arg) + radius),2)] # x
        coords += [round((radius * sin(arg) - radius),2)] # y

    return ', '.join([*map(str,coords)])

if __name__ == "__main__":
    # print(generateCoords(9,15))
    pass

dic = {}
with open("boroughIds.json","w+") as file:
    file.seek(0)
    boroughIDs = "brentPolygon, bexleyPolygon, bromleyPolygon, camdenPolygon, cityPolygon, croydonPolygon, ealingPolygon, enfieldPolygon, greenwichPolygon, hackneyPolygon, hamletsPolygon, hammfullPolygon, haringeyPolygon, harrowPolygon, haveringPolygon, hillingdonPolygon, hounslowPolygon, islingtonPolygon, kensChelsPolygon, kingstonPolygon, lambethPolygon, lewishamPolygon, mertonPolygon, newhamPolygon, redbridgePolygon, richmondPolygon, southwarkPolygon, suttonPolygon, thamesPolygon, walthamPolygon, wandsworthPolygon, westminsterPolygon".split(', ')
    dic = {i:"" for i in boroughIDs}
    # boroughNames = "Brent, Bexley, Bromley, Camden, City, Croydon, Ealing, Enfield, Greenwhich, Hackney, Tower Hamlets, Hammersmith And Fulham, Haringey, Harrow, Havering, Hillingdon, Hounslow, Islington, Kensington And Chelsea, Kingston, Lambeth, Lewisham, Merton, Newham, Redbridge, Richmond Upon Thames, "
    json.dump(dic,file, indent=4)