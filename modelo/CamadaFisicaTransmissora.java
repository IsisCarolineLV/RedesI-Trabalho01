/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/08/2025
* Ultima alteracao.: 28/08/2025
* Nome.............: CamadaFisicaTransmissora.java
* Funcao...........: Essa camada eh responsavel por codificar o 
sinal na codificacao escolhida (binario, manchester ou manchester
diferencial) e enviar a mensagem escolhida para o meio de comunicacao
*************************************************************** */
package modelo;

public class CamadaFisicaTransmissora {

  private MeioDeComunicacao meioDeComunicacao;
  private int tipoDeCodificacao;
  private MeioDeComunicacao copiaMeioDeComunicacao;

  //Construtor
  public CamadaFisicaTransmissora(MeioDeComunicacao meioDeComunicacao, int tipoDeCodificacao) {
    this.meioDeComunicacao = meioDeComunicacao;
    this.tipoDeCodificacao = tipoDeCodificacao;
  }

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissora
   * Funcao: verifica qual a codificacao escolhida e chama o metodo
   * equivalente a essa escolha, por fim envia o fluxoBrutoDeBits 
   * codificados para o meioDeComunicacao
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: vazio
   * ****************************************************************/
  public void camadaFisicaTransmissora(int quadro[]) {
    //tipoDeCodificacao = 2; // alterar de acordo o teste
    int fluxoBrutoDeBits[] = new int[quadro.length]; // ATENÇÃO: trabalhar com BITS!!!
    switch (tipoDeCodificacao) {
      case 0: // codificao binaria
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchester(quadro);
        break;
      case 2: // codificacao manchester diferencial
         fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        break;
    }// fim do switch/case
    copiaMeioDeComunicacao = meioDeComunicacao.clone();
    copiaMeioDeComunicacao.comunicar(fluxoBrutoDeBits);
  }// fim do metodo CamadaFisicaTransmissora

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoBinaria
   * Funcao: nao muda nada no quadro original entao apenas o retorna
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado)
   * ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) {
    System.out.println("Original:\n" + imprimirVetor(quadro));
    return quadro;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoBinaria

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchester
   * Funcao: para cada 0 escreve 01 e para cada 1 escreve 10
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado em Manchester)
   * ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchester(int quadro[]) {
    int[] quadroManchester = new int[quadro.length * 2];
    int cont = 0, k = 31;
    for (int i = 0; i < quadro.length; i++) {
      k = 31;
      quadroManchester[cont] = 0;
      for (int j = 31; j >= 0; j--) {
        if (j == 15) {
          cont++;
          quadroManchester[cont] = 0;
        }
        int mascara = 1 << j;
        if ((mascara & quadro[i]) != 0) { //se for 1 escreve 10
          quadroManchester[cont] = (quadroManchester[cont] | 1 << k);
          k -= 2; //pula a posicao do '1' e do '0'
          
        } else {  //se for 0 escreve 01
          k--;  //pula a posicao do '0'
          quadroManchester[cont] = (quadroManchester[cont] | 1 << k);
          k--;  //pula a posicao do '1'
        }
      }
      cont++;
    }
    
    // impressao para eu acompanhar: (PARTE EXCLUIVEL)
    System.out.println("Original:\n" + imprimirVetor(quadro));
    System.out.println("Codificado:\n" + imprimirVetor(quadroManchester));
    //PARTE EXCLUIVEL
    
    return quadroManchester;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoManchester

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchesterDiferencial
   * Funcao: a variavel booleana sinalAtual representa a polaridade
   * do sinal, quando ele eh verdadeiro escreve 10, quando ele eh falso
   * escreve 01. Antes de escrever esses valores no novo array, porem,
   * caso ele veja um 0 o valor verdade de sinalAtual eh invertido e
   * caso ele veja 1, o sinalAtual permanece o mesmo
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado em Manchester Diferencial)
   * ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) {
    // impressao para eu acompanhar: (PARTE EXCLUIVEL)
    System.out.println("Original:\n" + imprimirVetor(quadro));
    //PARTE EXCLUIVEL
    
    int[] quadroManchesterDiferencial = new int[quadro.length*2];
    boolean sinalAtual = true; // Começa com nível alto
    int k, cont=0;
    for(int i=0; i<quadro.length; i++){
      quadroManchesterDiferencial[cont]=0;
      k=31;
      for(int j=31; j>=0; j--){
        if(j==15) {cont++;k=31;}
        int mascara = 1 << j;
        boolean novoBit = (mascara & quadro[i])!=0; //eh 1
        
        /*se foi zero trocou vai ter mudado entao ele vai imprimir o 
        oposto do que estava antes. Ja se nao foi 0, entao trocou segue 
        com o mesmo valor verdade, portanto o sinal mantem seu fluxo:*/
        // Manchester Diferencial: bit 0 = transição, bit 1 = mantém
        if (!novoBit) { // Se for 0, faz transição
            sinalAtual = !sinalAtual;
        }
        if (!sinalAtual) { //se for 0 escreve 01
          k--;  //pula a posicao do '0'
          quadroManchesterDiferencial[cont] |= 1 << k;
          k--;  //pula a posicao do '1'
          
        } else {  //se for 1 escreve 10
          quadroManchesterDiferencial[cont] |= 1 << k;
          k -= 2; //pula a posicao do '1' e do '0'
        }
      } //fim do for interno (passando pelos 31 bits do inteiro
      cont++;
    } //fim do for externo (passando por cada inteiro do array)
    System.out.println("Codificado:\n" + imprimirVetor(quadroManchesterDiferencial));
    //transforma o sinal codificado para codificacao manchester
    return quadroManchesterDiferencial;
  }// fim do CamadaFisicaTransmissoraCodificacaoManchesterDiferencial
  
  /* ***************************************************************
   * Metodo: parar
   * Funcao: chama o metodo para matar a Copia do MeioDeComunicacao
   * Parametros: nenhum
   * Retorno: vazio
   * ****************************************************************/
  public void parar(){
    if(copiaMeioDeComunicacao!=null && copiaMeioDeComunicacao.isAlive())
      copiaMeioDeComunicacao.matarThread();
  } //fim do metodo parar

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
} //fim da classe camadaFisicaTransmissora