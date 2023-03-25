package org.example;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {

        File cdrFile = new File("src\\main\\java\\org\\example\\cdr.txt");
        FileReader reader = new FileReader(cdrFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        long seconds;
        double AllMinutes = 0,totalCost = 0, minutes = 0, cost = 0;
        String number = "";
        Date startTimeOld,endTimeOld, durationOldFormat;
        String startTimeNew,endTimeNew, durationNewFormat;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        ArrayList<Call> AllCalls = new ArrayList<>();

        HashMap<String, ArrayList<Call>> numberInfo = new HashMap<>();
        HashMap<String,Double> TotalCallsMinutes = new HashMap<>();


        for(int j = 0; j < Files.lines(Paths.get("src\\main\\java\\org\\example\\cdr.txt")).count();j++) {
            String str_cdr = bufferedReader.readLine();

            String[] Arr = str_cdr.split(", ");

            long t1 = formatter.parse(Arr[2]).getTime();
            long t2 = formatter.parse(Arr[3]).getTime();
            seconds = (t2 - t1)/1000;

            minutes = seconds/60 + (seconds%60)*0.01;

            number = Arr[1];

            if (TotalCallsMinutes.containsKey(number)) {
                AllMinutes = 0;
                AllMinutes = TotalCallsMinutes.get(number);
                AllMinutes += minutes;
                TotalCallsMinutes.put(number, AllMinutes);
            } else {
                AllMinutes = 0;
                AllMinutes += minutes;
                TotalCallsMinutes.put(number, AllMinutes);

            }
            switch (Arr[4]) {
                case "03":
                    cost += minutes * 1.5;
                    break;
                case "06":
                    if(AllMinutes <= 300){
                        cost = 0;
                    }
                    else {
                        if(AllMinutes - minutes < 300){
                            cost = (AllMinutes - 300)*1 + 100;
                        }
                        else {
                            cost = minutes*1;
                        }
                    }
                    break;
                case "11":
                    if (Arr[0].equals("01")) {
                        if(AllMinutes <= 100){
                            cost = minutes * 0.5;
                        }
                        else{
                            if(AllMinutes - minutes < 100){
                                cost = (AllMinutes - 100)*1.5 + (minutes - AllMinutes + 100)*0.5;
                            }
                            else {
                                cost = minutes*1.5;
                            }
                        }
                    }
                    else {
                        cost += 0;
                    }
                    break;
            }

            Call newCall = new Call(Arr[4],Arr[0],Arr[2],Arr[3], minutes, cost);
            ArrayList<Call> AllCallsNew = new ArrayList<>();
            if(numberInfo.get(number) != null) {
                AllCallsNew = numberInfo.get(number);
                AllCallsNew.add(newCall);
                numberInfo.put(number, AllCallsNew);
            }
            else{
                AllCallsNew.add(newCall);
                numberInfo.put(number, AllCallsNew);
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat( "###.##" );
        SimpleDateFormat oldformat = new SimpleDateFormat("yyyymmddHHmmss");
        SimpleDateFormat newformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        SimpleDateFormat durationOld = new SimpleDateFormat("mm.ss");
        SimpleDateFormat durationnew = new SimpleDateFormat("HH:mm:ss");
        for ( Map.Entry<String, ArrayList<Call>> entry : numberInfo.entrySet()) {
            String key = entry.getKey();
            ArrayList<Call> arrcalls = entry.getValue();
            String filepath = "reports\\" + key + ".txt";
            File ans = new File(filepath);
            StringBuilder text = new StringBuilder("Tariff index: " + arrcalls.get(0).Tariff);
            text.append("\n----------------------------------------------------------------------------\n");
            text.append("Report for phone number: ").append(key).append(":\n");
            text.append("----------------------------------------------------------------------------\n");
            text.append("| Call Type |   Start Time        |     End Time        | Duration | Cost  |\n");
            text.append("----------------------------------------------------------------------------\n");
            totalCost = 0;
            for (Call call : arrcalls) {
                startTimeOld = oldformat.parse(call.StartTime);
                startTimeNew = newformat.format(startTimeOld);
                endTimeOld = oldformat.parse(call.EndTime);
                endTimeNew = newformat.format(endTimeOld);
                durationOldFormat = durationOld.parse(String.valueOf(call.Duration));
                durationNewFormat = durationnew.format(durationOldFormat);
                text.append("|    ").append(call.CallType).append("     | ").append(startTimeNew).append(" | ").append(endTimeNew).append(" | ").append(durationNewFormat).append(" | ").append(decimalFormat.format(call.Cost)).append(" |\n");
                totalCost += call.Cost;
            }
            text.append("----------------------------------------------------------------------------\n");
            text.append("|                                           Total Cost: |\t"+ decimalFormat.format(totalCost) + " rubles |\n");
            text.append("----------------------------------------------------------------------------\n");
            FileWriter writer = new FileWriter(ans);
            writer.write(text.toString());
            writer.close();
        }
    }
}