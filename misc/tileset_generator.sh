rm -r ../assets/levels/*

export CLASSPATH=$CLASSPATH:extensions/gdx.jar:extensions/gdx-tools/gdx-tools.jar:extensions/gdx-tiled-preprocessor/gdx-tiled-preprocessor.jar

for i in $(ls ../assets/tiles); do
    echo "Generating map $i"
    java com.badlogic.gdx.tiledmappacker.TiledMapPacker "../assets/tiles/$i" "../assets/levels/$i"
    cp "../assets/tiles/$i/level.properties" "../assets/levels/$i/level.properties"
done
