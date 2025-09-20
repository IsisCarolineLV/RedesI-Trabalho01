/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/08/2025
* Ultima alteracao.: 28/08/2025
* Nome.............: CamadaFisicaReceptora.java
* Funcao...........: Essa camada eh responsavel por decodificar o 
sinal recebido em binario comum a partir da codificacao escolhida 
(binario, manchester ou manchester diferencial) e enviar a mensagem 
escolhida para a CamadaAplicacaoReceptora
****************************************************************/
package modelo;

import javafx.scene.image.ImageView;
import java.util.Arrays;

public class CamadaFisicaReceptora{
  
  private CamadaAplicacaoReceptora camada_Aplicacao_Receptora;
  private int tipoDeDecodificacao;
  
  //Construtor
  public CamadaFisicaReceptora(CamadaAplicacaoReceptora camada_Aplicacao_Receptora, int tipoDeDecodificacao, ImageView imagemImpressora){
    this.camada_Aplicacao_Receptora = camada_Aplicacao_Receptora;
    this.tipoDeDecodificacao = tipoDeDecodificacao;
    //fazer animacao da impressora
  }
  
  /* ***************************************************************
   * Metodo: camadaFisicaReceptora
   * Funcao: verifica qual a codificacao escolhida e chama o metodo
   * equivalente a essa escolha, por fim envia o fluxoBrutoDeBits 
   * codificados para a camadaAplicacaoReceptora
   * Parametros: int[] (mensagem codificada no tipo escolhido)
   * Retorno: vazio
   * ****************************************************************/
  public void camadaFisicaReceptora (int quadro[]) {
    int fluxoBrutoDeBits [] = new int[(int) quadro.length/2]; 
    switch (tipoDeDecodificacao) {
      case 0 : //codificao binaria
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoBinaria(quadro);
        break;
      case 1 : //codificacao manchester
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchester(quadro);
        break;
      case 2 : //codificacao manchester diferencial
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
        break;
    }//fim do switch/case
    System.out.println("Decodificado:\n"+imprimirVetor(fluxoBrutoDeBits));
    //chama proxima camada
    camada_Aplicacao_Receptora.camadaAplicacaoReceptora(fluxoBrutoDeBits);
  }//fim do metodo CamadaFisicaTransmissora
  
  /* ***************************************************************
   * Metodo: camadaFisicaReceptoraDecodificacaoBinaria
   * Funcao: a mensagem ja esta em AscII binario, entao apenas se
   * retorna o quadro enviado como parametro
   * Parametros: int[] (mensagem codificada em binario)
   * Retorno: int[] (mensagem codificada em binario)
   * ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoBinaria (int quadro []) {
    return quadro;
  }//fim do metodo CamadaFisicaReceptoraDecodificacaoBinaria

  /* ***************************************************************
   * Metodo: camadaFisicaReceptoraDecodificacaoManchester
   * Funcao: verifica os bits de 2 em 2, se achar a sequencia 10
   * escreve 1 na posicao (iteracao) em que ele foi achado no Array
   * quadroDecodificado
   * Parametros: int[] (mensagem codificada em manchester)
   * Retorno: int[] (mensagem codificada em binario)
   * ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoManchester (int quadro []) {
    int cont = 0, k=31;
    int[] quadroDecodificado = new int[(int)quadro.length/2];
    for(int i=0; i<quadroDecodificado.length;i++){
      k=31;
      quadroDecodificado[i] =0;  //zera todas as posicoes
      for(int j=31; j>=0 ; j--){
        if(j==15) {cont++; k=31;} //cada quadroDecodificado[] guarda 4 caracteres 
                                  //entao na metade k volta pro final
        //se o par for 10 entao escreve um 1
        if(((quadro[cont] & 1 << k) != 0) && ((quadro[cont] & 1 << (k-1))==0))
          quadroDecodificado[i]= (quadroDecodificado[i] | 1 << j);
        k-=2; //verifica o proximo par
      }
      cont++; //
    }
    return quadroDecodificado;
  }//fim do metodo CamadaFisicaReceptoraDecodificacaoManchester

  
  /* ***************************************************************
   * Metodo: CamadaFisicaReceptoraDecodificacaoManchesterDiferencial
   * Funcao: verifica os bits de 2 em 2, se ouve mudanca de um bit 
   * para o outro escreve 0, se nao escreve 1
   * Parametros: int[] (mensagem codificada em manchester diferencial)
   * Retorno: int[] (mensagem codificada em binario)
   * ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoManchesterDiferencial(int quadro[]){
    boolean estadoAnterior = true; //to usando boolean pq int ia embolar tudo  0=false e 1=true
    int[] quadroDecodificado = new int[(int)quadro.length/2];
    int cont=0;
    int k=31;
    for(int i=0; i<quadro.length; i++){
      for(int j=31; j>=0; j-=2){
        int mascara = 1<<j;
        boolean bitNovo = (quadro[i] & mascara)!=0;
        if(estadoAnterior==bitNovo){
          quadroDecodificado[cont] |= 1<<k;
        }
        estadoAnterior=bitNovo;
        k--;
      }
      if(k<0){
        k=31;
        cont++;
      }
    }
    
    return quadroDecodificado;
  }//fim do CamadaFisicaReceptoraDecodificacaoManchesterDiferencial

  /************************************ METODOS AUXILIARES ************************/
  public String imprimirBinario(int teste) {
    String a ="";
    for (int i = 31; i >= 0; i--) { // do bit mais alto para o mais baixo
      int mascara = 1 << i;
      if ((teste & mascara) != 0) {
        a+="1";
      } else {
        a+="0";
      }
    }
    a+="\n";
    return a;
  }
  
  public String imprimirVetor(int[] vetor){
    String a = "";
    for(int i: vetor){
      a+=imprimirBinario(i);
    }
    return a;
  }
  
} //fim da classe CamadaFisicaReceptora