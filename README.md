# Engenharia de Linguagens de Software (ELS) - Year 5, Semester 1 (Y5S1)

This repository contains all the exercises and assessments of the UC Engenharia de Linguagens de Software, taught by [João Bispo](https://sigarra.up.pt/feup/pt/func_geral.formview?p_codigo=519965) at [Master in Informatics and Computing Engineering](https://sigarra.up.pt/feup/pt/cur_geral.cur_view?pv_curso_id=22862) [MEIC] at the [Faculty of Engineering of the University of Porto](https://sigarra.up.pt/feup/pt/web_page.Inicial) [FEUP]. <br> <br>

<h2 align = "center" >Final Grade: 19/20</h2>
<p align = "center" >
  <img 
       title = "FEUP logo"
       src = "Images//FEUP_Logo.png" 
       alt = "FEUP Logo"  
       />
</p>

## Here are several documents, namely:

### Notes

Notes that I take during theoretical lectures in Markdown. <br>

### PreQL - Project (Grade: 19/20)

The `PreQL` (Pre-SQL) language is based on a subset of SQL functionalities used to import, manipulate, and export tabular data.

```sql
CREATE TABLE "Bob Table";

IMPORT DATA FROM FILE "resources/assignment_2/input/vitis-report.xml" {
    SELECT TABLE "/AreaEstimates/Resources";
};

IMPORT DATA FROM FOLDERS "resources/assignment_3/input/" {
    WITH EXTENSIONS {
        EXTENSION YAML {
            SELECT TABLE "/total/results/dynamic";
        };
        EXTENSION XML {
            SELECT TABLE "/total/results/static";
        };
        EXTENSION JSON {
            FILTER BY ("/functions/time%", MAX, ["name #1", "time% #1", "name #2", "time% #2", "name #3", "time% #3"], 3);
            ADD COLUMN "Folder" AS "FOLDERNAME";
        };
    };
};

ADD SUFFIX "(Static)" TO "nodes";
ADD SUFFIX "(Static)" TO "functions";

ADD GLOBAL COLUMN "Folder" AS "/input/" AT FIRST;

EXPORT TO "resources/assignment_3/output/bob.html";
```

#### Members

- Eduardo Luís Tronjo Ramos (up201906732@up.pt);
- Fábio Araújo de Sá (up202007658@up.pt);
- Pedro Pereira Ferreira (up202004986@up.pt);

**@ Fábio Araújo de Sá** <br>
**2024/2025**
