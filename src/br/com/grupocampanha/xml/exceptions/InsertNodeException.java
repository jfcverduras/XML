/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupocampanha.xml.exceptions;

/**
 *
 * @author jfc
 */
public class InsertNodeException  extends RuntimeException{
  public InsertNodeException(){ 
      super("Não é possivel adicionar um node em um node de valor");
  } 
}
