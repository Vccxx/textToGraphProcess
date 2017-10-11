import java.io.*;
import java.util.*;
//change B2
public class GraphTest  {
    final static int bufSize = 1024;
    static String buf = "";
    static String path = "C:\\Users\\18965\\Desktop\\softwareEngineer\\lab1\\";
    static String libPath = "C:\\Users\\18965\\Desktop\\softwareEngineer\\lab1\\Graphviz2.38\\bin\\";
    static int[][] tempWeight= new int[bufSize][bufSize];
    private static String ReadFile(String fileName){
        Reader reader = null;
        try {
            // 一次读多个字符
            int tempchars;
            reader = new InputStreamReader(new FileInputStream(fileName));
            String tempBuf = "";
            // 读入多个字符到字符数组中，charread为一次读取字符数
            int flag = 0 ;
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
                    System.out.println("File closed");
                    return buf;
                } catch (IOException e1) {
                    System.out.println(e1);
                    return "";
                }
            }
        }
    }
    private static int GenerateGraph(String[] proc){
        Writer output = null;
        Map m = new HashMap();
        try{
            output = new OutputStreamWriter(new FileOutputStream(path+"out.dot"));
            String dotout = "digraph abc{\n";
            for(int i = 0; i < proc.length;i++) {
                dotout += "\t" + proc[i] + ";\n";
            }
            int seq = 0;
            for(int i = 0; i<proc.length;i++){//每个字符串号
                System.out.println(proc[i]+":"+m.containsKey(proc[i]));
                if(!m.containsKey(proc[i])){
                    m.put(proc[i],seq++);
                }
            }
            System.out.println(m.toString());
            for(int i = 0;i<proc.length-1;i++){//统计权值
                tempWeight[(int)m.get(proc[i])][(int)m.get(proc[i+1])] += 1;
            }
            for(Object s : m.keySet()){
                for(Object s1:m.keySet()){
                    if(tempWeight[(int)m.get(s)][(int)m.get(s1)] != 0){
                        dotout+="\t"+s+"->"+s1+"[label=\""+String.valueOf(tempWeight[(int)m.get(s)][(int)m.get(s1)])+"\"]"+";\n";
                    }
                }
            }
            dotout+="}";
            output.write(dotout);
            output.close();
        }
        catch(Exception e){
            System.out.println(e);
            return 0;
        }

        try{
            String[] cmd = {"cmd.exe","/C","start "+libPath+"dot "+path+"out.dot -T png -o "+path+"out.png"};
            Process pro = Runtime.getRuntime().exec(cmd);
        }
        catch(Exception e){
            System.out.println(e);
            return 0;
        }
        return 1;
    }
    public static void main(String[] args) {
        String buf = ReadFile("C:\\Users\\18965\\IdeaProjects\\graphTest\\src\\test.txt");
        String[] procBuf = buf.split(" ");
        GenerateGraph(procBuf);
        System.out.println(buf);
    }
}
class Vertex{
    String s;
    int index;
    public Vertex(String contain,int index){
        this.s = contain;
        this.index = index;
    }
}
