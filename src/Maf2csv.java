import java.io.*;
import java.util.ArrayList;

public class Maf2csv {

    public static void maf2CSV(String infile, String outfile, String infileS, String mode) throws IOException {
//        String filePath = "/Users/jiayudong/Desktop/";
//        String infile = filePath + "test.maf";
//        String outfile = filePath + "test.txt.gz";
//        BufferedReader br = new BufferedReader(IOutils.getTextReader(infile));
        BufferedReader br;
        if (infile.endsWith("gz" )) {
            br = new BufferedReader(IOutils.getTextGzipReader(infile));
        } else {
            br = new BufferedReader(IOutils.getTextReader(infile));
        }
        BufferedWriter out = new BufferedWriter(IOutils.getTextGzipWriter(outfile));

        int a = 0;
        String temp = null;
        StringBuilder strb = new StringBuilder(1024);
        while ((temp = br.readLine()) != null) {
            if (temp.startsWith("s") || temp.equals("")) {
                if (temp.startsWith("s")) {
                    String[] tem = temp.split("\\s+");
                    String[] te = tem[1].split("\\.");
                    int gap = tem[6].length() - tem[6].replace("-","").length();
                    if (temp.contains("traes"+mode)) {
                        int stop = Integer.parseInt(tem[2]) + Integer.parseInt(tem[3]);
                        strb.append(te[1]).append("\t").append(tem[2]).append("\t").append(Integer.toString(stop)).append("\t").append(Integer.toString(tem[6].length())).append("\t")
                                .append("traes").append(mode).append("\t").append(tem[3]).append("\t").append(Integer.toString(gap)).append("\t");
//                        a++;
                    } else {
                        strb.append(te[0]).append("\t").append(tem[3]).append("\t").append(Integer.toString(gap)).append("\t");
                    }
                } else { strb.append("\n"); }
            }
        }
        String[] str1 = strb.toString().split("\n");

        ArrayList<String> AL1 = new ArrayList<>();
        ArrayList<ArrayList<String>> AL2 = new ArrayList<>();
        int gapCounts = 0;
        for (String value : str1) {
            String[] str2 = value.split("\t");
            for (int j = 0; j < str2.length; j++) {
                if (j >= 6 && (j - 6) % 3 == 0) {
                    gapCounts += Integer.parseInt(str2[j]);
                } else {
                    AL1.add(str2[j]);
                }
            }
            AL1.add(4, Integer.toString(gapCounts));
            AL2.add(AL1);
            gapCounts = 0;
            AL1 = new ArrayList<>();
        }
//        System.out.println(strb);
//        for (int m = 0; m < AL2.size(); m++) {
//            System.out.println(AL2.get(m));
//        }

        strb.delete(0,strb.length());
        ArrayList<String> lineage = IOutils.getLineageSpecies(infileS);
        ArrayList<String> al = new ArrayList<>();
        for (ArrayList<String> strings : AL2) {
            int blockSize = 0;
            al.add(strings.get(0));
            al.add(strings.get(1));
            al.add(strings.get(2));
            al.add(strings.get(3));
            al.add(strings.get(4));
            for (int i = 0; i < lineage.size(); i++) {
                int index = 2 * (lineage.indexOf(lineage.get(i))) + 5;
                if (strings.contains(lineage.get(i))) {
                    blockSize++;
                    int original = strings.indexOf(lineage.get(i));
                    al.add(index, strings.get(original));
                    al.add(index + 1, strings.get(original + 1));
                } else {
                    al.add(index, lineage.get(i));
                    al.add(index + 1, "NA");
                }
            }
            al.add(5, Integer.toString(blockSize));
            for (String s : al) {
                strb.append(s).append("\t");
            }
            al.clear();
            strb.append("\n");
        }

//        System.out.println(strb);
        out.write(strb.toString());
        out.flush();
        out.close();

    }

    public static void main(String[] args) throws IOException {
        maf2CSV(args[0],args[1],args[2],args[3]);
    }

}
