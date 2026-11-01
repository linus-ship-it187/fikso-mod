Random Jumpscare Mod (Fabric, Minecraft 26.3)

Voraussetzungen: Java 25, Gradle 9.6.0 (oder IntelliJ 2025.3+), Fabric Loader 0.19.5, Fabric API 0.162.0+26.3

Audios einbauen:
1. Audios als .ogg (Vorbis) nach src/main/resources/assets/jumpscare/sounds/ legen,
   z.B. audio1.ogg, audio2.ogg
2. In src/main/resources/assets/jumpscare/sounds.json jeden Namen ohne .ogg eintragen:
   "sounds": ["jumpscare:audio1", "jumpscare:audio2"]
   (es wird zufaellig eins davon gespielt)

Bauen:
  gradle wrapper --gradle-version 9.6.0    (einmalig, oder Projekt in IntelliJ oeffnen)
  ./gradlew build
Die fertige Mod liegt danach in build/libs/jumpscare-mod-1.0.0.jar

Testen: im Spiel /jumpscare eingeben (loest sofort einen Jumpscare aus).

Einstellungen (Zeitabstand, Dauer, Lautstaerke) oben in
src/main/java/de/jumpscare/JumpscareMod.java

.jar ohne Installation bauen lassen (GitHub):
1. Ordner als neues Repository auf GitHub hochladen
2. Tab "Actions" -> Workflow "build" laeuft automatisch
3. Fertige .jar unter "Artifacts" -> jumpscare-mod herunterladen

Oder lokal unter Windows: build.bat doppelklicken.
