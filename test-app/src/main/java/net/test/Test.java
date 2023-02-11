package net.test;

public class Test {


    public static void Funca(){

     try{
      System.out.println("Hello");
      Thread.sleep(5000);
     } catch(Exception e){}

   }
     public static void main(String[] args){
        for(int i=0; i <5000; i++)Funca();
     }

}

