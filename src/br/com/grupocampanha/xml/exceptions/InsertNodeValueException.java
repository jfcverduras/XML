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
public class InsertNodeValueException extends RuntimeException {
    public InsertNodeValueException(){
    super("Não e possivel adicionar um valor em um node contendo nodes");
    }

    public InsertNodeValueException(String s) {
        super (s);//To change body of generated methods, choose Tools | Templates.
    }
}
