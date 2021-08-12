/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyNameException;
import br.com.grupocampanha.xml.exceptions.IllegalNodePropetyValueException;
import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import br.com.grupocampanha.xml.exceptions.NodeIndexOfBoundsException;
import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author jfc
 */
public class XML {

    public static Node parse(File arquivo) throws IOException, InsertNodeException, InsertNodeValueException, UnformattedXmlException, IllegalNodePropetyNameException, IllegalNodePropetyValueException {
        String xml = removerEscapes(new String(Files.readAllBytes(arquivo.toPath())));
        
        Node nodePai = criarNode(xml.substring(0, xml.indexOf('>')));
        if(nodePai.getNome().toLowerCase().equals("?xml")){
            xml = xml.substring(xml.indexOf('>') + 1);
        }else{
        nodePai = new Node("");
        }
  
    if(!subNode(xml, nodePai).equals("")){
   throw new UnformattedXmlException();
   }
    if(nodePai.getNome().equals("")){
        try{
        return nodePai.nodeAt(0);
        }catch (NodeIndexOfBoundsException e){
            e.printStackTrace();
        }
    }
    return nodePai;
    }

    private static Node criarNode(String fragmento) throws IllegalNodePropetyNameException, IllegalNodePropetyValueException {
        String nome = "";
        Node node = null;
        boolean primeiraLetra = false;

        String nomeProPriedade = "";
        boolean nomeFinalizado = false;
        boolean valor = false;
        boolean aspasUnicas = false;
        boolean primeiraAspa = false;
        String valorPropriedade = "";
        for (int i = 1; i < fragmento.length(); i++) {
            char c = fragmento.charAt(i);
            if (node == null) {

                if (!primeiraLetra && c != ' ' && c != '/' && c != '<') {
                    primeiraLetra = true;
                    nome = Character.toString(c);
                } else if (primeiraLetra && (c == ' ' || c == '/' || c == '>')) {
                    node = new Node(nome);
                } else if (primeiraLetra && c != ' ') {
                    nome = nome + Character.toString(c);
                }
            } else {
                if (c == '=') {
                    nomeFinalizado = true;
                } else if (c != ' ' && c != '"' && c != '>' && c != '\'' && !nomeFinalizado) {
                    nomeProPriedade = nomeProPriedade + Character.toString(c);
                } else if (c == '>' && valorPropriedade.equals("") && nomeProPriedade.equals("")) {
                    return node;
                } else if (nomeFinalizado && c == '\'' && valorPropriedade.equals("") && !primeiraAspa) {
                    aspasUnicas = true;
                    valor = true;
                    primeiraAspa = true;
                } else if (nomeFinalizado && c == '\"' && valorPropriedade.equals("") && !primeiraAspa) {
                    aspasUnicas = false;
                    valor = true;
                    primeiraAspa = true;
                } else if (!valorPropriedade.equals("") && aspasUnicas && c == '\'') {
                    node.adicionarPropriedade(nomeProPriedade.trim(), valorPropriedade);
                    nomeProPriedade = "";
                    valorPropriedade = "";
                    aspasUnicas = false;
                    primeiraAspa = false;
                    nomeFinalizado = false;
                } else if (!valorPropriedade.equals("") && !aspasUnicas && c == '\"') {
                    node.adicionarPropriedade(nomeProPriedade.trim(), valorPropriedade);
                    nomeProPriedade = "";
                    valorPropriedade = "";
                    aspasUnicas = false;
                    primeiraAspa = false;
                    nomeFinalizado = false;
                } else if (valor && primeiraAspa) {
                    valorPropriedade = valorPropriedade + Character.toString(c);
                }
            }

        }

        return node;
    }

    private static String subNode(String fragmento, Node node) throws InsertNodeException, InsertNodeValueException, IllegalNodePropetyNameException, IllegalNodePropetyValueException  {
        if (!fragmento.equals("")) {
            String nodeString = "";
            int indexAbridor = fragmento.indexOf('<');
            int indexFechador = fragmento.indexOf('>');

            nodeString = fragmento.substring(indexAbridor, indexFechador + 1);

            fragmento = fragmento.substring(indexFechador + 1);

            if (!nodeFechamento(nodeString)) {
                Node tempNode = criarNode(nodeString);
                node.adicionarNode(tempNode);

                if (fragmento.trim().indexOf("<") != 0) {
                    tempNode.setValor(fragmento.substring(0, fragmento.indexOf("<")));
                    return fragmento.substring(fragmento.indexOf(">") + 1);
                }

                if (nodeFechamentoComPropriedade(nodeString)) {
                    return fragmento;
                }

                String nomeProximoNode = fragmento.substring(fragmento.indexOf("<") + 1, fragmento.indexOf(">")).replace("/", "").trim();
                while (!nomeProximoNode.equals(tempNode.getNome())) {

                    fragmento = subNode(fragmento, tempNode);
                    nomeProximoNode = fragmento.substring(fragmento.indexOf("<") + 1, fragmento.indexOf(">")).replace("/", "").trim();
                }
           
                fragmento = fragmento.substring(fragmento.indexOf(">") + 1);
            } else {
                return fragmento;
            }

            return fragmento;

        }
        return fragmento;
    }

    private static boolean nodeFechamentoComPropriedade(String fragmento) {
        fragmento = fragmento.replace(" ", "");
        if (fragmento.substring(fragmento.length() - 2).equals("/>")) {
            return true;
        }
        return false;

    }

    private static boolean nodeFechamento(String fragmento) {
        if (fragmento.replace(" ", "").substring(0, 2).equals("</")) {
            return true;
        }
        return false;
    }
    
    private static String removerEscapes(String string){
    String novaString = string;
    novaString = novaString.replace("\t", "");
    novaString = novaString.replace("\b", "");
    novaString = novaString.replace("\n", "");
    novaString = novaString.replace("\r", "");
    novaString = novaString.replace("\f", "");
    return novaString; 
    }
}
