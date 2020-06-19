import cv2
import os
from os import listdir
from os.path import isfile, join
import numpy as np
import skimage.measure as sime
import skimage.color as sic
import pandas as pd
import matplotlib.pyplot as plt

def poprawne_rozszerzenie(plik):
    rozszerzenia = [".png", ".jpg", ".jpeg"]
    nazwa, rozszerzenie = os.path.splitext(plik)
    if rozszerzenie in rozszerzenia:
        return True
    return False

def pobierz_info(obrazki):
    rozszerzenia = [".png", ".jpg", ".jpeg"]
    info = {
        "nazwy": [],
        "rozszerzenia": []
    }
    for obrazek in obrazki:
        nazwa, rozszerzenie = os.path.splitext(obrazek)
        info["nazwy"].append(nazwa)
        info["rozszerzenia"].append(rozszerzenie)
    return info

def znajdz_pliki(sciezka):
    obrazki = [plik for plik in listdir(sciezka) if isfile(join(sciezka, plik)) and poprawne_rozszerzenie(plik)]
    return obrazki

def pobierz_obrazki(sciezka, nazwy):
    obrazki = []
    for nazwa in nazwy:
        obrazki.append(cv2.imread(sciezka + "/" + nazwa))
    return obrazki
'''
- TODO
def etykietuj(obrazki):
    etykietowane = []
    for obraz in obrazki:
        etykiety = sime.label(obraz)
        etykietowany = sic.label2rgb(etykiety, image=obraz, bg_label=0)
        etykietowane.append(etykietowany)
    return etykietowane
'''
def zapisz_obrazki(obrazki, dane_plikow):
    for i in range(0, len(obrazki)):
        cv2.imwrite("{}.{}".format(dane_plikow["nazwy"][i], dane_plikow["rozszerzenia"][i]), obrazki[i])

def filtruj(obrazki):
    filtrowane = []
    #print(None in obrazki)
    for obraz in obrazki:
        if obraz is not None:
            for i in range(0, len(obraz)):
                for j in range(0, len(obraz[i])):
                    if nie_zielony_pixel(obraz, i, j) or not odpowiedni_odcien(obraz, i, j):
                        czysc_pixel(obraz, i, j)
    return obrazki

def nie_zielony_pixel(obraz, i, j):
    return not (0 < obraz[i][j][0] and obraz[i][j][0] < 192) and not (39 < obraz[i][j][2] and obraz[i][j][2] < 47)

def odpowiedni_odcien(obraz, i, j):
    return obraz[i][j][0] < 230 and 170 < obraz[i][j][1]

def czysc_pixel(obraz, i, j):
    obraz[i][j][0] = 0
    obraz[i][j][1] = 0
    obraz[i][j][2] = 0
    
def wizualizuj_obrazek(obrazek, daneObrazka): #pojedynczy obraz, pojedynczy wiersz z tabeli "daneObrazki"   
    plt.imshow(obrazek)
    if(daneObrazka[1]!= None):      
        minr, minc, maxr, maxc = daneObrazka[1]
        bx = (minc, maxc, maxc, minc, minc)
        by = (minr, minr, maxr, maxr, minr)       
        plt.plot(bx, by, '-r', linewidth=2) 
        
def wizualizuj_wszystkie(obrazki, daneObrazki): #zbior wszystkich zdjec, tablica danych wszystkich obrazow 
    plt.figure(figsize=(15,15), dpi = 80)
    for i in range(len(obrazki)):
        plt.subplot(len(obrazki),1,i+1)  
        wizualizuj_obrazek(obrazki[i], daneObrazki.loc[i,:])
        
#Przyklad uzycia:    
obraz1 = cv2.imread('ball5.jpg',1)[...,::-1]    
obraz2 = cv2.imread('ball100.jpg',1)[...,::-1] 
obraz3 = cv2.imread('empty6.jpg',1)[...,::-1] 
obrazki = [obraz1,obraz2,obraz3]  
rogi = [(268, 72, 293, 110),(100, 100, 200, 200),None]
data = {'nazwaObrazka':  ['ball5','ball100','empty6'],'rogi': [rogi[0], rogi[1],rogi[2]]}
daneObrazki = pd.DataFrame (data, columns = ['nazwaObrazka','rogi'])

wizualizuj_wszystkie(obrazki, daneObrazki)