Kígyók és Létrák követelmény specifikáció
=========================================

Bevezető
--------

Ez a dokumentum a Szoftverarchitektúrák tárgyhoz kötődő házifeladat koncepcióját és követelmény specifikációját tartalmazza, azaz azt, hogy az elkészülő szoftver mit fog tudni, és milyen technológia felhasználásával készül majd el a szoftver.

### Feladatkiírás

A hallgató feladata egy online társasjáték alkalmazás elkészítése (webes felületen). A játék a Kígyók és Létrák játékhoz hasonló játékokat kezelje! Legyen lehetőség véletlenszerű pályagenerálásra. A pálya tartalmazhasson létrákat (előrejutás), ill. kígyókat (visszacsúszás). A pálya legyen könnyen követhető formájú, legyenek átláthatóak a létrák és a kígyók is! A játék legyen online játszható, többjátékos üzemmódban!

### Koncepció
A játékot böngészőn keresztül lehet elérni. A weboldalt elérve egy lobbiban találja magát a játékos. 

#### Lobbi

A lobbiban a játékos válogathat a szobák között, vagy új szobát hozhatnak létre. A szobába belépő játékosok, beleértve a szoba gazdáját, jelezhetik, hogy készen állnak-e a játékra, aminek hatására a játék elindul. 

#### Játékmenet

A pálya egy nxn-es számozott grid. Minden játékost egy token reprezentál, amely kezdéskor a bal alsó mezőre kerül (1-es sorszám). A játékos célja, hogy feljusson a bal felső mezőre. Az út boustrophedon (http://en.wikipedia.org/wiki/Boustrophedon) alakban megy fel, azaz lentről felfele felváltva balról jobbra, aztán jobbról balra kell továbbhaladni. Amikor egy játékos sorra kerül, egy a játékos által kezdeményezett véletlen kockadobás (1-6-ig) határozza meg, mennyit kell lépnie.

A pályán kígyók és létrák találhatóak. Minden létra és kígyó két mezőt köt össze. A pályán a kígyók és a létrák véletlen generálódnak. Hogyha létra aljához (kisebb sorszámú vége a létrának) lépett a játékos, akkor annak a tetejére mászik. Ha kígyó fejéhez (nagyobb sorszámú vége a kígyónak), akkor azon lecsúszik a kígyó farkához. 

Kockadobásnál, ha a játékos 6-ost dobott, akkor lépése után újra jön. Három 6-os után viszont a játékos visszakerül az 1-es mezőre, és nem léphet, amíg nem dob még egy 6-ost.

A játékosok a dobás előtt dönthetnek úgy, hogy használják a különleges képességüket amivel “földrengést” kelthetnek. Ebben az esetben minden játékos annak a sornak, amelyben van a legjobboldali cellájába kerül. 

Az a játékos nyer, aki hamarabb elér a bal felső mezőig. Ha egy játékos beér a célba, ő nem jön többet, ezért kiléphet, a többiek pedig folytathatják a játékot.

Funkcionális követelmények
--------------------------

### Lobbi
* Egy szobában maximum 4 játékos lehet
* A szoba gazdája hozzáadhat a játékhoz bizonyos számú robotot
* A szoba gazdája megszüntetheti a játékot, mindaddig amíg az el nem indult
* A lobbiban látható az összes játék, amelyben legalább egy aktív játékos van
* Egy játék automatikusan megszűnik, ha nem indul el a létrehozását követő 1 órán belül

### Pálya
* A pálya mérete a szoba indításakor beállítható, lehet 8x8, 10x10, 12x12
* A pályák minden alkalommal véletlenszerűen generálódjanak, de csak olyan pálya generálódhasson amit minden mezőből be lehet fejezni. Tehát például nincsen benne végtelen kígyó-létre lánc

### Játékmenet
* A szoba indításához szükséges, hogy minden nem robot játékos jelezze, hogy készen áll a játékra
* A szoba indításához szükséges, hogy legalább két játékos legyen
* A játékosnak saját magának kell kezdeményeznie a kockadobást, de ha ezt egy limit időn belül nem teszi meg, a kocka automatikusan dobódik
* A játékosnak saját magának kell kattintani arra a mezőre ahova lépnie kell, de ha ezt egy limit időn belül nem teszi meg automatikusan lép
* Egy játék során egy játékosnak legfeljebb kétszer legyen lehetősége a földrengést használni
* Földrengés esetén minden játékos a pálya legjobboldalibb mezőjére kerül abban a sorban ahol állt. Ha itt létra vagy kígyó * megfelelő vége található akkor ennek megfelelően tovább mozog a pályán.
* Ha a játék befejeződett egy játékos számára, mert például nyert, akkor ki kell tudnia lépni a játékból és más játékhoz csatlakoznia

### Egyéb követelmények
* Mivel a játék célközönsége elsősorban a 6-12 éves korosztály a következő követelményeket fogalmazzuk meg:
* A játék legyen könnyen tanulható és érthető
* A játékmenet legyen pörgős, ne kelljen sokat várakozni felhasználói interakciók között



Technológiai terv
-----------------
A webalkalmazást kliens szerver minta szerint fogjuk megvalósítani, a kliensek közötti kommunikáció a szerveren keresztül történik. 

![A kliens és a szerver oldal rétegei](https://raw.githubusercontent.com/wther/ladder/master/docs/multitier-application.png)

### Kliens oldal

Kliens oldalon HTML5 alapú technológiákat fogunk használni a lobbi és a játéktábla megjelenítéséhez. Ehhez többek között a  egy canvas könyvtárat, a KineticJS-t (http://kineticjs.com/) használjuk, amely nagyteljesítményű animációkat, réteg- és eseménykezelést tesz lehetővé.

### Szerver oldal

A szerver oldalt a Java alapú Spring Framework keretrendszerre épülő alkalmazás fogja megvalósítani. A szervert felépítésében három rétegre bontjuk. 
* A web service réteg feladata a kliensekkel történő kommunikáció biztosítása, ezt REST technológiával valósítjuk majd meg. 
* Az adathozzáférési réteget a Spring Data és JPA technológiák segítségével valósítjuk majd meg. Az biztosított ORM interfész el fogja fedni a mögöttes adatbázist, ezzel növelve a szerver hordozhatóságát környezetek között.
* Az adathozzáférési és web service réteget az üzleti logika réteg köti össze, ebben a rétegben implementáljuk majd játék és az alkalmazás szabályait és működését.

