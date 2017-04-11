package set.docprocess;

import set.beans.TermInfo;
import set.beans.TokenDetails;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by surabhi on 11/23/16.
 */
public class NaiveBayesian {

    Indexing index;
    Rocchio c;
    String[]dict;
    //map to store term and its p(t|c) value for terms in T
    Map<String,Double>hamprob=new HashMap<>();
    Map<String,Double>madprob=new HashMap<>();
    Map<String,Double>jayprob=new HashMap<>();

    //lists for maintaining the document lists of each class
    HashMap<String,Double>terminfo=new HashMap<>();
    Set<String>hamilton;
    Set<String>madison;
    Set<String>all;
    Set<String>jay;
    Queue<TermInfo>pq=null;
    //set that stores important vocab terms from all the classes
    List<TermInfo> important=new ArrayList<>();

    public NaiveBayesian(Indexing ind, Rocchio ctr) {
        index = ind;
        c = ctr;
        dict =index.getInvertedIndexDictionary();
        hamilton= Rocchio.getHamilton();
        madison= Rocchio.getMadison();
        all= Rocchio.getAll();
        jay= Rocchio.getJay();

    }
    public void calculateItc(String classname,String term,Set<String>classfilelist)
    {
        List<TokenDetails>postings=new ArrayList<>();
        Set<String>commonDocs=new HashSet<>();
        double N00=0,N01=0,N10=0,N11=0;
        double N1x=0,Nx1=0,N0x=0,Nx0=0;
        double N;
        postings=index.getInvertedIndexPostings(term);
        int numOfDocs=0;
        double minfo;
        //System.out.println(postings.size());

        commonDocs=findCommonDocuments(postings,classfilelist);
        for(String s:commonDocs)
        {
            //numOfDocs=isTermInDoc(term,"/home/surabhi/Articles/"+classname+"/"+s)?numOfDocs+1:numOfDocs;
            numOfDocs=classfilelist.contains(s)?numOfDocs+1:numOfDocs;

        }
        N11=numOfDocs;
        N10=c.getFileNameLists().size()-classfilelist.size()-(postings.size()-commonDocs.size());
        N01=Math.abs(classfilelist.size()-N11);
        N00=Math.abs(c.getFileNameLists().size()-classfilelist.size()-N10);
        N=N11+N10+N01+N00;
        N0x = N00+N01;
        N1x = N10+N11;
        Nx0 = N00+N10;
        Nx1 = N01+N11;

       // System.out.println(term+" "+N0x+" "+N1x+" "+Nx0+" "+Nx1);
        minfo = ((N11 / N) *(Math.log((N * N11) / (N1x * Nx1))/Math.log(2)) +
                (N10 / N) * (Math.log((N * N10) / (N1x * Nx0))/Math.log(2)) +
                (N01 / N) * (Math.log((N * N01) / (N0x * Nx1))/Math.log(2)) +
                (N00 / N) * (Math.log((N * N00) / (N0x * Nx0))/Math.log(2)));

       // System.out.println(term+" " +" "+N11+" "+N01+" "+N00+" "+N10+" "+minfo);

        //System.out.println(term+" " +minfo+" "+N11+" "+N01+" "+N00+" "+N10);

        if(!Double.isNaN(minfo))
        {
            important.add(new TermInfo(term, minfo));
        }
        //pq.offer(new TermInfo(term, minfo));
    }

    public void train()
    {

        int N00=0,N01=0,N10=0,N11=0;
        int N1x=0,Nx1=0,N0x=0,Nx0=0;
        int N;
        List<TokenDetails>postings=null;
        Set<String>commonDocs=null;
        TermInfo ti;
        int numOfDocs=0;
        double minfo;
        String path="/home/surabhi/Articles/";
        //implementation of priority queue
        /* pq = new PriorityQueue<>(10, new Comparator<TermInfo>() {
            public int compare(TermInfo lhs, TermInfo rhs) {
               if(lhs.getScore()>rhs.getScore())
                   return 1;
                if(lhs.getScore()<rhs.getScore())
                    return -1;
                else
                    return 0;
            }


        });*/
        //for Madison

        for(int i=0;i<dict.length;i++) {
            calculateItc("MADISON",dict[i],madison);
            calculateItc("HAMILTON",dict[i],hamilton);
            calculateItc("JAY",dict[i],jay);
            //N11- number of documents that contain t and are in madison
            //N10- number of documents that contain t and are not in madison
            //N01- number of documents that don't contain t and are in madison
            //N00- number of documents that don't contain t and are not in madison
            //get vocab of important terms
      //     pq.stream().forEach(val->System.out.println(val.getTerm()+" "+val.getScore()));
        /*pq.stream().forEach(val->{
            important.add(val.getTerm());
    });*/

        }
       // System.out.println(pq.size());
        //System.out.println(important.size());
        //System.out.println();
        //for each term in important vocab set compute product of p(t,c1)
       // System.out.println(important.size());
        //Collections.sort(important,Collections.revers);
        Collections.sort(important, new Comparator<TermInfo>() {

            public int compare(TermInfo o1, TermInfo o2) {
                if(o1.getScore()<o2.getScore())
                    return 1;
                if(o1.getScore()>o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });

       for(int i=0;i<25;i++)
       {
           String str=important.get(i).getTerm();
          System.out.println(str+" "+important.get(i).getScore());

           int div1=c.getHamiltonVocab().size()+10;
           int div2=c.getMadisonVocab().size()+10;
           int div3=c.getJayVocab().size()+10;

           double x=10000*(Collections.frequency(c.getHamiltonVocab(),str)+1);
           double value1=(x)/(div1);
           if(hamprob.containsKey(str))
               continue;
           hamprob.put(str,(Math.log(value1)/Math.log(2)));

           double y=10000*(Collections.frequency(c.getMadisonVocab(),str)+1);
           double value2=(y)/(div2);
           if (madprob.containsKey(str))
               continue;
           madprob.put(str,Math.log(value2)/Math.log(2));

           double z=10000*(Collections.frequency(c.getJayVocab(),str)+1);
           double value3=(y)/(div3);
           if (jayprob.containsKey(str))
               continue;
           jayprob.put(str,Math.log(value3)/Math.log(2));
        }
       // madprob.forEach((k,v)->System.out.println((k+" "+v)));
       // jayprob.forEach((k,v)->System.out.println((k+" "+v)));



    }

    private Set<String> findCommonDocuments(List<TokenDetails> postings, Set<String> file) {
        Set<String>docs=new HashSet<>();
        int doc;

        HashMap<Integer, String>filemap=c.getFileNameLists();
        String st;

        for(TokenDetails td:postings)
        {
            doc=td.getDocId();
            st=filemap.get(doc);
            if(file.contains(st))
               docs.add(st);
        }
        return docs;
    }
   /* private boolean isTermInDoc(String term,String str){

        try (BufferedReader br = new BufferedReader(new FileReader(str))) {

            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                if(line.toLowerCase().contains(term)) {

                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        return false;
    }*/


    public void test() throws IOException
    {

        Path p1= Paths.get("/home/surabhi/Articles/HAMILTON-OR-MADISON/");
        Files.walkFileTree(p1, new SimpleFileVisitor<Path>() {
            double com1=0;
            double com2=0;
            double com3=0;
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // make sure we only process the current working

                if (p1 != null) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws FileNotFoundException {

                // only process .txt files
                if (file.toString().endsWith(".txt")) {
                    try {
                        //for the remaining 11 conflicted files
                        // Get a token stream on the file
                        TokenStream stream = new SimpleTokenStream(file.toFile());
                        while (stream.hasNextToken()) {
                            String str=index.processWord(stream.nextToken());
                            //for each term
                            com1+=hamprob.containsKey(str)?hamprob.get(str):0;

                            com2+=madprob.containsKey(str)?madprob.get(str):0;
                            com3+=jayprob.containsKey(str)?jayprob.get(str):0;



                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                com1=com1+Math.log(1+(hamilton.size()/74));
                com2=com2+Math.log(1+(madison.size()/74));
                com3=com3+Math.log(1+(jay.size()/74));

               // System.out.println(com1+" "+com2);
                if(com1>com2&&com1>com3)
                    System.out.println(file.getFileName()+" is written by "+"Hamilton");
                if(com2>com1&&com2>com3)
                    System.out.println(file.getFileName()+" is written by "+"Madison");
                if(com3>com1&&com3>com2)
                   System.out.println(file.getFileName()+" is written by "+"Jay");
                return FileVisitResult.CONTINUE;
        }

        });

        }

}


