package org.example;
import java.util.*;

public class Call {
    String Tariff;
    String CallType;
    String StartTime;
    String EndTime;
    Double Duration;
    Double Cost;
    public Call(String Tariff,String CallType, String StartTime, String EndTime, Double Duration, Double Cost){
        this.Tariff = Tariff;
        this.CallType = CallType;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.Duration = Duration;
        this.Cost = Cost;
    }
}
