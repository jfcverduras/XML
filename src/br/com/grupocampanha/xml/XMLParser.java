/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.UnformattedXmlException;
import java.util.Stack;

/**
 *
 * @author jfc
 */
public class XMLParser {

    public static void main(String args[]) throws UnformattedXmlException {

        XMLParser parser = new XMLParser();
        Node node = parser.parse("<?xml version      =   \n     \"1"
                + ".0\" encoding=\"UTF-8\"?>\n"
                + "<note>\n"
                + "  <to><![CDATA[Toveç]]></to><CAFE/>\n"
                + "</note>");
        System.out.print(node);

    }

    
    public Document parse(String xml) throws UnformattedXmlException {

        Stack<Node> stackNodes = new Stack();
        stackNodes.add(new Document());
        int greaterThan = 0;
        String nodeContent = "";
        String nodeValue = "";
        char lastChar = 0;

        char[] chars = xml.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == ' ' && lastChar == '<')
                throw new UnformattedXmlException(i);
            if (c == '<' && greaterThan == 0) {
                if (!nodeValue.trim().isEmpty()) {
                    String trinedValue = nodeValue.trim();
                    nodeValueValidator(trinedValue);
                    stackNodes.peek().add(trinedValue);
                    nodeValue = "";
                }
                greaterThan++;
            } else if (c == '>' && greaterThan == 1) {
                greaterThan--;
                Operation operation = nodeOperation(nodeContent);
                if (operation.equals(Operation.OPENING) || operation.equals(Operation.BOTH))
                    createNewNode(nodeContent, stackNodes);
                if (operation.equals(Operation.CLOSING) || operation.equals(Operation.BOTH))
                    stackNodes.pop();
                nodeContent = "";
            } else if (greaterThan > 0)
                nodeContent += c;
            else if (c != '\t' && c != '\n' && c != '\r' && c != '\b')
                nodeValue += c;

            lastChar = c;
        }

        if (stackNodes.size() != 1 || stackNodes.peek().size() > 1)
            throw new UnformattedXmlException("O NODE ROOT DEVE CONTER APENAS UM NODE");

        return (Document) stackNodes.pop();

    }

    private void nodeValueValidator(String nodeValue) throws UnformattedXmlException {
        if (!nodeValue.chars().boxed().allMatch(Character::isAlphabetic))
            throw new UnformattedXmlException("o nome do node \"" + nodeValue + "\" e invalido;");
    }

    private void validateNodeName(String name) throws UnformattedXmlException {
        if (!name.chars().allMatch(Character::isAlphabetic))
            throw new UnformattedXmlException("o node de nome´" + name + " é invalido");
    }

    private void createNewNode(String frag, Stack<Node> stackNodes) throws UnformattedXmlException {
        if (Execution.PROCEED.equals(alternativeExecutions(frag, stackNodes))) {
            Node node = createDefaultNode(frag);
            stackNodes.peek().add(node);
            stackNodes.push(node);
        }
    }

    private Node createDefaultNode(String frag) throws UnformattedXmlException {
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

            if (i == chars.length - 1 && c == '/')
                continue;
            else if ((i == 0 && c == ' '))
                throw new UnformattedXmlException();
            else if (node == null && c != ' ')
                nome += c;
            else if (node == null && c == ' ') {
                validateNodeName(nome);
                node = new Node(nome);
            } else if (c != ' '  &&(propetieName.equals("") && !isEspecialChar(c)|| (!waitingForQuoute && Character.isAlphabetic(c) && lastchar == propetieName.charAt(propetieName.length() - 1))))
                propetieName += c;
            else if (!propetieName.isEmpty() && c == '=' && !waitingForQuoute)
                waitingForQuoute = true;
            else if (Character.isAlphabetic(c) && !waitingForQuoute && !quoteOpened && lastchar != propetieName.charAt(propetieName.length() - 1))
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

    private boolean isEspecialChar(char c) {
        return c == '\n' || c == '\t' || c == '\b' || c == '\r';

    }

    private void validarNomePropriedade(String propetieName, String nodename) throws UnformattedXmlException {
        if (!propetieName.chars().boxed().allMatch(Character::isAlphabetic))
            throw new UnformattedXmlException("node de nome: " + nodename + "contem uma propriedade invalida");
    }

    private Execution alternativeExecutions(String frag, Stack<Node> stackNodes) throws UnformattedXmlException {
        //COMENTARIO
        if (frag.startsWith("!--") && frag.endsWith("--")) {
            frag = frag.substring(3);
            frag = frag.substring(0, frag.length() - 2);
            stackNodes.peek().addComentario(frag);
            return Execution.STOP;
        }
        if (frag.toUpperCase().startsWith("![CDATA[") && frag.endsWith("]]")) {
            frag = frag.substring(8);
            frag = frag.substring(0, frag.length() - 2);
            stackNodes.peek().add(frag);
            return Execution.STOP;
        }
        if (frag.toLowerCase().startsWith("?xml")) {
            Node node = createDefaultNode(frag.replace("?", ""));
            stackNodes.peek().clonePropeties(node);
            return Execution.STOP;
        }
        return Execution.PROCEED;
    }

    private Operation nodeOperation(String frag) {
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
