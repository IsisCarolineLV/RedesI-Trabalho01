package modelo;

public interface Conversor{
  public static String converteAscII(char caracter){
    String a = Integer.toBinaryString(caracter);
    while(a.length()<8)
      a = 0+a;
    return a;
  }
  public static char desconverteAscII(String charEmBinario){
    char caracter = (char) Integer.parseInt(charEmBinario, 2);
    return caracter;
  }
}