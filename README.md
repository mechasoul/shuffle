# shuffle
ruin all of your files

# seriously this will ruin all of your files be careful

# Use

Run from command line as `shuffle.exe [suffixes] [-seed <seed>]`. For each suffix supplied, all files ending with that suffix within the current directory and all subdirectories will be shuffled and replaced with a different file from that set. So for example, running it as `shuffle.exe wav jpg` will cause all files ending in wav in the current directory and all subdirectories to be replaced with another random file ending in wav in the current directory or subdirectory, and then the same for jpg.

Setting a seed (for example, `shuffle.exe png -seed hello` to set hello as a seed) should cause the randomness to work in a set way according to your seed, so running it repeatedly with the same seed on the same set of files should produce the same results. It probably should produce the same results across multiple computers but I don't think it actually does??

Note that png files will not just be shuffled, but also the new file will be resized to the dimensions of the old file it replaced.

# seriously be careful!!

this will do whatever you tell it to indiscriminately, so be careful what you tell it! If you put it into your C:\ directory and run it on .exe files your computer will explode so if you do that it's not my fault because I told you ok

back up any files you don't want to be permanently mixed up and useless

# notes

things that will make this break probably: 

- if your drive doesn't have enough space to make a temporary copy of every file included
- if you have png files that aren't actually png files
- if you don't have java installed
- idk probably more
