# Java_skill_dev

## compile command

```bash
javac -d out $(find src -name "*.java")
```

-d --> means create directory for 
out --> directory name
$(find src -name "*.java") --> find all java files in src directory and compile them
-name --> find files with specific name

## run command

```bash
java -cp out com.campus.app.Main
```

-cp --> means classpath
-out --> directory name
-com.campus.app.Main --> package name + class name

why we com.campus.app,model,service?
java follows reverse domain name + project name + folder name to create packages

to avoid naming conflicts

java maintain this architecture pattern for code organization

