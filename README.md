# script-engine-test

In order to test concurrency:

* ab -n 5000 -c 4 http://localhost:8080/mustache/                   (works)
* ab -n 5000 -c 4 http://localhost:8080/handlebars/                (broken)
* ab -n 5000 -c 4 http://localhost:8080/handlebars/newglobal/      (broken)
