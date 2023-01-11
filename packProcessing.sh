mkdir -p processingLibrary/colorgen/library/
cp -R lib/build/libs/colorgen.jar processingLibrary/colorgen/library/
cp -R docs/api processingLibrary/colorgen/reference
cp -R lib/src/main/java/colorgen processingLibrary/colorgen/src
zip forProcessing.zip processingLibrary