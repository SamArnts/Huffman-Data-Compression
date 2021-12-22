Sam Arnts
sarnts@u.rochester.edu
csc172 Project 2


In this project, we were tasked with implementing the Huffman algorithm for data
compression, as well as decoding files created through Huffman encoding. The 
idea behind Huffman encoding is to assign prefix codes to every distinct
character, with the characters that occur most frequently getting the smallest codes 
and the characters that appear least having the largest codes. I had to build
a Huffman tree from the input by creating leaf nodes for each distinct character,
and building a heap based priority queue with the least frequent characters at the 
front, and then combining the two nodes with the smallest frequency to form a tree 
with the parent node having a frequency of the sum of the two nodes. This process 
must be repeated for the rest of the priority queue. To decode the tree, we start 
at the top of the tree, and if the current bit is a 0 we traverse left, and if its 
a 1 we traverse right until we find a leaf node and print that character associated 
with that leaf. 

Compile:
> javac HuffmanSubmit.java

Run:
> java HuffmanSubmit.java

(I tested the code using the main method provided in the assingment instructions.)