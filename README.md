OBJLoader
=========

A small and simple library for reading Wavefront *.OBJ files.
To actually get the result from the loader, you will have to implement a IOBJOutput class,
that takes the output of the loader and puts it into a usable form for the application.

The simple way to load a model:

    OBJLoader.loadModel(scanner, objectOutput);

The (slighty) more complicated way to load a model:

    OBJLoader ol = new OBJLoader();
    ol.setInput( scanner );
    ol.setOutput( objectOutput );
    ol.setTessellate( true );
    ol.setNormalizeNormals( false );
    ol.setSubtractOneFromIndices( true );
    ol.setClampTextureCoordinates( false );
    ol.process();

It is possible to run the OBJLoader in a multi-threaded environment by making use of the 'OBJLoader.run()' method.