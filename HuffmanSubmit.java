/*Sam Arnts
*sarnts@u.rochester.edu
*csc172 project 2
*/

import java.io.*;
import java.util.Comparator;


//heap-based min priority queue class used to construct the huffman tree
class PriorityQueue<Key> {
    private Key[] list;                    
    private int size;                       
    private Comparator<Key> comparator;  

	//constructor
    public PriorityQueue() {
        list = (Key[]) new Object[1];
        size = 0;
    }

	//getter for size
    public int size() {
        return size;
    }

	//readjusts the size of the array for when capacity is reached
    private void ensureCapcity(int capacity) {
        assert capacity > size;
        Key[] temp = (Key[]) new Object[capacity];
        for (int i = 1; i <= size; i++) {
            temp[i] = list[i];
        }
        list = temp;
    }

	//inserts the key in the correct position 
    public void insert(Key x) {
        
		//doubles size of the array
        if (size == list.length - 1) ensureCapcity(2 * list.length);
        list[++size] = x;
		
		//called to maintain order
        percolateUp(size);
        
    }

	//removes and returns the frontmost element of the queue 
    public Key removeFirst() {
        Key min = list[1];
        swap(1, size--);

		//called to maintain order
        percolateDown(1);
        list[size+1] = null;     
        if ((size > 0) && (size == (list.length - 1) / 4)) ensureCapcity(list.length / 2);
        
        return min;
    }

	//maintains order of the priority queue when new keys are added
    private void percolateUp(int k) {
        while (k > 1 && isGreater(k/2, k)) {
            swap(k, k/2);
            k = k/2;
        }
    }

	//maintains order of the priority queue when keys are removed
    private void percolateDown(int k) {
        while (2*k <= size) {
            int j = 2*k;
            if (j < size && isGreater(j, j+1)) j++;
            if (!isGreater(k, j)) break;
            swap(k, j);
            k = j;
        }
    }

	//compares value of keys, allows us to know when elements need to be swapped
    private boolean isGreater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) list[i]).compareTo(list[j]) > 0;
        }
        else {
            return comparator.compare(list[i], list[j]) > 0;
        }
    }

	//basic swap function that exhcanges elements in the queue
    private void swap(int i, int j) {
        Key swap = list[i];
        list[i] = list[j];
        list[j] = swap;
    }
}

public class HuffmanSubmit implements Huffman {
   
   //method that constructs the huffman tree utilizing a priority queue
   public static Node huffTree(int [] freqArray) {

	  //creating a new priority queue of nodes with no childrem
	  PriorityQueue<Node> myQueue = new PriorityQueue<Node>();
	  for (char c = 0; c < 256; c++) {
		  if (freqArray[c] > 0) {
			  myQueue.insert(new Node(c, freqArray[c], null, null));
		  }

		  //cobines smallest two trees
		  while (myQueue.size() > 1) {
			  Node left = myQueue.removeFirst();
			  Node right = myQueue.removeFirst();
			  Node parent = new Node('\0', left.frequency + right.frequency, left, right);
			  myQueue.insert(parent);
		  }
		  
	  }
	   return myQueue.removeFirst(); //returns minimum element from queue
   }


   //prints frequency table as "binary:freq"
   public void printCountAry(FileWriter writer, int [] charCountAry) {
		for(int i = 0; i < charCountAry.length; i++) {
			if(charCountAry[i] != 0) {
				try {
					String binaryKey = Integer.toBinaryString(i); //converts to binary
					while (binaryKey.length() < 8){
						binaryKey = "0" + binaryKey; //pushes 0's infront of keys that are too short
					}
					writer.write(binaryKey+ ":" + charCountAry[i] + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

   //recursively makes a lookup table from the symbol and their encoded form
   public void lookupTable(String [] string, Node n, String s) {
	   if (!n.isLeaf()) {
		   lookupTable(string, n.left, s + '0');
		   lookupTable(string, n.right, s + '1');
	   }else{
		   string[n.character] = s;
	   }
   }

   //reads in a huffman tree created by encoding, used for decoding
   public static Node readTree(BinaryIn in) {
	   boolean isLeaf = in.readBoolean();
	   if (isLeaf) {
		   return new Node(in.readChar(), -1, null, null);
	   }else {
		   return new Node('\0', -1, readTree(in), readTree(in));
	   }

   }
   //writes the huffman tree to a file
   public static void makeTrie (Node n, BinaryOut output) {
	   if (n.isLeaf()) {
		   output.write(true);
		   output.write(n.character, 8);
		   return;
	   }
	   output.write(false);
	   makeTrie(n.left,output);
	   makeTrie(n.right, output);
   }

   //simple node class for constructing huffman trees
    private static class Node implements Comparable<Node> {
		private final char character;
		private final int frequency;
		private final Node left;
		private final Node right;

		//Nodes store character and frequency data
		Node(char character, int frequency, Node left, Node right) {
			this.character = character;
			this.frequency = frequency;
			this.left = left;
			this.right = right;
		}

		//determines if the node is a leaf node
		private boolean isLeaf() {
			if (left == null && right == null) {
				return true;
			}
			else {
				return false;
			}
		}

		//compares two nodes based on frequency
		@Override
		public int compareTo(HuffmanSubmit.Node o) {
			return this.frequency - o.frequency;
		}
	   
   }

   //encodes a file into a binary format, produces a chracter frequency file
	public void encode(String inputFile, String outputFile, String freqFile){
		
		//take the file name and produces a BinayIn and BinaryOut file
		BinaryIn in = new BinaryIn(inputFile);
		BinaryOut output = new BinaryOut(outputFile);
		String debug = freqFile;

		//converting input to string and then charArray
		String s = in.readString();
		char[] input = s.toCharArray();


		int [] freq = new int [256]; //size of the ASCII Alphabet

		//counts the frequencies of characters
		for(int i = 0; i < input.length; i++) {
			freq[input[i]]++;
		}

		//creating the frequency file using the freq array, catching any IOException
		try {
			FileWriter writeFreq = new FileWriter(debug);
			printCountAry(writeFreq, freq);
			writeFreq.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// creating a huffman tree given the freq array
		Node root = huffTree(freq);
		
		//building a table of character keys and values
		String [] table = new String[256];
		lookupTable(table, root, "");

		//printing out the tree as well as the size of the file
		makeTrie(root, output);
		output.write(input.length);

		//encoding input
		for (int i = 0; i < input.length; i++) {
			String code = table[input[i]];
			for (int j = 0; j < code.length(); j++) {
				if (code.charAt(j) == '0') {
					output.write(false);
				}else {
					output.write(true);
				}
			}
		}
		output.close();
   }


    //decode method that turns a binary file into the original file
	public void decode(String inputFile, String outputFile, String freqFile){
	   
		//reading the binary in, creating an output file
		BinaryIn in = new BinaryIn(inputFile);
		BinaryOut out = new BinaryOut(outputFile);

		//read in in the tree created by the decoder
		Node root = readTree(in);

		int length = in.readInt(); // determins the size of the file to write


		//decodes the file
		for (int i = 0; i < length - 1; i++) {
			Node x = root;
            while (!x.isLeaf()) {
                boolean bit = in.readBoolean(); //actually reads the binary input
                if (bit) x = x.right;
                else     x = x.left;
            }out.write(x.character, 8); //writes the character associated with the found key
			
		}
		out.flush();
   }

   

   public static void main(String[] args) {
      Huffman  huffman = new HuffmanSubmit();
		huffman.encode("alice30.txt", "alice.enc" , "freq.txt");
		huffman.decode("alice.enc", "alice_new.txt", "freq.txt");
   }

}
