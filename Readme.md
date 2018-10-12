Application reads JSON.
It creates 3 folders :
"by_id" ( creates N lite jsons from given Json)
"by_subject" ( creates N jsons grouping by gibdd_code tag )
"all" ( creates 1 lite json from Given)

RUN with :
``` bash 
    java -jar gisorgsJsonParser-1.0-SNAPSHOT-jar-with-dependencies.jar 
    C:\IdeaProjects\gisorgsJsonParser\gisorgs.json 
    C:\IdeaProjects\gisorgsJsonParser\target\ 
```
or
``` bash 
    java -jar gisorgsJsonParser-1.0-SNAPSHOT-jar-with-dependencies.jar 
    C:\IdeaProjects\gisorgsJsonParser\gisorgs.json 
    C:\IdeaProjects\gisorgsJsonParser\target\ 
    > C:\IdeaProjects\gisorgsJsonParser\target\logs.txt
```