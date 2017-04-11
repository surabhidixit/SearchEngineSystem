package set.docprocess;
import set.beans.TokenDetails;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;


public class Rocchio {
    private  static HashMap<String,List<Double>>Vector1=new HashMap<>();
    private  static HashMap<String,List<Double>>Vector2=new HashMap<>();
    private  static HashMap<String,List<Double>>Vector3=new HashMap<>();
    private  static HashMap<String,List<Double>>Vector4=new HashMap<>();
    private  static List<Double>centroid1=new ArrayList<>();
    private  static List<Double>centroid2=new ArrayList<>();
    private  static List<Double>centroid3=new ArrayList<>();
    private static Set<String>hamilton=new HashSet<>();
    private static Set<String>madison=new HashSet<>();
    private static Set<String>all=new HashSet<>();
    private static Set<String>jay=new HashSet<>();

    //vocab of the three classes
    private static List<String>hamiltonVocab=new ArrayList<>();
    private static List<String>madisonVocab=new ArrayList<>();

    public List<String> getHamiltonVocab() {
        return hamiltonVocab;
    }

    public List<String> getMadisonVocab() {
        return madisonVocab;
    }

    public List<String> getJayVocab() {
        return jayVocab;
    }

    private static List<String>jayVocab=new ArrayList<>();

    static int l = 0;
    private HashMap<Integer, String> fileNameLists = new HashMap<>();
    public HashMap<Integer, String> getFileNameLists() {
        return fileNameLists;
    }


    //for all folder and Hamilton OR Madison
	private Indexing allIndex = new Indexing();

    //Hashmap for files and their vectors
    private  HashMap<String,List<Double>>vectorList=new HashMap<>();

    public static HashMap<String, List<Double>> getVector1() {
        return Vector1;
    }

    public static void setVector1(HashMap<String, List<Double>> vector1) {
        Vector1 = vector1;
    }

    public static HashMap<String, List<Double>> getVector2() {
        return Vector2;
    }

    public static void setVector2(HashMap<String, List<Double>> vector2) {
        Vector2 = vector2;
    }


    public static Set<String> getHamilton() {
        return hamilton;
    }

    public static Set<String> getMadison() {
        return madison;
    }

    public static Set<String> getAll() {
        return all;
    }

    public static Set<String> getJay() {
        return jay;
    }

    // getter for index object
	public Indexing getAllIndex() {
		return allIndex;
	}

	// setter for index object
	public void setAllIndex(Indexing indexingObj) {
		this.allIndex = indexingObj;
	}


    // End of variables declaration//GEN-END:variables


    public static void main(String[] args) throws  IOException{
        Rocchio c=new Rocchio();

      //  System.out.println("indexing resolved files");
        Scanner sc=new Scanner(System.in);
        Path p=Paths.get("/home/surabhi/Articles/ALL/");
        c.visitFiles(p);
       /* c.seventyfour=c.getAllIndex().getmIndex();*/
       // all=Arrays.asList(c.allIndex.getInvertedIndexDictionary());
        // System.out.println("indexing conflicted files");
        Path p1=Paths.get("/home/surabhi/Articles/HAMILTON-OR-MADISON/");
        c.visitFiles(p1);

      //  System.out.println("creating complete vectorlist");
        c.createAllVectors();

     //   System.out.println("creating vector for first class");
        Path x=Paths.get("/home/surabhi/Articles/HAMILTON/");
        Vector1= c.createVector(x,hamilton,hamiltonVocab);

 //       System.out.println("creating vector for second class");
        Path y=Paths.get("/home/surabhi/Articles/MADISON");
        Vector2=c.createVector(y,madison,madisonVocab);
        //vector for conflicted files
        Vector3=c.createVector(p1,null,null);

        //create vector for Jay
        Path p2=Paths.get("/home/surabhi/Articles/JAY");
        Vector4=c.createVector(p2,jay,jayVocab);

        System.out.println("enter approach");
        String apr=sc.next();
        if(apr.equalsIgnoreCase("Rocchio")){

            //centroid for Hamilton
            centroid1= addVectors(Vector1);
            centroid1=centroid1.stream().filter(i->i!=null).map(i->i/Vector1.size()).collect(Collectors.toList());

            //centroid for Madison
            centroid2=addVectors(Vector2);
            centroid2=centroid2.stream().filter(i->i!=null).map(i->i/Vector2.size()).collect(Collectors.toList());

            //centroid for Jay
            centroid3=addVectors(Vector4);
            centroid3=centroid3.stream().filter(i->i!=null).map(i->i/Vector4.size()).collect(Collectors.toList());
            calculateDistance();
       }
        else if(apr.equalsIgnoreCase("Bayesian")) {
            NaiveBayesian nb=new NaiveBayesian(c.allIndex,c);
            nb.train();
            nb.test();
        }

    }

    //to iterate over the vectorlist of class to add the vectors
   static List<Double> addVectors(HashMap<String,List<Double>>vec)
    {
        List<Double> res = new ArrayList<Double>(Collections.nCopies(8899,0.0));
        Iterator it = vec.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,List> pair = (Map.Entry)it.next();
            String key=pair.getKey();
            compute(res,vec.get(key));
        }
        return res;
    }
    //to add all the document vectors
    public static void compute(List<Double> x,List<Double>y)
    {
        for(int i=0;i<Math.max(x.size(),y.size());i++) {
            x.set(i,x.get(i)+y.get(i));
       }
        //return x;
    }
    public FileVisitResult visitFiles(Path p) throws  IOException {

        Files.walkFileTree(p, new SimpleFileVisitor<Path>() {

            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // make sure we only process the current working

                if (p != null) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws FileNotFoundException {

                // only process .txt files
                if (file.toString().endsWith(".txt")) {

                    fileNameLists.put(l, file.getFileName().toString());

                    try {
                        //for the remaining 11 conflicted files
                        // Get a token stream on the file
                        TokenStream stream = new SimpleTokenStream(file.toFile());
                        int i = 0;
                        while (stream.hasNextToken()) {
                            String token = allIndex.processWord(stream.nextToken()).trim();
                            invertedIndexTerm(token, l, i, allIndex);
                            i++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    l++;

                }
                return FileVisitResult.CONTINUE;
            }
            // don't throw exceptions if files are locked/other
            // errors
            // occur
            public FileVisitResult visitFileFailed(Path file, IOException e) {

                return FileVisitResult.CONTINUE;
            }
        });

        return null;
    }

    //passing token to Index file for PI Index
	private void invertedIndexTerm(String token1,Integer docid, Integer i, Indexing index) {

			index.addTermInvertedIndex(index.processWord(token1), docid, i);

	}
	void createAllVectors() {

        HashMap<String, List<TokenDetails>> mainIndex = allIndex.getmIndex();
        String[]dict=allIndex.getInvertedIndexDictionary();
        int vectorDim = 0;
        for(String s:dict)
        {
            //iterate over the full vocab and set the vectorlists
            //vectorDim is the position of the term in the vectorlist
            iterate(mainIndex.get(s), vectorDim);
            //for a single term set the vector
            vectorDim++;
        }
    }

    public void iterate(List<TokenDetails>arr, int vectorDim)
    {

        TokenDetails td;
        List<Double>value;
        for(int i=0;i<arr.size();i++)
        {
            td=arr.get(i);
            //get the filename corresponding
            String s=fileNameLists.get(td.getDocId()).toString();
            if(vectorList.containsKey(s)) {
                value =  vectorList.get(s);
            }
            else {
                value = new ArrayList(Collections.nCopies(8899, 0.0));
                vectorList.put(s,value);
            }
            //the frequency of the term in the document
            //so for every document the vector defines the frequency of all the terms in the document
            value.set(vectorDim,1+Math.log(td.getPosition().size()));
        }
    }
    double finalsum=0;
    public HashMap<String,List<Double>> createVector(Path p,Set<String>filelist,List vocab) throws IOException {

        HashMap<String, List<Double>> hash = new HashMap<>();
        Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // make sure we only process the current working
                if (p != null) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws FileNotFoundException {
                String str;

                List<Double> arr;
                // only process .txt files
                if (file.toString().endsWith(".txt")) {
                    try {
                        //for the remaining 11 conflicted files
                        str = file.getFileName().toString();

                        TokenStream stream = new SimpleTokenStream(file.toFile());

                        while (stream.hasNextToken()) {
                            String st=allIndex.processWord(stream.nextToken());
                            if(filelist==null)
                                break;
                            else {
                                filelist.add(str);
                                vocab.add(st);
                            }
                        }
                        arr = vectorList.get(str);
                        //sum all the square values

                        //calculate Ld
                        finalsum=arr.stream().mapToDouble(val->Math.pow(val,2)).sum();

                        finalsum = Math.sqrt(finalsum);
                        arr=arr.stream().map(i ->
                            i = i / finalsum).collect(Collectors.toList());
                        //each vector contains filename mapped to the
                        hash.put(str, arr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }


            // don't throw exceptions if files are locked/other
            // errors
            // occur
            public FileVisitResult visitFileFailed(Path file, IOException e) {

                return FileVisitResult.CONTINUE;
            }
        });

        return hash;

    }

  static void calculateDistance()
    {
     //   double distance=Double.MAX_VALUE;
        HashMap<String,String>classify=new HashMap<>();
        Iterator it = Vector3.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key=(String)pair.getKey();
            double euc1=finddiff(centroid1,((List<Double>)pair.getValue()));
            double euc2=finddiff(centroid2,((List<Double>)pair.getValue()));

            double euc3=finddiff(centroid3,((List<Double>)pair.getValue()));
           // Vector4.forEach((k,v)->System.out.println(k+" "+v));
            //centroid3.stream().forEach(System.out::println);

            if(euc1<euc2&&euc1<euc3)
            {
               //System.out.println(key+" "+euc1+" "+euc2);
                classify.put(key,"HAMILTON");
            }
            else if(euc2<euc1&&euc2<euc3)
            {
               // System.out.println(key+" "+" "+euc1+" "+euc2);
                classify.put(key,"MADISON");
            }
            else
                classify.put(key,"Jay");
        }
     classify.forEach((k,v)-> System.out.println(k+" is written by "+v));
    }
    //to add all the document vectors
    public static double finddiff(List<Double>x,List<Double>y)
    {
        double sum=0;

        for(int i=0;i<Math.max(x.size(),y.size());i++)
        {
            sum+=Math.pow(Math.abs(y.get(i)-x.get(i)),2);
        }
        return Math.sqrt(sum);
    }


}






