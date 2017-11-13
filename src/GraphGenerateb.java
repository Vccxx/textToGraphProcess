import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphGenerate {
    private final static int bufSize = 1024;
    private static int numOfwords = 1024;
    private static String buf = "";
    private static String path = "./";
    private static String libPath = "./bin/";
    private static int[][] graphOfWords = new int[bufSize][bufSize];
    private static int INF = 1000000000;
    private static Map m;
    private static int[][] minRouteMatrix = new int[bufSize][bufSize];
    private static int[][] pathMatrix = new int[bufSize][bufSize];
    private static void Floyd() {
        //鍒濆鍖栨渶鐭矾寰勬暟缁�
        for (int i = 0; i < numOfwords; i++) {
            for (int j = 0; j < numOfwords; j++) {
                if (graphOfWords[i][j] > 0) {
                    minRouteMatrix[i][j] = graphOfWords[i][j];
                } else {
                    minRouteMatrix[i][j] = INF;
                }
            }
        }
        //寮�濮嬭绠楁渶鐭矾寰勫苟涓旇褰曡矾寰�
        for (int i = 0; i < numOfwords; i++) {
            for (int j = 0; j < numOfwords; j++){
                for (int k = 0; k < numOfwords; k++) {
                    if (minRouteMatrix[i][k] + minRouteMatrix[k][j] < minRouteMatrix[i][j]) {
                       minRouteMatrix[i][j] = minRouteMatrix[i][k] + minRouteMatrix[k][j];
                       pathMatrix[i][j] = k;
                    }
                }
            }
        }
    }
    private static String ReadFile(String fileName) {
        Reader reader = null;
        try {
            // 涓�娆¤澶氫釜瀛楃
            int tempchars;
            reader = new InputStreamReader(new FileInputStream(fileName));
            String tempBuf = "";
            // 璇诲叆澶氫釜瀛楃鍒板瓧绗︽暟缁勪腑锛宑harread涓轰竴娆¤鍙栧瓧绗︽暟
            int flag = 0;
            while ((tempchars = reader.read()) != -1) {
                if ((tempchars >= 97 && tempchars <= 122) || (tempchars >= 65 && tempchars <= 90)) {
                    flag = 0;
                    tempBuf += (char) tempchars;
                } else {
                    if (flag == 0) {
                        buf += tempBuf + " ";
                        tempBuf = "";
                    }
                    flag = 1;
                }
            }
            reader.close();
            return buf;
        } catch (Exception e1) {
            System.out.println(e1);
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    return buf;
                } catch (IOException e1) {
                    System.out.println(e1);
                    return "";
                }
            }
        }
    }
    public String[] queryBridgeWords(String word1,String word2,int opt){//鏌ヨ妗ユ帴璇�
        String[] bridgeWords = new String[numOfwords];
        if(!(m.containsKey(word1) || m.containsKey(word2))){
            if(opt == 0){
                System.out.println("No " + word1 + " and " + word2 + " in the graph!");
            }
            return null;
        }
        if(!(m.containsKey(word1) )){
            if(opt == 0){
                System.out.println("No "+ word1 + " in the graph!");
            }
            return null;
        }
        if(!(m.containsKey(word2))){
            if(opt == 0){
                System.out.println("No "+ word2 + " in the graph!");
            }
            return null;
        }
        int index = 0;
        int src = (int)m.get(word1);
        int dest = (int)m.get(word2);
        for(int j = 0;j < numOfwords;j++) {
            if(graphOfWords[src][j] * graphOfWords[j][dest] > 0){
                for(Object s:m.keySet()) {
                    if ((int) m.get(s) == j) {
                        bridgeWords[index++] = (String)s;
                    }
                }
            }
        }
        if(index == 0){
            if(opt == 0){
                System.out.println("No bridge words from "+word1+" to "+word2+"!");
            }
            return null;
        }
        if(opt == 0){
            System.out.print("The bridge words from \""+ word1+"\" to \""+ word2 +"\" are: ");
            for(int i = 0;i < index - 1;i++){
                System.out.print(bridgeWords[i] + ",");
            }
            System.out.println(bridgeWords[index - 1] + ".");
        }
        return bridgeWords;
    }
    public int showDirectedGraph(String[] proc) {//灞曠ず鏈夊悜鍥�
        Writer output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(path + "out.dot"));
            String dotout = "digraph dirGraph{\n";
            for (int i = 0; i < proc.length; i++) {
                dotout += "\t" + proc[i] + ";\n";
            }

            for (Object s : m.keySet()) {//鍐欏叆dot鏂囦欢
                for (Object s1 : m.keySet()) {
                    if (graphOfWords[(int) m.get(s)][(int) m.get(s1)] != 0) {
                        dotout += "\t" + s + "->" + s1 + "[label=\"" + String.valueOf(graphOfWords[(int) m.get(s)][(int) m.get(s1)]) + "\"]" + ";\n";
                    }
                }
            }
            dotout += "}";
            output.write(dotout);
            output.close();
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
        try {
            String[] cmd = {"cmd.exe", "/C", "start " + libPath + "dot " + path + "out.dot -T png -o " + path + "out.png"};
            Process pro = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
        return 1;
    }
    public String generateNewText(String inputText){//鏍规嵁bridge word鐢熸垚鏂版枃鏈�
        String temp = "";
        String result = "";
        int flag = 0;
        for(int i = 0 ;i < inputText.length();i++){
            int letter = (int)inputText.charAt(i);
            if ((letter >= 97 && letter <= 122) || (letter >= 65 && letter <= 90)) {
                flag = 0;
                temp += (char) letter;
            } else {
                if (flag == 0) {
                    temp += " ";
                }
                flag = 1;
            }
        }
        String[] tempArray = temp.split(" ");
        java.util.Random random = new java.util.Random();
        for(int i = 0;i < tempArray.length - 1;i++){
            String[] temp1 = queryBridgeWords(tempArray[i],tempArray[i + 1],1);
            if(temp1 == null){
                result += tempArray[i] + " ";
            }
            else {
                int count = 0;
                for(int m = 0;i < temp1.length;m++){
                    if (temp1[m] == null) break;
                    count++;
                }
                int rand = random.nextInt(count);
                result += tempArray[i] + " "+temp1[rand] + " ";
            }
        }
        result += tempArray[tempArray.length - 1] + ".";
        return result;
    }
    public static void clearMarked(String route,String color){
        Reader reader = null;
        int tempchars;
        String tempBuf = "";
        String[] array = route.split("->");
        try {
            // 涓�娆¤澶氫釜瀛楃
            reader = new InputStreamReader(new FileInputStream("out.dot"));
            // 璇诲叆澶氫釜瀛楃鍒板瓧绗︽暟缁勪腑锛宑harread涓轰竴娆¤鍙栧瓧绗︽暟
            int flag = 0;
            while ((tempchars = reader.read()) != -1) {
                tempBuf += (char) tempchars;
            }
        } catch (Exception e1) {
            System.out.println(e1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    System.out.println(e1);
                }
            }
        }
        for(int i = 0;i <array.length - 1;i++){
            String pattern = array[i] + "->" + array[i + 1]+"\\[color="+ color + ",";
            System.out.println(pattern);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(tempBuf);
            System.out.println(m);
            tempBuf = m.replaceFirst(array[i]+"->" + array[i+1] + "\\[");
        }
        Writer output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(path + "out.dot"));
            output.write(tempBuf);
            output.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String calcShortestPath(String word1, String word2,String color) {
    	 if(!(m.containsKey(word1) || m.containsKey(word2)))
    	 {
    		 return "No " + word1 + " and " + word2 + " in the graph!";
    	 }
    	 else if(!(m.containsKey(word1) ))
    	 {
        	 return "No "+ word1 + " in the graph!";
    	 }
    	 else if(!(m.containsKey(word2)))
    	 {
             return "No "+ word2 + " in the graph!";
    	 }
    	 else
    	 {
    		 String res = ShortestPath(word1,word2,color);
    	     //clearMarked(res,color);
    	     return res;
    	 }
    }
    
    private static String ShortestPath(String word1, String word2,String color){//璁＄畻涓や釜鍗曡瘝涔嬮棿鐨勬渶鐭矾寰�
        int[] formerPath = new int[numOfwords];
        int []latterPath = new int[numOfwords];
        int indexSrc = (int)m.get(word1);
        int indexDest = (int)m.get(word2);
        int formerIndex = pathMatrix[indexSrc][indexDest];
        int latterIndex = formerIndex;
        int t1 = 0;
        int t2 = 0;
        if(formerIndex == 0){//there is no path from word1 to word2
            return "No path from "+word1 + " to " + word2;
        }
        while(true){//鑾峰彇浠� word1->涓棿鐐� 缁忓巻鐨勬墍鏈夌偣鍒版暟缁刦ormerPath
            if(graphOfWords[indexSrc][formerIndex] != 0){//鍙竴姝ュ埌杈�
                formerPath[t1++] = formerIndex;
                break;
            }
            else{//涓嶅彲涓�姝ュ埌杈撅紝瀵绘壘涓婁竴鑺傜偣
                formerPath[t1++] = formerIndex;
                formerIndex = pathMatrix[indexSrc][formerIndex];
            }
        }
        while(true){//鑾峰彇浠� 涓棿鐐�->word2 缁忓巻鐨勬墍鏈夌偣鍒版暟缁刲atterPath
            if(graphOfWords[latterIndex][indexDest] != 0){
            	latterPath[t2] = latterIndex;
                break;
            }
            else{
                latterPath[t2++] = latterIndex;
                latterIndex = pathMatrix[latterIndex][indexDest];
            }
        }
        int[] routeArray = new int[t1 + t2];
        for(int i = 0; i < routeArray.length;i++){
            if(i < t1){
                routeArray[i] = formerPath[t1 - 1 - i];
            }
            else{
                routeArray[i] = latterPath[i - t1 + 1];
            }
        }
        String result = word1 + "->";
        for(int i = 0; i < routeArray.length;i++){
            for (Object s: m.keySet()){
                if((int)m.get(s) == routeArray[i]){
                    result += (String)s + "->";
                }
            }
        }
        result += word2;
        //System.out.println(result);
        Reader reader = null;
        int tempchars;
        String tempBuf = "";
        try {
            // 涓�娆¤澶氫釜瀛楃
            reader = new InputStreamReader(new FileInputStream("out.dot"));
            // 璇诲叆澶氫釜瀛楃鍒板瓧绗︽暟缁勪腑锛宑harread涓轰竴娆¤鍙栧瓧绗︽暟
            int flag = 0;
            while ((tempchars = reader.read()) != -1) {
                tempBuf += (char) tempchars;
            }
        } catch (Exception e1) {
            //System.out.println(e1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    //System.out.println(e1);
                }
            }
        }
        String[] tempArry = result.split("->");
        for(int i = 0;i < tempArry.length - 1;i++){
            String pattern = tempArry[i] + "->" + tempArry[i + 1]+"\\[";
            //System.out.println(pattern);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(tempBuf);
            //System.out.println(m);
            tempBuf = m.replaceFirst(tempArry[i]+"->" + tempArry[i+1] + "[color="+color+",");
        }
        Writer output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(path + "out.dot"));
            output.write(tempBuf);
            output.close();
        } catch (Exception e) {
            //System.out.println(e);
        }
        return "the shortest length from "+word1+" to " + word2  +" is :" + result;
    }
    public static String[] calcShortestPath(String word) {
        String[] result = new String[numOfwords];
        int index = 0;
        Random random = new java.util.Random();
        String[] stringChoice = {"red","yellow","green","blue"};
        String[] color = new String[numOfwords];
        for(Object s:m.keySet()){
            if ((String)s != word){
                int rand = random.nextInt(stringChoice.length);
                String c = stringChoice[rand];
                result[index] = ShortestPath(word,(String)s,c);
                color[index++]  = c;
            }
        }
        try {
            String[] cmd = {"cmd.exe", "/C", "start " + libPath + "dot " + path + "out.dot -T png -o " + path + "out.png"};
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            System.out.println(e);
        }
        for(int i = 0;i<  result.length;i++){
            clearMarked(result[i],color[i]);
        }
        return result;
    }
    public static String randomWalk(){//闅忔満娓歌蛋
        int[] resultIndex = new int[bufSize];
        String result = new String();
        java.util.Random random = new java.util.Random();
        int rand = random.nextInt(numOfwords);
        int viaIndex = 0;
        int[][] tempMatrix = new int[numOfwords][numOfwords];
        resultIndex[viaIndex++] = rand;
        while (true){
            int[] temp = new int[bufSize];//璁板綍涓�涓偣鐨勬墍鏈夊嚭杈规寚鍚戠殑鐐癸紝浠ヤ究闅忔満閫夊彇
            int index = 0;
            for(int i = 0; i < numOfwords;i++){//鎵弿褰撳墠鑺傜偣鐨勫嚭杈�
                if(graphOfWords[rand][i] != 0){
                    temp[index++]= i;
                }
            }
            if(index == 0){//鑻ュ嚭搴︿负0锛岀粓姝㈤殢鏈烘父鍔�
                break;
            }
            rand = temp[random.nextInt(index)];
            resultIndex[viaIndex++] = rand;//闅忔満閫夊彇涓�涓嚭杈�
            if(tempMatrix[resultIndex[viaIndex - 2]][resultIndex[viaIndex - 1]] == 1){//杈瑰湪涔嬪墠宸茬粡鍑虹幇杩囷紝涔熷仠姝㈡父璧�
               break;
            }
            tempMatrix[resultIndex[viaIndex- 2]][resultIndex[viaIndex - 1]] = 1;
        }
        for(int i = 0 ;i < viaIndex;i++){//灏嗙储寮曡浆鍖栦负瀛楃涓�
           for(Object s : m.keySet()){
               if((int)m.get(s) == resultIndex[i]){
                   result += (String)s;
               }
           }
           if(i != viaIndex - 1){
                   result += "->";
           }
        }
        return result;
    }

    public static void init(String[] s){
        int seq = 0;
        for (int i = 0; i < s.length; i++) {//姣忎釜瀛楃涓插彿
            if (!m.containsKey(s[i])) {
                m.put(s[i], seq++);
            }
        }
        for (int i = 0; i < s.length - 1; i++) {//缁熻鏉冨��
            graphOfWords[(int) m.get(s[i])][(int) m.get(s[i + 1])] += 1;
        }
        numOfwords = seq;
        Floyd();//浣跨敤Floyd绠楁硶鍦ㄧ▼搴忚鍏ユ枃鏈悗璁＄畻鍑烘渶鐭矾寰勶紝浠ヤ究涔嬪悗鐨勫姛鑳戒娇鐢�
    }
    public static void main(String[] args) {
        String buf = ReadFile("D:\\textToGraphProcess\\textToGraphProcess-master\\test.txt");
        String[] procBuf = buf.split(" ");
        init(procBuf);
        System.out.println(calcShortestPath("that","a","red"));
        //showDirectedGraph(procBuf);
        //queryBridgeWords("natio","any",0);
        //System.out.println(generateNewText("final place those who gave"));
        //System.out.println(calcShortestPath("lives"));
        //System.out.println(randomWalk());
    }
    
    GraphGenerate(){
    	m = new HashMap();
        String buf = ReadFile("D:\\textToGraphProcess\\textToGraphProcess-master\\test.txt");
        String[] procBuf = buf.split(" ");
        init(procBuf);
    }

}

