package br.com.grupocampanha.xml;


import br.com.grupocampanha.xml.exceptions.InsertNodeException;
import br.com.grupocampanha.xml.exceptions.InsertNodeValueException;
import java.util.HashMap;
import java.util.Map;
import br.com.grupocampanha.xml.interfaces.Find;

public class Node {

    private Map<String, String> propriedades = new HashMap<String, String>();
    private String valor;
    private Node[] nodes = new Node[0];
    private String nome;
    private int tamanho = 0;
    private int tamanhoAtributo = 0;

    public Node(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void mudarNome(String nome) {
        this.nome = nome;
    }

    public void adicionarNode(Node node) throws InsertNodeException {
        if (valor != null) {
            throw new InsertNodeException();
        }
        nodes = redimensionarArray(nodes, nodes.length + 1);
        nodes[nodes.length - 1] = node;
        tamanho++;
    }

    public boolean removerNode(Node node){

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].equals(node)) {
                for (int k = i; k < nodes.length - 1; k++) {
                    nodes[k] = nodes[k + 1];
                }
                nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
                tamanho--;
                return true;
            }
        }
       return false;
    }

    public boolean removerNode(int index){
        if (index > nodes.length - 1 || index < 0) {
         return false;
        }

        for (int i = index; i < nodes.length - 1; i++) {
            nodes[i] = nodes[i + 1];
        }
        nodes = (Node[]) redimensionarArray(nodes, nodes.length - 1);
        tamanho--;
        return true;
    }

    public void setValor(String valor) throws InsertNodeValueException {
        if (nodes.length > 0) {
            throw new InsertNodeValueException();
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public String getPropriedade(String propriedade) {
        return propriedades.get(propriedade);
    }

    public String propriedadeAt(int index) {
        return (String) propriedades.values().toArray()[index];
    }

    public Node nodeAt(int index)   {
            return nodes[index];
    }

    public void adicionarPropriedade(String propriedade, String Valor) {
        this.propriedades.put(propriedade, Valor);
        tamanhoAtributo++;
    }

    public boolean removerPropriedade(String propriedade) {
        Object[] keys = this.propriedades.keySet().toArray();

        boolean existe = false;
        for (Object o : keys) {
            if (((String) o).equals(propriedade)) {
                existe = true;
            }
        }
        this.propriedades.remove(propriedade);
        tamanhoAtributo--;
        return existe;
    }

    private Node[] redimensionarArray(Node[] array, int novoTamanho) {
        Node[] novoArray = new Node[novoTamanho];
        for (int i = 0; i < (novoTamanho > array.length ? array.length : novoTamanho   ); i++) {
            novoArray[i] = array[i];
        }
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
            for (int i = chaves.length - 1; i >= 0; i--) {
                if (((String) valores[i]).contains("\"")) {
                    sb.append(" " + chaves[i] + "='" + valores[i] + "'");
                } else {
                    sb.append(" " + chaves[i] + "=\"" + valores[i] + "\"");
                }
            }
        }

        if (valor == null && nodes.length == 0) {
            sb.append("/>");
            return sb.toString();
        }
        if (nome.toLowerCase().equals("?xml")) {
            sb.append("?>");
        } else {
            sb.append(">");
        }

        if (valor != null) {
            sb.append(valor);
            if (!nome.toLowerCase().equals("?xml")) {
                sb.append("</" + nome + ">");
            }
        } else {

            for (int i = 0; i < nodes.length; i++) {

                sb.append("\n" + gerarTabulacao(tabulacao) + nodes[i].toStringImplementation(tabulacao + 1));
            }

            if (!nome.toLowerCase().equals("?xml")) {
                sb.append("\n" + gerarTabulacao(tabulacao - 1) + "</" + nome + ">");
            }
        }
        return sb.toString();
    }

    private String gerarTabulacao(int qtd) {
        String tabulacao = "";
        for (int i = 0; i < qtd; i++) {
            tabulacao = tabulacao + "   ";
        }
        return tabulacao;
    }

    public Node find(Find f) {
        Node result = findImplementation(f, this);
        if (f.find(result)) {
            return result;
        }
        return null;
    }

    private Node findImplementation(Find f, Node node) {

        if (f.find(node)) {
            return node;
        }

        for (int i = 0; i < node.nodes.length; i++) {
        	Node n = findImplementation(f, node.nodes[i]);
        	if(n != null && f.find(n))
        		return n;
        }
        return null;
    }

    public Node[] findNodes(Find f) {

        return findNodesImplementation(f, this);
    }

    private Node[] findNodesImplementation(Find f, Node node) {
        Node[] nodes = new Node[0];

        if (f.find(node)) {
            nodes = new Node[1];
            nodes[0] = node;
        }

        for (int i = 0; i < node.nodes.length; i++) {
                nodes = Utilitario.juntarNodes(nodes, findNodesImplementation(f, node.nodes[i]));
        }
        return nodes;
    }

    public int propriedadesSize() {
        return tamanhoAtributo;
    }

    public boolean contains(Find f) {
        for (int i = 0; i < nodes.length; i++) {
            if (f.find(nodes[i])) {
                return true;
            }
        }
        return false;

    }

    public int size() {
        return tamanho;
    }

}

class Utilitario {

    public static boolean contemCaracterEspecial(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                return true;

            }

        }
        return false;
    }

    public static boolean contemCaracterEspecial(String string, boolean permirtir, char... caracter) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                for (char i : caracter) {
                    if (i == c && !permirtir) {
                        return true;
                    }
                }
            }
        }
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
