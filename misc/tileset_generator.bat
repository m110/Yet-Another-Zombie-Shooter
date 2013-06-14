cd "../assets/levels/"
rd /Q /S .
cd "../tiles"
set CLASSPATH=../../misc/extensions/gdx.jar;../../misc/extensions/gdx-tools/gdx-tools.jar;../../misc/extensions/gdx-tiled-preprocessor/gdx-tiled-preprocessor.jar



for /D %%c in (.\*) do ( 
    
echo %%c

java com.badlogic.gdx.tiledmappacker.TiledMapPacker %%c ..\levels\%%c

copy %%c\level.properties ..\levels\%%c\level.properties

)

cd ../../misc