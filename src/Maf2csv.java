import java.io.*;
import java.util.ArrayList;

public class Maf2csv {

    public static void maf2CSV(String infile, String outfile, String infileS, String sub) throws IOException {
//    public static void main (String[] args) throws IOException {
//        String filePath = "/Users/jiayudong/Desktop/";
//        String infile = filePath + "test.maf";
//        String outfile = filePath + "out.txt";
        BufferedReader br;
        BufferedWriter out;
        if (infile.endsWith("gz" )) {
            br = new BufferedReader(IOutils.getTextGzipReader(infile));
        } else {
            br = new BufferedReader(IOutils.getTextReader(infile));
        }
        if (outfile.endsWith("gz")) {
            out = new BufferedWriter(IOutils.getTextGzipWriter(outfile));
        } else {
            out = new BufferedWriter(IOutils.getTextWriter(outfile));
        }

        int count = 0;
        String temp = null;
        StringBuilder strb = new StringBuilder(65536);
        ArrayList<String> lineage = IOutils.getLineageSpecies(infileS);
        while ((temp = br.readLine()) != null) {
            if (temp.startsWith("s") || temp.equals("")) {
                String [] str1;
                ArrayList<String> AL = new ArrayList<>();
                if (temp.startsWith("s")) {
                    String[] tem = temp.split("\\s+");
                    String[] te = tem[1].split("\\.");
                    int gap = tem[6].length() - tem[6].replace("-","").length();
                    if (temp.contains("traes"+ sub)) {
                        int end = Integer.parseInt(tem[2]) + Integer.parseInt(tem[3]);
                        strb.append(te[1]).append("\t").append(tem[2]).append("\t").append(Integer.toString(end)).append("\t").append(Integer.toString(tem[6].length())).append("\t")
                                .append("traes").append(sub).append("\t").append(tem[3]).append("\t").append(Integer.toString(gap)).append("\t");
                    } else {
                        strb.append(te[0]).append("\t").append(tem[3]).append("\t").append(Integer.toString(gap)).append("\t");
                    }
                } else {
                    count ++;
                    str1 = strb.toString().split("\t");
                    int gapCounts = 0;
                    for (int j = 0; j < str1.length; j++) {
                        if (j >= 6 && (j - 6) % 3 == 0) {
                            gapCounts += Integer.parseInt(str1[j]);
                        } else {
                            AL.add(str1[j]);
                        }
                    }
                    AL.add(4, Integer.toString(gapCounts));

                    strb.delete(0,strb.length());
                    ArrayList<String> al = new ArrayList<>();
                    int blockSize = 0;
                    al.add(AL.get(0));
                    al.add(AL.get(1));
                    al.add(AL.get(2));
                    al.add(AL.get(3));
                    al.add(AL.get(4));
                    for (int i = 0; i < lineage.size(); i++) {
                        int index = 2 * (lineage.indexOf(lineage.get(i))) + 5;
                        if (AL.contains(lineage.get(i))) {
                            blockSize++;
                            int original = AL.indexOf(lineage.get(i));
                            al.add(index, AL.get(original));
                            al.add(index + 1, AL.get(original + 1));
                        } else {
                            al.add(index, lineage.get(i));
                            al.add(index + 1, "NA");
                        }
                    }
                    al.add(5, Integer.toString(blockSize));
                    if (count == 1) {
                        strb.append("Chr").append("\t").append("Start(inclusive)").append("\t").append("End(exclusive)").append("\t")
                                .append("BlockLength").append("\t").append("GapCounts(all)").append("\t").append("BlockSize").append("\t");
                        for (int i = 0; i < lineage.size(); i++) {
                            strb.append(lineage.get(i)).append("\t").append("AlignedBasesNumber").append("\t");
                        }
                        strb.append("\n");
                        System.out.println("Starting processing, please be patient!");
                    }
                    for (String i : al) {
                        strb.append(i).append("\t");
                    }
                    strb.append("\n");
                    al.clear();
                    out.write(strb.toString());
                    strb.delete(0,strb.length());

                    if (count % 500000 == 0) {
                        System.out.println("Processing" + " " + count + " " + "blocks :)");
                    }

                }

            }

        }

        out.flush();
        out.close();
    }

    public static void main(String[] args) throws IOException {
        maf2CSV(args[0],args[1],args[2],args[3]);

    }

}
