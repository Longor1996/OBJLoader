package de.longor1996.util.objloader;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * A parser for Wavefront *.OBJ files. Use the loadModel(...) method to start.
 * @author Longor1996
 **/
public class OBJLoader implements Runnable
{

	public static void loadModel(Scanner input, IOBJOutput output) throws OBJLoaderException
	{
		OBJLoader ol = new OBJLoader();
		ol.input = input;
		ol.output = output;
		ol.normalize = false;
		ol.tessellate = true;
		ol.indexMinusOne = true;
		ol.clampTexCoords = false;
		ol.process();
	}
	
	private int stat_vcount;
	private int stat_vtcount;
	private int stat_vncount;
	private int stat_fcount;
	
	private Scanner input;
	private IOBJOutput output;
	private boolean normalize;
	private boolean tessellate;
	private boolean indexMinusOne;
	private boolean clampTexCoords;
	
	private int lineNumber;
	private OBJLoaderException exception;
	private ArrayList<int[]> tempVertices;
	
	public void setTessellate(boolean flag)
	{
		tessellate = flag;
	}
	public void setSubtractOneFromIndices(boolean flag)
	{
		indexMinusOne = flag;
	}
	public void setNormalizeNormals(boolean flag)
	{
		normalize = flag;
	}
	public void setClampTextureCoordinates(boolean flag)
	{
		clampTexCoords = flag;
	}
	public void setInput(Scanner input)
	{
		this.input = input;
	}
	public void setOutput(IOBJOutput output)
	{
		this.output = output;
	}
	
	public OBJLoaderException getException()
	{
		return exception;
	}
	
	public int getVertexCount(){
		return stat_vcount;
	}
	public int getVertexNormalCount(){
		return stat_vncount;
	}
	public int getVertexTextureCoordinateCount(){
		return stat_vtcount;
	}
	public int getFaceCount(){
		return stat_fcount;
	}
	
	@Override
	public void run()
	{
		try
		{
			process();
		}
		catch (OBJLoaderException e)
		{
			e.printStackTrace();
			exception = e;
		}
	}
	
	public void process() throws OBJLoaderException
	{
		output.onProcessingStart(this);
		
		lineNumber = 0;
		tempVertices = new ArrayList<int[]>();
		String line = null;
		
		while(this.input.hasNextLine())
		{
			line = this.input.nextLine().trim();
			lineNumber++;
			
			if(line.isEmpty())
				continue;
			
			if(line.charAt(0) == '#')
				continue;
			
			this.processLine(line);
		}
		
		output.onProcessingDone(this);
		this.input.close();
	}
	
	public void processLine(String line) throws OBJLoaderException
	{
		
		if(line.startsWith("v "))
		{
			line = line.substring(2).trim();
			processVertex(line);
			return;
		}
		
		if(line.startsWith("vn "))
		{
			line = line.substring(3).trim();
			processNormal(line);
			return;
		}
		
		if(line.startsWith("vt "))
		{
			line = line.substring(3).trim();
			processTextureCoordinate(line);
			return;
		}
		
		if(line.startsWith("f "))
		{
			line = line.substring(2).trim();
			processFace(line);
			return;
		}
		
		// object group (A group of groups of polygons/faces)
		if(line.startsWith("o "))
		{
			line = line.substring(2).trim();
			output.outputObjectGroup(line);
			return;
		}
		
		// polygon group (A group of polygons/faces)
		if(line.startsWith("g "))
		{
			line = line.substring(2).trim();
			output.outputPolygonGroup(line);
			return;
		}
		
		// smoothing group
		if(line.startsWith("s "))
		{
			line = line.substring(2).trim();
			
			if(line.equalsIgnoreCase("off"))
			{
				output.outputSmoothingGroup(-1);
			}
			else
			{
				output.outputSmoothingGroup(parseInteger(line));
			}
			return;
		}
		
		//
		if(line.startsWith("mtllib "))
		{
			line = line.substring(7).trim();
			output.outputMTLLibDefinition(line);
			return;
		}
		
		//
		if(line.startsWith("usemtl "))
		{
			line = line.substring(7).trim();
			output.outputMaterialBind(line);
			return;
		}
		
		// 'flags'
		if(line.startsWith("--l19loader "))
		{
			line = line.substring(12);
			
			if(line.startsWith("tessellate "))
			{
				tessellate = parseBoolean(line.substring(11));
				return;
			}
			
			if(line.startsWith("normalize "))
			{
				normalize = parseBoolean(line.substring(10));
				return;
			}
			
			if(line.startsWith("subtractindices "))
			{
				indexMinusOne = parseBoolean(line.substring(16));
				return;
			}
			
			throw new OBJLoaderException("Unknown --l19loader directive: " + line);
		}
		
		if(output.processLine(line))
		{
			return;
		}
		
		throw new OBJLoaderException("Unknown directive: " + line);
	}
	
	private void processVertex(String line) throws OBJLoaderException
	{
		String[] vectorStr = line.split(" ");
		float x;
		float y;
		float z;
		
		if(vectorStr.length == 3)
		{
			x = parseFloat(vectorStr[0]);
			y = parseFloat(vectorStr[1]);
			z = parseFloat(vectorStr[2]);
		}
		else if(vectorStr.length == 2)
		{
			x = parseFloat(vectorStr[0]);
			y = parseFloat(vectorStr[1]);
			z = 0;
		}
		else
		{
			throw new OBJLoaderException("Incorrect number of vector components in vertex.");
		}
		
		output.outputVertex(x, y, z);
		stat_vcount++;
	}
	
	private void processNormal(String line) throws OBJLoaderException
	{
		String[] vectorStr = line.split(" ");
		float x;
		float y;
		float z;
		
		if(vectorStr.length == 3)
		{
			x = parseFloat(vectorStr[0]);
			y = parseFloat(vectorStr[1]);
			z = parseFloat(vectorStr[2]);
		}
		else
		{
			throw new OBJLoaderException("Incorrect number of vector components in normal.");
		}
		
		if(normalize)
		{
			float c = x*x + y*y + z*z;
			
			if(c <= 0)
				throw new OBJLoaderException("Normal cannot be normalized. X*X+Y*Y+Z*Z <= 0.");
			
			float sqrt = (float) Math.sqrt(c);
			
			if(c <= 0)
				throw new OBJLoaderException("Normal cannot be normalized. sqrt(X*X+Y*Y+Z*Z) <= 0.");
			
			x /= sqrt;
			y /= sqrt;
			z /= sqrt;
		}
		
		output.outputNormal(x, y, z);
		stat_vncount++;
	}
	
	private void processTextureCoordinate(String line) throws OBJLoaderException
	{
		String[] vectorStr = line.split(" ");
		float u; // also called s
		float v; // also called t
		float w;
		
		if(vectorStr.length == 2)
		{
			u = parseFloat(vectorStr[0]);
			v = parseFloat(vectorStr[1]);
			w = 0;
		}
		else if(vectorStr.length == 3)
		{
			u = parseFloat(vectorStr[0]);
			v = parseFloat(vectorStr[1]);
			w = parseFloat(vectorStr[2]);
		}
		else
		{
			throw new OBJLoaderException("Incorrect number of vector components in texture-coordinate.");
		}
		
		if(clampTexCoords)
		{
			u = clamp(0, 1, u);
			v = clamp(0, 1, u);
			w = clamp(0, 1, u);
		}
		
		output.outputTextureCoodinate(u, v, w);
		stat_vtcount++;
	}
	
	private void processFace(String line) throws OBJLoaderException
	{
		// Every string in this array is ONE edge-point of a single polygon (N-gon face).
		String[] faceStr = line.split(" ");
		int nPoints = faceStr.length;
		tempVertices.clear();
		
		if(nPoints < 3)
		{
			throw new OBJLoaderException("A face must have 3 or more vertices.");
		}
		
		// Triangle
		if(nPoints == 3)
		{
			int[] out = null;
			parseVertice(faceStr[0], out = new int[3]);
			tempVertices.add(out);
			
			parseVertice(faceStr[1], out = new int[3]);
			tempVertices.add(out);
			
			parseVertice(faceStr[2], out = new int[3]);
			tempVertices.add(out);
			
			output.outputFace(tempVertices.size(), tempVertices);
			stat_fcount++;
			return;
		}
		
		// Tessellated Quad
		if(nPoints == 4 && tessellate)
		{
			// Quad Vertices in clockwise order.
			int[] a = new int[3];
			int[] b = new int[3];
			int[] c = new int[3];
			int[] d = new int[3];
			
			parseVertice(faceStr[0], a);
			parseVertice(faceStr[1], b);
			parseVertice(faceStr[2], c);
			parseVertice(faceStr[3], d);
			
			// Triangle A
			tempVertices.add(a);
			tempVertices.add(b);
			tempVertices.add(c);
			
			// Triangle B
			// This MAY be the wrong order!
			tempVertices.add(a);
			tempVertices.add(c);
			tempVertices.add(d);
			
			output.outputFace(tempVertices.size(), tempVertices);
			stat_fcount++;
			return;
		}
		
		// N-gon
		for(int i = 0; i < nPoints; i++)
		{
			int[] out = new int[3];
			parseVertice(faceStr[i], out);
			tempVertices.add(out);
		}
		stat_fcount++;
		output.outputFace(tempVertices.size(), tempVertices);
		
	}
	
	private void parseVertice(String string, int[] is) throws OBJLoaderException
	{
		if(string.indexOf('/') != -1)
		{
			// has slashes
			String[] parts = split(string, '/', 3);
			
			for(int i = 0; i < 3; i++)
			{
				String vStr = parts[i];
				
				if(vStr != null)
				{
					is[i] = indexMinusOne ? (parseInteger(vStr)-1) : parseInteger(vStr);
				}
				else
				{
					is[i] = -1;
				}
			}
		}
		else
		{
			// is only vertex
			is[0] = parseInteger(string);
			is[1] = -1;
			is[2] = -1;
		}
	}
	
	public static final float clamp(float min, float max, float value) {
		return value < min ? min : (value > max ? max : value);
	}
	
	/**
	 * Special split method that is made to split strings according to a split-character (not a regex!).
	 * 
	 * <pre>
	 * Putting in "hi/1/d" will give ["hi","1","d"]
	 * Putting in "/1/d" will give [null,"1","d"]
	 * Putting in "hi//d" will give ["hi",null,"d"]
	 * Putting in "hi/1/" will give ["hi","1",null]
	 * </pre>
	 * @param input The string to split.
	 * @param splitter The character to use as the splitter.
	 * @param minOutSize The minimum size of the output-array.
	 * 
	 * @return A array containing the pieces of the string.
	 **/
	public static final String[] split(String input, char splitter, int minOutSize)
	{
		int splitCount = 0;
		int inLen = input.length();
		
		// count splits
		for(int i = 0; i < inLen; i++)
		{
			if(input.charAt(i) == splitter)
			{
				splitCount++;
			}
		}
		
		// if no splits, return input as is.
		if(splitCount == 0)
		{
			return new String[]{input};
		}
		
		int outSize = splitCount + 1;
		
		if(outSize < minOutSize)
		{
			outSize = minOutSize;
		}
		
		String[] output = new String[outSize];
		
		{
			int start = 0;
			int index = 0;
			int axind = 0;
			
			for(; index < inLen; index++)
			{
				char cAt = input.charAt(index);
				
				if(cAt == splitter)
				{
					if(start == index)
					{
						output[axind++] = null;
					}
					else
					{
						output[axind++] = input.substring(start, index);
					}
					start = index + 1;
				}
			}
			
			if(inLen-index+1 > 0)
			{
				if(start == index)
				{
					output[axind++] = null;
				}
				else
				{
					output[axind++] = input.substring(start, index);
				}
			}
		}
		
		return output;
	}

	public final float parseFloat(String string) throws OBJLoaderException
	{
		try
		{
			return Float.valueOf(string);
		}
		catch(NumberFormatException ex)
		{
			throw new OBJLoaderException("Failed to parse floating-point number: " + string, ex);
		}
	}
	
	public final int parseInteger(String string) throws OBJLoaderException
	{
		try
		{
			return Integer.valueOf(string);
		}
		catch(NumberFormatException ex)
		{
			throw new OBJLoaderException("Failed to parse integer number: " + string, ex);
		}
	}
	
	public final boolean parseBoolean(String string) throws OBJLoaderException
	{
		try
		{
			return Boolean.valueOf(string);
		}
		catch(NumberFormatException ex)
		{
			throw new OBJLoaderException("Failed to parse boolean: " + string, ex);
		}
	}
	
	public class OBJLoaderException extends Exception
	{
		private static final long serialVersionUID = -8819289391959558856L;

		public OBJLoaderException(String msg)
		{
			super(msg + " @Line " + lineNumber);
		}
		
		public OBJLoaderException(String msg, Exception ex)
		{
			super(msg + " @Line " + lineNumber, ex);
		}
	}
}
