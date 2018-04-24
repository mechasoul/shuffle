package my.cute.shuffle;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;


public class Shuffler {
	
	final Charset utf8 = StandardCharsets.UTF_8;
	String extensionToShuffle;
	Random rand;
	
	public Shuffler(String e) {
		
		extensionToShuffle = e;
		rand = new Random();
		
	}
	
	public Shuffler(String e, long s) {
		
		extensionToShuffle = e;
		rand = new Random(s);
		
	}
	
	public void logError(Exception e) {
		try {
			FileUtils.writeStringToFile(new File("error.log"), e.getMessage() + "\r\n" + e.getStackTrace() + "\r\n\r\n", utf8, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("error generating error log\r\n");
			e1.printStackTrace();
		}
	}
	
	public void shuffle(Object[] objects) {
		
		Object buffer = null;
		
		for(int i=objects.length-1; i > 0; i--) {
			
			int j = this.rand.nextInt(objects.length);
			buffer = objects[i];
			objects[i] = objects[j];
			objects[j] = buffer;
			
		}
		
	}
	
	public Dimension getImageDimensions(String file) {
		
		File img = new File(file);
		
		try(ImageInputStream in = ImageIO.createImageInputStream(img)){
		    final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
		    if (readers.hasNext()) {
		        ImageReader reader = readers.next();
		        try {
		            reader.setInput(in);
		            return new Dimension(reader.getWidth(0), reader.getHeight(0));
		        } finally {
		            reader.dispose();
		        }
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logError(e);
		} 
		
		return null;
		
	}
	
	public void resizeImage(String input, String output, Dimension d) {
		
		try {
			Thumbnails.of(new File(input))
			//.addFilter(new Transparency(0.0))
			.forceSize((int) d.getWidth(), (int) d.getHeight())
			.outputQuality(1.0f)
			.imageType(BufferedImage.TYPE_INT_ARGB)
			.useOriginalFormat()
			.scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
			.alphaInterpolation(AlphaInterpolation.QUALITY)
			.dithering(Dithering.DISABLE)
			.antialiasing(Antialiasing.ON)
			.toFile(new File(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logError(e);
		}
		
	}

	public void shuffleDirectoryContents(String dir) {

		System.out.printf("shuffling all %s files in %s...\n", this.extensionToShuffle, dir);
		
		//grab all files in the current directory and subdirectories
		System.out.printf("building list of %s files...\n", this.extensionToShuffle);
		File filesToShuffle[] = FileUtils.listFiles(new File(dir), FileFilterUtils.suffixFileFilter(this.extensionToShuffle), TrueFileFilter.INSTANCE).toArray(new File[0]);
		
		//sort it to have specified order
		Arrays.sort(filesToShuffle);
		
		//create array copy 
		File shuffledFiles[] = Arrays.copyOf(filesToShuffle, filesToShuffle.length);
		//and shuffle it

		System.out.printf("shuffling %s files...\n", this.extensionToShuffle);
		
		shuffle(shuffledFiles);
		
		//now we have two copies of the same collection of files: one in a shuffled order
		//iterate over them, and move each entry from the shuffled array to a temporary
		//buffer in the same spot as the unshuffled entry
		for(int i=0; i < shuffledFiles.length; i++) {
			
			String newFileBuffer = filesToShuffle[i].getAbsoluteFile().getParent();
			try {
				System.out.printf("moving %s to shuffle-tmp-%d...\n", shuffledFiles[i].getPath(), i);
				
				//file location for our temporary buffer
				File bufferFile = new File(newFileBuffer + "shuffle-tmp-" + i);
				//check if our buffer happens to exist for some reason
				if(bufferFile.exists()) {
					System.out.printf("buffer file '%s' already exists! aborting...\n",bufferFile.getPath());
					return;
				} else {
					FileUtils.copyFile(shuffledFiles[i], new File(newFileBuffer + "shuffle-tmp-" + i));
				}
			} catch (IOException e) {
				logError(e);
			}
			
		}
		
		//now treat png files separately from other files
		if(this.extensionToShuffle.equals(".png") || this.extensionToShuffle.equals("png")) {
			
			//for each buffered file, resize it to the dimensions of the corresponding
			//png that it's replacing, then delete the old png and rename the new one
			
			for(int i=0; i < filesToShuffle.length; i++) {
				//old file to replace
				File oldFile = filesToShuffle[i];
				//path to the buffer file we moved the new image to
				String bufferFile = oldFile.getAbsoluteFile().getParent() + "shuffle-tmp-" + i;
				//old file image dimensions
				Dimension dimensions = getImageDimensions(oldFile.getAbsolutePath());
				//resize the buffered image to the new image's dimensions, and output to 
				//the old image file location
				System.out.printf("replacing %s with %s...\n",  oldFile.getPath(), shuffledFiles[i].getPath());
				resizeImage(bufferFile, oldFile.getAbsolutePath(), dimensions);
				//clean up by deleting the buffered image
				FileUtils.deleteQuietly(new File(bufferFile));
				
			}
			
		//for non-png files, simply replace the old file with the new one
		} else {
			
			for(int i=0; i < filesToShuffle.length; i++) {
				//old file to replace
				File oldFile = filesToShuffle[i];
				//path to the buffer file we moved the new image to
				File bufferFile = new File(oldFile.getAbsoluteFile().getParent() + "shuffle-tmp-" + i);
				//delete old file and rename new file to the old file's name
				System.out.printf("replacing %s with %s...\n",  oldFile.getPath(), shuffledFiles[i].getPath());
				FileUtils.deleteQuietly(oldFile);
				//clean up by deleting the buffered image
				boolean renameStatus = bufferFile.renameTo(oldFile);
				if(!renameStatus) {
					try {
						FileUtils.writeStringToFile(new File("rename_error.log"), "error renaming " + bufferFile + " to " + oldFile + "\r\n", utf8, true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("error generating rename error log\r\n");
						e1.printStackTrace();
					}
					
				}
			}
			
		}
		
		
		//all files replaced!
		System.out.printf("shuffling complete!\n");

	}

}
