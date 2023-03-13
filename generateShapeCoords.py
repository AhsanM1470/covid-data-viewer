from math import pi, cos, sin

def generateCoords(sides, radius):
    coords = []
    for i in range(sides):
        arg = ((2*pi*i)/sides)
        coords += [round((radius * cos(arg) + radius),2)] # x
        coords += [round((radius * sin(arg) - radius),2)] # y

    return ', '.join([*map(str,coords)])

if __name__ == "__main__":
    print(generateCoords(9,15))