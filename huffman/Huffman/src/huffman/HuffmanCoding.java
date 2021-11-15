package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.tree.TreeSelectionModel;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                System.exit(1);
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }
    
    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    //MAIN METHODS I WRITE
    /**
     * Reads a given text file character by character, and returns an arraylist
     * of CharFreq objects with frequency > 0, sorted by frequency
     * 
     * @param filename The text file to read from
     * @return Arraylist of CharFreq objects, sorted by frequency
     */
    public static ArrayList<CharFreq> makeSortedList(String filename) {
        StdIn.setFile(filename);
        /* Your code goes here */
        ArrayList<CharFreq> freq = new ArrayList<>(); 
        int[] list = new int[128]; 
        int count = 0;

        while(StdIn.hasNextChar() == true) {
            char temp = StdIn.readChar();
            list[temp]++;
            count ++; 
        }

        for (int i = 0; i < 128; i++){
            if (list[i] != 0){
                double p = (double)list[i]/(count);
                CharFreq temp = new CharFreq((char)i, p);
                freq.add(temp);
            }
        }

        if (freq.size() == 1){ //single char type in file
            if (list[127] != 0){
                freq.add(new CharFreq((char)0, 0));
            }
            else{
                char temp = freq.get(freq.size() - 1).getCharacter();
                CharFreq tempx = new CharFreq((char)((int)temp + 1), 0);
                freq.add(tempx);
            }
        }
        Collections.sort(freq);

        return freq;
    }

    /**
     * Uses a given sorted arraylist of CharFreq objects to build a huffman coding tree
     * 
     * @param sortedList The arraylist of CharFreq objects to build the tree from
     * @return A TreeNode representing the root of the huffman coding tree
     */
    public static TreeNode makeTree(ArrayList<CharFreq> sortedList){
        /* Your code goes here */
        Queue<TreeNode> root = new Queue<>();
        Queue<TreeNode> treenode = new Queue<>();
        TreeNode l = null;
        TreeNode r = null;
        double prob = 0;

        for (int  i = 0; i < sortedList.size(); i++){
            TreeNode temp = new TreeNode(sortedList.get(i), null, null);
            root.enqueue(temp);
        }

        while (!root.isEmpty()){
            if (treenode.isEmpty()){
                l = root.dequeue();
                r = root.dequeue();
            }
            else{
                if (root.peek().getData().getProbOccurrence() <= treenode.peek().getData().getProbOccurrence()){
                    l = root.dequeue();
                }
                else{
                    l = treenode.dequeue();
                } 

                if (!root.isEmpty() && !treenode.isEmpty()){
                    if (root.peek().getData().getProbOccurrence() <= treenode.peek().getData().getProbOccurrence()){
                        r = root.dequeue();
                    }
                    else{
                        r = treenode.dequeue();
                    } 
                }

                else if (root.isEmpty() && !treenode.isEmpty()){
                    r = treenode.dequeue();
                } 
                else{
                    r = root.dequeue(); 
                } 
            }
            
            prob = l.getData().getProbOccurrence() + r.getData().getProbOccurrence();

            treenode.enqueue(new TreeNode(new CharFreq(null, prob), l, r));
        }

        while (treenode.size() > 1){  //CHECK code?
            l = treenode.dequeue();
            r = treenode.dequeue();
            prob = l.getData().getProbOccurrence() + r.getData().getProbOccurrence();
            CharFreq temp = new CharFreq(null, prob);
            treenode.enqueue(new TreeNode(temp, l, r));
        }

        return treenode.dequeue(); 
    }

    /**
     * Uses a given huffman coding tree to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null
     * 
     * @param root The root of the given huffman coding tree
     * @return Array of strings containing only 1's and 0's representing character encodings
     */
    public static String[] makeEncodings(TreeNode root) {
        String[] encoded = new String[128];
        String temp = "";
        recursiveEncode(encoded, root, temp);

        // for(int i = 0; i < encoded.length; i++){
        //     System.out.println(encoded[i]);
        // }

        return encoded; 
    }

    /**
     * Using a given string array of encodings, a given text file, and a file name to encode into,
     * this method makes use of the writeBitString method to write the final encoding of 1's and
     * 0's to the encoded file.
     * 
     * @param encodings The array containing binary string encodings for each ASCII character
     * @param textFile The text file which is to be encoded
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public static void encodeFromArray(String[] encodings, String textFile, String encodedFile) {
        StdIn.setFile(textFile);
        /* Your code goes here */
        String concat = new String();    

        while (StdIn.hasNextChar() == true){
            char c = StdIn.readChar();
            concat = concat + encodings[c];
        }

        writeBitString(encodedFile, concat);
        return;
    }
    
    /**
     * Using a given encoded file name and a huffman coding tree, this method makes use of the 
     * readBitString method to convert the file into a bit string, then decodes the bit string
     * using the tree, and writes it to a file.
     * 
     * @param encodedFile The file which contains the encoded text we want to decode
     * @param root The root of your Huffman Coding tree
     * @param decodedFile The file which you want to decode into
     */
    public static void decode(String encodedFile, TreeNode root, String decodedFile) {
        StdOut.setFile(decodedFile);
        /* Your code goes here */
        String encodeArray = readBitString(encodedFile);
        TreeNode ptr = root;

        for(int i = 0; i < encodeArray.length(); i++){
            if (encodeArray.charAt(i) == '1' && ptr.getRight() != null){
                ptr = ptr.getRight();
            }
            if (encodeArray.charAt(i) == '0' && ptr.getLeft() != null){
                ptr = ptr.getLeft();
            }
            if (ptr.getLeft() == null || ptr.getRight() == null){
                StdOut.print(ptr.getData().getCharacter()); 
                ptr = root;
            }
        } 
        return;
    }

    //extra private methods

    private static void recursiveEncode(String[] enc, TreeNode copy, String data) {
        if (copy.getLeft() != null) { //draw out
            recursiveEncode(enc,copy.getLeft(), data + "0");
        }

        if (copy.getRight() != null) {
            recursiveEncode(enc, copy.getRight(), data + "1");
        }

        else {
            enc[copy.getData().getCharacter()] = data;
            return;
        }
    }


}
 