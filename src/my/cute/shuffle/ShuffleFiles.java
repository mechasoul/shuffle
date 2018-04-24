package my.cute.shuffle;

import java.util.Scanner;



public class ShuffleFiles {
	
	//convert given string to long to be used as random seed
	static long stringToSeed(String s) {
	    if (s == null) {
	        return 0;
	    }
	    long hash = 0;
	    char chars[] = s.toCharArray();
	    //same implementation as hashCode()
	    for (int i=0; i < chars.length; i++) {
	        hash += ((long) Math.pow(31L, chars.length - 1 - i)) * chars[i];
	    }
	    return hash;
	}
	
	public static void main(String[] args) {
		
		String workingDirectory = System.getProperty("user.dir");
		String seed=null;
		
		//preliminary check for good arguments
		
		if(args.length == 0) {
			System.out.printf("no filetype supplied! exiting...\n");
			System.exit(0);
		}
		
		//acquire seed if supplied, 
		for(int i=0; i < args.length; i++) {
			if(args[i].equals("-seed")) {
				if(i+1 < args.length) {
					//no exception thrown by above check
					seed = args[i+1];
					break;
				} else {
					System.out.printf("error parsing seed, exiting...\n");
					System.exit(0);
				}
			}
		}
		
		//warnings for dummies
		System.out.printf("this will shuffle all files (+ in all subdirectories) ending in: \n");
		for(int i=0; i < args.length; i++) {
			//check for seed input
			if(args[i].equals("-seed")) {
				//skip this and the following argument
				i++;
			} else {
				System.out.printf("%s\n",args[i]);
			}
		}

		System.out.printf("\nthis could break all your shit!! continue?\nenter y to continue, enter anything else to quit\n");
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String s = reader.next(); // Scans the next token of the input as an int.
		reader.close();
		
		if(s.equalsIgnoreCase("y")) {
			
			//shuffle
			//shuffle over each supplied filetype
			for(int i=0; i < args.length; i++) {
				//seed check
				if(args[i].equals("-seed")) {
					//skip
					i++;
				} else {
					Shuffler shuffler;
					if(seed == null) {
						shuffler = new Shuffler(args[i]);
					} else {
						shuffler = new Shuffler(args[i], stringToSeed(seed));
					}
					shuffler.shuffleDirectoryContents(workingDirectory);
				}
			}
			
		} else {
			
			System.out.printf("come back when you are ready :)\nexiting...\n");
			
		}
		
		
		

	}

}
