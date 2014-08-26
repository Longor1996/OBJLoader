package de.longor1996.util.objloader;

import java.util.ArrayList;

/**
 * A interface (IOBJOutput) that defines the output of the processor (OBJLoader).
 * The interface should be self-explaining, mostly.
 * @author Longor1996
 **/
public interface IOBJOutput
{
	
	public void outputVertex(float x, float y, float z);
	
	public void outputNormal(float x, float y, float z);
	
	public void outputTextureCoodinate(float u, float v, float w);
	
	public void outputFace(int pointCount, ArrayList<int[]> tempPoints);
	
	// The following are part of the 'standard' as defined by:
	// https://en.wikipedia.org/wiki/Wavefront_.obj_file	
	
	public void outputObjectGroup(String objectGroupName);
	
	public void outputPolygonGroup(String polygonGroupName);
	
	public void outputMTLLibDefinition(String materialLibraryFileName);
	
	public void outputMaterialBind(String materialName);
	
	public void outputSmoothingGroup(int smoothingGroup);
	
	// The following are parts of the processor interface.
	
	/**
	 * This method is called when the processor starts processing the input.
	 * @param objLoader A reference to the processor.
	 **/
	public void onProcessingStart(OBJLoader objLoader);
	
	/**
	 * This method is called when the processor is done processing the input.
	 * @param objLoader A reference to the processor.
	 **/
	public void onProcessingDone(OBJLoader objLoader);
	
	/**
	 * If the processor doesn't know about a directive, it will ask the output if it knows the directive.
	 * @param line The directive the processor doesn't know about.
	 * @return True, if the output processed the directive, false if not.
	 **/
	public boolean processLine(String line);
	
}
