Servus,

bitte löscht nochmal ALLE Views bzw. materialized Views die Ihr habt.
Danach erzeugt Ihr alle erneut der Reihe nach mit den Statements in 'view_creation_statements.txt'
Damit sollten dann auch alle Queries (queries.txt) klappen.

Btw, Q3_1, Q3_4 bzw. Q7_1, Q7_4 werden noch falsche Daten für 2009 bzw. für die Differenz ausspucken.
Das liegt daran, dass wir die Zahl der Wahlberechtigten nicht anpassen (bzw. für 2009 gar nicht speichern).
Ich werde, sobald ich den CSV-Parser refactorn kann, die Tabelle Wahlkreis um ne Spalte 'wahljahr' erweitern.
Dann haben wir zwar für jeden Wahlkreis zwei Einträge, aber auch immer die passende Zahl Wahlberechtigter.
Danach muss ich natürlich alle Queries wieder anpassen, aber ich gebe Euch rechtzeitig Bescheid und pushe
die Änderungen dann natürlich auch :)