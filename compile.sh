rm -rf bin/*
javac -d bin src/*/*.java
echo Compiled `find bin -type f | wc -l` files.
