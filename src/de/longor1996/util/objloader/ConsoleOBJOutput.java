package de.longor1996.util.objloader;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple class that prints all the data extracted from the input to the console.
 * DO NOT USE THIS CLASS IN PRODUCTION-lEVEL CODE!
 * @author Longor1996
 **/
public class ConsoleOBJOutput implements IOBJOutput {

	@Override
	public void outputVertex(float x, float y, float z) {
		System.out.println("vertex " + x + " " + y + " " + z);
	}

	@Override
	public void outputNormal(float x, float y, float z) {
		System.out.println("normal " + x + " " + y + " " + z);
	}

	@Override
	public void outputTextureCoodinate(float u, float v, float w) {
		System.out.println("texcoord " + u + " " + v + " " + w);
	}

	@Override
	public void outputFace(int pointCount, ArrayList<int[]> tempPoints) {
		
		System.out.print("face [");
		for(int i = 0; i < pointCount; i++)
		{
			System.out.print(Arrays.toString(tempPoints.get(i)));
		}
		System.out.println("]");
		
	}

	@Override
	public void outputObjectGroup(String objectGroupName) {
		System.out.println("polygroupgroup " + objectGroupName);
	}

	@Override
	public void outputPolygonGroup(String polygonGroupName) {
		System.out.println("polygroup " + polygonGroupName);
	}

	@Override
	public void outputMTLLibDefinition(String materialLibraryFileName) {
		System.out.println("define material library " + materialLibraryFileName);
	}

	@Override
	public void outputMaterialBind(String materialName) {
		System.out.println("material " + materialName);
	}

	@Override
	public void outputSmoothingGroup(int smoothingGroup) {
		System.out.println("smoothingroup " + smoothingGroup);
		
	}

	@Override
	public void onProcessingStart(OBJLoader objLoader) {
		
	}

	@Override
	public void onProcessingDone(OBJLoader objLoader) {
		
	}

	@Override
	public boolean processLine(String line) {
		return false;
	}

}
