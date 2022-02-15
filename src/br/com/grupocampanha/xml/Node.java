package br.com.grupocampanha.xml;

import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import java.util.HashMap;
import java.util.Map;
import br.com.grupocampanha.xml.interfaces.Find;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Node {

    private Map<String, String> propriedades = new HashMap<String, String>();
    private String valor;
    private Node[] nodes = new Node[0];
    private String nome;
    private int tamanho = 0;
    private int tamanhoAtributo = 0;
    private List<String> comentarios = new ArrayList();

    public Node(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public Node mudarNome(String nome) {
        this.nome = nome;
        return this;
    }

    public Node add(Node node) {
        if (valor != null)
            throw new InsertNodeException();
        nodes = redimensionarArray(nodes, nodes.length + 1);
        nodes[nodes.length - 1] = node;
        tamanho++;
        return this;
    }

    public void remove() {
        this.nodes = new Node[0];
        this.valor = null;

    }

    public void remove(Node node) {

        for (int i = 0; i < nodes.length; i++)
            if (nodes[i].equals(node)) {
                for (int k = i; k < nodes.length - 1; k++)
                    nodes[k] = nodes[k + 1];
                nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
                tamanho--;
                break;
            }

    }

    public void remove(int index) {
        if (index > nodes.length - 1 || index < 0)
            return;

        for (int i = index; i < nodes.length - 1; i++)
            nodes[i] = nodes[i + 1];
        nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
        tamanho--;
    }

    public void removeAllPropeties() {
        Set<String> keys = this.propriedades.keySet();
        keys.forEach(f -> {
            propriedades.remove(f);
        });
    }
    protected void clonePropeties(Node node){
        this.propriedades = node.propriedades;
    }
    
    public Node add(String valor) {
        if (nodes.length > 0)
            throw new InsertNodeValueException();
        this.valor = valor;
        return this;
    }

    public void addComentario(String comentario) {
        this.comentarios.add(comentario);
    }

    public void removeComentario(int i) {
        this.comentarios.remove(i);
    }

    public String getComentario(int i) {
        return this.comentarios.get(i);
    }

    public void setComentario(String comentario, int index) {
        this.comentarios.set(index, comentario);
    }

    public String[] getAllComentarios() {
        return this.comentarios.toArray(new String[0]);
    }

    public void removeAllComentarios() {
        this.comentarios.removeAll(comentarios);
    }

    public String getValor() {
        return valor;
    }

    public void forEach(Consumer<Node> a) {
        for (Node n : nodes)
            a.accept(n);

    }

    public String getPropriedade(String propriedade) {
        return propriedades.get(propriedade);
    }

    public String getPropriedade(int index) {
        return (String) propriedades.values().toArray()[index];
    }

    public Node get(int index) {
        return nodes[index];
    }

    public Node get(String path) {
        return getByString(this, path);

    }

    private Node getByString(Node nodePrincipal, String path) {
        class PS {

            int initNextNode;
            int nodePosition;
            String nomeNode;

            public PS(String path) {
                path = path.substring(path.indexOf("/") + 1);
                initNextNode = path.indexOf("/") + 1;
                String frag = path.substring(0, path.indexOf("/"));
                nodePosition = nodeIndex(frag);
                if (nodePosition == -1)
                    nomeNode = frag;
                else
                    nomeNode = frag.substring(0, frag.indexOf("["));
            }

            private int nodeIndex(String frag) {
                int initIndex;
                int endIndex;
                initIndex = frag.indexOf("[");
                if (initIndex == -1)
                    return -1;
                int qtdchar = frag.substring(0, initIndex + 1).length();
                endIndex = frag.substring(initIndex + 1).indexOf("]");
                if (endIndex == -1)
                    return -1;

                endIndex += qtdchar;

                String numeros = frag.substring(initIndex + 1, endIndex);
                if (!numeros.chars().allMatch(Character::isDigit))
                    return -1;
                return Integer.parseInt(numeros);
            }

        }
        PS ps = new PS(path);
        Node[] nods = Arrays.asList(nodePrincipal.nodes).stream().filter(f -> f.getNome().equals(ps.nomeNode)).collect(Collectors.toList()).toArray(new Node[0]);
        Node node;
        if (nods.length > 1)
            if (ps.nodePosition == -1)
                return null;
            else
                node = nods[ps.nodePosition];
        else if (nods.length == 0)
            return null;
        else
            node = nods[0];

        if (!path.substring(ps.initNextNode + 1).isEmpty())
            return getByString(node, path.substring(ps.initNextNode));
        else
            return node;
    }

    public Node adicionarPropriedade(String propriedade, String Valor) {
        this.propriedades.put(propriedade, Valor);
        tamanhoAtributo++;
        return this;
    }

    public void removerPropriedade(String propriedade) {

        this.propriedades.remove(propriedade);
        tamanhoAtributo--;

    }

    private Node[] redimensionarArray(Node[] array, int novoTamanho) {
        Node[] novoArray = new Node[novoTamanho];
        for (int i = 0; i < (novoTamanho > array.length ? array.length : novoTamanho); i++)
            novoArray[i] = array[i];
        return novoArray;
    }

    @Override
    public String toString() {
        return toStringImplementation(1);

    }

    private String toStringImplementation(int tabulacao) {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + nome);
        if (propriedades != null) {
            Object[] chaves = propriedades.keySet().toArray();
            Object[] valores = propriedades.values().toArray();
            for (int i = chaves.length - 1; i >= 0; i--)
                if (((String) valores[i]).contains("\""))
                    sb.append(" " + chaves[i] + "='" + valores[i] + "'");
                else
                    sb.append(" " + chaves[i] + "=\"" + valores[i] + "\"");
        }

        if (valor == null && nodes.length == 0) {
            sb.append("/>");
            return sb.toString();
        }
        if (nome.toLowerCase().equals("?xml"))
            sb.append("?>");
        else
            sb.append(">");

        if (valor != null) {
            sb.append(valor);
            if (!nome.toLowerCase().equals("?xml"))
                sb.append("</" + nome + ">");
        } else {

            for (Node node : nodes)
                sb.append("\n" + gerarTabulacao(tabulacao) + node.toStringImplementation(tabulacao + 1));

            if (!nome.toLowerCase().equals("?xml"))
                sb.append("\n" + gerarTabulacao(tabulacao - 1) + "</" + nome + ">");
        }
        return sb.toString();
    }

    private String gerarTabulacao(int qtd) {
        String tabulacao = "";
        for (int i = 0; i < qtd; i++)
            tabulacao = tabulacao + "   ";
        return tabulacao;
    }

    public Node find(Find f) {
        return findImplementation(f, this);
    }

    private Node findImplementation(Find f, Node node) {

        if (f.find(node))
            return node;

        for (int i = 0; i < node.nodes.length; i++) {
            Node n = findImplementation(f, node.nodes[i]);
            if (n != null && f.find(n))
                return n;
        }
        return null;
    }

    public Node[] findAll(Find f) {

        return findNodesImplementation(f, this);
    }

    private Node[] findNodesImplementation(Find f, Node node) {
        Node[] nodes = new Node[0];

        if (f.find(node)) {
            nodes = new Node[1];
            nodes[0] = node;
        }

        for (int i = 0; i < node.nodes.length; i++)
            nodes = Utilitario.juntarNodes(nodes, findNodesImplementation(f, node.nodes[i]));
        return nodes;
    }

    public int propriedadesSize() {
        return tamanhoAtributo;
    }

    public boolean contains(Find f) {
        for (int i = 0; i < nodes.length; i++)
            if (f.find(nodes[i]))
                return true;
        return false;

    }

    public int size() {
        return tamanho;
    }

}

class Utilitario {

    public static boolean contemCaracterEspecial(String string) {
        for (char c : string.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c))
                return true;
        return false;
    }

    public static boolean contemCaracterEspecial(String string, boolean permirtir, char... caracter) {
        for (char c : string.toCharArray())
            if (!Character.isLetter(c) && !Character.isDigit(c))
                for (char i : caracter)
                    if (i == c && !permirtir)
                        return true;
        return false;
    }

    public static Node[] juntarNodes(Node[] n1, Node[] n2) {
        int sizeNovoNode = n1.length + n2.length;
        Node[] novoArrayNode = new Node[sizeNovoNode];
        int i = 0;
        for (Node n : n1) {
            novoArrayNode[i] = n;
            i++;
        }
        for (Node n : n2) {
            novoArrayNode[i] = n;
            i++;
        }
        return novoArrayNode;
    }
}
