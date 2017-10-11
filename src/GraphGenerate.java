import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
//change B2
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
    private static Map m = new HashMap();
    private static int[][] minRouteMatrix = new int[bufSize][bufSize];
    private static int[][] pathMatrix = new int[bufSize][bufSize];
    private static void Floyd() {
        //初始化最短路径数组
        for (int i = 0; i < numOfwords; i++) {
            for (int j = 0; j < numOfwords; j++) {
                if (graphOfWords[i][j] > 0) {
                    minRouteMatrix[i][j] = graphOfWords[i][j];
                } else {
                    minRouteMatrix[i][j] = INF;
                }
            }
        }
        //开始计算最短路径并且记录路径
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
            // 一次读多个字符
            int tempchars;
            reader = new InputStreamReader(new FileInputStream(fileName));
            String tempBuf = "";
            // 读入多个字符到字符数组中，charread为一次读取字符数
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
    public String[] queryBridgeWords(String word1,String word2,int opt){//查询桥接词
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
    public int showDirectedGraph(String[] proc) {//展示有向图
        Writer output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(path + "out.dot"));
            String dotout = "digraph dirGraph{\n";
            for (int i = 0; i < proc.length; i++) {
                dotout += "\t" + proc[i] + ";\n";
            }

            for (Object s : m.keySet()) {//写入dot文件
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
    public String generateNewText(String inputText){//根据bridge word生成新文本
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
            // 一次读多个字符
            reader = new InputStreamReader(new FileInputStream("out.dot"));
            // 读入多个字符到字符数组中，charread为一次读取字符数
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
        String res = ShortestPath(word1,word2,color);
        clearMarked(res,color);
        return res;
    }
    private static String ShortestPath(String word1, String word2,String color){//计算两个单词之间的最短路径
        int[] formerPath = new int[numOfwords];
        int []latterPath = new int[numOfwords];
        int indexSrc = (int)m.get(word1);
        int indexDest = (int)m.get(word2);
        int formerIndex = pathMatrix[indexSrc][indexDest];
        int latterIndex = formerIndex;
        int t1 = 0;
        int t2 = 0;
        if(formerIndex == 0){//there is no path from word1 to word2
            System.out.println("No path from "+word1 + " to " + word2);
             return null;
        }
        while(true){//获取从 word1->中间点 经历的所有点到数组formerPath
            if(graphOfWords[indexSrc][formerIndex] != 0){//可一步到达
                formerPath[t1++] = formerIndex;
                break;
            }
            else{//不可一步到达，寻找上一节点
                formerPath[t1++] = formerIndex;
                formerIndex = pathMatrix[indexSrc][formerIndex];
            }
        }
        while(true){//获取从 中间点->word2 经历的所有点到数组latterPath
            if(graphOfWords[latterIndex][indexDest] != 0){
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
                routeArray[i] = latterPath[i];
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
        System.out.println(result);
        Reader reader = null;
        int tempchars;
        String tempBuf = "";
        try {
            // 一次读多个字符
            reader = new InputStreamReader(new FileInputStream("out.dot"));
            // 读入多个字符到字符数组中，charread为一次读取字符数
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
        String[] tempArry = result.split("->");
        for(int i = 0;i < tempArry.length - 1;i++){
            String pattern = tempArry[i] + "->" + tempArry[i + 1]+"\\[";
            System.out.println(pattern);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(tempBuf);
            System.out.println(m);
            tempBuf = m.replaceFirst(tempArry[i]+"->" + tempArry[i+1] + "[color="+color+",");
        }
        Writer output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(path + "out.dot"));
            output.write(tempBuf);
            output.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println( "the shortest length from "+word1+" to " + word2  +" is :" + minRouteMatrix[indexSrc][indexDest]);
        return result;
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
    public static String randomWalk(){//随机游走
        int[] resultIndex = new int[bufSize];
        String result = new String();
        java.util.Random random = new java.util.Random();
        int rand = random.nextInt(numOfwords);
        int viaIndex = 0;
        int[][] tempMatrix = new int[numOfwords][numOfwords];
        resultIndex[viaIndex++] = rand;
        while (true){
            int[] temp = new int[bufSize];//记录一个点的所有出边指向的点，以便随机选取
            int index = 0;
            for(int i = 0; i < numOfwords;i++){//扫描当前节点的出边
                if(graphOfWords[rand][i] != 0){
                    temp[index++]= i;
                }
            }
            if(index == 0){//若出度为0，终止随机游动
                break;
            }
            rand = temp[random.nextInt(index)];
            resultIndex[viaIndex++] = rand;//随机选取一个出边
            if(tempMatrix[resultIndex[viaIndex - 2]][resultIndex[viaIndex - 1]] == 1){//边在之前已经出现过，也停止游走
               break;
            }
            tempMatrix[resultIndex[viaIndex- 2]][resultIndex[viaIndex - 1]] = 1;
        }
        for(int i = 0 ;i < viaIndex;i++){//将索引转化为字符串
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
        for (int i = 0; i < s.length; i++) {//每个字符串号
            if (!m.containsKey(s[i])) {
                m.put(s[i], seq++);
            }
        }
        for (int i = 0; i < s.length - 1; i++) {//统计权值
            graphOfWords[(int) m.get(s[i])][(int) m.get(s[i + 1])] += 1;
        }
        numOfwords = seq;
        Floyd();//使用Floyd算法在程序读入文本后计算出最短路径，以便之后的功能使用
    }
    public static void main(String[] args) {
        String buf = ReadFile("test.txt");
        String[] procBuf = buf.split(" ");
        init(procBuf);
        //showDirectedGraph(procBuf);
        //queryBridgeWords("natio","any",0);
        //System.out.println(generateNewText("final place those who gave"));
        //System.out.println(calcShortestPath("lives"));
        //System.out.println(randomWalk());
    }
}

