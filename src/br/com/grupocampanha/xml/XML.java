package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XML {

    public static Document parse(File arquivo) throws IOException, UnformattedXmlException {
        String xml = removerEscapes(new String(Files.readAllBytes(arquivo.toPath())));
        Document documento = new Document(arquivo.getAbsolutePath());
        Node nodePai = new Node("");

        if (!subNode(excluirComentarios(xml), nodePai).equals(""))
            throw new UnformattedXmlException();

        documento.node = nodePai.get(0);
        return documento;
    }

    public static Document parse(String xml) throws UnformattedXmlException {
        xml = removerEscapes(xml);
        Document documento = new Document("xml.xml");
        Node nodePai = new Node("");
        if (!subNode(xml, nodePai).equals(""))
            throw new UnformattedXmlException();
        documento.node = nodePai.get(0);

        return documento;

    }

    public static Document parse(org.w3c.dom.Document doc) throws UnformattedXmlException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
    
        try {
              Transformer aTransformer = tranFactory.newTransformer();
                      Source src = new DOMSource(doc);
        File file = new File("xml.xml");
        Result dest = new StreamResult(file);
        aTransformer.transform(src, dest);
         return parse(file);
        } catch ( IOException | TransformerException ex) {
           ex.printStackTrace();
        }
        return null;
    }

    private static Node criarNode(String fragmento) {

        Node node = null;
        String nomeProPriedade = "";
        String nome = "";
        String valorPropriedade = "";
        boolean primeiraLetra = false;
        boolean nomeFinalizado = false;
        boolean valor = false;
        boolean aspasUnicas = false;
        boolean primeiraAspa = false;

        for (int i = 1; i < fragmento.length(); i++) {
            char c = fragmento.charAt(i);
            if (node == null) {
                if (!primeiraLetra && c != ' ' && c != '/' && c != '<') {
                    primeiraLetra = true;
                    nome = Character.toString(c);
                } else if (primeiraLetra && (c == ' ' || c == '/' || c == '>'))
                    node = new Node(nome);
                else if (primeiraLetra && c != ' ')
                    nome = nome + Character.toString(c);
            } else if (c == '=')
                nomeFinalizado = true;
            else if (c != ' ' && c != '"' && c != '>' && c != '\'' && !nomeFinalizado)
                nomeProPriedade = nomeProPriedade + Character.toString(c);
            else {
                if (c == '>' && valorPropriedade.equals("") && nomeProPriedade.equals(""))
                    return node;
                if (nomeFinalizado && c == '\'' && valorPropriedade.equals("") && !primeiraAspa) {
                    aspasUnicas = true;
                    valor = true;
                    primeiraAspa = true;
                } else if (nomeFinalizado && c == '"' && valorPropriedade.equals("") && !primeiraAspa) {
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
                } else if (!valorPropriedade.equals("") && !aspasUnicas && c == '"') {
                    node.adicionarPropriedade(nomeProPriedade.trim(), valorPropriedade);
                    nomeProPriedade = "";
                    valorPropriedade = "";
                    aspasUnicas = false;
                    primeiraAspa = false;
                    nomeFinalizado = false;
                } else if (valor && primeiraAspa)
                    valorPropriedade = valorPropriedade + Character.toString(c);
            }
        }
        return node;
    }

    private static String excluirComentarios(String texto) {
        //ignora comentarios
        while (true) {
            int indexAbertura = texto.indexOf("<!--");
            int indexFechamento = texto.indexOf("-->");
            if (indexAbertura == -1 || indexFechamento == -1)
                break;
            texto = texto.substring(0, indexAbertura) + texto.substring(indexFechamento + 3);
        }
        return texto;
    }

    private static String subNode(String fragmento, Node node)  {
        fragmento = fragmento.trim();
        if (!fragmento.equals("")) {

            String nodeString = "";
            int indexAbridor = fragmento.indexOf('<');
            int indexFechador = fragmento.indexOf('>');
            nodeString = fragmento.substring(indexAbridor, indexFechador + 1);
            fragmento = fragmento.substring(indexFechador + 1).trim();
            if (!nodeFechamento(nodeString)) {

                Node tempNode = criarNode(nodeString);
                node.add(tempNode);

                fragmento = fragmento.trim();
                int indexAbertura = fragmento.indexOf("<");
                int indexFechamento = fragmento.indexOf(">");
                String inicioNode = fragmento.substring(0, 2);

                while (inicioNode.equals("<!")) {
                    indexAbertura = indexAbertura + 1 + fragmento.substring(indexAbertura + 1).indexOf('<');
                    indexFechamento = indexFechamento + 1 + fragmento.substring(indexFechamento + 1).indexOf('>');
                    inicioNode = fragmento.substring(indexAbertura, indexAbertura + 2);
                }
                if (indexAbertura != 0) {
                    tempNode.add(fragmento.substring(0, indexAbertura));
                    return fragmento.substring(indexFechamento + 1);
                }
                if (nodeFechamentoComPropriedade(nodeString))
                    return fragmento;
                String nomeProximoNode = fragmento.substring(fragmento.indexOf("<") + 1, fragmento.indexOf(">")).replace("/", "").trim();
                try {
                    while (!nomeProximoNode.equals(tempNode.getNome())) {
                        fragmento = subNode(fragmento, tempNode);
                        nomeProximoNode = fragmento.substring(fragmento.indexOf("<") + 1, fragmento.indexOf(">")).replace("/", "").trim();
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    return fragmento;
                }
                fragmento = fragmento.substring(fragmento.indexOf(">") + 1);
            }
        }
        if (fragmento.replace(" ", "").replace(" ", "").equals(""))
            fragmento = "";
        return fragmento;
    }

    private static boolean nodeFechamentoComPropriedade(String fragmento) {
        fragmento = fragmento.replace(" ", "");
       return (fragmento.substring(fragmento.length() - 2).equals("/>"));
          
    }

    private static boolean nodeFechamento(String fragmento) {
        return fragmento.replace(" ", "").substring(0, 2).equals("</");

    }

    private static String removerEscapes(String string) {
        String novaString = string;
        novaString = novaString.replace("\t", "");
        novaString = novaString.replace("\b", "");
        novaString = novaString.replace("\n", "");
        novaString = novaString.replace("\r", "");
        novaString = novaString.replace("\f", "");
        return novaString;
    }
}
