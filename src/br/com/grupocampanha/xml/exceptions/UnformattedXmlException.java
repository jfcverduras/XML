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
public class UnformattedXmlException extends Exception{
    public UnformattedXmlException(){
    super("XML mal formatado");
    }
    
}
