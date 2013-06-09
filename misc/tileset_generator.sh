rm ../assets/data/*
java -classpath ../Main/libs/gdx.jar:extensions/gdx-tools/gdx-tools.jar:extensions/gdx-tiled-preprocessor/gdx-tiled-preprocessor.jar com.badlogic.gdx.tiledmappacker.TiledMapPacker ../assets/tiles ../assets/data
