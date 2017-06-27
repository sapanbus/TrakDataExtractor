/**
 * 
 */
package com.trakdata.dataextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.txt.TXTParser;
import org.xml.sax.SAXException;
import org.apache.tika.parser.pdf.PDFParser;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author bandiboss
 *
 */
public class DataExtractorEngine {

	/**
	 * 
	 */
	public DataExtractorEngine() {
		// TODO Auto-generated constructor stub
	}

		
	private static List<Path> listFiles(Path path) throws IOException {
	    Deque<Path> stack = new ArrayDeque<Path>();
	    final List<Path> files = new LinkedList<>();

	    stack.push(path);

	    while (!stack.isEmpty()) {
	        DirectoryStream<Path> stream = Files.newDirectoryStream(stack.pop());
	        for (Path entry : stream) {
	            if (Files.isDirectory(entry)) {
	                stack.push(entry);
	            }
	            else {
	            	System.out.println(entry);
	                files.add(entry);
	            }
	        }
	        stream.close();
	    }

	    return files;
	}

	public static List<Path> listSourceFiles(Path dir) throws IOException {
	       List<Path> result = new ArrayList<>();
	       try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{txt,csv,xls,xlsx,pdf}")) {
	           for (Path entry: stream) {
	               result.add(entry);
	           }
	       } catch (DirectoryIteratorException ex) {
	           // I/O error encounted during the iteration, the cause is an IOException
	           throw ex.getCause();
	       }
	       return result;
	   }

	public static void extractFileContent(AutoDetectParser parser, Path filename, String filename1, boolean generateout ) {

		String OUTPUT_FILE = "/Users/bandiboss/TikaDocs/IDOL/OUTPUT/" + filename1 + ".txt";
		try {

			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();

			FileInputStream inputstream = new FileInputStream(new File(filename.toString()));

			parser.parse(inputstream, handler, metadata);

			String[] metadataNames = metadata.names();

			System.out.println("File Name:" + filename.toString());
			System.out.println("----------------------------------");
			System.out.println("MetaData");

			for (String name : metadataNames) {
				System.out.println(name + " : " + metadata.get(name));
			}

			System.out.println("------------------------------------");
			String bodyContent = handler.toString();
			System.out.println("Start Content-----------------------------------");
			//System.out.println(bodyContent);
			System.out.println("End Content-----------------------------------");

			if (generateout) {


			byte[] bytes = bodyContent.getBytes();

			try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(OUTPUT_FILE), 1024)) {

				// write a byte sequence
				out.write(bytes);

				// write a single byte

				//out.write(bytes[0]);

				// write sub sequence of the byte array

				//out.write(bytes,4,10);
				out.flush();

			} catch (IOException e) {
				e.printStackTrace();

			}
		}

			} catch(Exception ex){

				System.out.println();
			}

	}

  public static void main(String... aArgs) throws IOException{
    //String ROOT = "/Users/bandiboss/Downloads";
   String ROOT = "/Users/bandiboss/TikaDocs/IDOL";
   // String ROOT = "/Users/bandiboss/TikaDocs/IDOL/Training/TrainingInstallers/ODBCConnectorCFS/jre/lib/zi/Asia";
    FileVisitor<Path> fileProcessor = new ProcessFile();
    List<Path> listFiles = listFiles(Paths.get(ROOT));
    AutoDetectParser parser = new AutoDetectParser();

    // List<Path> listSourceFiles = listSourceFiles(Paths.get(ROOT));
    //listSourceFiles.forEach(System.out::println);
    //listSourceFiles.stream().map(file -> file.getFileName()).;;

    // Files.walkFileTree(Paths.get(ROOT), fileProcessor);
	  String filename1;
	  for(Path path_file:listFiles ) {
	  	  File f = path_file.toFile();
	  	  String s1 = f.getPath().toString();

	  	//  System.out.println("FilePath: " + f.getPath().toString() + " - Filename:" + f.getName());
	  	  //System.out.println("Start Index:" + s1.lastIndexOf('/') + "End Index" + s1.lastIndexOf('.'));
	  	  if (s1.lastIndexOf('.') > 0) {
			  filename1 = s1.substring(s1.lastIndexOf('/') + 1, s1.lastIndexOf('.'));
			 // System.out.println("File Cannoal path" + f.getParent() + "Filename:" + filename1);
			 extractFileContent(parser,path_file,filename1,true);
		  }
		  else {
	  	  	 filename1 = s1.substring(s1.lastIndexOf('/') + 1);
			  System.out.println("File path with no extension" + f.getParent() + " - Filename:" + filename1);
			  extractFileContent(parser,path_file,filename1,true);
		  }
	  }
  }

  private static final class ProcessFile extends SimpleFileVisitor<Path> {
    @Override public FileVisitResult visitFile(
      Path aFile, BasicFileAttributes aAttrs
    ) throws IOException {
      System.out.println("Processing file:" + aFile);
      return FileVisitResult.CONTINUE;
    }
    
    @Override  public FileVisitResult preVisitDirectory(
      Path aDir, BasicFileAttributes aAttrs
    ) throws IOException {
      System.out.println("Processing directory:" + aDir);
      return FileVisitResult.CONTINUE;
    }
  }
} 