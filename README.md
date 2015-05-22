Elastika
==============================


Command line utility that extracts the metadata and plain text content of files supported by Apache Tika and send them to the [Elastic](https://www.elastic.co/) server.

Relases
-------------

[v0.9](https://github.com/irontec/elastika/releases/tag/v0.9)

Usage
-------------
Once downloaded, place the `tika-app.jar` file that it´s placed inside the `libs/` folder on the same folder that contains your `elastika.jar`. Now, you're ready to use Elastika.

Note: this document assumes that the final user does have [Elastic](https://www.elastic.co/) installed and running at least on localhost or in some host that you can specify with the following options.

Options:

    usage: elastika
     -i,--indice <arg>   (Required) Elastic indice name.
     -t,--type <arg>     (Required) Elastic indice type name.
     -f,--file <arg>     (Required) The document to be parsed and sent to
                         Elastic.
     
     -h,--host <arg>     (Optional) Elastic REST Endpoint hostname. Default
                         http://localhost.
     -p,--port <arg>     (Optional) Elastic REST Endpoint port. Default 9200.
     
     -?,--help           Print this usage message
     -v,--version        Display version information
     


Usage sample:

    axier$ java -jar elastika.jar -i myIndice -t myType --file my_fancy_document.pdf

Outputs:

    # Extracting JSON Metadata from the file
    Executing: java -jar tika-app.jar -j my_fancy_document.pdf

	# Extracting the plain text content from the file
	Executing: java -jar tika-app.jar -T my_fancy_document.pdf
	
	# Result of the POST to Elastic
	{"_type":"data","_version":1,"_id":"AU12rlvKHYuWDiEyeqrY","created":true,"_index":"ekt"}


Building
-------------

On the first place the code into your java project [Eclipse](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr2). Now, for generating the jar file just follow this simple steps:

- Right click on your project and click on `Export`
- Select `Java > JAR File` and click `Next`
- Enter the path of the folder where you want to leave the jar file on the `Select the export destination` section and click `Next` and `Next` again.
- Now, on the JAR Manifest Specification part, on the `Select the class of the application entry point` select `Browse` and then select `Elastika`
- Click `Finish` and you're done

Libraries
-------------

 - [Apache Commons Cli](https://commons.apache.org/proper/commons-cli/)
 - [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
 - [Apache Tika 1.8](https://tika.apache.org/)


License
-------------
[EUPL v1.1](https://github.com/irontec/elastika/blob/master/LICENSE.txt)

> Copyright 2015 Irontec SL
> 
> Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European
> Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
> except in compliance with the Licence.
> 
> You may obtain a copy of the Licence at:
> http://ec.europa.eu/idabc/eupl.html
> 
> Unless required by applicable law or agreed to in writing, software distributed under 
> the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF 
> ANY KIND, either express or implied. See the Licence for the specific language 
> governing permissions and limitations under the Licence.
