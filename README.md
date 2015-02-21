# LOD Hackathon: rendiamo semantici i dati della PA piemontese

In questo repository vengono riportate le informazioni e i riferimenti al codice sorgente necessario per poter affrontare il Linked Open Data Hackathon organizzato dal [Centro Nexa su Internet & Società del Politecnico di Torino](nexa.polito.it) in occasione dell'edizione 2015 dell'[International Open Data Day](http://opendataday.org/).

Obiettivo principale dell'hackathon è quello di passare da dati a "tre stelle" pubblicati secondo formati aperti (ad esempio CSV) a dati a "quattro o cinque stelle", pubblicati secondo gli standard del W3C ([RDF](http://www.w3.org/RDF/) e [SPARQL](http://www.w3.org/TR/rdf-sparql-query/)) e interlinkati con altri dataset.  

L'hackathon è stato organizzato con il prezioso supporto di [Diego Camarda](https://github.com/dvcama) e di [Regesta.exe](http://www.regesta.com/info/) che hanno fornito il codice sorgente di base per la trasformazione di dati CSV in formato RDF e hanno messo a disposizione strumenti quali [Bygle Open Source](https://github.com/regestaexe/bygle-ldp) e [LodView](https://github.com/dvcama/LodView).

## Indice della documentazione

* [La roadmap della giornata](https://github.com/giuseppefutia/lod-hackathon-2015#la-roadmap-della-giornata)
* [Il challenge](https://github.com/giuseppefutia/lod-hackathon-2015#il-challenge)
* [Set up del sistema](https://github.com/giuseppefutia/lod-hackathon-2015#set-up-del-sistema)
* [Conversione dei dati da CSV a RDF](https://github.com/giuseppefutia/lod-hackathon-2015#conversione-dei-dati-da-csv-a-rdf)
* [Interlinking e allineamento semantico dei dati](https://github.com/giuseppefutia/lod-hackathon-2015#interlinking-e-allineamento-semantico-dei-dati)
* [Pubblicazione dei dati](https://github.com/giuseppefutia/lod-hackathon-2015#pubblicazione-dei-dati)

## La roadmap della giornata

* L'hackathon si svolgerà **dalle ore 12.00 alle ore 00.00 CET** di sabato 21 febbraio 2015
* Nella fase iniziale si svolgerà una discussione fra i partecipanti per definire i compiti di ciascuno per poter affrontare il challenge proposto
* **Dalle ore 15.30 alle ore 16.15** si svolgerà un intervento di **Federico Morando**, *Director of Research and Policy & Research Fellow del Centro Nexa*. Durante l'incontro ci si collegherà con l'evento organizzato a Roma in occasione dell'International Open Data Day 2015.
* **Dalle ore 21 e fino alla fine dell'hackathon** gli sforzi prodotti verranno concretizzati in un output che si tradurrà in codice open source pubblicato su GitHub, documentazione del processo e Linked Open Data a disposizione della comunità.

## Il challenge

**Il challenge dell'hackathon consiste nel trasformare e pubblicare i dati delle amministrazioni secondo lo standard "Linked Data" definito dal W3C**.

Di seguito sono riportati i punti contraddistinguono le diverse fasi del challenge dell'hackathon e i link al codice di supporto:

1. [Conversione dei dati da CSV a RDF](https://github.com/giuseppefutia/lod-hackathon-2015#conversione-dei-dati-da-csv-a-rdf)
2. [Interlinking e allineamento semantico dei dati](https://github.com/giuseppefutia/lod-hackathon-2015#interlinking-e-allineamento-semantico-dei-dati)
3. [Pubblicazione dei dati](https://github.com/giuseppefutia/lod-hackathon-2015#pubblicazione-dei-dati)

## Set up del sistema

Per poter utilizzare il codice di conversione dei dati è necessario installare alcuni pacchetti all'interno del proprio sistema e importare il [progetto Java](https://github.com/giuseppefutia/lod-hackathon-2015/tree/master/csv-to-rdf).

### Installazione e import del progetto

1. Installazione di Java 7 o superiore: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.
2. Download di Eclipse STS per il proprio sistema operativo: http://spring.io/tools/sts/all.
3. Download del file zip: [csv-to-rdf.zip](https://github.com/giuseppefutia/lod-hackathon-2015/)
4. Import... --> General --> Existing projects into workspace.
5. Navigare il file system fino alla cartella lod-hackathon-2015 e premere "ok".
6. Nella finestra di importazione verrà mostrato "csv-to-rdf" come progetto da importare.
7. Spuntare il progetto, selezionare "Copy project into workspace" tra le opzioni presenti e premere "Finish".

### Test del sistema

1. Aprire l'albero del progetto: src/main/java > org.dvcama.csvtordf.test e cliccare con il destro su DbMaker.java. Nel menu contestuale selezionare "run as" -> "java application".
2. Nell'alberatura del progetto cercare la cartella di primo livello "db", posizionarsi con un click (evidenziandola) e premere F5 per aggiornarne il contenuto; se questa contiene la cartella "testCsvToRdf" il sistema compila e funziona a dovere.
3. Cliccare con il tasto destro del mouse sul progetto "csv-to-rdf-test" e selezionare "run as" -> "Run on Server" e scegliere un server nella lista di quelli proposti.
4. Al termine dell'operazione dovrebbe automaticamente aprirsi una pagina in Eclipse dallo sfondo nero con su scritto "YOUR HOME PAGE!".

## Conversione dei dati da CSV a RDF

Il codice di esempio a vostra disposizione vi permetterà di trasformare dati CSV in RDF, serializzato in XML, JSON o N-Turtle.

Per ottenere questo obiettivo verrà utilizzato [Apache Jena](https://jena.apache.org/), framework scritto in JAVA per costruire *Semantic Web applications*. Jena ha delle funzionalità specifiche per poter creare un *triple store*, ovvero un modello/grafo al cui interno è possibile scrivere triple RDF.

### Step01.java - Download del file CSV

Il codice sorgente di esempio che avete importato in Eclipse racchiude i singoli step per poter passare da un CSV a un RDF. Nel dettaglio, all'interno del package org.dvcama.csvtordf.triplify, il codice sorgente incluso in [Step01.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step01.java), [Step02.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step02.java), [Step03.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step03.java), [Step04.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step04.java) vi consentirà progressivamente di ottenere un file RDF pubblicabile all'interno di un *triple store*.

[Step01.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step04.java) non fa nient'altro che scaricare e salvare su disco il [file CSV dei musei piemontesi](http://www.dati.piemonte.it/catalogodati/scarica.html?idallegato=458), a partire dal link segnalato all'interno del catalogo [dati.piemonte.it](http://www.dati.piemonte.it/catalogodati/dato/100439-.html). Il file sarà disponibile nella cartella hackathon-test/dataset.csv.

### Step02.java - Dal CSV alla lista di JSON

All'interno di [Step0.02.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step02.java) lavorerete direttamente sul CSV, utilizzando la libreria [CSVParser](https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVParser.html). Affinché  il parser funzioni correttamente, occorre conoscere e specificare l'*encoding* del file.

Nel caso specifico dei musei piemontesi, occorre implementare:

``` java

CSVParser parser = CSVParser.parse(inf, Charsets.toCharset("Windows-1252"), //
	CSVFormat.DEFAULT //
		.withSkipHeaderRecord() //
		.withHeader() //
		.withDelimiter(';') //
	);

```

I passaggi logici che vengono effettuati in questo step sono i seguenti:

* Prendo il CSV e scorro tutti i record.
* Per ciascuna *entry* del CSV creo un oggetto JSON che contiene delle coppie chiave-valore ottenute tramite l'elaborazione effettuata su ciascuna colonna del CSV. Ogni oggetto viene salvato all'interno di una lista.

``` javascript

{
    "address/id:streetAddress": "Piazza Luigi Rossi - Pessione di Chieri, 2",
    "address/id:faxNumber": "+39-0119419266",
    "rdfs:label": "Museo \"Martini\" di Storia dell'Enologia",
    "schema:image": "http://www.piemonteitalia.eu/images/tools/musei/torino/m_martini_enologia_chieri.jpg",
    "address/id:telephone": "+39-0119419217",
    "geo:lat": "44.96548843",
    "about": "http://localhost/id/musei/46",
    "categoria/id": "6",
    "place/myschema:codiceIstat": "001078",
    "myschema:update": "2014-07-29T15:22:03Z",
    "dc:description": "Ambientato nelle sale ricavate dalle antiche cantine della villa settecentesca che fu sede dei primi stabilimenti della Martini & Rossi a Pessione, presso Chieri, il museo presenta oggetti raccolti sotto un unico denominatore comune: il vino e la sua lunga storia, legata indissolubilmente a quella dell'uomo. In questa collezione, prima in Europa per la vastità dell'esposizione, sono presentati in ordine tematico, fra le pareti in mattoni a vista e le volte perfettamente conservate dei sotterranei, oggetti rituali e simbolici, anfore e vasi databili a partire dal secondo millennio avanti Cristo fino all'età Imperiale romana, grandi torchi in legno, carri per il trasporto di uve e di botti, cristalli e argenti preziosi, accanto a filtri e alambicchi di rame, prime testimonianze dello sviluppo industriale, che ha contribuito enormemente ad ampliare le conoscenze e le tecniche in campo enologico. Un'ultima sezione racconta la storia del vermouth piemontese, dalla sua invenzione alla sempre più diffusa commercializzazione.",
    "address/id:email": "martini.pessione@martini.com",
    "place/provincia_id:label": "Torino",
    "place/comune_id": "http://localhost/id/luogo/comune/001078",
    "place/comune_id:label": "Chieri",
    "schema:openingHours": "Orario: settembre-luglio :martedì-venerdì 14.00-17.00, sabato-domenica 9.00-12.00, 14.00-17.00.\nUltimo ingresso 16.30.\nChiuso nel mese di agosto.\nChiusure festività annuali: 1 Gennaio, Pasqua, Pasquetta, 25 Aprile, 15 Agosto, 2 Giugno, 1 Novembre, 8 Dicembre, 25 Dicembre, 26 Dicembre.\nTipo ingresso: Gratuito",
    "categoria/id/rdfs:label": "tematici",
    "address/id": "http://localhost/id/musei/46/address",
    "geo:long": "7.84122896",
    "address/id:postalCode": "10023",
    "place/provincia_id": "http://localhost/id/luogo/provincia/to"
}

```

All'interno della chiave "about", ad esempio, vado a salvare l'IRI che identificherà la risorsa, ad esempio "http://localhost/id/musei" + id. La scelta di inserire la parola "id" all'interno dell'IRI mi consente di avere un'espressione chiave che m'informa che da qui in avanti entro nel dominio RDF.

Per favorire la riconciliazione all'interno del *triple store* sarebbe l'ideale utilizzare lo stesso identificativo dell'*authority* che si occupa di gestire quel tipo di dato, in modo tale da favorire una processo di riconciliazione automatica. Viceversa si può utilizzare la proprietà [owl:sameAs](http://www.w3.org/TR/owl-ref/#sameAs-def).

* Il JSON che viene generato a questo step viene creato all'interno della cartella hackathon-test/dataset.json.
  
### Utilizzo di ontologie standard per la creazione dell'RDF

Per i musei, si è scelto di utilizzare l'ontologia [schema.org](http://schema.org/), in particolare le proprietà dell'entità [Museum](http://schema.org/Museum). Schema.org è un'ontologia leggera utilizzata di solito per arricchire le pagine Web con informazioni che i motori di ricerca a tendere saranno in grado di utilizzare. Un esempio di utilizzo nel dominio dei musei piemontesi è il seguente:

``` java

IRI.addProperty(m.createProperty("http://schema.org/", "openingHours"), prendiValore("schema:openingHours", record), "it");

```

Nel caso dei musei è stata sfruttata anche l'ontologia [Dublin Core](http://dublincore.org/) per fare in modo di utilizzare uno standard che viene spesso usato per il campo descrizione [dc:description](http://purl.org/dc/elements/1.1/description). Segue un esempio:


``` java

IRI.addProperty(DCTerms.description, prendiValore("dc:description", record), "it");

```

**CHALLENGE**: Provare ad utilizzare anche l'ontologia [Good Relations](http://www.heppnetz.de/projects/goodrelations/), in modo simile all'esempio presentato qui di seguito:

    <gr:openingHoursSpecification rdf:about="timetable/IT-FC0160_1">
          <rdfs:label>lun 09:30-12:30</rdfs:label>
          <gr:hasOpeningHoursDayOfWeek rdf:resource="http://purl.org/goodrelations/v1#Monday"/>
          <gr:opens>09:30</gr:opens>
          <gr:closes>12:30</gr:closes>
    </gr:openingHoursSpecification>
       
    <gr:openingHoursSpecification rdf:about="timetable/IT-FC0160_2">
          <rdfs:label>mar 09:30-12:30</rdfs:label>
          <gr:hasOpeningHoursDayOfWeek rdf:resource="http://purl.org/goodrelations/v1#Tuesday"/>
          <gr:opens>09:30</gr:opens>
          <gr:closes>12:30</gr:closes>
    </gr:openingHoursSpecification>

Nel caso dei musei, infatti, risulta molto complesso riuscire ad estrarre le informazioni. Provate ad eseguire la query SPARQL seguente sull'[endpoint](http://community-dati-piemonte-it.nexacenter.org/sparql) a vostra disposizione o cliccando il link in basso:

    PREFIX schema-org: <http://schema.org/>

    SELECT * WHERE {?museo a schema-org:Museum.
      OPTIONAL {?museo  schema-org:openingHours ?orari.}
    }

http://community-dati-piemonte-it.nexacenter.org/sparql?default-graph-uri=&query=PREFIX+schema-org%3A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0A%0D%0ASELECT+*+WHERE+%7B%3Fmuseo+a+schema-org%3AMuseum.%0D%0A+OPTIONAL+%7B%3Fmuseo++schema-org%3AopeningHours+%3Forari.%7D%0D%0A%7D+&should-sponge=&format=text%2Fhtml&timeout=0&debug=on 

Per poter esplorare ontologie per il proprio dominio, è possible utilizzare LOV (Linked Open Vocabularies). Nell'esempio dei musei può essere molto utile sfruttare questo riferimento: http://lov.okfn.org/dataset/lov/terms?q=Museum.

#### Ontologie utilizzabili per gli eventi
In questa sezione vengono riportati alcuni esempi per utilizzare ontologie standard nel dominio degli eventi.

    @base          <http://community-dati-piemonte-it.nexacenter.org/id/> .
    @prefix time:  <http://www.w3.org/2006/time#> .
    @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix dc:    <http://purl.org/dc/elements/1.1/> .
    @prefix dbpedia-owl: <http://dbpedia.org/ontology/> .
    @prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#> .
    @prefix foaf:  <http://xmlns.com/foaf/0.1/> .
    @prefix yago:  <http://dbpedia.org/class/yago/> .
    @prefix owl:   <http://www.w3.org/2002/07/owl#> .
    @prefix geonames: <http://www.geonames.org/ontology#> .
    @prefix org:   <http://www.w3.org/ns/org#> .
    @prefix meta:  <http://example.org/metadata#> .
    @prefix dcterms: <http://purl.org/dc/terms/> .
    @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix trasparenza:  <http://trasparenza.nexacenter.org/id/> .
    @prefix schema-org: <http://schema.org/> .
    @prefix dbpedia: <http://dbpedia.org/resource/> .
    @prefix dbpedia-it: <http://it.dbpedia.org/resource/> .
    @prefix dbpprop: <http://dbpedia.org/property/> .
    @prefix units: <http://dbpedia.org/units/> .
    @prefix bibo:  <http://purl.org/ontology/bibo/> .
    @prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
    @prefix vallesusa:   <http://vallesusa-tesori.it/id/> .

    http://www.vallesusa-tesori.it/en/
    col seguente tracciato record per gli eventi:

    <eventi/esempio>  a              schema-org:Event ;

    nomeIT:   rdfs:label    "Nome standard evento"@it .
    nomeEN:   rdfs:label    "Nome standard evento"@en .
    nomeFR:   rdfs:label    "Nome standard evento"@fr .
    descrizioneIT:  dcterms:description "Bla bla medio lungo."@it .
    descrizioneEN dcterms:description "Bla bla medio lungo."@en .
    descrizioneFR:  dcterms:description "Bla bla medio lungo."@fr .
    data_inizio:  schema-org:startDate  ISO 8601 date format .
    data_fine:  schema-org:endDate  ISO 8601 date format .
    latitudine: geo:lat     "number" .
    longitudine:  geo:long    "number" .
    POI_correlati:  community:hasRelatedPOI <POIs/esempio/> .
        community:hasRelatedPOI rdfs:subPropertyOf  rdfs:seeAlso .
    categorizzazione: vallesusa:haTematismo vallesusa:Tematismo .

    vallesusa:haTematismo rdfs:subPropertyOf  dc:subject .

    vallesusa:tematismi/territorio  a   vallesusa:Tematismo ;
            rdfs:seeAlso  dbpedia-it:Val_di_Susa ;
            rdfs:seeAlso  dbpedia:Susa_Valley ;
            foaf:isPrimaryTopicOf
        <http://www.vallesusa-tesori.it/tematismi/territorio/> .

    Elenco tematismi sito:
        <http://www.vallesusa-tesori.it/tematismi/enogastronomia/>
        <http://www.vallesusa-tesori.it/tematismi/archeologia/>
        <http://www.vallesusa-tesori.it/tematismi/ambiente/>
        <http://www.vallesusa-tesori.it/tematismi/cultura-popolare/>
        <http://www.vallesusa-tesori.it/tematismi/fortificazioni/>
        <http://www.vallesusa-tesori.it/tematismi/arte-sacra/>
        <http://www.vallesusa-tesori.it/tematismi/itinerari/>
        <http://www.vallesusa-tesori.it/tematismi/comunita/>

    tag:      dc:subject    "tag"@it
    immagini:   schema-org:photo  <http://esempio.org/image.jpg> .
          foaf:depiction    <http://esempio.org/image.jpg> .
          schema-org:image  <http://esempio.org/image.jpg> .
    video:      schema-org:video  #uso un po' improprio...
    audio:      schema-org:audio  #uso un po' improprio...
    documenti:    foaf:isPrimaryTopicOf <http://esempio.org/documento/>.

    -_-_-_-_-

    http://www.vallesusa-tesori.it/en/
    col seguente tracciato record per i Points Of Interest:

    <POIs/esempio>    a     schema-org:Place .
    NB, di norma si può essere più precisi:
    <POIs/esempio>    a     TouristAttraction .

    nomeIT:     rdfs:label    "Nome standard del POI"@it .
    nomeEN:     rdfs:label    "POI standard name"@en .
    nomeFR:     rdfs:label    "Nom standard du POI"@fr .
    descrizioneIT:    dcterms:description "Bla bla medio lungo."@it .
    descrizioneEN   dcterms:description "Bla bla medio lungo."@en .
    descrizioneFR:    dcterms:description "Bla bla medio lungo."@fr .
    InformazioniIT:   rdfs:comment    "Bla bla medio lungo."@it .
    InformazioniEN:   rdfs:comment    "Bla bla medio lungo."@en .
    InformazioniFR:   rdfs:comment    "Bla bla medio lungo."@fr .
    categorizzazione: vallesusa:haTematismo vallesusa:Tematismo .

    vallesusa:haTematismo rdfs:subPropertyOf  dc:subject .

    vallesusa:tematismi/territorio  a   vallesusa:Tematismo ;
            rdfs:seeAlso  dbpedia-it:Val_di_Susa ;
            rdfs:seeAlso  dbpedia:Susa_Valley ;
            foaf:isPrimaryTopicOf
        <http://www.vallesusa-tesori.it/tematismi/territorio/> .

    Elenco tematismi sito:
        <http://www.vallesusa-tesori.it/tematismi/enogastronomia/>
        <http://www.vallesusa-tesori.it/tematismi/archeologia/>
        <http://www.vallesusa-tesori.it/tematismi/ambiente/>
        <http://www.vallesusa-tesori.it/tematismi/cultura-popolare/>
        <http://www.vallesusa-tesori.it/tematismi/fortificazioni/>
        <http://www.vallesusa-tesori.it/tematismi/arte-sacra/>
        <http://www.vallesusa-tesori.it/tematismi/itinerari/>
        <http://www.vallesusa-tesori.it/tematismi/comunita/>

    tag:      dc:subject    "tag"@it

    Tre (quattro) righe seguenti dubbie:

    servizi correlati:    schema-org:potentialAction    (tabella???)
    servizi in loco:    schema-org:hasPOS        vallesusa:Organization1
    POI correlati:    community:hasRelatedPOI   <POIs/altro_esempio/> .
      community:hasRelatedPOI rdfs:subPropertyOf  rdfs:seeAlso.

    telefono: v. sotto, contactPoint
    email:    v. sotto, contactPoint
    web:    v. sotto, contactPoint

        schema-org:contactPoint <POIs/esempio/contacts>
    <POIs/esempio/contacts> a     schema.org:ContactPoint ;
          schema-org:telephone  "+39-xxx-yyy-zzz-wwww" ;
          email     "info@esempio.org" ;
          url     <http://esempio.org/contact> .

    orariIT:    schema-org:openingHours "Orario: ..."@it .
    orariEN:    schema-org:openingHours "Orario: ..."@enr .
    orariFR:    schema-org:openingHours "Orario: ..."@fr .
    comune:     schema-org:location <luogo/comune/00123> .

    CAP:      va dentro indirizzo:
          <POIs/esempio/address>  schema-org:postalCode number.

    indirizzo:    schema-org:address  <POIs/esempio/address> .

      <POIs/esempio/address>  addressCountry  "ITA" or "IT" ;
            addressLocality "Turin" ;
            addressRegion "TO" ;
            postalCode  "10100" ;
            streetAddress "Via Boggio, 65/A" .

    latitudine:   geo:lat     number .
    longitudine:    geo:long    number .
    rilevanza:    vallesusa:rilevanza number .
    immagini:   schema-org:photo  <http://esempio.org/image.jpg> .
          foaf:depiction    <http://esempio.org/image.jpg> .
          schema-org:image  <http://esempio.org/image.jpg> .
    video:      schema-org:video  #uso un po' improprio...
    audio:      schema-org:audio  #uso un po' improprio...
    documenti:    foaf:isPrimaryTopicOf <http://esempio.org/documento/>.

          owl:sameAs    <http://it.dbpedia.org/esempio>,
            dbpedia:example,
            <http://wikidata.org/entity/esempio>,
            <http://yago-knowledge.org/resource/esempio>,
            <http://sws.geonames.org/esempio> .


    -----------------------------

    Fonti di dati aggiuntivi possibili (in ordine indicativo di interesse):


    Muesi Torino
      Challenge: cross-checking e merge
      http://aperto.comune.torino.it/?q=node/144
        http://aperto.comune.torino.it/sites/default/files/musei.csv  
        NB: occhio che è separato da ";" e non ","

    Eventi Firenze
      http://opendata.comune.fi.it/cultura_turismo/dataset_0337.html
        http://wwwext.comune.fi.it/opendata/files/eventi.json

    Eventi Ravenna
      http://opendata.comune.ravenna.it/dataset/eventi
        http://www.comune.ra.it/eventi/feed.xml

    Eventi Rovereto
      http://dati.trentino.it/dataset/comune-di-rovereto-eventi
        http://www2.comune.rovereto.tn.it/servizionline/extra/json_sito/event/

    Eventi Emilia-Romagna
      http://www.dati.gov.it/catalog/dataset/regione-emilia-romagna_37
        help: http://dati.emilia-romagna.it/media/rdp/comune/licenze/IstruzioniERCultura.pdf
        chiamata di esempio: http://wwwservizitest.regione.emilia-romagna.it/cultura/opendata/default.aspx?data=20100101&pagenum=3&pagesize=100

    Eventi Matera
      http://www.dati.gov.it/catalog/dataset/regione-basilicata_652b16fd-dc0a-434d-abcc-fcd97a2613e0

    Eventi Palermo
      http://www.dati.gov.it/catalog/dataset/comune-di-palermo_309
        http://www.comune.palermo.it/xmls/VIS_DATASET_NEWS.xml

    Uffici turistici Torino
      http://aperto.comune.torino.it/?q=node/140

    Hic Sunt Leones
    Mibact
      http://www.beniculturali.it/mibac/export/MiBAC/sito-MiBAC/MenuPrincipale/Trasparenza/Open-Data/Sviluppatori/index.html
        v. anche mail di Chiara Veninata e Giogia Lodi

    Poco aggiornata/consistente:

    Eventi Fibreno
      NB: c'è già un RDF un po' autoreferenziale...
      http://www.leggiposta.comune.postafibreno.fr.it/opendata/
        http://www.leggiposta.comune.postafibreno.fr.it/tool-admin/cms/?controller=punti&action=selectByIdCategoria&id=14&all&view=open
        http://www.leggiposta.comune.postafibreno.fr.it/tool-admin/cms/?controller=punti&action=selectByIdCategoria&id=14&all&view=rdf
        http://www.leggiposta.comune.postafibreno.fr.it/tool-admin/cms/?controller=punti&action=selectByIdCategoria&id=14&all&view=xml

    Roba "meta" o più o meno trascurabile

    Eventi Rignano Flaminio
      http://opendatarignanoflaminio.it/dataset.html?id=eventi
        Meta-RDF: http://opendatarignanoflaminio.it/api/categoria/eventi/rdfxml

    ------------------------

    Valutare se le informazioni sui musei sono in linea con quanto specificato qui di seguito:

    Sulle localizzazioni, considerando che nel caso specifico trattiamo Organization, abbiamo per ogni org:Organization un puntamento a org:Site che serve a descrivere la localizzazione fisica del soggetto. Nell'org:Site c'è un org:siteAddress che punta a vcard:Address (classe) che contiene le informazioni geografiche. Un esempio pratico è il seguente:


    <org:Organization rdf:about="ex/1234">
        <org:hasSite rdf:resource="site/1234"/>
    </org:Organization>

    <org:Site rdf:about="site/1234">
      <rdfs:label>sede ...</rdfs:label>
      <org:siteAddress rdf:resource="address/1234"/>
    </org:Site>


    <vcard:Address rdf:about="address/1234">
       <rdfs:label>indirizzo ...</rdfs:label>
      <gn:parentADM1>...</gn:parentADM1>
      <gn:name>...</gn:name>
      ....
      <geo:lat>...</geo:lat>
      <geo:long>...</geo:long>
      <geo:long>...</geo:long>
      <gn:isLocatedIn rdf:resource="luogo1"/>
    </vcard:Address>

    <ex:luogo rdf:about="luogo1">
      <rdfs:label>
      <owl:sameAs rdf:resource="http://sws.geonames.org/..."/>
     </ex:luogo>

## Step03.java - Creazione del modello di Jena

A questo livello si utilizzano le API di Jena per la creazione del modello RDF. Definisco i prefissi che mi serviranno per la pubblicazione dell'RDF all'interno del *triple store*, sfruttando alcune costanti di Jena specifiche per definire i predicati di una tripla, come ad esempio "RDFS".

``` java
	
Model m = ModelFactory.createDefaultModel();
Map<String, String> prefixMap = new HashMap<String, String>();

prefixMap.put("rdfs", RDFS.getURI());
prefixMap.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
prefixMap.put("schema", "http://schema.org/");
prefixMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
prefixMap.put("gn", "http://www.geonames.org/ontology#");
prefixMap.put("rdf", RDF.getURI());
prefixMap.put("dcterms", DCTerms.getURI());

m.setNsPrefixes(prefixMap);

```

A questo punto le entry del JSON create in [Step02.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step02.java) andranno inserite nel modello creato con Jena. Per poter creare una tripla all'interno del modello, nel seguente *snippet* di codice vengono mostrate 3 modalità differenti. 

``` java

Resource IRI = m.createResource(prendiValore("about", record));

// usando le costanti di jena (es. RDF)
m.add(IRI, RDF.type, m.createResource("http://schema.org/Museum"));

// definendo una propria proprietà
m.add(IRI, m.createProperty("http://www.w3.org/2000/01/rdf-schema#", "label"), m.createLiteral(prendiValore("rdfs:label", record), "it"));

// aggiungendo alla risorsa i valori nella maniera più pratica
if (record.get("geo:lat") != null) {
	IRI.addProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "lat"), prendiValore("geo:lat", record));
	IRI.addProperty(m.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#", "long"), prendiValore("geo:long", record));
}

```

Per ciascuna entità identificata da un IRI è una buona prassi creare anche una proprietà di tipo rdfs:label, che sia espressa in linguaggio naturale. Un esempio viene presentato nello snippet di codice pubblicato qui di seguito:

``` java

// indirizzo e contatti
Resource IRIaddress = m.createResource(prendiValore("address/id", record));
IRIaddress.addProperty(RDF.type, m.createResource("http://schema.org/PostalAddress"));
IRIaddress.addProperty(RDFS.label, "indirizzo e contatti: " + prendiValore("rdfs:label", record), "it");

```

E' importante specificare che in questa fase di triplificazione si possono (e si devono) aggiungere nuovi valori in modo tale da rendere il file RDF più ricco e più interoperabile con altri dataset. Per arricchire le informazioni in merito al comune nel quale si trova il mio museo, posso specificare che il comune di appartenenza è anche un oggetto "#Feature" dell'ontologia di [GeoNames](http://www.geonames.org/ontology/documentation.html) (tale informazione non era presente nel file JSON generato nella fase precedente).

``` java

IRIcomune.addProperty(RDF.type, m.createResource("http://www.geonames.org/ontology#Feature"));

```

Avendo utilizzato GeoNames posso sfruttare la sua ontologia per definire il rapporto che esiste tra comuni e provincie in Italia.

``` java

IRIcomune.addProperty(m.createProperty("http://www.geonames.org/ontology#", "parentFeature"), IRIprovincia);
IRIcomune.addProperty(m.createProperty("http://www.geonames.org/ontology#", "parentADM1"), IRIprovincia);
    
```

L'RDF finale può essere serializzato a seconda delle proprie necessità:


``` java
		
OutputStream out = new FileOutputStream(new File("hackathon-test/dataset.xml"));
OutputStream outAbbr = new FileOutputStream(new File("hackathon-test/dataset.abbr.xml"));
OutputStream outNt = new FileOutputStream(new File("hackathon-test/dataset.nt"));
OutputStream outTurtle = new FileOutputStream(new File("hackathon-test/dataset.turtle"));
OutputStream outJson = new FileOutputStream(new File("hackathon-test/dataset.jsonld"));

RDFDataMgr.write(out, m, RDFFormat.RDFXML);
RDFDataMgr.write(outAbbr, m, RDFFormat.RDFXML_ABBREV);
RDFDataMgr.write(outNt, m, RDFFormat.NT);
RDFDataMgr.write(outTurtle, m, RDFFormat.TURTLE_PRETTY);
RDFDataMgr.write(outJson, m, RDFFormat.JSONLD);

```

### Step04.java - Generazione del modello Jena su file system

In [Step04.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step04.java) i dati salvati vengono caricati all'interno del modello, creando un'implementazione di grafo su file system. Il risultato ottenuto sarà disponibile nella cartella: hackathon-test/musei.

## Interlinking e allineamento semantico dei dati

### Step05.java - Interlinking

Il codice presente in [Step05.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step05.java) vi consentirà di creare un file di interlinking all'interno del quale ci sarà un insieme di triple in cui vengono definite le relazioni con dataset esterni. In questo specifico caso, il dataset dei musei è stato collegato con [DBpedia](http://spcdata.digitpa.gov.it/index.html), [DBpedia Italiana](http://it.dbpedia.org/) e [SPC Data](http://spcdata.digitpa.gov.it/index.html).

Per poter creare l'interlinking con altri dataset vengono effettuate delle query SPARQL su repository menzionati in precedenza, cercando di recuperare il maggior numero di informazioni corrette possibili. Ovviamente, la scelta dei repository di dati e delle query dipende strettamente dal dominio scelto.

#### Utilizzo di TellMeFirst per la classificazione semantica dei dati non strutturati

Per poter creare ulteriori occasioni di interlinking sono a vostra disposizione le [API di TellMeFirst](https://github.com/TellMeFirst/tellmefirst/tree/improve-doc/doc/api), tool open source per la classificazione semantica tramite Wikipedia/DBpedia e l'arricchimento di documenti testuali tramite Linked Data. Un esempio di implementazione Java di chiamata alle API è disponibile qui di seguito:

``` java

String charset = "UTF-8";
String requestURL = "http://tellmefirst.polito.it:2222/rest/classify";
String jsonResponse = "";
try {
    MultipartUtility multipart = new MultipartUtility(requestURL, charset);    
    multipart.addFormField("text", textToClassify);
    multipart.addFormField("numTopics", "7");
    multipart.addFormField("lang", "italian");
    List<String> response = multipart.finish();
    for (String line : response) {
        jsonResponse += line;
    }
} catch (IOException ex) {
    System.err.println(ex);
}
return jsonResponse;

```

Il file generato a questo livello si troverà in hackathon-test/interlinking.nt.

### Step06.java - Aggiunta del file di interlinking al grafo Jena

Il codice implementato in [Step06.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step06.java) è necessario per poter aggiungere al grafo generato allo [Step04](https://github.com/giuseppefutia/lod-hackathon-2015#step04java---generazione-del-modello-jena-su-file-system) le nuove triple che definiscono l'interlinking. 

Questo passaggio è necessario soltanto qualora si voglia testare l'[istanza in locale di LodView](https://github.com/giuseppefutia/lod-hackathon-2015#test-di-lodview-nella-propria-macchina-locale) che viene rilasciata all'interno del codice sorgente. In alternativa, è già possibile caricare su Virtuoso uno dei dataset RDF generati nello [Step03](https://github.com/giuseppefutia/lod-hackathon-2015#step03java---creazione-del-modello-di-jena) e il file di interlinking generato nello [Step05](https://github.com/giuseppefutia/lod-hackathon-2015#step05java---interlinking).

## Pubblicazione dei dati
Per testare l'RDF è possibile pubblicare [il grafo generato su file system attraverso Jena](https://github.com/giuseppefutia/lod-hackathon-2015#step04java---generazione-del-modello-jena-su-file-system) sull'istanza di [LodView](http://lodview.it/) rilasciata all'interno del pacchetto che avete importato.

### Test di LodView nella propria macchina locale

All'interno del file /csv-to-rdf/src/main/webapp/WEB-INF/conf.ttl occorre configurare il parametro conf:endpoint inserendo il path della
cartella in cui è presente il modello di Jena, ovvero hackathon-test/musei.

Successivamente dovete cliccare sul tasto destro dell'interno progetto --> scegliere "Run As" --> Scegliere "Run on Server" --> Selezionare il server di default --> Cliccare "Finish".

### Step07.java - Upload dei dati su Bygle
Avete a disposizione in anteprima un'istanza di [Bygle Open Source](http://www.regesta.com/2015/01/28/nasce-bygle/), interfaccia LDP dell'Enterprise Linked Data Platform Java2ee, sviluppata da Regesta.exe, capace di gestire completamente l'importazione e il processamento di dati RDF.

Il codice sorgente per gestire le risorse RDF su Bygle è disponibile all'interno di [Step07.java](https://github.com/giuseppefutia/lod-hackathon-2015/blob/master/csv-to-rdf/src/main/java/org/dvcama/csvtordf/triplify/Step07.java).
