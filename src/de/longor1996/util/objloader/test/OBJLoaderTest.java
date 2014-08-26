package de.longor1996.util.objloader.test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

import de.longor1996.util.objloader.ConsoleOBJOutput;
import de.longor1996.util.objloader.OBJLoader;
import de.longor1996.util.objloader.OBJLoader.OBJLoaderException;

public class OBJLoaderTest {
	
	@Test
	public void testModelLoader()
	{
		try {
			OBJLoader.loadModel(new Scanner(new File("ship.obj")), new ConsoleOBJOutput());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OBJLoaderException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testStringSplit()
	{
		System.out.println(">> " + toString(OBJLoader.split("345/0/LOL", '/', 0)));
		System.out.println(">> " + toString(OBJLoader.split("345//LOL", '/', 0)));
		System.out.println(">> " + toString(OBJLoader.split("345/0/", '/', 0)));
		System.out.println(">> " + toString(OBJLoader.split("/0/LOL", '/', 0)));
	}

	private String toString(String[] a)
	{
        if (a == null)
            return "null";
        
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";
        
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
        	String val = a[i];
        	
        	if(val != null)
        	{
        		b.append('"');
        		b.append(val);
        		b.append('"');
        	}
            else
            	b.append("null");
            
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
	}
}
