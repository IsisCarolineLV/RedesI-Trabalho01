package modelo;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;

public class MeioDeComunicacao extends Thread {
  private Image imagensSinal[] = new Image[7];
  private ImageView[] sinalMostrado;
  private int[] sinal;
  private int[] fluxoBrutoDeBits;
  private int tipoDeCodificacao;
  private float porcentagem;
  private CamadaFisicaReceptora camada_Fisica_Receptora;
  private volatile boolean interrompido = false;
  private ProgressBar barraDeProgresso;

  public MeioDeComunicacao(int tipoDeCodificacao, CamadaFisicaReceptora camada_Fisica_Receptora,
      ImageView[] sinalMostrado,  ProgressBar barraDeProgresso) {
    imagensSinal[0] = new Image("/imagens/0.png");
    imagensSinal[1] = new Image("/imagens/1.png");
    imagensSinal[2] = new Image("/imagens/t.png");
    imagensSinal[3] = null;
    imagensSinal[4] = new Image("/imagens/0m.png");
    imagensSinal[5] = new Image("/imagens/1m.png");
    imagensSinal[6] = new Image("/imagens/tm.png");
    if(tipoDeCodificacao==0) this.tipoDeCodificacao = 0;
    else this.tipoDeCodificacao =1;
    
    sinal = new int[(this.tipoDeCodificacao+1)*8];
    for(int s: sinal){
      s=3;
    }
    this.camada_Fisica_Receptora = camada_Fisica_Receptora;
    this.barraDeProgresso = barraDeProgresso;
    this.barraDeProgresso.setVisible(false);
    this.sinalMostrado = sinalMostrado;
    this.setDaemon(true);
  }

  public MeioDeComunicacao clone() {
    return new MeioDeComunicacao(tipoDeCodificacao, camada_Fisica_Receptora,sinalMostrado, barraDeProgresso);
  }

  public void comunicar(int[] fluxoBrutoDeBits) {
    this.fluxoBrutoDeBits = fluxoBrutoDeBits;
    barraDeProgresso.setVisible(true);
    this.start();
  } // fim do metodo comunicar

  public void run() {
    int[] fluxoBrutoDeBitsTransmissor = fluxoBrutoDeBits;
    int[] fluxoBrutoDeBitsReceptor = new int[fluxoBrutoDeBits.length];
    for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
      int inteiroCopia = 0;
      int mascara = 0;
      if(interrompido) return;
      for (int j = 31; j >= 0; j--) {
        mascara = 1 << j;
        if(interrompido) return;
        else porcentagem = ((i*32+(32-j))*100/(fluxoBrutoDeBits.length*32));
        if ((mascara & fluxoBrutoDeBitsTransmissor[i]) != 0) {
          inteiroCopia = inteiroCopia | mascara;
          atualizaSinalMostrado(1 + (tipoDeCodificacao*4));
        } else {
          atualizaSinalMostrado(0 + (tipoDeCodificacao*4));
        }
      }
      fluxoBrutoDeBitsReceptor[i] = inteiroCopia;
    }
    
    for(int i=0; i<(tipoDeCodificacao+1)*8 ; i++){
      atualizaSinalMostrado(3);
    }
 
    barraDeProgresso.setVisible(false);
    porcentagem=0;
    camada_Fisica_Receptora.camadaFisicaReceptora(fluxoBrutoDeBitsReceptor);
  }

  public void atualizaSinalMostrado(int bitNovo) {
    if(interrompido) return;
    for (int i = sinal.length - 1; i >= 0; i--) {
      if(interrompido) return;
      if (i != 0) {
        sinal[i] = sinal[i - 1];
      } else {
        sinal[0] = bitNovo;
      } // fim do if-else
      /*if (i < sinal.length - 1) {
        if (sinal[i] != sinal[i + 1] && (sinal[i] != 3))
          transicoes[i] = true;
        else
          transicoes[i] = false;
      }*/

    } // fim do for

    mostrarSinalNaTela();

  } // fim do metodo atualizaSinalMostrado

  public void mostrarSinalNaTela() {
    if(interrompido) return;
    for (int i = 0; i < 8*(tipoDeCodificacao+1); i++) {
      if(interrompido) return;
      int index = i;
      Platform.runLater(() -> {
        sinalMostrado[index].setImage(imagensSinal[sinal[index]]);
        /*if (index < 4)
          sinalMostrado[index + 5].setVisible(transicoes[index]);*/
        barraDeProgresso.setProgress(porcentagem/100);
      });
      
    }
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      return; // sai do método se a thread foi interrompida
    }
  }

  public void matarThread(){
    barraDeProgresso.setProgress(0);
    barraDeProgresso.setVisible(false);
    for(int s=0; s<sinal.length; s++){
      int index=s;
      Platform.runLater(() -> {
        sinalMostrado[index].setImage(imagensSinal[3]);
      });
    }
    
    interrompido = true;
    this.interrupt(); // acorda a thread se estiver dormindo
  }

  /************************************ METODO AUXILIAR ************************/
  public void imprimirBinario(int teste) {
    for (int i = 31; i >= 0; i--) { // do bit mais alto para o mais baixo
      int mascara = 1 << i;
      if ((teste & mascara) != 0) {
        System.out.print("1");
      } else {
        System.out.print("0");
      }
    }
    System.out.println(); // quebra de linha no final
  }
}