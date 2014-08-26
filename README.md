OBJLoader
=========

A small and simple library for reading Wavefront *.OBJ files.
To actually get the result from the loader, you will have to implement a IOBJOutput class,
that takes the output of the loader and puts it into a usable form for the application.

Easy Usage:

    OBJLoader.loadModel(scanner, objectOutput);

Complex Usage:

    OBJLoader ol = new OBJLoader();
    ol.setInput( scanner );
    ol.setOutput( objectOutput );
    ol.setTessellate( true );
    ol.setNormalizeNormals( false );
    ol.setSubtractOneFromIndices( true );
    ol.setClampTextureCoordinates( false );
    ol.process();
