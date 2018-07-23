import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {
  
  public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings) 
  public DirectedGraph internet;             // this is our internet graph
  
  
  
  // Constructor initializes everything to empty data structures
  // It also sets the location of the internet files
  SearchEngine() {
    // Below is the directory that contains all the internet files
    HtmlParsing.internetFilesLocation = "internetFiles";
    wordIndex = new HashMap<String, LinkedList<String> > ();  
    internet = new DirectedGraph();    
  } // end of constructor//2017
  
  
  // Returns a String description of a searchEngine
  public String toString () {
    return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
  }
  
  void traverseInternet(String url) throws Exception {
    System.out.println(url);
    internet.addVertex(url);
    internet.setVisited(url, true);
    LinkedList<String> neigbors = HtmlParsing.getLinks(url);
    LinkedList<String> content = HtmlParsing.getContent(url);
    Iterator<String> i = content.iterator();   //This is going to be used to add words from the url to the WordIndex HashMap
    while(i.hasNext()){
      String s = i.next();
      if(wordIndex.containsKey(s)){
        if(!(wordIndex.get(s).contains(url))){
          wordIndex.get(s).addLast(url);
        }//If the word is in the index and the url is not in the linked list mapping to the word, then add it
      }else{
        LinkedList<String> urlList = new LinkedList<String>();
        urlList.add(url);
        wordIndex.put(s, urlList);
      }//End of Condition
    }//End of Iterator1
    /*This iterator adds words to the wordIndex using the following steps
     * If the word is already in the index, then check to see if the url is in the linked list mapped to the word
     * If not in the linked list then add it
     * If the word is not in the index, then add it alongside a Linked List of URLS that contain such word
     */
    i = neigbors.iterator();
    while( i.hasNext() ){
      String s = i.next();
      internet.addEdge(url,s);
      if(internet.getVisited(s) == false){
        traverseInternet(s);
      }//end of Recursive Conditions
    }//end of Iterator2
    //This iterator, iteratates through all the neigbors of the current url to recursively build the internet graph though a depth first search
  } // end of traverseInternet
  
  void computePageRanks() {
    LinkedList<String> vertices = internet.getVertices();
    Iterator<String> i = vertices.iterator();
    while(i.hasNext()){    //Here each vertex's PR is initialized to 1
      String s1 = i.next();
      internet.setPageRank(s1, 1.0);
    }//end of Iterator 1 While loop
    for(int a = 0; a < 100; a++){
      Iterator<String> j = vertices.iterator();
      while(j.hasNext()){      //Here each vertex's PR will be computed
        String s2 = j.next();
        double d = 0.5;
        double pageRank = (1.0 - d);       //This is used ot initialize the page rank value as the others will be iteratively added to this value thus folliwng the equation
        LinkedList<String> edgesInto = internet.getEdgesInto(s2);
        Iterator<String> linksTo = edgesInto.iterator();
        while(linksTo.hasNext()){
          String s3 = linksTo.next();
          pageRank = pageRank + d*(internet.getPageRank(s3)/internet.getOutDegree(s3));  //Here the pageRank Equation is iteratively build using links that point to the vertex s2
        }
        internet.setPageRank(s2, pageRank);
      }//end of Iterator 2 While loop
    }//end of Iterative Numerical Approximation for loop
  } // end of computePageRanks
  
  String getBestURL(String query) {
    LinkedList<String> results = null;
    String bestURL = null;
    double bestPageRank = 0.0;
    if ( wordIndex.containsKey(query) ){
      results = wordIndex.get(query);
      Iterator<String> i = results.iterator();   //Will be used to iterate through all resulting sites to find the one with the highest page rank
      while (i.hasNext()){
        String s = i.next();
        double pageRank = internet.getPageRank(s);
        if (pageRank > bestPageRank){
          bestURL = s;
          bestPageRank = pageRank;
        }//end of condition that assigns the highest pageranked page to the best URL
      }//end of Iterator 1 While loop
    }else{
      String noResult = "No such query is found";
      return noResult;
    }//end of condition to check if the query is in the Word Index
    return bestURL;
  } // end of getBestURL
  
  
  
  public static void main(String args[]) throws Exception{  
    SearchEngine mySearchEngine = new SearchEngine();
    //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
    mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
    
    mySearchEngine.computePageRanks();
    
    BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
    String query;
    do {
      System.out.print("Enter query: ");
      query = stndin.readLine();
      if ( query != null && query.length() > 0 ) {
        System.out.println("Best site = " + mySearchEngine.getBestURL(query));
      }
    } while (query!=null && query.length()>0);    
  } // end of main
}
