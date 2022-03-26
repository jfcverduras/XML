/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Stack;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author jfc
 */
public class XML {

    public static Document parse(File file) throws UnformattedXmlException, IOException {
        
        if (!file.exists())
            throw new IOException("Arquivo nao existe");
        String filename = file.getName();
        filename = filename.substring(0, filename.lastIndexOf('.'));

        return parse(new String(Files.readAllBytes(file.toPath())), filename);

    }
    
    public static  Document parse(org.w3c.dom.Document doc) throws UnformattedXmlException {
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


    public static Document parse(String xml, String docName) throws UnformattedXmlException {
        Document doc = new Document(docName);
        doc.cab = null;
        Stack<Node> stackNodes = new Stack();
        stackNodes.add(new Node("topRootLevel"));
        int greaterThan = 0;
        String nodeContent = "";
        String nodeValue = "";
        char lastChar = 0;

        char[] chars = xml.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            char c = chars[i];

            if (c == ' ' && lastChar == '<')
                throw new UnformattedXmlException(i);
            if (c == '<' && greaterThan == 0)
                try {
                if (!nodeValue.trim().isEmpty()) {
                    String trinedValue = nodeValue.trim();
                    nodeValueValidator(trinedValue);
                    stackNodes.peek().add(trinedValue);
                    nodeValue = "";
                }
                greaterThan++;
            } catch (Exception ex) {
                UnformattedXmlException newEx = new UnformattedXmlException(ex.getMessage() + "\n erro perto do index " + (i - nodeValue.length()));
                newEx.setStackTrace(ex.getStackTrace());
                throw newEx;
            } else if (c == '>' && greaterThan == 1) {
                greaterThan--;
                Operation operation = nodeOperation(nodeContent);
                try {
                    createNewNode(nodeContent.replace('\n', ' ').replace('\r' ,' ').replace('\t', ' '), doc, stackNodes);
                    if (operation.equals(Operation.CLOSING)) {
                        Node closer = stackNodes.pop();
                        if (!stackNodes.pop().getNome().equals(closer.getNome()))
                            throw new UnformattedXmlException("Node fechado mais nao aberto");
                    }
                    if (operation.equals(Operation.BOTH))
                        stackNodes.pop();
                    nodeContent = "";
                } catch (Exception ex) {
                    UnformattedXmlException newEx = new UnformattedXmlException(ex.getMessage() + "\n erro perto do index " + (i - nodeValue.length()));
                    newEx.setStackTrace(ex.getStackTrace());
                    throw newEx;
                }
            } else if (greaterThan > 0)
                nodeContent += c;
            else if (c != '\t' && c != '\n' && c != '\r' && c != '\b')
                nodeValue += c;

            lastChar = c;
        }

        if (stackNodes.size() != 1)
            throw new RuntimeException("Erro de logica na pilha, e esperado apena um node a este ponto");
        if (stackNodes.peek().size() > 1)
            throw new UnformattedXmlException("Deve haver somente um node root");
        doc.setRootNode(stackNodes.pop().get(0));
        return doc;

    }

    private static void nodeValueValidator(String nodeValue) throws UnformattedXmlException {

    }

    private static void validateNodeName(String name) throws UnformattedXmlException {
       //NOT USED
        // if (!name.chars().allMatch(Character::isAlphabetic))
         //   throw new UnformattedXmlException("o node de nome´" + name + " é invalido");
    }

    private static void createNewNode(String frag, Document doc, Stack<Node> stackNodes) throws UnformattedXmlException {
        if (Execution.PROCEED.equals(alternativeExecutions(frag, doc, stackNodes))) {
            Node node = createDefaultNode(frag);
            if (!node.isFechamento())
                stackNodes.peek().add(node);
            stackNodes.push(node);
        }
    }

    private  static Node createDefaultNode(String frag) throws UnformattedXmlException {
        char[] chars = frag.toCharArray();
        String nome = "";
        Node node = null;
        boolean quoteOpened = false;
        boolean findedEqual = false;
        String propetieName = "";
        String propetieValue = "";
        QuouteType q = QuouteType.UNDEFINED;
        boolean waitingForQuoute = false;
        char lastchar = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i == 0 && c == '/')
                return new Node(frag.substring(1), true);
            else if (i == chars.length - 1 && c == '/')
                continue;
            else if ((i == 0 && c == ' '))
                throw new UnformattedXmlException();
            else if (node == null && c != ' ')
                nome += c;
            else if (node == null && c == ' ') {
                validateNodeName(nome);
                node = new Node(nome);
            } else if (c != ' ' && c != '\'' c != '\"' && (propetieName.equals("") && !isEspecialChar(c) || (!waitingForQuoute && lastchar == propetieName.charAt(propetieName.length() - 1))))
                propetieName += c;
            else if (!propetieName.isEmpty() && c == '=' && !waitingForQuoute)
                waitingForQuoute = true;
            else if (!waitingForQuoute && !quoteOpened && lastchar != propetieName.charAt(propetieName.length() - 1))
                throw new UnformattedXmlException("o node: " + nome + " contem propriedades invalidas");
            else if (c != ' ' && c != '\t' && c != '\r' && c != '\n' && c != '\'' && c != '\"' && waitingForQuoute && !quoteOpened)
                throw new UnformattedXmlException("o node: " + nome + " contem valores de propriedade fora de aspas");
            else if (c == '\'' && !quoteOpened) {
                q = QuouteType.SINGLE;
                quoteOpened = true;
            } else if (c == '\"' && !quoteOpened) {
                q = QuouteType.DOUBLE;
                quoteOpened = true;
            } else if (((c == '\'' && q.equals(QuouteType.DOUBLE)) || (c == '\"' && q.equals(QuouteType.SINGLE)) || (c != '\'' && c != '\"')) && quoteOpened)
                propetieValue += c;
            else if ((c == '\'' && q.equals(QuouteType.SINGLE)) || (c == '\"' && q.equals(QuouteType.DOUBLE))) {
                validarNomePropriedade(propetieName, node.getNome());
                node.adicionarPropriedade(propetieName, propetieValue);
                propetieName = "";
                propetieValue = "";
                quoteOpened = false;
                waitingForQuoute = false;
                q = QuouteType.UNDEFINED;
            }
            lastchar = c;
        }
        if (node == null)
            node = new Node(nome);
        return node;
    }

    private static boolean isEspecialChar(char c) {
        return c == '\n' || c == '\t' || c == '\b' || c == '\r';

    }

    private static void validarNomePropriedade(String propetieName, String nodename) throws UnformattedXmlException {
        //NOT USED
        //if (!propetieName.chars().boxed().allMatch(Character::isAlphabetic))
        //   throw new UnformattedXmlException("node de nome: " + nodename + "contem uma propriedade invalida");
    }

    private static Execution alternativeExecutions(String frag, Document doc, Stack<Node> stackNodes) throws UnformattedXmlException {
        //COMENTARIO
        if (frag.startsWith("!--") && frag.endsWith("--")) {
            stackNodes.peek().addComentario(frag);
            return Execution.STOP;
        }
        if (frag.startsWith("![CDATA[") && frag.endsWith("]]")) {
            frag = frag.substring(8);
            frag = frag.substring(0, frag.length() - 2);
            stackNodes.peek().add(frag);
            return Execution.STOP;
        }
        if (frag.startsWith("?xml") && frag.endsWith("?")) {
            if (doc.cab != null)
                throw new RuntimeException("Xml nao pode conter dois cabecalhos");
            if(stackNodes.size() > 1)
                 throw new RuntimeException("esse node deveria estar no topo do documento");
            Node node = createDefaultNode(frag.replace("?", ""));
            if (node.getPropriedade("version") == null || node.getPropriedade("version").isEmpty())
                throw new UnformattedXmlException("Era esperado a versao");
            DocumentCab cab = new DocumentCab();
            cab.propeties = node.propriedades;
            doc.setRootNode(node);
            doc.cab = cab;
            return Execution.STOP;
        }
        return Execution.PROCEED;
    }

    private static Operation nodeOperation(String frag) {
        char[] chars = frag.toCharArray();
        if (chars[chars.length - 1] == '/')
            return Operation.BOTH;
        else if (chars[0] == '/')
            return Operation.CLOSING;
        else
            return Operation.OPENING;
    }

    enum Operation {
        OPENING, CLOSING, BOTH
    }

    enum Execution {
        PROCEED, STOP
    }

    enum QuouteType {
        SINGLE, DOUBLE, UNDEFINED

    }
}
