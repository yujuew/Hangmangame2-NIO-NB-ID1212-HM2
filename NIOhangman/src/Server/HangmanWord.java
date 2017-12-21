package Server;

/**
 *
 * @author Shine
 */
import java.io.*;  
import java.util.ArrayList;  
import acm.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class HangmanWord{
    private ArrayList<String> wordList = new ArrayList<String>();  
    private BufferedReader br; 
    
    public HangmanWord(){  
        openFile();  
        getWordFromFile();  
    }
    
    //open file words.txt
    public void openFile(){
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\Shine\\Documents\\NetBeansProjects\\JavaHangman\\src\\javahangman\\words.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HangmanWord.class.getName()).log(Level.SEVERE, null, ex);          
        }
     }  
    
    //get the words from the tile and put them in a string str
    private void getWordFromFile() {  
        String str;  
        try {  
            while(true){  
                str = br.readLine();  
                if(str==null) break;  
                wordList.add(str);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    //count the number of words in the words.txt
    public int getWordCount(){
             return wordList.size();
     }
    
    //get word at specific index
         public String getWord(int index){
             return wordList.get(index);
     }
}

