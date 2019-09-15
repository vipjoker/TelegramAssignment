package vipjokerstudio.com.telegramchart.util;

public class FormatUtil {
    public static  String formatNumber(float num){


        int rank = (int) Math.log10(num);

        if(rank > 3  && rank<= 6){

            return String.format("%.1fK",num/1000);
        }else if(rank > 6 && rank < 9){
            return String.format("%.1fM",num/1000000);
        }else{

            return String.format("%.2f",num);
        }

    }
}
